package dev.ingon.json.zero;

import java.util.UUID;

import org.junit.Test;

import dev.ingon.json.zero.hl.JsonMapHandler;
import dev.ingon.json.zero.hl.JsonParser;
import dev.ingon.json.zero.hl.JsonStringHandler;

public class ParserBenchmark {
    @Test
    public void testHelloLow() throws Exception {
        Parser.parse("{\"hello\": \"world\"}".toCharArray(), new DefaultContentHandler() {
            @Override
            public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
                System.out.print(readString(source, begin, end, escapeCount));
                return true;
            }
            
            @Override
            public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
                System.out.print(" ");
                System.out.println(readString(source, begin, end, escapeCount));
                return false;
            }
        });
    }
    
    @Test
    public void testHelloHigh() throws Exception {
        var result = JsonParser.parse("{\"hello\": \"world\"}".toCharArray(), new JsonMapHandler<String>(new JsonStringHandler()));
        for (var e : result.entrySet()) {
            System.out.format("%s %s", e.getKey(), e.getValue());
        }
    }
    
    public static void main(String[] args) {
        int sz = 1;
        String[] names = new String[sz];
        ParserRun[] parsers = new ParserRun[sz];

        int idx = 0;
        names[idx] = "Parser";
        parsers[idx++] = Parser::parse;

        runBigObject(names, parsers);
        runDeepObject(names, parsers);
        runBigStringObject(names, parsers);
        runBigNumberObject(names, parsers);
        runStringArray(names, parsers);
        runNumberArray(names, parsers);
    }
    
    private static void runBigObject(String[] names, ParserRun[] parsers) {
        System.out.println("OBJECT");
        
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int i = 0; i < 1000000; i++) {
            sb.append("\"" + i + "\": " + i + ", ");
        }
        sb.append("\"\": null }");
        
        runBenchmark(sb.toString(), names, parsers);
    }
    
    private static void runDeepObject(String[] names, ParserRun[] parsers) {
        System.out.println("OBJECT DEEP");
        
        StringBuilder sb = new StringBuilder();
        sb.append("{}");
        for (int i = 0; i < 20; i++) {
            sb.insert(0, "{\"abc\": 123, \"123\": \"abc\", \"false\": true, \"arr\": [\"\\u82c3\", 99, null], \"0123456789\": ");
            sb.append("}");
        }
        String dept = sb.toString();
        
        sb.setLength(0);
        sb.append("{");
        for (int i = 0; i < 100000; i++) {
            sb.append("\"" + i + "\": [" + dept + "], ");
        }
        sb.append("\"\": null }");
        
        runBenchmark(sb.toString(), names, parsers);
    }
    
    private static void runBigStringObject(String[] names, ParserRun[] parsers) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < 200; i++) {
            sb.append("\"").append(UUID.randomUUID().toString()).append("\"");
            sb.append(":");

            sb.append("[");
            for (int j = 0; j < 200; j++) {
                sb.append("\"").append(UUID.randomUUID().toString()).append("\"").append(",");
            }
            sb.append("{}]");

            sb.append(",");
        }
        sb.append("\"").append(UUID.randomUUID().toString()).append("\"");
        sb.append(":{}");
        sb.append("}");

        runBenchmark(sb.toString(), names, parsers);
    }

    private static void runBigNumberObject(String[] names, ParserRun[] parsers) {
        System.out.println("BIG NUMBER");
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < 200; i++) {
            sb.append("\"").append(UUID.randomUUID().toString()).append("\"");
            sb.append(":");

            sb.append("[");
            for (int j = 0; j < 200; j++) {
                sb.append(j * 876521).append(",");
            }
            sb.append("{}]");

            sb.append(",");
        }
        sb.append("\"").append(UUID.randomUUID().toString()).append("\"");
        sb.append(":{}");
        sb.append("}");

        runBenchmark(sb.toString(), names, parsers);
    }

    private static void runStringArray(String[] names, ParserRun[] parsers) {
        System.out.println("STRING ARRAY");
        
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < 1000000; i++) {
            sb.append("\"" + i + "\", ");
        }
        sb.append("\"\"]");
        
        runBenchmark(sb.toString(), names, parsers);
    }

    private static void runNumberArray(String[] names, ParserRun[] parsers) {
        System.out.println("NUMBER ARRAY");
        
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < 1000000; i++) {
            sb.append(i + ", ");
        }
        sb.append("100000 ]");
        
        runBenchmark(sb.toString(), names, parsers);
    }

    private static void runBenchmark(String s, String[] names, ParserRun[] parsers) {
        char[] source = s.toCharArray();

        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < names.length; i++) {
                var parser = parsers[i];

                gc();
                runTest(names[i], () -> {
                    try {
                        parser.parse(source, new DefaultContentHandler());
                    } catch (ParseException exc) {
                        exc.printStackTrace();
                    }
                });
            }
            System.out.println("");
        }
    }

    private static interface ParserRun {
        public void parse(char[] source, ContentHandler handler) throws ParseException;
    }

    private static void gc() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ie) {
        }
        System.gc();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ie) {
        }
    }

//    static final int LOOP = 1;
    static final int LOOP = 20;

    static void runTest(String message, Runnable e) {
        long t1 = System.currentTimeMillis();
        String s = null;
        for (int i = 0; i < LOOP; i++) {
            e.run();
//          s = e.run();
        }
        long t2 = System.currentTimeMillis();
//        System.out.println(message + " :: " + ((t2 - t1) / (LOOP + 0D)) + " ms\t\t" + s);
        System.out.println(message + ", " + ((t2 - t1) / (LOOP + 0D)));
    }
}
