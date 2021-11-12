package com.github.idegtiarenko.json.ui.components;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class LabeledProgressBar extends StackPane {

    private final ProgressBar progressBar;
    private final Text text;

    public LabeledProgressBar() {
        this.progressBar = new ProgressBar();
        this.text = new Text();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(progressBar, text);
    }

    public void update(ProgressAndLabel value) {
        if (value != null) {
            progressBar.setProgress(value.ratio());
            text.setText(value.label());
        } else {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            text.setText("");
        }
    }
}
