package dev.ingon.json.zero.hl;

import dev.ingon.json.zero.ParseException;

public class JsonBooleanHandler extends JsonBaseHandler<Boolean> {
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