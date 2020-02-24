package dev.ingon.json.zero;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.ingon.json.zero.DefaultContentHandler;
import dev.ingon.json.zero.ParseException;
import dev.ingon.json.zero.Parser;

public class ParserFailTest {
    private static int allCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;
    private static int excCount = 0;
    
    public static void main(String[] args) throws Exception {
        check("number/bounds: leading 0", "01");
        check("number/bounds: leading 00", "00");
        check("number/bounds: just -", "-");
        check("number/bounds: no digits after fraction", "3.");
        check("number/bounds: no digits after exponent", "3e");
        check("number/bounds: no digits after exponent+", "3e+");
        
        check("string/bounds: no close", "\"");
        check("string/bounds: no close a", "\"a");
        check("string/bounds: no close escape", "\"\\");
        check("string/bounds: no close after escape", "\"\\a");
        check("string/bounds: no close escape unicode 0", "\"\\u");
        check("string/bounds: no close escape unicode 1", "\"\\u1");
        check("string/bounds: no close escape unicode 12", "\"\\u12");
        check("string/bounds: no close escape unicode 123", "\"\\u123");
        check("string/bounds: no close escape unicode 1234", "\"\\u1234");
        
        check("null/bounds: just n", "n");
        check("null/bounds: just nu", "nu");
        check("null/bounds: just nul", "nul");

        check("true/bounds: just t", "t");
        check("true/bounds: just tr", "tr");
        check("true/bounds: just tru", "tru");

        check("false/bounds: just f", "f");
        check("false/bounds: just fa", "fa");
        check("false/bounds: just fal", "fal");
        check("false/bounds: just fals", "fals");

        check("object/bounds: open brace", "{");
        check("object/bounds: open brace and key", "{\"");
        check("object/bounds: up to value sep", "{\"\":");
        check("object/bounds: after value", "{\"\":3");
        
        check("object/unexpect: missing :value", "{\"\"}");
        check("object/unexpect: missing value", "{\"\":}");
        check("object/unexpect: number key, no :", "{3}");
        check("object/unexpect: literal", "{true}");
        check("object/unexpect: number key, no val", "{3:}");
        check("object/unexpect: number key/val", "{3:3}");

        check("array/bounds: no close bracked", "[");
        check("array/bounds: no close, value", "[3");
        check("array/bounds: no close, string", "[\"3");
        check("array/bounds: no close, no value", "[,");
        check("array/bounds: no close, no after value", "[ 1,");
        check("array/bounds: no close, after value", "[ 1, 3");
        
        
        check("array/incomplete: ", "[1,]");
        
        Path tests = Paths.get("../../../Downloads/test/");
        try (var ds = Files.newDirectoryStream(tests, "fail*.json")) {
            for (var p : ds) {
                check(p);
            }
        }
        
        System.out.format("\n ------------- \nALL: %d\nPASS: %d\nFAIL: %d\nEXC: %d\n", allCount, passCount, failCount, excCount);
    }

    private static void check(Path path) throws IOException {
        String str = Files.readString(path);
        System.out.print(path.getFileName() + ": ");
        char[] data = str.toCharArray();
        allCount++;
        try {
            Parser.parse(data, new DefaultContentHandler());
            failCount++;
            System.out.println("FAIL: " + str);
        } catch (ParseException exc) {
            passCount++;
            System.out.println("PASS");
        } catch (Throwable th) {
            excCount++;
            System.out.println("EXC: " + str);
            th.printStackTrace();
        }
    }
    
    private static void check(String name, String str) {
        System.out.print(name + ": ");
        char[] data = str.toCharArray();
        allCount++;
        try {
            Parser.parse(data, new DefaultContentHandler());
            failCount++;
            System.out.println("FAIL: " + str);
        } catch (ParseException exc) {
            passCount++;
            System.out.println("PASS");
        } catch (Throwable th) {
            excCount++;
            System.out.println("EXC: " + str);
            th.printStackTrace();
        }
    }
}
