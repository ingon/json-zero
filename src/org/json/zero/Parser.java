package org.json.zero;

public class Parser {
    public static void parse(char[] source, ContentHandler handler) throws ParseException {
        handler.beginJSON();
        
        int end = element(source, handler, 0);
        if (end == -1) {
            handler.endJSON();
            return;
        }
        
        while (end < source.length && source[end] == '\0') {
            end++;
        }
        
        if (end < source.length) {
            throw new ParseException(end, "unexpected content");
        }
        
        handler.endJSON();
    }
    
    private static int element(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = whitespaceNoEnd(source, begin);
        
        end = value(source, handler, end);
        if (end == -1) {
            return -1;
        }

        return whitespace(source, end);
    }
    
    private static int value(char[] source, ContentHandler handler, int begin) throws ParseException {
        char ch = source[begin];
        
        switch (ch) {
        case '{':
            return objectValue(source, handler, begin);
        case '[':
            return arrayValue(source, handler, begin);
        case '"':
            return stringValue(source, handler, begin, true);
        case 'n':
            return nullValue(source, handler, begin);
        case 't':
            return trueValue(source, handler, begin);
        case 'f':
            return falseValue(source, handler, begin);
        case '-':
            return numberNegativeValue(source, handler, begin);
        case '0':
            return numberZeroValue(source, handler, begin, begin);
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return numberValue(source, handler, begin, begin);
        default:
            throw new ParseException(begin, "unexpected character: " + ch);
        }
    }
    
    private static int objectValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (! handler.beginObject()) {
            return -1;
        }
        
        int end = whitespaceNoEnd(source, begin + 1);
        
        if (source[end] == '}') {
            if (! handler.endObject()) {
                return -1;
            }
            return end + 1;
        }
        
