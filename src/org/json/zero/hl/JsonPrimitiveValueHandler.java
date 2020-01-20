package org.json.zero.hl;

import org.json.zero.ParseException;

public class JsonPrimitiveValueHandler extends JsonBaseHandler<Object> {
    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        complete(readString(source, begin, end, escapeCount));
        return true;
    }
    
    @Override
    public boolean longValue(char[] source, int begin, int end) throws ParseException {
        complete(readLong(source, begin, end));
        return true;
    }
    
    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        complete(readDouble(source, begin, end));
        return true;
    }
    
    @Override
    public boolean trueValue(char[] source, int begin, int end) throws ParseException {
        complete(Boolean.TRUE);
        return true;
    }
    
    @Override
    public boolean falseValue(char[] source, int begin, int end) throws ParseException {
        complete(Boolean.FALSE);
        return true;
    }
    
    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        complete(null);
        return true;
    }
}
