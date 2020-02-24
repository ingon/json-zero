package dev.ingon.json.zero.hl;

import java.util.Deque;
import java.util.LinkedList;

import dev.ingon.json.zero.ContentHandler;
import dev.ingon.json.zero.ParseException;
import dev.ingon.json.zero.Parser;

public class JsonParser<V> implements ContentHandler {
    public static <T> T parse(char[] data, JsonBaseHandler<T> handler) throws ParseException {
        var parser = new JsonParser<T>(handler);
        Parser.parse(data, parser);
        return parser.getValue();
    }    

    private final JsonBaseHandler<V> root;
    private final Deque<String> debug = new LinkedList<String>();
    private final Deque<JsonBaseHandler<?>> stack = new LinkedList<JsonBaseHandler<?>>();
    private V value;
    
    public JsonParser(JsonBaseHandler<V> root) {
        this.root = root;
    }
    
    public V getValue() {
        return value;
    }
    
    @Override
    public void beginJSON() throws ParseException {
        root.valueConsumer = (v) -> {
            value = v;
        };
        enque("/", root);
    }
    
    @Override
    public void endJSON() throws ParseException {
        // -\../-
    }
    
    @Override
    public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
        return stack.peekLast().stringValue(source, begin, end, escapeCount);
    }
    
    @Override
    public boolean longValue(char[] source, int begin, int end) throws ParseException {
        return stack.peekLast().longValue(source, begin, end);
    }
    
    @Override
    public boolean doubleValue(char[] source, int begin, int end) throws ParseException {
        return stack.peekLast().doubleValue(source, begin, end);
    }

    @Override
    public boolean trueValue(char[] source, int begin, int end) throws ParseException {
        return stack.peekLast().trueValue(source, begin, end);
    }

    @Override
    public boolean falseValue(char[] source, int begin, int end) throws ParseException {
        return stack.peekLast().falseValue(source, begin, end);
    }

    @Override
    public boolean nullValue(char[] source, int begin, int end) throws ParseException {
        return stack.peekLast().nullValue(source, begin, end);
    }

    @Override
    public boolean beginObject() throws ParseException {
        return stack.peekLast().beginObject();
    }
    
    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        return stack.peekLast().beginObjectEntry(source, begin, end, escapeCount);
    }
    
    @Override
    public boolean endObjectEntry() throws ParseException {
        return stack.peekLast().endObjectEntry();
    }
    
    @Override
    public boolean endObject() throws ParseException  {
        return stack.peekLast().endObject();
    }
    
    @Override
    public boolean beginArray() throws ParseException {
        return stack.peekLast().beginArray();
    }
    
    @Override
    public boolean beginArrayEntry() throws ParseException {
        return stack.peekLast().beginArrayEntry();
    }
    
    @Override
    public boolean endArrayEntry() throws ParseException {
        return stack.peekLast().endArrayEntry();
    }
    
    @Override
    public boolean endArray() throws ParseException {
        return stack.peekLast().endArray();
    }
    
    protected void enque(String name, JsonBaseHandler<?> handler) {
        handler.setParser(this);
        debug.offerLast(name);
        stack.offerLast(handler);
    }
    
    protected void deque() {
        debug.pollLast();
        stack.pollLast();
    }
}
