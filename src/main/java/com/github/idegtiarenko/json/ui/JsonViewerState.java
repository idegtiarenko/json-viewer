package com.github.idegtiarenko.json.ui;

import com.github.idegtiarenko.json.Json;
import com.github.idegtiarenko.json.Node;

import java.io.File;

public record JsonViewerState(File file, Node node) {

    public static JsonViewerState from(File file) {
        return new JsonViewerState(file, Json.parse(file));
    }
}
