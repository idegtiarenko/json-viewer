package com.github.idegtiarenko.json.ui.components;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class NodeUtils {

    public static <T extends Region> T useAllWidth(T region) {
        region.setMaxWidth(Double.MAX_VALUE);
        return region;
    }

    public static <T extends Node> T fillWidth(T node) {
        HBox.setHgrow(node, Priority.ALWAYS);
        return node;
    }

    public static <T extends Node> T fillHeight(T node) {
        VBox.setVgrow(node, Priority.ALWAYS);
        return node;
    }
}
