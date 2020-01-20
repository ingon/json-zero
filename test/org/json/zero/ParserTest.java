package org.json.zero;

public class ParserTest {
    public static void main(String[] args) throws Exception {
//        checkParse("");
//        checkParse(" ");
//        checkParse("\"\"");
//        checkParse("\"abc\"");
//        checkParse("\"\\t\"");
//        checkParse("\"\\t\\b\"");
//        checkParse("\"a\\u5da9b\"");
//        checkParse("null");
//        checkParse("true");
//        checkParse("false");
//        checkParse("0");
//        checkParse("1");
//        checkParse("101");
//        checkParse("0.2");
//        checkParse("1.2");
//        checkParse("10.2e+2");
//        checkParse("10e-2");
//        checkParse("0e+2");
//        printParse("{}");
//        printParse("{ }");
//        printParse("{\"a\": 2, \"b\" :\"abc\", \"c\": { \"d\": 100}}");
//        printParse("[]");
//        printParse("[ ]");
//        printParse("[ 1, 2, 3, [true, false, null], {\"a\": 1000.2e3}]");
        printParse("{\"title\":\"A note to Trash\",\"ainfo\":\"Let’s create a note that we will throw in the trash but not expunge.\",\"ps\":0}");
    }
    
    private static void printParse(String s) throws ParseException {
        Parser p = new Parser();
        p.parse(s.toCharArray(), new PrintingContentHandler());
    }
    
    private static class PrintingContentHandler implements ContentHandler {
        @Override
        public void beginJSON() throws ParseException {
            System.out.print(" ");
        }

        @Override
        public void endJSON() throws ParseException {
            System.out.println();
        }

        @Override
        public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
            System.out.format("<S:%d:%s>", escapeCount, readString(source, begin, end, escapeCount));
            return true;
        }

        @Override
        public boolean integerValue(char[] source, int begin, int end) throws ParseException {
            System.out.format("<I:%d>", readInteger(source, begin, end));
            return true;
        }
        
        @Override
        public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
            System.out.format("<D:%f>", readDouble(source, begin, end));
            return true;
        }

        @Override
        public boolean trueValue(char[] source, int begin, int end) throws ParseException {
            System.out.print("<true>");
            return true;
        }

        @Override
        public boolean falseValue(char[] source, int begin, int end) throws ParseException {
            System.out.print("<false>");
            return true;
        }

        @Override
        public boolean nullValue(char[] source, int begin, int end) throws ParseException {
            System.out.print("<null>");
            return true;
        }

        @Override
        public boolean beginObject() throws ParseException {
            System.out.print("{");
            return true;
        }

        @Override
        public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
            System.out.print(readString(source, begin, end, escapeCount) + ": ");
            return true;
        }

        @Override
        public boolean endObjectEntry() throws ParseException {
            System.out.print(", ");
            return true;
        }

        @Override
        public boolean endObject() throws ParseException {
            System.out.print("}");
            return true;
        }

        @Override
        public boolean beginArray() throws ParseException {
            System.out.print("[");
            return true;
        }
        
        @Override
        public boolean beginArrayEntry() throws ParseException {
            return true;
        }

        @Override
        public boolean endArrayEntry() throws ParseException {
            System.out.print(",");
            return true;
        }

        @Override
        public boolean endArray() throws ParseException {
            System.out.print("]");
            return true;
        }
        
    }
}
