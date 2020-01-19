package org.json.zero.hl;

import org.json.zero.ParseException;

public class JsonStringHandler extends JsonBaseHandler<String> {
    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        complete(readString(source, begin, end, escapeCount));
        return true;
    }
    
    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        complete(null);
        return true;
    }
}