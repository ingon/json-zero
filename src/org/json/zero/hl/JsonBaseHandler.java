package org.json.zero.hl;

import java.util.function.Consumer;

import org.json.zero.ContentHandler;
import org.json.zero.ParseException;

public class JsonBaseHandler<T> implements ContentHandler {
    JsonParser<?> parser;
    Consumer<T> valueConsumer = (t) -> {};
    
    protected void setParser(JsonParser<?> parser) {
        this.parser = parser;
    }
    
    protected void complete(T value) {
        parser.deque();
        valueConsumer.accept(value);
    }

    @Override
    public void beginJSON() throws ParseException {
        throw new ParseException(-1, "unexpected beginJSON call");
    }

    @Override
    public void endJSON() throws ParseException {
        throw new ParseException(-1, "unexpected endJSON call");
    }

    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        throw new ParseException(-1, "unexpected stringValue call");
    }

    @Override
    public boolean longValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, "unexpected integerValue call");
    }

    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, "unexpected doubleValue call");
    }

    @Override
    public boolean trueValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, "unexpected trueValue call");
    }

    @Override
    public boolean falseValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, "unexpected falseValue call");
    }

    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, "unexpected nullValue call");
    }

    @Override
    public boolean beginObject() throws ParseException {
        throw new ParseException(-1, "unexpected beginObject call (" + getClass() + ")");
    }

    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        throw new ParseException(-1, "unexpected beginObjectEntry call");
    }

    @Override
    public boolean endObjectEntry() throws ParseException {
        throw new ParseException(-1, "unexpected endObjectEntry call");
    }

    @Override
    public boolean endObject() throws ParseException {
        throw new ParseException(-1, "unexpected endObject call");
    }

    @Override
    public boolean beginArray() throws ParseException {
        throw new ParseException(-1, "unexpected beginArray call");
    }

    @Override
    public boolean beginArrayEntry() throws ParseException {
        throw new ParseException(-1, "unexpected beginArrayEntry call");
    }

    @Override
    public boolean endArrayEntry() throws ParseException {
        throw new ParseException(-1, "unexpected endArrayEntry call");
    }

    @Override
    public boolean endArray() throws ParseException {
        throw new ParseException(-1, "unexpected endArray call");
    }
}