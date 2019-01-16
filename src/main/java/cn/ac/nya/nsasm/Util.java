package cn.ac.nya.nsasm;

import cn.ac.nya.nsasm.NSASM.*;

import java.io.*;
import java.util.*;

/**
 * Created by drzzm on 2017.4.21.
 */
public class Util {

    public static void print(Object value) {
        System.out.print(value);
    }

    public static String scan() {
        return new Scanner(System.in).nextLine();
    }

    private static String cleanSymbol(String var, String symbol, String trash) {
        String tmp = var;
        while (tmp.contains(symbol + trash))
            tmp = tmp.replace(symbol + trash, symbol);
        while (tmp.contains(trash + symbol))
            tmp = tmp.replace(trash + symbol, symbol);
        return tmp;
    }

    private static String cleanSymbol(String var, String symbol, String trashA, String trashB) {
        String tmp = var;
        while (tmp.contains(symbol + trashA) || tmp.contains(symbol + trashB))
            tmp = tmp.replace(symbol + trashA, symbol).replace(symbol + trashB, symbol);
        while (tmp.contains(trashA + symbol) || tmp.contains(trashB + symbol))
            tmp = tmp.replace(trashA + symbol, symbol).replace(trashB + symbol, symbol);
        return tmp;
    }

    private static String cleanSymbolLeft(String var, String symbol, String trashA, String trashB) {
        String tmp = var;
        while (tmp.contains(trashA + symbol) || tmp.contains(trashB + symbol))
            tmp = tmp.replace(trashA + symbol, symbol).replace(trashB + symbol, symbol);
        return tmp;
    }

    private static String cleanSymbolRight(String var, String symbol, String trashA, String trashB) {
        String tmp = var;
        while (tmp.contains(symbol + trashA) || tmp.contains(symbol + trashB))
            tmp = tmp.replace(symbol + trashA, symbol).replace(symbol + trashB, symbol);
        return tmp;
    }

    public static String formatLine(String var) {
        if (var.isEmpty()) return "";
        while (var.contains("\r")) {
            var = var.replace("\r", "");
            if (var.isEmpty()) return "";
        }
        while (var.charAt(0) == '\t' || var.charAt(0) == ' ') {
            var = var.substring(1);
            if (var.isEmpty()) return "";
        }
        while (var.charAt(var.length() - 1) == '\t' || var.charAt(var.length() - 1) == ' ') {
            var = var.substring(0, var.length() - 1);
            if (var.isEmpty()) return "";
        }

        String left, right;
        if (var.contains("\'")) {
            left = var.split("\'")[0];
            right = var.substring(left.length());
        } else if (var.contains("\"")) {
            left = var.split("\"")[0];
            right = var.substring(left.length());
            if (right.substring(1).split("\"").length > 1) {
                if (right.substring(1).split("\"")[1].contains("*")) {
                    right = cleanSymbol(right, "*", "\t", " ");
                }
            }
        } else {
            left = var;
            right = "";
        }
        while (left.contains("\t"))
            left = left.replace("\t", " ");
        while (left.contains("  "))
            left = left.replace("  ", " ");
        left = cleanSymbol(left, ",", " ");
        left = cleanSymbol(left, "=", " ");
        left = cleanSymbol(left, "{", "\t", " ");
        left = cleanSymbol(left, "}", "\t", " ");
        left = cleanSymbol(left, "(", "\t", " ");
        left = cleanSymbol(left, ")", "\t", " ");

        left = cleanSymbol(left, "]", "\t", " ");

        return left + right;
    }

    public static String formatCode(String var) {
        String varBuf = ""; Scanner scanner = new Scanner(var);
        while (scanner.hasNextLine()) {
            varBuf = varBuf.concat(formatLine(scanner.nextLine()) + "\n");
        }
        while (varBuf.contains("\n\n")) {
            varBuf = varBuf.replace("\n\n", "\n");
        }
        scanner.close();

        varBuf = cleanSymbolRight(varBuf, "<", "\t", " ");
        varBuf = cleanSymbolLeft(varBuf, ">", "\t", " ");
        varBuf = cleanSymbolRight(varBuf, "[", "\t", " ");
        varBuf = cleanSymbolLeft(varBuf, "]", "\t", " ");

        return varBuf;
    }

    public static String repairBrackets(String var, String left, String right) {
        while (var.contains('\n' + left))
            var = var.replace('\n' + left, left);
        var = var.replace(left, left + '\n');
        var = var.replace(right, '\n' + right);
        while (var.contains("\n\n"))
            var = var.replace("\n\n", "\n");
        while (var.contains(left + " "))
            var = var.replace(left + " ", left);
        while (var.contains(" \n" + right))
            var = var.replace(" \n" + right, "\n" + right);
        return var;
    }

