package org.json.zero.hl;

import org.json.zero.ParseException;

public class JsonNumberHandler extends JsonBaseHandler<Number> {
    @Override
    public boolean integerValue(char[] source, int begin, int end) throws ParseException {
        complete(readInteger(source, begin, end));
        return true;
    }
    
    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        complete(readDouble(source, begin, end));
        return true;
    }
    
    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        complete(null);
        return true;
    }
}