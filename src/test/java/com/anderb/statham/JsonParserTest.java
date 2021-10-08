package com.anderb.statham;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(5, JsonParser.countBetween(
                "{\n" +
                        "   {\n" +
                        "       {\n" +
                        "           {\n" +
                        "               {\n" +
                        "                   {", 0, 66, '{'));
        assertEquals(0, JsonParser.countBetween("", 0, "".length(), '{'));
        assertEquals(5, JsonParser.countBetween("[][][][][][][][][]", 0, 10, ']'));
        assertEquals(0, JsonParser.countBetween("{{{{{{{{{{", 0, 10, '}'));
        assertEquals(9, JsonParser.countBetween("{{{{{{{{{{", 0, 10, '{'));
        assertEquals(8, JsonParser.countBetween("{{{{{{{{{}", 0, 10, '{'));
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

    @Test
    void parseToList() {
    }

}