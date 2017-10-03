package cn.ac.nya.nsasm;

import java.util.*;

/**
 * Created by drzzm on 2017.4.21.
 */
public class NSASM {

    public static final String version = "0.30 (Java)";

    protected enum RegType {
        CHAR, STR, INT, FLOAT
    }

    protected class Register {
        public RegType type;
        public Object data;
        public int strPtr = 0;
        public boolean readOnly;

        @Override
        public String toString() {
            return "Type: " + type.name() + "\n" +
                    "Data: " + data.toString() + "\n" +
                    "ReadOnly: " + readOnly;
        }

        public void copy(Register reg) {
            type = reg.type;
            data = reg.data;
            strPtr = reg.strPtr;
            readOnly = reg.readOnly;
        }
    }

    protected interface Operator {
        Result run(Register dst, Register src);
    }

    private LinkedHashMap<String, Register> heapManager;
    private LinkedList<Register> stackManager;
    private int stackSize;
    protected Register[] regGroup;
    private Register stateReg;

    private LinkedList<Integer> backupReg;
    private int progSeg, tmpSeg;
    private int progCnt, tmpCnt;

    protected LinkedHashMap<String, Operator> funList;
    private LinkedHashMap<String, String[]> code;

    public enum Result {
        OK, ERR, ETC
    }

    private enum WordType {
        REG, CHAR, STR, INT,
        FLOAT, VAR, TAG, SEG
    }

    private boolean verifyBound(String var, char left, char right) {
        return var.charAt(0) == left && var.charAt(var.length() - 1) == right;
    }

    private boolean verifyWord(String var, WordType type) {
        switch (type) {
            case REG:
                return var.charAt(0) == 'r' || var.charAt(0) == 'R';
            case CHAR:
                return verifyBound(var, '\'', '\'');
            case STR:
                return verifyBound(var, '\"', '\"') ||
                       (var.split("\"").length > 2 && var.contains("*"));
            case INT:
                if (var.endsWith("f") || var.endsWith("F"))
                    return var.startsWith("0x") || var.startsWith("0X");
                return (
                    !var.contains(".")
                ) && (
                    (var.charAt(0) >= '0' && var.charAt(0) <= '9') ||
                    var.charAt(0) == '-' || var.charAt(0) == '+' ||
                    var.endsWith("h") || var.endsWith("H")
                );
            case FLOAT:
                return (
                    var.contains(".") ||
                    var.endsWith("f") || var.endsWith("F")
                ) && (
                    (var.charAt(0) >= '0' && var.charAt(0) <= '9') ||
                    var.charAt(0) == '-' || var.charAt(0) == '+'
                ) && (!var.startsWith("0x") || !var.startsWith("0X"));
            case VAR:
                return !verifyWord(var, WordType.REG) && !verifyWord(var, WordType.CHAR) &&
                       !verifyWord(var, WordType.STR) && !verifyWord(var, WordType.INT) &&
                       !verifyWord(var, WordType.FLOAT) && !verifyWord(var, WordType.TAG);
            case TAG:
                return verifyBound(var, '[', ']');
            case SEG:
                return verifyBound(var, '<', '>');
        }
        return false;
    }

