package com.github.idegtiarenko.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.function.IntConsumer;

import static com.fasterxml.jackson.core.JsonToken.VALUE_FALSE;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NULL;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT;
import static com.fasterxml.jackson.core.JsonToken.VALUE_STRING;
import static com.fasterxml.jackson.core.JsonToken.VALUE_TRUE;

public class Json {

    public static Node parse(String json) {
        try {
            return parse(new JsonFactory().createParser(json), p -> {});
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Node parse(File file, IntConsumer onProgress) {
        try {
            return parse(new JsonFactory().createParser(file), onProgress);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Node parse(JsonParser parser, IntConsumer onProgress) throws IOException {
        parser.nextToken();
        return readValue(parser, "", onProgress);
    }

    private static Node readValue(JsonParser parser, String name, IntConsumer onProgress) throws IOException {
        onProgress.accept(getLocation(parser));
        var token = parser.currentToken();
        return switch (token) {
            case START_OBJECT -> readObject(parser, name, onProgress);
            case START_ARRAY -> readArray(parser, name, onProgress);
            case VALUE_STRING -> readString(parser, name);
            case VALUE_NUMBER_INT -> readTokenValue(parser, name, VALUE_NUMBER_INT);
            case VALUE_NUMBER_FLOAT -> readTokenValue(parser, name, VALUE_NUMBER_FLOAT);
            case VALUE_TRUE -> readTokenValue(parser, name, VALUE_TRUE);
            case VALUE_FALSE -> readTokenValue(parser, name, VALUE_FALSE);
            case VALUE_NULL -> readTokenValue(parser, name, VALUE_NULL);
            default -> throw new UnsupportedOperationException("Unexpected token type: " + token.asString());
        };
    }

    private static Node readObject(JsonParser parser, String name, IntConsumer onProgress) throws IOException {
        assert parser.currentToken() == JsonToken.START_OBJECT;
        var from = getLocation(parser);
        var fields = new ArrayList<Node>();
        while (true) {
            var next = parser.nextToken();
            if (next == JsonToken.END_OBJECT) {
                break;
            } else if (next == JsonToken.FIELD_NAME) {
                //skip
            } else {
                fields.add(readValue(parser, parser.getCurrentName(), onProgress));
            }
        }
        var to = getLocation(parser) + 1;
        return Node.object(name, from, to, fields);
    }

    private static Node readArray(JsonParser parser, String name, IntConsumer onProgress) throws IOException {
        assert parser.currentToken() == JsonToken.START_ARRAY;
        var from = getLocation(parser);
        var items = new ArrayList<Node>();
        var index = 0;
        while (true) {
            var next = parser.nextToken();
            if (next == JsonToken.END_ARRAY) {
                break;
            } else {
                items.add(readValue(parser, Integer.toString(index), onProgress));
                index++;
            }
        }
        var to = getLocation(parser) + 1;
        return Node.array(name, from, to, items);
    }

    private static Node readString(JsonParser parser, String name) throws IOException {
        assert parser.currentToken() == VALUE_STRING;
        var from = getLocation(parser) + 1;
        var to = from + parser.getTextLength();
        return Node.value(name, from, to, "\"" + parser.getText() + "\"");
    }

    private static Node readTokenValue(JsonParser parser, String name, JsonToken token) throws IOException {
        assert parser.currentToken() == token;
        var from = getLocation(parser);
        var to = from + parser.getTextLength();
        return Node.value(name, from, to, parser.getText());
    }

    private static int getLocation(JsonParser parser) {
        var location = parser.getTokenLocation();
        if (location.getCharOffset() >= 0) {
            return (int) location.getCharOffset();
        } else if (location.getByteOffset() >= 0) {
            return (int) location.getByteOffset();
        } else {
            return -1;
        }
    }

    public static String toAbbreviatedFormattedString(Node node, int maxLength) {
        return appendJson(new AbbreviatedStringBuilder(maxLength), "", node).toString();
    }

    private static AbbreviatedStringBuilder appendJson(AbbreviatedStringBuilder builder, String ident, Node node) {
        if (!builder.isLimitReached()) {
            switch (node.type()) {
                case VALUE -> builder.append(node.value());
                case OBJECT -> {
                    var nestedIdent = ident + "  ";
                    builder.append("{", System.lineSeparator());
                    var count = node.childrenCount();
                    for (int i = 0; i < count; i++) {
                        builder.append(nestedIdent, "\"", node.child(i).name(), "\": ");
                        appendJson(builder, nestedIdent, node.child(i));
                        if (i + 1 < count) {
                            builder.append(',');
                        }
                        builder.append(System.lineSeparator());
                    }
                    builder.append(ident, "}");
                }
                case ARRAY -> {
                    var nestedIdent = ident + "  ";
                    builder.append("[", System.lineSeparator());
                    var count = node.childrenCount();
                    for (int i = 0; i < count; i++) {
                        builder.append(nestedIdent);
                        appendJson(builder, nestedIdent, node.child(i));
                        if (i + 1 < count) {
                            builder.append(',');
                        }
                        builder.append(System.lineSeparator());
                    }
                    builder.append(ident, "]");
                }
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        }
        return builder;
    }

    public static String toAbbreviatedJsonPath(TreeItem<Node> item, int maxLength) {
        return appendPath(new AbbreviatedStringBuilder(maxLength), item).toString();
    }

    private static AbbreviatedStringBuilder appendPath(AbbreviatedStringBuilder builder, TreeItem<Node> item) {
        if (item.getParent() != null) {
            appendPath(builder, item.getParent()).append(" > ");
        }
        return builder.append(item.getValue().name());
    }
}
