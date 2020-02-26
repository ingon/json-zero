package dev.ingon.json.zero;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ParserPositiveTests {
    @Test
    public void testLiterals() throws ParseException {
        assertPass("null", ">>> ", "NULL", " <<<");
        assertPass("true", ">>> ", "TRUE", " <<<");
        assertPass("false", ">>> ", "FALSE", " <<<");
    }
    
    @Test
    public void testNumbers() throws ParseException {
        // easy
        assertPass("0", ">>> ", "L:0", " <<<");
        assertPass("1", ">>> ", "L:1", " <<<");
        assertPass("123", ">>> ", "L:123", " <<<");
        
        // harder
        assertPass("0.4", ">>> ", "D:0.4", " <<<");
        assertPass("5.31", ">>> ", "D:5.31", " <<<");
        assertPass("6e2", ">>> ", "D:600.0", " <<<");
        assertPass("2.98e-2", ">>> ", "D:0.0298", " <<<");
    }
    
    @Test
    public void testStrings() throws ParseException {
        // easy
        assertPass("\"\"", ">>> ", "S:", " <<<");
        assertPass("\" \"", ">>> ", "S: ", " <<<");
        assertPass("\"a\"", ">>> ", "S:a", " <<<");
        assertPass("\"abc\"", ">>> ", "S:abc", " <<<");
        
        // harder
        assertPass("\"\\t\\b\"", ">>> ", "S:\t\b", " <<<");
        assertPass("\"\\u73e1\\u52ab\"", ">>> ", "S:\u73e1\u52ab", " <<<");
    }
    
    @Test
    public void testArrays() throws ParseException {
        assertPass("[]", ">>> ", "[ ", "] ", " <<<");
        assertPass("[ ]", ">>> ", "[ ", "] ", " <<<");
        assertPass("[2]", ">>> ", "[ ", "> ", "L:2", ", ", "] ", " <<<");
        assertPass("[16, 9.5e2, true, false, null, \"abc\\u9321\"]",
            ">>> ", "[ ", 
            "> ", "L:16", ", ",
            "> ", "D:950.0", ", ",
            "> ", "TRUE", ", ",
            "> ", "FALSE", ", ",
            "> ", "NULL", ", ",
            "> ", "S:abc\u9321", ", ",
            "] ", " <<<");
    }
    
    @Test
    public void testObjects() throws ParseException {
        assertPass("{}", ">>> ", "{ ", "} ", " <<<");
        assertPass("{ }", ">>> ", "{ ", "} ", " <<<");
        assertPass("{\"a\": 1}", 
            ">>> ", "{ ", 
            "a: ", "L:1", ", ", 
            "} ", " <<<");
        assertPass("{\"double\": 3.2e3, \"null\": null, \"string\": \"c\\u2234\"}", 
            ">>> ", "{ ", 
            "double: ", "D:3200.0", ", ", 
            "null: ", "NULL", ", ", 
            "string: ", "S:c\u2234", ", ", 
            "} ", " <<<");
    }
    
    @Test
    public void testNesting() throws ParseException {
        assertPass("[{\"1\": 2}, [3]]", 
            ">>> ", "[ ", 
            "> ",
            "{ ", "1: ", "L:2", ", ", "} ",
            ", ", 
            "> ",
            "[ ", "> ", "L:3", ", ", "] ",
            ", ", 
            "] ", " <<<");
        
        assertPass("{\"1\": {\"2\": 3}, \"4\": [5]}", 
            ">>> ", "{ ",
            "1: ", "{ ", "2: ", "L:3", ", ", "} ", ", ",
            "4: ", "[ ", "> ", "L:5", ", ", "] ", ", ",
            "} ", " <<<");
    }
    
    @Test
    public void testSpaces() throws ParseException {
        assertPass("   {   \"abc\"   :   [   1   ,   true   ,   \"xxx\"   ]   ,   \"d\"   :   1   }   ",
            ">>> ", "{ ",
            "abc: ", "[ ", "> ", "L:1", ", ", "> ", "TRUE", ", ", "> ", "S:xxx", ", ", "] ", ", ",
            "d: ", "L:1", ", ", 
            "} ", " <<<");
    }
    
    private void assertPass(String source, String... expected) throws ParseException {
        var handler = new CollectingContentHandler();
        Parser.parse(source.toCharArray(), handler);
        assertEquals(Arrays.asList(expected), handler.elements);
    }
}