    private Register getRegister(String var) {
        if (var.length() == 0) return null;
        if (verifyWord(var, WordType.REG)) {
            //Register
            int index = Integer.valueOf(var.substring(1));
            if (index < 0 || index >= regGroup.length) return null;
            return regGroup[index];
        } else if (verifyWord(var, WordType.VAR)) {
            //Variable
            if (!heapManager.containsKey(var)) return null;
            return heapManager.get(var);
        } else {
            //Immediate number
            Register register = new Register();
            if (verifyWord(var, WordType.CHAR)) {
                if (var.length() < 3) return null;
                char tmp = 0;
                if (var.charAt(1) == '\\') {
                    if (var.length() < 4) return null;
                    switch (var.charAt(2)) {
                        case 'n': tmp = '\n'; break;
                        case 'r': tmp = '\r'; break;
                        case 't': tmp = '\t'; break;
                        case '\\': tmp = '\\'; break;
                    }
                } else {
                    tmp = var.charAt(1);
                }
                register.type = RegType.CHAR;
                register.readOnly = true;
                register.data = tmp;
            } else if (verifyWord(var, WordType.STR)) {
                if (var.length() < 3) return null;
                String tmp, rep;
                try {
                    if (var.split("\"").length > 2) {
                        tmp = rep = var.split("\"")[1];
                        Register repeat = getRegister(var.split("\"")[2].replace("*", ""));
                        if (repeat == null) return null;
                        if (repeat.type != RegType.INT) return null;
                        for (int i = 1; i < (int) repeat.data; i++)
                            tmp = tmp.concat(rep);
                    } else {
                        tmp = var.split("\"")[1];
                    }
                } catch (Exception e) {
                    return null;
                }
                register.type = RegType.STR;
                register.readOnly = true;
                register.data = tmp;
            } else if (verifyWord(var, WordType.INT)) {
                int tmp;
                if (
                    (var.contains("x") || var.contains("X")) ^
                    (var.contains("h") || var.contains("H"))
                ) {
                    if (
                        (var.contains("x") || var.contains("X")) &&
                        (var.contains("h") || var.contains("H"))
                    ) return null;
                    try {
                        tmp = Integer.valueOf(
                                var.replace("h", "").replace("H", "")
                                   .replace("x", "").replace("X", ""),
                            16);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    try {
                        tmp = Integer.parseInt(var);
                    } catch (Exception e) {
                        return null;
                    }
                }
                register.type = RegType.INT;
                register.readOnly = true;
                register.data = tmp;
            } else if (verifyWord(var, WordType.FLOAT)) {
                float tmp;
                try {
                    tmp = Float.parseFloat(var.replace("f", "").replace("F", ""));
                } catch (Exception e) {
                    return null;
                }
                register.type = RegType.FLOAT;
                register.readOnly = true;
                register.data = tmp;
            } else if (verifyWord(var, WordType.TAG) || verifyWord(var, WordType.SEG)) {
                register.type = RegType.STR;
                register.readOnly = true;
                register.data = var;
            } else return null;
            return register;
        }
    }

    public Result execute(String var) {
        String operator, dst, src;
        Register dr = null, sr = null;

        operator = var.split(" ")[0];
        operator = operator.toLowerCase(); //To lower case
        if (operator.length() + 1 < var.length()) {
            if (
                operator.equals("var") || operator.equals("int") ||
                operator.equals("char") || operator.equals("float") ||
                operator.equals("str")
            ) { //Variable define
                dst = var.substring(operator.length() + 1).split("=")[0];
                if (var.length() <= operator.length() + 1 + dst.length()) return Result.ERR;
                if (var.charAt(operator.length() + 1 + dst.length()) == '=')
                    src = var.substring(operator.length() + 1 + dst.length() + 1);
                else src = "";
                dr = new Register();
                dr.readOnly = true; dr.type = RegType.STR; dr.data = dst;
                sr = getRegister(src);
            } else { //Normal code
                if (
                    verifyWord(var.substring(operator.length() + 1), WordType.STR) ||
                    verifyWord(var.substring(operator.length() + 1), WordType.CHAR)
                ) {
                    dst = var.substring(operator.length() + 1);
                    src = "";
                } else {
                    dst = var.substring(operator.length() + 1).split(",")[0];
                    if (var.length() <= operator.length() + 1 + dst.length())
                        src = "";
                    else if (var.charAt(operator.length() + 1 + dst.length()) == ',')
                        src = var.substring(operator.length() + 1 + dst.length() + 1);
                    else src = "";
                }
                dr = getRegister(dst);
                sr = getRegister(src);
            }
        }

        if (!funList.containsKey(operator))
            return verifyWord(operator, WordType.TAG) ? Result.OK : Result.ERR;

        return funList.get(operator).run(dr, sr);
    }

    public void run() {
        if (code == null) return;
        Result result; String segBuf, codeBuf;

        progSeg = progCnt = 0;

        for (; progSeg < code.keySet().size(); progSeg++) {
            segBuf = (String) (code.keySet().toArray())[progSeg];
            if (code.get(segBuf) == null) continue;

            for (; progCnt < code.get(segBuf).length; progCnt++) {
                if (tmpSeg >= 0 || tmpCnt >= 0) {
                    progSeg = tmpSeg; progCnt = tmpCnt;
                    tmpSeg = -1; tmpCnt = -1;
                }

                segBuf = (String) (code.keySet().toArray())[progSeg];
                if (code.get(segBuf) == null) break;
                codeBuf = code.get(segBuf)[progCnt];

                if (codeBuf.length() == 0) {
                    continue;
                }

                result = execute(codeBuf);
                if (result == Result.ERR) {
                    Util.print("\nNSASM running error!\n");
                    Util.print("At "+ segBuf + ", line " + (progCnt + 1) + ": " + codeBuf + "\n\n");
                    return;
                } else if (result == Result.ETC) {
                    return;
                }
            }

            if (!backupReg.isEmpty()) {
                progCnt = backupReg.pop() + 1;
                progSeg = backupReg.pop() - 1;
            } else progCnt = 0;
        }
    }

    private String[] convToArray(String var) {
        Scanner scanner = new Scanner(var);
        LinkedList<String> buf = new LinkedList<>();

        while (scanner.hasNextLine()) {
            buf.add(scanner.nextLine());
        }

        if (buf.isEmpty()) return null;
        return buf.toArray(new String[0]);
    }

    private Result appendCode(String[][] code) {
        if (code == null) return Result.OK;
        for (String[] seg : code) {
            if (seg[0].startsWith(".")) continue; //This is conf seg
            if (seg[0].startsWith("@")) { //This is override seg
                if (!this.code.containsKey(seg[0].substring(1))) {
                    Util.print("\nNSASM loading error!\n");
                    Util.print("At "+ seg[0].substring(1) + "\n");
                    return Result.ERR;
                }
                this.code.replace(seg[0].substring(1), convToArray(seg[1]));
            } else {
                if (this.code.containsKey(seg[0])) {
                    if (seg[0].startsWith("_pub_")) continue; //This is pub seg
                    Util.print("\nNSASM loading error!\n");
                    Util.print("At "+ seg[0] + "\n");
                    return Result.ERR;
                }
                this.code.put(seg[0], convToArray(seg[1]));
            }
        }
        return Result.OK;
    }

    public NSASM(int heapSize, int stackSize, int regCnt, String[][] code) {
        heapManager = new LinkedHashMap<>(heapSize);
        stackManager = new LinkedList<>();
        this.stackSize = stackSize;

        stateReg = new Register();
        stateReg.data = 0;
        stateReg.readOnly = false;
        stateReg.type = RegType.INT;

        backupReg = new LinkedList<>();
        progSeg = 0; progCnt = 0;
        tmpSeg = -1; tmpCnt = -1;

        regGroup = new Register[regCnt];
        for (int i = 0; i < regGroup.length; i++) {
            regGroup[i] = new Register();
            regGroup[i].type = RegType.CHAR;
            regGroup[i].readOnly = false;
            regGroup[i].data = 0;
        }

        funList = new LinkedHashMap<>();
        loadFunList();

        this.code = new LinkedHashMap<>();
        if (appendCode(code) == Result.ERR) {
            Util.print("At file: " + "_main_" + "\n\n");
            this.code.clear();
        }
    }
    
    private Object convValue(Object value, RegType type) {
        switch (type) {
            case INT:
                return Integer.valueOf(value.toString());
            case CHAR:
                return (value.toString()).charAt(0);
            case FLOAT:
                return Float.valueOf(value.toString());
        }
        return value;
    }

    private Result calcInt(Register dst, Register src, char type) {
        switch (type) {
            case '+': dst.data = (int) convValue(dst.data, RegType.INT) + (int) convValue(src.data, RegType.INT); break;
            case '-': dst.data = (int) convValue(dst.data, RegType.INT) - (int) convValue(src.data, RegType.INT); break;
            case '*': dst.data = (int) convValue(dst.data, RegType.INT) * (int) convValue(src.data, RegType.INT); break;
            case '/': dst.data = (int) convValue(dst.data, RegType.INT) / (int) convValue(src.data, RegType.INT); break;
            case '&': dst.data = (int) convValue(dst.data, RegType.INT) & (int) convValue(src.data, RegType.INT); break;
            case '|': dst.data = (int) convValue(dst.data, RegType.INT) | (int) convValue(src.data, RegType.INT); break;
            case '~': dst.data = ~(int) convValue(dst.data, RegType.INT); break;
            case '^': dst.data = (int) convValue(dst.data, RegType.INT) ^ (int) convValue(src.data, RegType.INT); break;
            case '<': dst.data = (int) convValue(dst.data, RegType.INT) << (int) convValue(src.data, RegType.INT); break;
            case '>': dst.data = (int) convValue(dst.data, RegType.INT) >> (int) convValue(src.data, RegType.INT); break;
            default: return Result.ERR;
        }
        return Result.OK;
    }

    private Result calcChar(Register dst, Register src, char type) {
        switch (type) {
            case '+': dst.data = (char) convValue(dst.data, RegType.CHAR) + (char) convValue(src.data, RegType.CHAR); break;
            case '-': dst.data = (char) convValue(dst.data, RegType.CHAR) - (char) convValue(src.data, RegType.CHAR); break;
            case '*': dst.data = (char) convValue(dst.data, RegType.CHAR) * (char) convValue(src.data, RegType.CHAR); break;
            case '/': dst.data = (char) convValue(dst.data, RegType.CHAR) / (char) convValue(src.data, RegType.CHAR); break;
            case '&': dst.data = (char) convValue(dst.data, RegType.CHAR) & (char) convValue(src.data, RegType.CHAR); break;
            case '|': dst.data = (char) convValue(dst.data, RegType.CHAR) | (char) convValue(src.data, RegType.CHAR); break;
            case '~': dst.data = ~(char) convValue(dst.data, RegType.CHAR); break;
            case '^': dst.data = (char) convValue(dst.data, RegType.CHAR) ^ (char) convValue(src.data, RegType.CHAR); break;
            case '<': dst.data = (char) convValue(dst.data, RegType.CHAR) << (char) convValue(src.data, RegType.CHAR); break;
            case '>': dst.data = (char) convValue(dst.data, RegType.CHAR) >> (char) convValue(src.data, RegType.CHAR); break;
            default: return Result.ERR;
        }
        return Result.OK;
    }

    private Result calcFloat(Register dst, Register src, char type) {
        switch (type) {
            case '+': dst.data = (float) convValue(dst.data, RegType.FLOAT) + (float) convValue(src.data, RegType.FLOAT); break;
            case '-': dst.data = (float) convValue(dst.data, RegType.FLOAT) - (float) convValue(src.data, RegType.FLOAT); break;
            case '*': dst.data = (float) convValue(dst.data, RegType.FLOAT) * (float) convValue(src.data, RegType.FLOAT); break;
            case '/': dst.data = (float) convValue(dst.data, RegType.FLOAT) / (float) convValue(src.data, RegType.FLOAT); break;
            default: return Result.ERR;
        }
        return Result.OK;
    }

    private Result calcStr(Register dst, Register src, char type) {
        switch (type) {
            case '+': dst.strPtr = dst.strPtr + (int) convValue(src.data, RegType.INT); break;
            case '-': dst.strPtr = dst.strPtr - (int) convValue(src.data, RegType.INT); break;
            default: return Result.ERR;
        }
        return Result.OK;
    }

    private Result calc(Register dst, Register src, char type) {
        switch (dst.type) {
            case INT:
                return calcInt(dst, src, type);
            case CHAR:
                return calcChar(dst, src, type);
            case FLOAT:
                return calcFloat(dst, src, type);
            case STR:
                return calcStr(dst, src, type);
        }
        return Result.OK;
    }

    protected void loadFunList() {
        funList.put("rem", (dst, src) -> {
            return Result.OK;
        });

        funList.put("var", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.VAR)) return Result.ERR;
            if (heapManager.containsKey((String) dst.data)) return Result.ERR;
            if (src.type != RegType.STR) src.readOnly = false;
            heapManager.put((String) dst.data, src);
            return Result.OK;
        });

        funList.put("int", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.VAR)) return Result.ERR;
            if (heapManager.containsKey((String) dst.data)) return Result.ERR;
            if (src.type != RegType.STR) src.readOnly = false;
            if (src.type != RegType.INT) return Result.ERR;
            heapManager.put((String) dst.data, src);
            return Result.OK;
        });

        funList.put("char", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.VAR)) return Result.ERR;
            if (heapManager.containsKey((String) dst.data)) return Result.ERR;
            if (src.type != RegType.STR) src.readOnly = false;
            if (src.type != RegType.CHAR) return Result.ERR;
            heapManager.put((String) dst.data, src);
            return Result.OK;
        });

        funList.put("float", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.VAR)) return Result.ERR;
            if (heapManager.containsKey((String) dst.data)) return Result.ERR;
            if (src.type != RegType.STR) src.readOnly = false;
            if (src.type != RegType.FLOAT) return Result.ERR;
            heapManager.put((String) dst.data, src);
            return Result.OK;
        });

        funList.put("str", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.VAR)) return Result.ERR;
            if (heapManager.containsKey((String) dst.data)) return Result.ERR;
            if (src.type != RegType.STR) src.readOnly = false;
            if (src.type != RegType.STR) return Result.ERR;
            heapManager.put((String) dst.data, src);
            return Result.OK;
        });

        funList.put("mov", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            if (dst.type == RegType.CHAR && src.type == RegType.STR) {
                dst.data = ((String) src.data).charAt(src.strPtr);
            } else if (dst.type == RegType.STR && src.type == RegType.CHAR) {
                char[] array = ((String) dst.data).toCharArray();
                array[dst.strPtr] = (char) src.data;
                dst.data = new String(array);
            } else {
                dst.copy(src);
                if (dst.readOnly) dst.readOnly = false;
            }
            return Result.OK;
        });

        funList.put("push", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (stackManager.size() >= stackSize) return Result.ERR;
            stackManager.push(dst);
            return Result.OK;
        });

        funList.put("pop", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            dst.copy(stackManager.pop());
            return Result.OK;
        });

        funList.put("in", (dst, src) -> {
            if (src == null) {
                src = new Register();
                src.type = RegType.INT;
                src.data = 0x00;
                src.readOnly = true;
            }
            if (dst == null) return Result.ERR;
            String buf; Register reg;
            switch ((int) src.data) {
                case 0x00:
                    if (dst.readOnly && dst.type != RegType.STR) return Result.ERR;
                    buf = Util.scan();
                    switch (dst.type) {
                        case INT:
                            reg = getRegister(buf);
                            if (reg == null) return Result.OK;
                            if (reg.type != RegType.INT) return Result.OK;
                            dst.data = reg.data;
                            break;
                        case CHAR:
                            if (buf.length() < 1) return Result.OK;
                            dst.data = buf.charAt(0);
                            break;
                        case FLOAT:
                            reg = getRegister(buf);
                            if (reg == null) return Result.OK;
                            if (reg.type != RegType.FLOAT) return Result.OK;
                            dst.data = reg.data;
                            break;
                        case STR:
                            if (buf.length() < 1) return Result.OK;
                            dst.data = buf;
                            dst.strPtr = 0;
                            break;
                    }
                    break;
                case 0xFF:
                    Util.print("[DEBUG] <<< ");
                    if (dst.readOnly && dst.type != RegType.STR) return Result.ERR;
                    buf = Util.scan();
                    switch (dst.type) {
                        case INT:
                            reg = getRegister(buf);
                            if (reg == null) return Result.OK;
                            if (reg.type != RegType.INT) return Result.OK;
                            dst.data = reg.data;
                            break;
                        case CHAR:
                            if (buf.length() < 1) return Result.OK;
                            dst.data = buf.charAt(0);
                            break;
                        case FLOAT:
                            reg = getRegister(buf);
                            if (reg == null) return Result.OK;
                            if (reg.type != RegType.FLOAT) return Result.OK;
                            dst.data = reg.data;
                            break;
                        case STR:
                            if (buf.length() < 1) return Result.OK;
                            dst.data = buf;
                            dst.strPtr = 0;
                            break;
                    }
                    break;
                default:
                    return Result.ERR;
            }
            return Result.OK;
        });

        funList.put("out", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            switch ((int) dst.data) {
                case 0x00:
                    if (src.type == RegType.STR) {
                        Util.print(((String) src.data).substring(src.strPtr));
                    } else Util.print(src.data);
                    break;
                case 0xFF:
                    Util.print("[DEBUG] >>> ");
                    if (src.type == RegType.STR) {
                        Util.print(((String) src.data).substring(src.strPtr));
                    } else Util.print(src.data);
                    break;
                default:
                    return Result.ERR;
            }
            return Result.OK;
        });

        funList.put("prt", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type == RegType.STR) {
                Util.print(((String) dst.data).substring(dst.strPtr) + '\n');
            } else Util.print(dst.data.toString() + '\n');
            return Result.OK;
        });

        funList.put("add", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '+');
        });

        funList.put("inc", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            Register register = new Register();
            register.readOnly = false;
            register.type = RegType.CHAR;
            register.data = 1;
            return calc(dst, register, '+');
        });

        funList.put("sub", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '-');
        });

        funList.put("dec", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            Register register = new Register();
            register.readOnly = false;
            register.type = RegType.CHAR;
            register.data = 1;
            return calc(dst, register, '-');
        });

        funList.put("mul", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '*');
        });

        funList.put("div", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '/');
        });

        funList.put("and", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '&');
        });

        funList.put("or", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '|');
        });

        funList.put("xor", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '^');
        });

        funList.put("not", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, null, '~');
        });

        funList.put("shl", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '<');
        });

        funList.put("shr", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.readOnly) return Result.ERR;
            return calc(dst, src, '>');
        });

        funList.put("cmp", (dst, src) -> {
            if (src == null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (funList.get("mov").run(stateReg, dst) == Result.ERR)
                return Result.ERR;
            if (funList.get("sub").run(stateReg, src) == Result.ERR)
                return Result.ERR;
            return Result.OK;
        });

        funList.put("jmp", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.TAG)) return Result.ERR;
            String tag = (String) dst.data;
            String segBuf, lineBuf;

            for (int seg = 0; seg < code.keySet().size(); seg++) {
                segBuf = (String) (code.keySet().toArray())[seg];
                if (code.get(segBuf) == null) continue;
                for (int line = 0; line < code.get(segBuf).length; line++) {
                    lineBuf = code.get(segBuf)[line];
                    if (tag.equals(lineBuf)) {
                        tmpSeg = seg;
                        tmpCnt = line;
                        return Result.OK;
                    }
                }
            }

            return Result.ERR;
        });

        funList.put("jz", (dst, src) -> {
            if ((float) convValue(stateReg.data, RegType.FLOAT) == 0) {
                return funList.get("jmp").run(dst, src);
            }
            return Result.OK;
        });

        funList.put("jnz", (dst, src) -> {
            if ((float) convValue(stateReg.data, RegType.FLOAT) != 0) {
                return funList.get("jmp").run(dst, src);
            }
            return Result.OK;
        });

        funList.put("jg", (dst, src) -> {
            if ((float) convValue(stateReg.data, RegType.FLOAT) > 0) {
                return funList.get("jmp").run(dst, src);
            }
            return Result.OK;
        });

        funList.put("jl", (dst, src) -> {
            if ((float) convValue(stateReg.data, RegType.FLOAT) < 0) {
                return funList.get("jmp").run(dst, src);
            }
            return Result.OK;
        });

        funList.put("end", (dst, src) -> {
            if (dst == null && src == null)
                return Result.ETC;
            return Result.ERR;
        });

        funList.put("nop", (dst, src) -> {
            if (dst == null && src == null)
                return Result.OK;
            return Result.ERR;
        });

        funList.put("rst", (dst, src) -> {
            if (dst == null && src == null) {
                tmpSeg = 0;
                tmpCnt = 0;
                return Result.OK;
            }
            return Result.ERR;
        });

        funList.put("run", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.SEG)) return Result.ERR;
            String segBuf, target = (String) dst.data;
            for (int seg = 0; seg < code.keySet().size(); seg++) {
                segBuf = (String) (code.keySet().toArray())[seg];
                if (target.equals(segBuf)) {
                    tmpSeg = seg;
                    tmpCnt = 0;
                    return Result.OK;
                }
            }
            return Result.ERR;
        });

        funList.put("call", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            if (!verifyWord((String) dst.data, WordType.SEG)) return Result.ERR;
            String segBuf, target = (String) dst.data;
            for (int seg = 0; seg < code.keySet().size(); seg++) {
                segBuf = (String) (code.keySet().toArray())[seg];
                if (target.equals(segBuf)) {
                    tmpSeg = seg;
                    tmpCnt = 0;
                    backupReg.push(progSeg);
                    backupReg.push(progCnt);
                    return Result.OK;
                }
            }
            return Result.OK;
        });

        funList.put("ld", (dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;
            if (dst.type != RegType.STR) return Result.ERR;
            String path = (String) dst.data;
            String code = Util.read(path);
            if (code == null) return Result.ERR;
            String[][] segs = Util.getSegments(code);
            if (appendCode(segs) == Result.ERR) {
                Util.print("At file: " + path + "\n");
                return Result.ERR;
            }
            return Result.OK;
        });
    }

}
