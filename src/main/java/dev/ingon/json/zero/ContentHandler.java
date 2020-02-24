package dev.ingon.json.zero;

import java.nio.CharBuffer;

public interface ContentHandler {
    void beginJSON() throws ParseException;
    void endJSON() throws ParseException;
    
    boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException;
    boolean longValue(char[] source, int begin, int end) throws ParseException;
    boolean doubleValue(char[] source, int begin, int end) throws ParseException;
    boolean trueValue(char[] source, int begin, int end) throws ParseException;
    boolean falseValue(char[] source, int begin, int end) throws ParseException;
    boolean nullValue(char[] source, int begin, int end) throws ParseException;
    
    boolean beginObject() throws ParseException;
    boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException;
    boolean endObjectEntry() throws ParseException;
    boolean endObject() throws ParseException;

    boolean beginArray() throws ParseException;
    boolean beginArrayEntry() throws ParseException;
    boolean endArrayEntry() throws ParseException;
    boolean endArray() throws ParseException;

    default String readString(char[] source, int begin, int end, int escapeCount) {
        if (escapeCount == 0) {
            return String.valueOf(source, begin, end - begin);
        } else {
            StringBuilder sb = new StringBuilder(end - begin);
            for (int i = begin; i < end; i++) {
                char ch = source[i];
                switch (ch) {
                case '\\': 
                    ch = source[++i];
                    switch (ch) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(ch);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        int codePoint = Integer.parseInt(CharBuffer.wrap(source, i + 1, 4), 0, 4, 16);
                        sb.append(Character.toChars(codePoint));
                        i += 4;
                    }
                    break;
                case '"':
                    throw new RuntimeException("wat");
                default:
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
    }
    
    default long readLong(char[] source, int begin, int end) {
        return Long.parseLong(new String(source, begin, end - begin));
    }
    
    default double readDouble(char[] source, int begin, int end) {
        return Double.parseDouble(new String(source, begin, end - begin));
    }
}