    public static String encodeLambda(String var) {
        return var.replace("\n", "\f");
    }

    public static String decodeLambda(String var) {
        return var.replace("\f", "\n");
    }

    public static String formatString(String var) {
        return var.replace("\\\"", "\"").replace("\\\'", "\'")
                .replace("\\\\", "\\").replace("\\n", "\n")
                .replace("\\t", "\t");
    }

    public static String formatLambda(String var) {
        final int IDLE = 0, RUN = 1, DONE = 2;
        int state = IDLE, count = 0, begin = 0, end = 0;

        for (int i = 0; i < var.length(); i++) {
            switch (state) {
                case IDLE:
                    count = begin = end = 0;
                    if (var.charAt(i) == '(') {
                        begin = i;
                        count += 1;
                        state = RUN;
                    }
                    break;
                case RUN:
                    if (var.charAt(i) == '(')
                        count += 1;
                    else if (var.charAt(i) == ')')
                        count -= 1;
                    if (count == 0) {
                        end = i;
                        state = DONE;
                    }
                    break;
                case DONE:
                    String a, b, c;
                    a = var.substring(0, begin);
                    b = var.substring(begin, end + 1);
                    c = var.substring(end + 1);
                    b = encodeLambda(b);
                    var = a + b + c;
                    state = IDLE;
                    break;
                default:
                    return var;
            }
        }

        return var;
    }

    public static String preProcessCode(String var) {
        String varBuf = var;

        List<DefBlock> blocks = getDefBlocks(varBuf);
        if (blocks != null)
            varBuf = doPreProcess(blocks, varBuf);

        if (blocks == null || varBuf == null) {
            varBuf = var;

            varBuf = formatCode(varBuf);
            varBuf = repairBrackets(varBuf, "{", "}");
            varBuf = repairBrackets(varBuf, "(", ")");
            varBuf = formatCode(varBuf);

            varBuf = formatLambda(varBuf);
        }

        return varBuf;
    }

    public static String[][] getSegments(String var) {
        LinkedHashMap<String, String> segBuf = new LinkedHashMap<>();
        LinkedList<String> pub = new LinkedList<>();

        String varBuf = preProcessCode(var);

        // Here we got formated code

        Scanner scanner = new Scanner(varBuf);

        String head = "", body = "", tmp;
        final int IDLE = 0, RUN = 1;
        int state = IDLE, count = 0;
        while (scanner.hasNextLine()) {
            switch (state) {
                case IDLE:
                    head = scanner.nextLine();
                    count = 0; body = "";
                    if (head.contains("{")) {
                        head = head.replace("{", "");
                        count += 1;
                        state = RUN;
                    } else pub.add(head);
                    break;
                case RUN:
                    if (scanner.hasNextLine()) {
                        tmp = scanner.nextLine();
                        if (tmp.contains("{"))
                            count += 1;
                        else if (tmp.contains("}"))
                            count -= 1;
                        if (tmp.contains("(") && tmp.contains(")")) {
                            if (tmp.contains("{") && tmp.contains("}"))
                                count -= 1;
                        }
                        if (count == 0) {
                            segBuf.put(head, body);
                            state = IDLE;
                        }
                        body = body.concat(tmp + "\n");
                    }
                    break;
                default:
                    break;
            }
        }

        String[][] out = new String[segBuf.size() + 1][2];

        out[0][0] = "_pub_" + Integer.toHexString(Integer.signum(var.hashCode()) * var.hashCode());
        out[0][1] = "";
        for (String i : pub) {
            out[0][1] = out[0][1].concat(i + "\n");
        }

        for (int i = 0; i < segBuf.keySet().size(); i++) {
            out[i + 1][0] = (String) segBuf.keySet().toArray()[i];
            out[i + 1][1] = segBuf.get(out[i + 1][0]);
        }

        return out;
    }

    public static String getSegment(String var, String head) {
        String[][] segments = getSegments(var);
        String result = "";
        for (String[] i : segments) {
            if (i[0].equals(head)) {
                if (result.isEmpty())
                    result = i[1];
                else
                    return null;
            }
        }
        return result;
    }

