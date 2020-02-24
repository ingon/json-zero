package dev.ingon.json.zero.hl;

import dev.ingon.json.zero.ParseException;

public class JsonNumberHandler extends JsonBaseHandler<Number> {
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
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        complete(null);
        return true;
    }
}