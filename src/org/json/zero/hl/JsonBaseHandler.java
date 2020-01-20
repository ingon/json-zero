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
        throw new ParseException(-1, getClass() + ": unexpected beginJSON call");
    }

    @Override
    public void endJSON() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected endJSON call");
    }

    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected stringValue call");
    }

    @Override
    public boolean longValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected integerValue call");
    }

    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected doubleValue call");
    }

    @Override
    public boolean trueValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected trueValue call");
    }

    @Override
    public boolean falseValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected falseValue call");
    }

    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected nullValue call");
    }

    @Override
    public boolean beginObject() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected beginObject call");
    }

    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected beginObjectEntry call");
    }

    @Override
    public boolean endObjectEntry() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected endObjectEntry call");
    }

    @Override
    public boolean endObject() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected endObject call");
    }

    @Override
    public boolean beginArray() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected beginArray call");
    }

    @Override
    public boolean beginArrayEntry() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected beginArrayEntry call");
    }

    @Override
    public boolean endArrayEntry() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected endArrayEntry call");
    }

    @Override
    public boolean endArray() throws ParseException {
        throw new ParseException(-1, getClass() + ": unexpected endArray call");
    }
}