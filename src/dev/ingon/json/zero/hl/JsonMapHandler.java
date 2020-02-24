package dev.ingon.json.zero.hl;

import java.util.HashMap;
import java.util.Map;

import dev.ingon.json.zero.ParseException;

public class JsonMapHandler<V> extends JsonBaseHandler<Map<String, V>> {
    private final JsonBaseHandler<V> elementHandler;
    private Map<String, V> map;

    public JsonMapHandler(JsonBaseHandler<V> elementHandler) {
        this.elementHandler = elementHandler;
    }
    
    @Override
    public boolean beginObject() throws ParseException {
        map = new HashMap<String, V>();
        return true;
    }
    
    @Override
    public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
        String name = readString(source, begin, end, escapeCount);
        elementHandler.valueConsumer = (v) -> map.put(name, v);
        parser.enque(name, elementHandler);
        return true;
    }
    
    @Override
    public boolean endObjectEntry() throws ParseException {
        return true;
    }
    
    @Override
    public boolean endObject() throws ParseException {
        complete(map);
        return true;
    }
}
