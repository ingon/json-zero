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
        return readChars(source, begin, end, escapeCount).toString();
    }

    default CharBuffer readChars(char[] source, int begin, int end, int escapeCount) {
        if (escapeCount == 0) {
            return CharBuffer.wrap(source, begin, end - begin);
        } 
        
        CharBuffer cb = CharBuffer.allocate(end - begin);
        for (int i = begin; i < end; i++) {
            char ch = source[i];
            switch (ch) {
            case '\\': 
                ch = source[++i];
                switch (ch) {
                case '"':
                case '\\':
                case '/':
                    cb.put(ch);
                    break;
                case 'b':
                    cb.put('\b');
                    break;
                case 'f':
                    cb.put('\f');
                    break;
                case 'n':
                    cb.put('\n');
                    break;
                case 'r':
                    cb.put('\r');
                    break;
                case 't':
                    cb.put('\t');
                    break;
                case 'u':
                    int codePoint = Integer.parseInt(CharBuffer.wrap(source, i + 1, 4), 0, 4, 16);
                    cb.put(Character.toChars(codePoint));
                    i += 4;
                }
                break;
            case '"':
                throw new RuntimeException("wat");
            default:
                cb.put(ch);
            }
        }
        
        return cb.flip();
    }

    default long readLong(char[] source, int begin, int end) {
        return Long.parseLong(new String(source, begin, end - begin));
    }
    
    default double readDouble(char[] source, int begin, int end) {
        return Double.parseDouble(new String(source, begin, end - begin));
    }
}
