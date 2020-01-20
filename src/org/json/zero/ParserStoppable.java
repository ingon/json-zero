package org.json.zero;

// TODO: verify length checks
public class ParserStoppable {    
    public static void parse(char[] source, ContentHandler handler) throws ParseException {
        handler.beginJSON();
        
        int finalPosition = element(source, handler, 0);
        if (finalPosition == -1) {
            handler.endJSON();
            return;
        }
        
        while (finalPosition < source.length && source[finalPosition] == '\0') {
            finalPosition++;
        }
        
        if (finalPosition < source.length) {
            throw new ParseException(finalPosition, "unexpected content");
        }
        
        handler.endJSON();
    }
    
    private static int element(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = whitespace(source, begin);
        
        end = value(source, handler, end);
        if (end == -1) {
            return -1;
        }
        
        return whitespace(source, end);
    }
    
    private static int value(char[] source, ContentHandler handler, int position) throws ParseException {
        if (position >= source.length) {
            return position;
        }

        switch (source[position]) {
        case '{': // object
            return objectValue(source, handler, position);
        case '[': // array
            return arrayValue(source, handler, position);
        case '"': // string
            return stringValue(source, handler, position, true);
        case 'n': // null
            return nullValue(source, handler, position);
        case 't': // true
            return trueValue(source, handler, position);
        case 'f': // false
            return falseValue(source, handler, position);
        case '-': // negative number
            return numberNegativeValue(source, handler, position);
        case '0': // 0 or fractional/exponential number
            return numberRest(source, handler, position, position + 1);
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return numberValue(source, handler, position, position + 1);
        default:
            throw new ParseException(position, "unexpected character: " + source[position]);
        }
    }
    
    private static int objectValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (! handler.beginObject()) {
            return -1;
        }
        
        int end = whitespace(source, begin + 1);
        if (end >= source.length) {
            throw new ParseException(end, "reached end, expected property or '}'");
        }
        
        if (source[end] == '}') {
            if (! handler.endObject()) {
                return -1;
            }
            return end + 1;
        }
        
