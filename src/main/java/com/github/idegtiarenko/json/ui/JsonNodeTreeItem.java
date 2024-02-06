package com.github.idegtiarenko.json.ui;

import com.github.idegtiarenko.json.Node;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class JsonNodeTreeItem extends TreeItem<Node> {

    private final Node node;
    private boolean initialized = false;

    public JsonNodeTreeItem(Node node) {
        super(node);
        this.node = node;
    }

    @Override
    public boolean isLeaf() {
        return node.children().isEmpty();
    }

    @Override
    public ObservableList<TreeItem<Node>> getChildren() {
        if (!initialized) {
            initialized = true;
            super.getChildren().setAll(node.children().stream().map(JsonNodeTreeItem::new).toList());
        }
        return super.getChildren();
    }
}
