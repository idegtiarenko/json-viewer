package com.github.idegtiarenko.json;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NodeTest {

    @Test
    void shouldParseEmptyJson() {
        assertThat(Json.parse("{}")).isEqualTo(new ObjectNode("", 0, 2, List.of()));
    }

    @Test
    void shouldParseJsonObject() {
        var json = """
                {"string-field": "string", "int-field": 0, "float-field": 0.0, "boolean-field": true}
                """;
        assertThat(Json.parse(json)).isEqualTo(
                new ObjectNode("", 0, 85, List.of(
                        new ValueNode("string-field", 18, 24, "\"string\""),
                        new ValueNode("int-field", 40, 41, "0"),
                        new ValueNode("float-field", 58, 61, "0.0"),
                        new ValueNode("boolean-field", 80, 84, "true")
                ))
        );
    }

    @Test
    void shouldParseJsonArray() {
        var json = """
                ["string", 0, 0.0, true, false, null]
                """;
        assertThat(Json.parse(json)).isEqualTo(
                new ArrayNode("", 0, 37, List.of(
                        new ValueNode("0", 2, 8, "\"string\""),
                        new ValueNode("1", 11, 12, "0"),
                        new ValueNode("2", 14, 17, "0.0"),
                        new ValueNode("3", 19, 23, "true"),
                        new ValueNode("4", 25, 30, "false"),
                        new ValueNode("5", 32, 36, "null")
                ))
        );
    }

    @Test
    void shouldParseComplexJsonObject() {
        var json = """
                {
                  "string-field": "string",
                  "int-field": 0,
                  "float-field": 0.0,
                  "boolean-field": true,
                  "null-field": null,
                  "array-field": [0,1,2],
                  "object-field": {"key": "value"}
                }
                """;
        assertThat(Json.parse(json)).isEqualTo(
                new ObjectNode("", 0, 179, List.of(
                        new ValueNode("string-field", 21, 27, "\"string\""),
                        new ValueNode("int-field", 45, 46, "0"),
                        new ValueNode("float-field", 65, 68, "0.0"),
                        new ValueNode("boolean-field", 89, 93, "true"),
                        new ValueNode("null-field", 111, 115, "null"),
                        new ArrayNode("array-field", 134, 141, List.of(
                                new ValueNode("0", 135, 136, "0"),
                                new ValueNode("1", 137, 138, "1"),
                                new ValueNode("2", 139, 140, "2")
                        )),
                        new ObjectNode("object-field", 161, 177, List.of(
                                new ValueNode("key", 170, 175, "\"value\"")
                        ))
                ))
        );
    }
}