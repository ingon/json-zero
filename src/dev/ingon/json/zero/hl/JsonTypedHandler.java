package dev.ingon.json.zero.hl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import dev.ingon.json.zero.ParseException;

public class JsonTypedHandler<T> extends JsonBaseHandler<T> {
    private final Supplier<T> factory;
    
    private final Map<String, JsonBaseHandler<?>> handlers = new HashMap<>();
    
    private T instance;
    
    public JsonTypedHandler(Supplier<T> factory) {
        this.factory = factory;
    }
    
    public void stringProperty(String name, BiConsumer<T, String> consumer) {
        anyProperty(name, new JsonStringHandler(), consumer);
    }
    
    public void numberProperty(String name, BiConsumer<T, Number> consumer) {
        anyProperty(name, new JsonNumberHandler(), consumer);
    }
    
    public void booleanProperty(String name, BiConsumer<T, Boolean> consumer) {
        anyProperty(name, new JsonBooleanHandler(), consumer);
    }
    
    public <S> void objectProperty(String name, JsonTypedHandler<S> handler, BiConsumer<T, S> consumer) {
        anyProperty(name, handler, consumer);
    }
    
    public <S> void mapProperty(String name, JsonBaseHandler<S> handler, BiConsumer<T, Map<String, S>> consumer) {
        anyProperty(name, new JsonMapHandler<S>(handler), consumer);
    }
    
    public <S> void arrayProperty(String name, JsonBaseHandler<S> handler, BiConsumer<T, List<S>> consumer) {
        anyProperty(name, new JsonArrayHandler<S>(handler), consumer);
    }
    
    public void valueProperty(String name, BiConsumer<T, Object> consumer) {
        anyProperty(name, new JsonValueHandler(), consumer);
    }
    
    public void primitiveProperty(String name, BiConsumer<T, Object> consumer) {
        anyProperty(name, new JsonPrimitiveValueHandler(), consumer);
    }
    
    protected <S> void anyProperty(String name, JsonBaseHandler<S> handler, BiConsumer<T, S> consumer) {
        handler.valueConsumer = (s) -> consumer.accept(instance, s);
        handlers.put(name, handler);
    }
    
    @Override
    public boolean beginObject() throws ParseException {
        instance = factory.get();
        return true;
    }
    
    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        String name = readString(source, begin, end, escapeCount);
        var handler = handlers.get(name);
        if (handler == null) {
            throw new ParseException(2, String.format("(parser %s) handler not found: %s", instance.getClass(), name));
        }
        parser.enque(name, handler);
        return true;
    }
    
    @Override
    public boolean endObjectEntry() throws ParseException {
        return true;
    }
    
    @Override
    public boolean endObject() throws ParseException {
        complete(instance);
        return true;
    }
}