        do {
            if (source[end] != '\"') {
                throw new ParseException(end, "expected property name, got: " + source[end]);
            }
            end = stringValue(source, handler, end, false); // notification handled 
            if (end == -1) {
                return -1;
            }
            if (end >= source.length) {
                throw new ParseException(end, "reached end, expected ':'");
            }
            
            end = whitespace(source, end);
            if (end >= source.length) {
                throw new ParseException(end, "reached end, expected ':'");
            }
            
            if (source[end] != ':') {
                throw new ParseException(end, "expected ':', but got: " + source[end]);
            }
            end++;
            if (end >= source.length) {
                throw new ParseException(end, "reached end, expected property value");
            }
            
            end = element(source, handler, end);
            if (end == -1) {
                return -1;
            }
            if (!handler.endObjectEntry()) {
                return -1;
            }
            
            if (end >= source.length) {
                throw new ParseException(end, "reached end, expected ',' or '}'");
            }
            if (source[end] == ',') {
                end++;
                if (end >= source.length) {
                    throw new ParseException(end, "reached end, expected object entry");
                }
                end = whitespace(source, end);
                if (end >= source.length) {
                    throw new ParseException(end, "reached end, expected object key");
                }
                continue;
            } else if (source[end] == '}') {
                if (! handler.endObject()) {
                    return -1;
                }
                return end + 1;
            }
        } while(true);
    }
    
    private static int arrayValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        if (! handler.beginArray()) {
            return -1;
        }
        int end = whitespace(source, begin + 1);
        if (end >= source.length) {
            throw new ParseException(end, "reached end, expected value or ']'");
        }
        
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
                throw new ParseException(end, "reached end, expected ',' or ']'");
            }
            
            if (source[end] == ',') {
                end++;
                if (end >= source.length) {
                    throw new ParseException(end, "reached end, expected array element");
                }                
                continue;
            } else if (source[end] == ']') {
                if (! handler.endArray()) {
                    return -1;
                }
                return end + 1;
            }
        } while (true);
    }
    
    private static int stringValue(char[] source, ContentHandler handler, int begin, boolean value) throws ParseException {
        int end = begin + 1;
        if (end >= source.length) {
            throw new ParseException(end, "reached end, expected '\"'");
        }
        int escapeCount = 0;
        
        while (source[end] != '"') {
            if (source[end] == '\\') {
                end++;
                if (end >= source.length) {
                    throw new ParseException(end, "reached end, expected escape sequence");
                }
                
                switch (source[end]) {
                case '"':
                case '\\':
                case '/':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    break;
                case 'u':
                    if (! isHex(source[++end]))
                        throw new ParseException(end, "unexpected unicode point: " + source[end]);
                    if (! isHex(source[++end]))
                        throw new ParseException(end, "unexpected unicode point: " + source[end]);
                    if (! isHex(source[++end]))
                        throw new ParseException(end, "unexpected unicode point: " + source[end]);
                    if (! isHex(source[++end]))
                        throw new ParseException(end, "unexpected unicode point: " + source[end]);
                    break;
                default:
                    throw new ParseException(end, "unexpected escape char: " + source[end]);
                }
                
                escapeCount++;
            }
            end++;
            if (end >= source.length) {
                throw new ParseException(end, "reached end, expected '\"'");
            }
        }
        
        if (value) {
            if (! handler.stringValue(source, begin + 1, end, escapeCount)) {
                return -1;
            }
        } else {
            if (! handler.beginObjectEntry(source, begin + 1, end, escapeCount)) {
                return -1;
            }
        }
        return end + 1;
    }
    
    private static int nullValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = begin + 1;
        if (end >= source.length || source[end] != 'u')
            throw new ParseException(end, "expected u");
        
        end++;
        if (end >= source.length || source[end] != 'l')
            throw new ParseException(end, "expected l");
        
        end++;
        if (end >= source.length || source[end] != 'l')
            throw new ParseException(end, "expected l");
        
        if (! handler.nullValue(source, begin, end)) {
            return -1;
        }
        return end + 1;
    }

    private static int trueValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = begin + 1;
        if (end >= source.length || source[end] != 'r')
            throw new ParseException(end, "expected r");
        
        end++;
        if (end >= source.length || source[end] != 'u')
            throw new ParseException(end, "expected u");
        
        end++;
        if (end >= source.length || source[end] != 'e')
            throw new ParseException(end, "expected e");
        
        if (! handler.trueValue(source, begin, end)) {
            return -1;
        }
        return end + 1;
    }

    private static int falseValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = begin + 1;
        if (end >= source.length || source[end] != 'a')
            throw new ParseException(end, "expected a");
        
        end++;
        if (end >= source.length || source[end] != 'l')
            throw new ParseException(end, "expected l");
        
        end++;
        if (end >= source.length || source[end] != 's')
            throw new ParseException(end, "expected s");
        
        end++;
        if (end >= source.length || source[end] != 'e')
            throw new ParseException(end, "expected e");
        
        if (! handler.falseValue(source, begin, end)) {
            return -1;
        }
        return end + 1;
    }

    private static int numberNegativeValue(char[] source, ContentHandler handler, int begin) throws ParseException {
        int end = begin + 1;
        if (end >= source.length) {
            throw new ParseException(begin, "reached end, expected number");
        }
        
        switch (source[end]) {
        case '0':
            return numberRest(source, handler, begin, end + 1);
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return numberValue(source, handler, begin, end + 1);
        default:
            throw new ParseException(begin, "expected digit but got: " + source[begin]);
        }
    }
    
    private static int numberValue(char[] source, ContentHandler handler, int begin, int position) throws ParseException {
        if (position >= source.length) {
            if (! handler.longValue(source, begin, position)) {
                return -1;
            }
            return position;
        }
        
        int current = position;
        while (isDigit(source[current])) {
            current++;

            if (current >= source.length) {
                if (! handler.longValue(source, begin, current)) {
                    return -1;
                }
                return current;
            }
        }
        
        return numberRest(source, handler, begin, current);
    }
    
    private static int numberRest(char[] source, ContentHandler handler, int begin, int position) throws ParseException {
        int current = position;
        if (current >= source.length) {
            final int end = current;
            if (! handler.longValue(source, begin, current)) {
                return -1;
            }
            return end;
        }
        
        if (isDigit(source[current])) {
            throw new ParseException(current, "unexpected digit");
        }
        
        boolean fractional = false;
        if (source[current] == '.') {
            fractional = true;
            current++;
            if (current >= source.length) {
                final int end = current;
                if (! handler.doubleValue(source, begin, current)) {
                    return -1;
                }
                return end;
            }
            
            boolean hasDigits = false;
            while (isDigit(source[current])) {
                hasDigits = true;
                current++;
                if (current >= source.length) {
                    final int end = current;
                    if (! handler.doubleValue(source, begin, current)) {
                        return -1;
                    }
                    return end;
                }
            }
            
            if (!hasDigits) {
                throw new ParseException(current, "expected at least one digit");
            }
        }
        
        if (source[current] == 'e' || source[current] == 'E') {
            fractional = true;
            current++;
            if (current >= source.length) {
                throw new ParseException(current, "expected exponent");
            }
            
            if (source[current] == '+' || source[current] == '-') {
                current++;
                if (current >= source.length) {
                    throw new ParseException(current, "expected at least one digit");
                }
            }
            
            boolean hasDigits = false;
            while (isDigit(source[current])) {
                current++;
                hasDigits = true;
                if (current >= source.length) {
                    final int end = current;
                    if (! handler.doubleValue(source, begin, current)) {
                        return -1;
                    }
                    return end;
                }
            }
            if (!hasDigits) {
                throw new ParseException(current, "expected at least one digit");
            }
        }
        
        final int end = current;
        if (fractional) {
            if (! handler.doubleValue(source, begin, current)) {
                return -1;
            }
        } else {
            if (! handler.longValue(source, begin, current)) {
                return -1;
            }
        }
        return end;
    }
    
    private static int whitespace(char[] source, int position) {
        while (position < source.length && isWhitespace(source[position])) {
            position++;
        }
        return position;
    }

    private static boolean isHex(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
    
    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isWhitespace(char ch) {
        return (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t');
    }
}
