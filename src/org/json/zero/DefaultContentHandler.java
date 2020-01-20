package org.json.zero;

public class DefaultContentHandler implements ContentHandler {
    @Override
    public void beginJSON() throws ParseException {
    }

    @Override
    public void endJSON() throws ParseException {
    }

    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        return true;
    }

    @Override
    public boolean longValue(char[] source, int begin, int end) throws ParseException {
        return true;
    }

    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        return true;
    }

    @Override
    public boolean trueValue(char[] source, int begin, int end) throws ParseException {
        return true;
    }

    @Override
    public boolean falseValue(char[] source, int begin, int end) throws ParseException {
        return true;
    }

    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        return true;
    }

    @Override
    public boolean beginObject() throws ParseException {
        return true;
    }

    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        return true;
    }

    @Override
    public boolean endObjectEntry() throws ParseException {
        return true;
    }

    @Override
    public boolean endObject() throws ParseException {
        return true;
    }

    @Override
    public boolean beginArray() throws ParseException {
        return true;
    }
    
    @Override
    public boolean beginArrayEntry() throws ParseException {
        return true;
    }

    @Override
    public boolean endArrayEntry() throws ParseException {
        return true;
    }
    
    @Override
    public boolean endArray() throws ParseException {
        return true;
    }
}