    public static List<String> parseArgs(String str, char split) {
        List<String> args = new ArrayList<>();

        final int IDLE = 0, RUN = 1;
        int state = IDLE;
        StringBuilder builder = new StringBuilder();
        char old, now = '\0';
        for (int i = 0; i < str.length(); i++) {
            old = now;
            now = str.charAt(i);
            switch(state) {
                case IDLE:
                    if (now == split) {
                        args.add(builder.toString());
                        builder = new StringBuilder();
                        continue;
                    }
                    if (now == ' ' || now == '\t')
                        continue;
                    builder.append(now);
                    if (now == '\'' || now == '\"')
                        state = RUN;
                    break;
                case RUN:
                    builder.append(now);
                    if (now == '\'' || now == '\"')
                        if (old != '\\')
                            state = IDLE;
                    break;
                default:
                    break;
            }
        }

        if (state == IDLE && builder.length() != 0)
            args.add(builder.toString());

        return args;
    }

    public static class DefBlock {
        public String name;
        public List<String> args;
        public String block;

        public DefBlock() {
            name = "";
            args = new ArrayList<>();
            block = "";
        }

        public DefBlock(DefBlock defBlock) {
            name = defBlock.name;
            args = new ArrayList<>(defBlock.args);
            block = defBlock.name;
        }

        public static DefBlock getBlock(String head, String body) {
            if (!head.contains("<") || !head.endsWith(">"))
                return null;

            DefBlock ret = new DefBlock();
            ret.name = head.substring(1).split("<")[0];
            String arg = head.split("[<>]")[1];
            ret.args = parseArgs(arg, ',');
            ret.block = body;

            return ret;
        }
    }

    public static List<DefBlock> getDefBlocks(String var) {
        List<DefBlock> blocks = new ArrayList<>();
        String varBuf = var;

        varBuf = formatCode(varBuf);
        varBuf = repairBrackets(varBuf, "{", "}");
        varBuf = repairBrackets(varBuf, "(", ")");
        varBuf = formatCode(varBuf);

        varBuf = formatLambda(varBuf);

        Scanner scanner = new Scanner(varBuf);

        String head = "", body = "", tmp; DefBlock blk;
        final int IDLE = 0, RUN = 1;
        int state = IDLE, count = 0;
        while (scanner.hasNextLine()) {
            switch (state) {
                case IDLE:
                    head = scanner.nextLine();
                    count = 0; body = "";
                    if (head.contains("{")) {
                        head = head.replace("{", "");
                        count += 1;
                        state = RUN;
                    }
                    break;
                case RUN:
                    if (scanner.hasNextLine()) {
                        tmp = scanner.nextLine();
                        if (tmp.contains("{"))
                            count += 1;
                        else if (tmp.contains("}"))
                            count -= 1;
                        if (tmp.contains("(") && tmp.contains(")")) {
                            if (tmp.contains("{") && tmp.contains("}"))
                                count -= 1;
                        }
                        if (count == 0) {
                            if (head.startsWith(".") && !head.startsWith(".<")) {
                                blk = DefBlock.getBlock(head, body);
                                if (blk == null) {
                                    print("Error at: \"" + head + "\"\n\n");
                                    return null;
                                }
                                blocks.add(blk);
                            }
                            state = IDLE;
                        }
                        body = body + (tmp + "\n");
                    }
                    break;
                default:
                    break;
            }
        }

        return blocks;
    }

    public static class DefCall {
        public String name;
        public List<String> args;

        public DefCall() {
            name = "";
            args = new ArrayList<>();
        }

        public DefCall(DefCall defCall) {
            name = defCall.name;
            args = new ArrayList<>(defCall.args);
        }

        public static DefCall getCall(String str) {
            DefCall ret = new DefCall();
            ret.name = str.split("<")[0];
            String arg = str.split("[<>]")[1];
            ret.args = parseArgs(arg, ',');

            return ret;
        }
    }

