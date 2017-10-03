package cn.ac.nya.nsasm;

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

    public static String formatCode(String var) {
        if (var.isEmpty()) return "";
        while (var.contains("\r")) {
            var = var.replace("\r", "");
            if (var.isEmpty()) return "";
        }
        while (var.charAt(0) == '\t' || var.charAt(0) == ' ') {
            var = var.substring(1);
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

        return left + right;
    }

    public static String[][] getSegments(String var) {
        LinkedHashMap<String, String> segBuf = new LinkedHashMap<>();
        String varBuf = ""; Scanner scanner = new Scanner(var);
        LinkedList<String> pub = new LinkedList<>();

        while (scanner.hasNextLine()) {
            varBuf = varBuf.concat(formatCode(scanner.nextLine()) + "\n");
        }
        while (varBuf.contains("\n\n")) {
            varBuf = varBuf.replace("\n\n", "\n");
        }
        scanner = new Scanner(varBuf);

        String head, body = "", tmp;
        while (scanner.hasNextLine()) {
            head = scanner.nextLine();
            if (!head.contains("{")) {
                pub.add(head);
                continue;
            }
            head = head.replace("{", "");

            if (scanner.hasNextLine()) {
                tmp = scanner.nextLine();
                while (!tmp.contains("}") && scanner.hasNextLine()) {
                    body = body.concat(tmp + "\n");
                    tmp = scanner.nextLine();
                }
            }

            segBuf.put(head, body);
            body = "";
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

}
