package com.github.idegtiarenko.json.ui.components;

import javafx.scene.control.TreeTableCell;

import static com.github.idegtiarenko.json.ui.components.NodeUtils.useAllWidth;

public class LabeledProgressBarTreeTableCell<S> extends TreeTableCell<S, ProgressAndLabel> {

    private final LabeledProgressBar labeledProgressBar;

    public LabeledProgressBarTreeTableCell() {
        this.getStyleClass().add("progress-bar-tree-table-cell");
        this.labeledProgressBar = useAllWidth(new LabeledProgressBar());
    }

    @Override
    protected void updateItem(ProgressAndLabel value, boolean empty) {
        super.updateItem(value, empty);

        if (value == null || empty) {
            setGraphic(null);
        } else {
            labeledProgressBar.update(value);
            setGraphic(labeledProgressBar);
        }
    }
}