        do {
            if (source[end] != '"') {
                throw new ParseException(end, "expected '\"', but got: " + source[end]);
            }
            end = stringValue(source, handler, end, false);
            if (end == -1) {
                return -1;
            }
            
            end = whitespaceNoEnd(source, end);
            if (source[end] != ':') {
                throw new ParseException(end, "expected ':', but got: " + source[end]);
            }
            end++;
            
            end = element(source, handler, end);
            if (end == -1) {
                return -1;
            }
            
            if (! handler.endObjectEntry()) {
                return -1;
            }
            
            if (end >= source.length) {
                throw new ParseException(end, "expected '}' or ',', but got EOF");
            }
            
            char ch = source[end];
            if (ch == ',') {
                end = whitespaceNoEnd(source, end + 1);
                continue;
            } else if (ch == '}') {
                if (! handler.endObject()) {
                    return -1;
                }
                return end + 1;
            } else {
                throw new ParseException(end, "expected '}' or ',', but got: " + ch);
            }
        } while(true);
    }
    
    private static int arrayValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (! handler.beginArray()) {
            return -1;
        }
        
        int end = whitespaceNoEnd(source, begin + 1);
        
        if (source[end] == ']') {
            if (! handler.endArray()) {
                return -1;
            }
            return end + 1;
        }
        
        do {
            if (! handler.beginArrayEntry()) {
                return -1;
            }
            
            end = element(source, handler, end);
            if (end == -1) {
                return -1;
            }
            if (! handler.endArrayEntry()) {
                return -1;
            }
            
            if (end >= source.length) {
                throw new ParseException(end, "expected array close, but reached EOF");
            }
            
            char ch = source[end];
            if (ch == ',') {
                end ++;
                continue;
            } else if (ch == ']') {
                if (! handler.endArray()) {
                    return -1;
                }
                return end + 1;
            } else {
                throw new ParseException(end, "expected ']' or ',', but got: " + ch);
            }
        } while (true);
    }
    
    private static int stringValue(char[] source, ContentHandler handler, int begin, boolean value) throws ParseException {
        int escapeCount = 0;
        
        int contentBegin = begin + 1;
        int contentEnd = contentBegin;
        
        for (; contentEnd < source.length; contentEnd++) {
            char ch = source[contentEnd];
            if (ch == '"') {
                if (value) {
                    if (! handler.stringValue(source, contentBegin, contentEnd, escapeCount)) {
                        return -1;
                    }
                } else {
                    if (! handler.beginObjectEntry(source, contentBegin, contentEnd, escapeCount)) {
                        return -1;
                    }
                }
                return contentEnd + 1;
            } else if (ch == '\\') {
                escapeCount++;
                contentEnd++;
                if (contentEnd >= source.length) {
                    throw new ParseException(contentEnd, "expected escaped character, but got EOF");
                }
                
                ch = source[contentEnd];
                switch (ch) {
                case '"':
                case '\\':
                case '/':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    continue;
                case 'u':
                    if (contentEnd + 4 >= source.length) {
                        throw new ParseException(contentEnd, "expect unicode escape, but got EOF");
                    }
                    
                    if (! isHex(source[++contentEnd])) {
                        throw new ParseException(contentEnd, "unexpected unicode point: " + source[contentEnd]);
                    }
                    if (! isHex(source[++contentEnd])) {
                        throw new ParseException(contentEnd, "unexpected unicode point: " + source[contentEnd]);
                    }
                    if (! isHex(source[++contentEnd])) {
                        throw new ParseException(contentEnd, "unexpected unicode point: " + source[contentEnd]);
                    }
                    if (! isHex(source[++contentEnd])) {
                        throw new ParseException(contentEnd, "unexpected unicode point: " + source[contentEnd]);
                    }
                    continue;
                default:
                    throw new ParseException(contentEnd, "expected an escape sequence");
                }
            } else if (ch < ' ') {
                throw new ParseException(contentEnd, "invalid character in string");
            }
        }
        
        throw new ParseException(contentEnd, "expected '\"', but got EOF");
    }
    
    private static int nullValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (begin + 3 >= source.length) {
            throw new ParseException(begin, "expected null, but found EOF");
        }
        
        if (source[begin + 1] != 'u') {
            throw new ParseException(begin + 1, "expected 'u'");
        }
        if (source[begin + 2] != 'l') {
            throw new ParseException(begin + 2, "expected 'l'");
        }
        if (source[begin + 3] != 'l') {
            throw new ParseException(begin + 3, "expected 'l'");
        }
        
        if (! handler.nullValue(source, begin, begin + 4)) {
            return -1;
        }
        
        return begin + 4;
    }

    private static int trueValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (begin + 3 >= source.length) {
            throw new ParseException(begin, "expected true, but found EOF");
        }
        
        if (source[begin + 1] != 'r') {
            throw new ParseException(begin + 1, "expected 'r'");
        }
        if (source[begin + 2] != 'u') {
            throw new ParseException(begin + 2, "expected 'u'");
        }
        if (source[begin + 3] != 'e') {
            throw new ParseException(begin + 3, "expected 'e'");
        }
        
        if (! handler.trueValue(source, begin, begin + 4)) {
            return -1;
        }
        
        return begin + 4;
    }

    private static int falseValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (begin + 4 >= source.length) {
            throw new ParseException(begin, "expected null, but found EOF");
        }
        
        if (source[begin + 1] != 'a') {
            throw new ParseException(begin + 1, "expected 'a'");
        }
        if (source[begin + 2] != 'l') {
            throw new ParseException(begin + 2, "expected 'l'");
        }
        if (source[begin + 3] != 's') {
            throw new ParseException(begin + 3, "expected 's'");
        }
        if (source[begin + 4] != 'e') {
            throw new ParseException(begin + 4, "expected 'e'");
        }
        
        if (! handler.falseValue(source, begin, begin + 5)) {
            return -1;
        }
        
        return begin + 5;
    }
    
    private static int numberNegativeValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = begin + 1;
        if (end >= source.length) {
            throw new ParseException(end, "expected number, but got EOF");
        }
        char ch = source[end];
        
        if (ch == '0') {
            return numberZeroValue(source, handler, begin, end);
        } else if (ch >= '1' && ch <= '9') {
            return numberValue(source, handler, begin, end);
        } else {
            throw new ParseException(end, "expected [0-9] but got: " + ch);
        }
    }
    
    private static int numberZeroValue(char[] source, ContentHandler handler, int begin, int position) throws ParseException {
        int end = position + 1;
        if (end >= source.length) {
            if (! handler.longValue(source, begin, end)) {
                return -1;
            }
            return end;
        }
        char ch = source[end];
        
        if (ch == '.') {
            return numberFraction(source, handler, begin, end);
        } else if (ch == 'e' || ch == 'E') {
            return numberExponent(source, handler, begin, end);
        } else if (ch >= '0' && ch <= '9') {
            throw new ParseException(end, "did not expect a digit after 0, but got: " + ch);
        }
        
        if (! handler.longValue(source, begin, end)) {
            return -1;
        }
        return end;
    }
    
    private static int numberValue(char[] source, ContentHandler handler, int begin, int position) throws ParseException {
        int end = position + 1;
        for (; end < source.length; end++) {
            char ch = source[end];
            if (! (ch >= '0' && ch <= '9')) {
                break;
            }
        }

        if (end >= source.length) {
            if (! handler.longValue(source, begin, end)) {
                return -1;
            }
            return end;
        }
        
        char ch = source[end];
        if (ch == '.') {
            return numberFraction(source, handler, begin, end);
        } else if (ch == 'e' || ch == 'E') {
            return numberExponent(source, handler, begin, end);
        }
        
        if (! handler.longValue(source, begin, end)) {
            return -1;
        }
        return end;
    }
    
    private static int numberFraction(char[] source, ContentHandler handler, int begin, int position) throws ParseException {
        int end = position + 1;
        
        boolean hadDigit = false;
        for (; end < source.length; end++) {
            char ch = source[end];
            if (ch >= '0' && ch <= '9') {
                hadDigit = true;
            } else {
                break;
            }
        }
        
        if (!hadDigit) {
            if (end >= source.length) {
                throw new ParseException(end, "expected [0-9], but got EOF");
            }
            throw new ParseException(end, "expected [0-9], but got: " + source[end]);
        }
        
        if (end >= source.length) {
            if (! handler.doubleValue(source, begin, end)) {
                return -1;
            }
            return end;
        }
        
        char ch = source[end];
        if (ch == 'e' || ch == 'E') {
            return numberExponent(source, handler, begin, end);
        }
        
        if (! handler.doubleValue(source, begin, end)) {
            return -1;
        }
        return end;
    }
    
    private static int numberExponent(char[] source, ContentHandler handler, int begin, int position) throws ParseException {
        int end = position + 1;
        if (end >= source.length) {
            throw new ParseException(end, "expected exponent, but got EOF");
        }
        
        if (source[end] == '+' || source[end] == '-') {
            end++;
        }
        
        boolean hadDigit = false;
        for (; end < source.length; end++) {
            char ch = source[end];
            if (ch >= '0' && ch <= '9') {
                hadDigit = true;
            } else {
                break;
            }
        }

        if (!hadDigit) {
            if (end >= source.length) {
                throw new ParseException(end, "expected [0-9], but got EOF");
            }
            throw new ParseException(end, "expected [0-9], but got: " + source[end]);
        }
        
        if (! handler.doubleValue(source, begin, end)) {
            return -1;
        }
        return end;
    }
    
    private static int whitespace(char[] source, int begin) {
        int position = begin;
        for (; position < source.length; position++) {
            char ch = source[position];
            if (! (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t')) {
                break;
            }
        }
        return position;
    }
    
    private static int whitespaceNoEnd(char[] source, int begin) throws ParseException {
        for (int position = begin; position < source.length; position++) {
            char ch = source[position];
            if (! (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t')) {
                return position;
            }
        }
        throw new ParseException(begin, "unexpected EOF");
    }

    private static boolean isHex(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
}