    public static String doPreProcess(List<DefBlock> blocks, String var) {
        String varBuf = var;

        varBuf = formatCode(varBuf);
        varBuf = repairBrackets(varBuf, "{", "}");
        varBuf = repairBrackets(varBuf, "(", ")");
        varBuf = formatCode(varBuf);

        varBuf = formatLambda(varBuf);

        Scanner scanner = new Scanner(varBuf);
        StringBuilder builder = new StringBuilder("");
        String line, block; DefCall call; boolean defRes;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains("<") && !line.startsWith("<") && line.endsWith(">") && !line.split("<")[0].contains(" ")) {
                call = DefCall.getCall(line); defRes = false;
                for (DefBlock blk : blocks) {
                    if (blk.name.equals(call.name))
                        if (blk.args.size() == call.args.size()) {
                            block = blk.block;
                            for (int i = 0; i < call.args.size(); i++) {
                                block = block.replace(blk.args.get(i) + ",", call.args.get(i) + ",");
                                block = block.replace(blk.args.get(i) + "\n", call.args.get(i) + "\n");
                            }
                            builder.append(block);
                            builder.append("\n");
                            defRes = true;
                            break;
                        }
                }
                if (!defRes) {
                    print("Error at: \"" + line + "\"\n\n");
                    return null;
                }
            } else {
                builder.append(line);
                builder.append("\n");
            }
        }

        varBuf = builder.toString();
        varBuf = formatCode(varBuf);

        return varBuf;
    }

    public static String read(String path) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (Exception e) {
            print("File open failed.\n");
            print("At file: " + path + "\n\n");
            return null;
        }

        String str = "";
        try {
            while (reader.ready())
                str = str.concat(reader.readLine() + "\n");
            reader.close();
        } catch (Exception e) {
            print("File read error.\n");
            print("At file: " + path + "\n\n");
            return null;
        }
        return str;
    }

    public static void run(String path) {
        if (path.endsWith(".nsb")) {
            binary(path);
            return;
        }

        String str = read(path);
        if (str == null) return;

        int heap = 64, stack = 32, regs = 16;

        String conf = getSegment(str, ".<conf>");
        if (conf == null) {
            print("Conf load error.\n");
            print("At file: " + path + "\n\n");
            return;
        }
        if (!conf.isEmpty()) {
            Scanner confReader = new Scanner(conf);
            try {
                String buf;
                while (confReader.hasNextLine()) {
                    buf = confReader.nextLine();
                    switch (buf.split(" ")[0]) {
                        case "heap":
                            heap = Integer.valueOf(buf.split(" ")[1]);
                            break;
                        case "stack":
                            stack = Integer.valueOf(buf.split(" ")[1]);
                            break;
                        case "reg":
                            regs = Integer.valueOf(buf.split(" ")[1]);
                            break;
                    }
                }
            } catch (Exception e) {
                print("Conf load error.\n");
                print("At file: " + path + "\n\n");
                return;
            }
        }

        String[][] code = getSegments(str);
        NSASM nsasm = new NSASM(heap, stack, regs, code);
        nsasm.run();
        print("\nNSASM running finished.\n\n");
    }

    public static void execute(String str) {
        String path = "local";
        if (str == null) return;

        int heap = 64, stack = 32, regs = 16;

        String conf = getSegment(str, ".<conf>");
        if (conf == null) {
            print("Conf load error.\n");
            print("At file: " + path + "\n\n");
            return;
        }
        if (!conf.isEmpty()) {
            Scanner confReader = new Scanner(conf);
            try {
                String buf;
                while (confReader.hasNextLine()) {
                    buf = confReader.nextLine();
                    switch (buf.split(" ")[0]) {
                        case "heap":
                            heap = Integer.valueOf(buf.split(" ")[1]);
                            break;
                        case "stack":
                            stack = Integer.valueOf(buf.split(" ")[1]);
                            break;
                        case "reg":
                            regs = Integer.valueOf(buf.split(" ")[1]);
                            break;
                    }
                }
            } catch (Exception e) {
                print("Conf load error.\n");
                print("At file: " + path + "\n\n");
                return;
            }
        }

        String[][] code = getSegments(str);
        NSASM nsasm = new NSASM(heap, stack, regs, code);
        nsasm.run();
        print("\nNSASM running finished.\n\n");
    }

    public static void interactive() {
        Util.print("Now in console mode.\n\n");
        String buf;
        int lines = 1; Result result;

        String[][] code = getSegments("nop\n"); //ld func allowed
        NSASM nsasm = new NSASM(64, 32, 16, code);

        while (true) {
            Util.print(lines + " >>> ");
            buf = scan();
            if (buf.length() == 0) {
                lines += 1;
                continue;
            }
            buf = formatLine(buf);

            if (buf.contains("#")) {
                Util.print("<" + buf + ">\n");
                continue;
            }

            result = nsasm.execute(buf);
            if (result == Result.ERR) {
                Util.print("\nNSASM running error!\n");
                Util.print("At line " + lines + ": " + buf + "\n\n");
            } else if (result == Result.ETC) {
                break;
            }
            if (buf.startsWith("run") || buf.startsWith("call")) {
                nsasm.run();
            }

            lines += 1;
        }
    }

    public static void gui() {

    }

    private static void putToList(List<Byte> list, int value) {
        list.add((byte) (value & 0xFF));
        list.add((byte) ((value >> 8) & 0xFF));
    }

    private static void putToList(List<Byte> list, String value) {
        for (int i = 0; i < value.length(); i++)
            list.add((byte) value.charAt(i));
    }

    public static String compile(String inPath, String outPath) {
        String str = read(inPath);
        if (str == null) return null;

        if (outPath == null) return preProcessCode(str);

        int heap = 64, stack = 32, regs = 16;

        String conf = getSegment(str, ".<conf>");
        if (conf == null) {
            print("Conf load error.\n");
            print("At file: " + inPath + "\n\n");
            return null;
        }
        if (!conf.isEmpty()) {
            Scanner confReader = new Scanner(conf);
            try {
                String buf;
                while (confReader.hasNextLine()) {
                    buf = confReader.nextLine();
                    switch (buf.split(" ")[0]) {
                        case "heap":
                            heap = Integer.valueOf(buf.split(" ")[1]);
                            break;
                        case "stack":
                            stack = Integer.valueOf(buf.split(" ")[1]);
                            break;
                        case "reg":
                            regs = Integer.valueOf(buf.split(" ")[1]);
                            break;
                    }
                }
            } catch (Exception e) {
                print("Conf load error.\n");
                print("At file: " + inPath + "\n\n");
                return null;
            }
        }

        String[][] code = getSegments(str);
        int segCnt = code.length;

        List<Byte> bytes = new ArrayList<Byte>();
        putToList(bytes, "NS");
        putToList(bytes, 0xFFFF);
        putToList(bytes, heap);
        putToList(bytes, stack);
        putToList(bytes, regs);
        putToList(bytes, segCnt);

        for (String[] seg : code) {
            putToList(bytes, 0xA5A5);
            putToList(bytes, seg[0]);
            putToList(bytes, 0xAAAA);
            putToList(bytes, seg[1]);
        }

        putToList(bytes,0xFFFF);
        int sum = 0;
        for (int i = 0; i < bytes.size(); i++)
            sum = (sum + (bytes.get(i) & 0xFF)) & 0xFFFF;
        putToList(bytes, sum);

        try {
            FileOutputStream stream = new FileOutputStream(outPath);
            byte[] byteArray = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++)
                byteArray[i] = bytes.get(i);
            stream.write(byteArray);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            print("File write failed.\n");
            print("At file: " + outPath + "\n\n");
            return null;
        }

        return preProcessCode(str);
    }

    private static int getUint16(byte[] data, int offset) {
        int res = 0;
        if (data.length >= offset + 1)
            res = ((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8));
        return res;
    }

    private static String getStr2(byte[] data, int offset) {
        String res = "";
        if (data.length >= offset + 1) {
            res += (char) data[offset];
            res += (char) data[offset + 1];
        }
        return res;
    }

    public static void binary(String path) {
        byte[] data = null;
        try {
            FileInputStream stream = new FileInputStream(path);
            data = new byte[stream.available()];
            int siz = stream.read(data);
            if (siz == -1) return;
        } catch (Exception e) {
            print("File read error.\n");
            print("At file: " + path + "\n\n");
            return;
        }

        if (data.length < 16) return;

        if (!getStr2(data, 0).equals("NS")) return;
        if (getUint16(data, 2) != 0xFFFF) return;

        int sum = 0;
        for (int i = 0; i < data.length - 2; i++)
            sum = (sum + (data[i] & 0xFF)) & 0xFFFF;
        if (sum != getUint16(data, data.length - 2))
            return;

        int heap, stack, regs, segCnt;
        heap = getUint16(data, 4);
        stack = getUint16(data, 6);
        regs = getUint16(data, 8);
        segCnt = getUint16(data, 10);

        int offset = 12, segPos = -1;
        String[][] code = new String[segCnt][];
        for (int i = 0; i < segCnt; i++)
            code[i] = new String[2];

        final int SEG_NAME = 0, SEG_CODE = 1;
        int state = SEG_NAME, offmax = data.length - 1;
        while (offset <= offmax - 4) {
            if (getUint16(data, offset) == 0xA5A5) {
                if (segPos >= segCnt - 1)
                    return;
                segPos += 1; offset += 2;
                state = SEG_NAME;
                code[segPos][0] = "";
            } else if (getUint16(data, offset) == 0xAAAA) {
                offset += 2;
                state = SEG_CODE;
                code[segPos][1] = "";
            } else {
                if (state == SEG_NAME)
                    code[segPos][0] += (char)data[offset];
                else if (state == SEG_CODE)
                    code[segPos][1] += (char)data[offset];
                offset += 1;
            }
        }

        if (getUint16(data, offset) != 0xFFFF) return;

        NSASM nsasm = new NSASM(heap, stack, regs, code);
        nsasm.run();
        print("\nNSASM running finished.\n\n");
    }

}
