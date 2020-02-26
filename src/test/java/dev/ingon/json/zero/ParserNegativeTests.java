package dev.ingon.json.zero;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ParserNegativeTests {
    @Test
    public void testNumbers() {
        assertFail("01", 1, "did not expect a digit after 0, but got: 1");
        assertFail("00", 1, "did not expect a digit after 0, but got: 0");
        assertFail("-", 1, "expected number, but got EOF");
        assertFail("-a", 1, "expected number, but got: a");
        assertFail("3.", 2, "expected fraction digits, but got EOF");
        assertFail("3.a", 2, "expected fraction digits, but got: a");
        assertFail("3e", 2, "expected exponent digits, but got EOF");
        assertFail("3ea", 2, "expected exponent digits, but got: a");
    }
    
    @Test
    public void testStrings() {
        assertFail("\"", 1, "expected '\"', but got EOF");
        assertFail("\"a", 2, "expected '\"', but got EOF");
        
        assertFail("\"\\", 2, "expected escaped character, but got EOF");
        assertFail("\"\\a", 2, "expected escape sequence char, but got: a");
        assertFail("\"\\b", 3, "expected '\"', but got EOF");
        
        assertFail("\"\\u", 3, "expect unicode escape, but got EOF");
        assertFail("\"\\u", 3, "expect unicode escape, but got EOF");
        assertFail("\"\\u1", 4, "expect unicode escape, but got EOF");
        assertFail("\"\\u12", 5, "expect unicode escape, but got EOF");
        assertFail("\"\\u123", 6, "expect unicode escape, but got EOF");

        assertFail("\"\\ux123", 3, "unexpected unicode point: x");
        assertFail("\"\\u1x23", 4, "unexpected unicode point: x");
        assertFail("\"\\u12x3", 5, "unexpected unicode point: x");
        assertFail("\"\\u123x", 6, "unexpected unicode point: x");

        assertFail("\"\\u1234", 7, "expected '\"', but got EOF");
        assertFail("\"\\u1234a", 8, "expected '\"', but got EOF");
    }
    
    @Test
    public void testLiterals() {
        assertFail("n", 1, "expected null, but found EOF");
        assertFail("nu", 2, "expected null, but found EOF");
        assertFail("nul", 3, "expected null, but found EOF");

        assertFail("t", 1, "expected true, but found EOF");
        assertFail("tr", 2, "expected true, but found EOF");
        assertFail("tru", 3, "expected true, but found EOF");

        assertFail("f", 1, "expected false, but found EOF");
        assertFail("fa", 2, "expected false, but found EOF");
        assertFail("fal", 3, "expected false, but found EOF");
        assertFail("fals", 4, "expected false, but found EOF");
    }
    
    @Test
    public void testArrays() {
        assertFail("[", 1, "expected value or ']', but got EOF");
        assertFail("[3", 2, "expected array close, but reached EOF");
        assertFail("[\"3", 3, "expected '\"', but got EOF");
        assertFail("[,", 1, "expected value, but got: ,");
        assertFail("[ 1,", 4, "expected value, but got EOF");
        assertFail("[ 1, 3", 6, "expected array close, but reached EOF");
        
        assertFail("[1,]", 3, "expected value, but got: ]");
    }
    
    @Test
    public void testObjects() {
        assertFail("{", 1, "expected object key or '}', but got EOF");
        assertFail("{\"", 2, "expected '\"', but got EOF");
        assertFail("{\"\"", 3, "expected ':', but got EOF");
        assertFail("{\"\":", 4, "expected value, but got EOF");
        assertFail("{\"\":3", 5, "expected '}' or ',', but got EOF");
        
        assertFail("{\"\"}", 3, "expected ':', but got: }");
        assertFail("{\"\":}", 4, "expected value, but got: }");
        assertFail("{3}", 1, "expected '\"', but got: 3");
        assertFail("{true}", 1, "expected '\"', but got: t");
        assertFail("{3:}", 1, "expected '\"', but got: 3");
        assertFail("{\"3\":}", 5, "expected value, but got: }");
        assertFail("{3:3}", 1, "expected '\"', but got: 3");
    }
    
    private void assertFail(String source, int position, String description) {
        try {
            Parser.parse(source.toCharArray(), new DefaultContentHandler());
            fail("should have failed");
        } catch (ParseException exc) {
            assertEquals(String.format("error while parsing [pos=%d]: %s", position, description), exc.getMessage());
        }
    }
}
