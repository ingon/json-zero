package org.json.zero;

public interface ContentHandler {
    void beginJSON() throws ParseException;
    void endJSON() throws ParseException;
    
    boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException;
    // TODO: split into double/int value
    boolean numberValue(char[] source, int begin, int end, boolean fractional) throws ParseException;
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
            throw new UnsupportedOperationException();
        }
    }
    
    default Number readNumber(char[] source, int begin, int end, boolean fractional) {
        if (fractional) {
            return Double.valueOf(new String(source, begin, end - begin));
        } else {
            return Integer.valueOf(new String(source, begin, end - begin));
        }
    }
}
