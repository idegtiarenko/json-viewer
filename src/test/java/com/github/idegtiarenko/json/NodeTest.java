package com.github.idegtiarenko.json;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NodeTest {

    @Test
    void shouldParseEmptyJson() {
        assertThat(Json.parse("{}")).isEqualTo(Node.object("", 0, 2, List.of()));
    }

    @Test
    void shouldParseJsonObject() {
        var json = """
                {"string-field": "string", "int-field": 0, "float-field": 0.0, "boolean-field": true}
                """;
        assertThat(Json.parse(json)).isEqualTo(
                Node.object("", 0, 85, List.of(
                        Node.value("string-field", 18, 24, "\"string\""),
                        Node.value("int-field", 40, 41, "0"),
                        Node.value("float-field", 58, 61, "0.0"),
                        Node.value("boolean-field", 80, 84, "true")
                ))
        );
    }

    @Test
    void shouldParseJsonArray() {
        var json = """
                ["string", 0, 0.0, true, false, null]
                """;
        assertThat(Json.parse(json)).isEqualTo(
                Node.array("", 0, 37, List.of(
                        Node.value("0", 2, 8, "\"string\""),
                        Node.value("1", 11, 12, "0"),
                        Node.value("2", 14, 17, "0.0"),
                        Node.value("3", 19, 23, "true"),
                        Node.value("4", 25, 30, "false"),
                        Node.value("5", 32, 36, "null")
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
                Node.object("", 0, 179, List.of(
                        Node.value("string-field", 21, 27, "\"string\""),
                        Node.value("int-field", 45, 46, "0"),
                        Node.value("float-field", 65, 68, "0.0"),
                        Node.value("boolean-field", 89, 93, "true"),
                        Node.value("null-field", 111, 115, "null"),
                        Node.array("array-field", 134, 141, List.of(
                                Node.value("0", 135, 136, "0"),
                                Node.value("1", 137, 138, "1"),
                                Node.value("2", 139, 140, "2")
                        )),
                        Node.object("object-field", 161, 177, List.of(
                                Node.value("key", 170, 175, "\"value\"")
                        ))
                ))
        );
    }
}