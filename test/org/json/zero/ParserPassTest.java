package org.json.zero;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ParserPassTest {
    private static int allCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;
    private static int excCount = 0;
    
    public static void main(String[] args) throws Exception {
        // fixed literals
        check("literal: null", "null");
        check("literal: true", "true");
        check("literal: false", "false");
        
        // easy numbers
        check("number: 0", "0");
        check("number: 1", "1");
        check("number: 123", "123");
        
        // harder numbers
        check("number: 0.4", "0.4");
        check("number: 5.31", "5.31");
        check("number: 1e2", "1e2");
        check("number: 2.98e-2", "2.98e-2");
        
        // easy strings
        check("string: empty", "\"\"");
        check("string: space", "\" \"");
        check("string: char", "\"a\"");
        check("string: multi char", "\"abc\"");
        
        // harder strings
        check("string: simple escape", "\"\\t\\b\"");
        check("string: unicode escape", "\"\\u73e1\\u52ab\"");
        
        // arrays
        check("array: empty", "[]");
        check("array: space", "[ ]");
        check("array: values", "[16, 9.5e2, true, false, null, \"abc\\u9321\"]");
        
        // objects
        check("object: empty", "{}");
        check("object: space", "{ }");
        check("object: values", "{\"a1\": 3.2e3, \"a2\": true, \"a3\": \"c\\u2234\"}");
        
        // nesting
        check("nesting: array", "[{\"1\": 2}, [3]]");
        check("nesting: object", "{\"1\": {\"2\": 3}, \"4\": [5]}");
        
        // spaces
        check("spaces: 3 around", "   {   \"abc\"   :   [   1   ,   true   ,   \"xxx\"   ]   ,   \"d\"   :   1   }   ");
        
        //
        Path tests = Paths.get("../../../Downloads/test/");
        try (var ds = Files.newDirectoryStream(tests, "pass*.json")) {
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
            passCount++;
            System.out.println("PASS");
        } catch (ParseException exc) {
            failCount++;
            System.out.println("FAIL: " + str);
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
            passCount++;
            System.out.println("PASS");
        } catch (ParseException exc) {
            failCount++;
            System.out.println("FAIL: " + str);
        } catch (Throwable th) {
            excCount++;
            System.out.println("EXC: " + str);
            th.printStackTrace();
        }
    }
}
