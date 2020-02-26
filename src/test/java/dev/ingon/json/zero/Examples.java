package dev.ingon.json.zero;

import org.junit.Test;

import dev.ingon.json.zero.hl.JsonMapHandler;
import dev.ingon.json.zero.hl.JsonParser;
import dev.ingon.json.zero.hl.JsonStringHandler;

public class Examples {
    @Test
    public void testHello() throws Exception {
        Parser.parse("{\"hello\": \"world\"}".toCharArray(), new DefaultContentHandler() {
            @Override
            public boolean beginObjectEntry(char[] source, int begin, int end, int escapeCount) throws ParseException {
                System.out.print(readString(source, begin, end, escapeCount));
                return true;
            }
            
            @Override
            public boolean stringValue(char[] source, int begin, int end, int escapeCount) throws ParseException {
                System.out.print(" ");
                System.out.println(readString(source, begin, end, escapeCount));
                return false;
            }
        });
    }
    
    @Test
    public void testHelloHL() throws Exception {
        var result = JsonParser.parse("{\"hello\": \"world\"}".toCharArray(), 
            new JsonMapHandler<String>(new JsonStringHandler()));
        for (var e : result.entrySet()) {
            System.out.format("%s %s", e.getKey(), e.getValue());
        }
    }
}
