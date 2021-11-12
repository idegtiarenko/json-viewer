module com.github.idegtiarenko.json {
    requires javafx.controls;
    requires com.fasterxml.jackson.core;
    requires static lombok;

    exports com.github.idegtiarenko.json.ui;
    exports com.github.idegtiarenko.json.ui.components;
}
