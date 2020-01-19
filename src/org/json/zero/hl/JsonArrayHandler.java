package org.json.zero.hl;

import java.util.ArrayList;
import java.util.List;

import org.json.zero.ParseException;

public class JsonArrayHandler<T> extends JsonBaseHandler<List<T>> {
    private final JsonBaseHandler<T> elementHandler;
    private List<T> list;
    
    public JsonArrayHandler(JsonBaseHandler<T> elementHandler) {
        this.elementHandler = elementHandler;
        this.elementHandler.valueConsumer = (v) -> {
            list.add(v);
            expectNext();
        };
    }
    
    private void expectNext() {
        elementHandler.parser = parser;
    }
    
    @Override
    public boolean beginArray() throws ParseException {
        list = new ArrayList<T>();
        return true;
    }
    
    @Override
    public boolean beginArrayEntry() throws ParseException {
        parser.enque("[]", elementHandler);
        return true;
    }
    
    @Override
    public boolean endArrayEntry() throws ParseException {
        return true;
    }

    @Override
    public boolean endArray() throws ParseException {
        complete(list);
        return true;
    }
}