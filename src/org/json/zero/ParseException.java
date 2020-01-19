package org.json.zero;

public class ParseException extends Exception {
    private static final long serialVersionUID = 1L;

    public ParseException(int position, String description) {
        super(String.format("error while parsing [pos=%d]: %s", position, description));
    }
}
