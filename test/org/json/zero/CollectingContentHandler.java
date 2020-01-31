package org.json.zero;

import java.util.ArrayList;
import java.util.List;

public class CollectingContentHandler implements ContentHandler {
    final List<String> elements = new ArrayList<String>();
    
    @Override
    public void beginJSON() throws ParseException {
        elements.add(">>> ");
    }
    
    @Override
    public void endJSON() throws ParseException {
        elements.add(" <<<");
    }
    
    @Override
    public boolean beginObject() throws ParseException {
        elements.add("{ ");
        return true;
    }
    
    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        elements.add(readString(source, begin, end, escapeCount) + ": ");
        return true;
    }
    
    @Override
    public boolean endObjectEntry() throws ParseException {
        elements.add(", ");
        return true;
    }
    
    @Override
    public boolean endObject() throws ParseException {
        elements.add("} ");
        return true;
    }
    
    @Override
    public boolean beginArray() throws ParseException {
        elements.add("[ ");
        return true;
    }
    
    @Override
    public boolean beginArrayEntry() throws ParseException {
        elements.add("> ");
        return true;
    }
    
    @Override
    public boolean endArrayEntry() throws ParseException {
        elements.add(", ");
        return true;
    }
    
    @Override
    public boolean endArray() throws ParseException {
        elements.add("] ");
        return true;
    }

    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        elements.add("S:" + readString(source, begin, end, escapeCount));
        return true;
    }

    @Override
    public boolean longValue(char[] source, int begin, int end) throws ParseException {
        elements.add("L:" + readLong(source, begin, end));
        return true;
    }

    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        elements.add("D:" + readDouble(source, begin, end));
        return true;
    }

    @Override
    public boolean trueValue(char[] source, int begin, int end) throws ParseException {
        elements.add("TRUE");
        return true;
    }

    @Override
    public boolean falseValue(char[] source, int begin, int end) throws ParseException {
        elements.add("FALSE");
        return true;
    }

    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        elements.add("NULL");
        return true;
    }
}
