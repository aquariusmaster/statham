package com.anderb.statham;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void findEndElementIndex() {
        String json = "{\n" +
                "  \"first\": {\n" +
                "    \"second\": {\n" +
                "      \"key\": \"value\" \n" +
                "    }\n" +
                "  }\n" +
                "}";
        assertEquals(63, JsonParser.findEndingElementIndex(json, '{', '}', 0));
    }

    @Test
    void skipWhiteSpaces() {
        assertEquals(0, JsonParser.skipWhiteSpaces("", 0));
        assertEquals(2, JsonParser.skipWhiteSpaces("  ", 0));
        assertEquals(1, JsonParser.skipWhiteSpaces(" 0", 0));
        assertEquals(3, JsonParser.skipWhiteSpaces(":  [", 1));
    }

    @Test
    void countBetween() {
        assertEquals(5,
                JsonParser.countBetween(
                        "{\n    {\n       {\n           {\n               {\n                   {",
                        0,
                        67,
                        '{')
        );
        assertEquals(0, JsonParser.countBetween("", 0, "".length(), '{'));
        assertEquals(5, JsonParser.countBetween("[][][][][][][][][]", 0, 10, ']'));
        assertEquals(0, JsonParser.countBetween("{{{{{{{{{{", 0, 10, '}'));
        assertEquals(9, JsonParser.countBetween("{{{{{{{{{{", 0, 10, '{'));
        assertEquals(8, JsonParser.countBetween("{{{{{{{{{}", 0, 10, '{'));
    }

    @Test
    void parseToList() {
        var json = "[1,2,3,4]";
        List actual = (List) JsonParser.parseToList(json, 0).getValue();
        assertNotNull(actual);
        assertEquals("1", actual.get(0));
        assertEquals("2", actual.get(1));
        assertEquals("3", actual.get(2));
        assertEquals("4", actual.get(3));
    }

    @Test
    void parseToList_strings() {
        var json = "[\"a\",\"b\",\"c\",\"d\"]";
        List actual = (List) JsonParser.parseToList(json, 0).getValue();
        assertNotNull(actual);
        assertEquals("a", actual.get(0));
        assertEquals("b", actual.get(1));
        assertEquals("c", actual.get(2));
        assertEquals("d", actual.get(3));
    }

    @Test
    void parseToList_boolean() {
        var json = "[true,false,false,true]";
        List actual = (List) JsonParser.parseToList(json, 0).getValue();
        assertNotNull(actual);
        assertEquals(true, actual.get(0));
        assertEquals(false, actual.get(1));
        assertEquals(false, actual.get(2));
        assertEquals(true, actual.get(3));
    }

    @Test
    void parseToList_withAnyTypes() {
        var json = "[\"true\",null,\"323\",\"any\", 456, true,  false,19 ]";
        List actual = (List) JsonParser.parseToList(json, 0).getValue();
        assertNotNull(actual);
        assertEquals(8, actual.size());
        assertEquals("true", actual.get(0));
        assertNull(actual.get(1));
        assertEquals("323", actual.get(2));
        assertEquals("any", actual.get(3));
        assertEquals("456", actual.get(4));
        assertEquals(true, actual.get(5));
        assertEquals(false, actual.get(6));
        assertEquals("19", actual.get(7));

    }

    @Test
    void parse() {
    }

    @Test
    void keyParser() {
    }

    @Test
    void parseToString() {
    }

    @Test
    void parseToNumber() {
    }

    @Test
    void parseToBoolean() {
    }

    @Test
    void parseToNull() {
    }

    @Test
    void parseToObject() {
    }

}