package com.github.idegtiarenko.json.ui;

import com.github.idegtiarenko.json.Json;
import com.github.idegtiarenko.json.Node;
import com.github.idegtiarenko.json.ui.components.LabeledProgressBarTreeTableCell;
import com.github.idegtiarenko.json.ui.components.MutableObservableValue;
import com.github.idegtiarenko.json.ui.components.ProgressAndLabel;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

import static com.github.idegtiarenko.json.FileSystem.sizeToString;
import static java.util.Optional.ofNullable;
import static javafx.scene.control.TreeTableView.CONSTRAINED_RESIZE_POLICY;

public class JsonViewer extends Application {

    private static final String APP_NAME = "Json viewer";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        var state = new MutableObservableValue<JsonViewerState>();

        var components = createJsonViewComponents(state);

        var root = new VBox(
                createMenu(stage, state),
                components.path,
                createSplitPane(components.tree, components.preview)
        );

        Scene scene = new Scene(root);

        stage.setTitle(APP_NAME);
        stage.setScene(scene);
        stage.show();

        state.setOptionalValue(getInitialFile().map(JsonViewerState::from));
    }

    private MenuBar createMenu(Stage stage, MutableObservableValue<JsonViewerState> state) {

        var open = new MenuItem("Open");
        open.setOnAction(event -> {
            try {
                state.setOptionalValue(ofNullable(new FileChooser().showOpenDialog(stage)).map(JsonViewerState::from));
            } catch (Exception e) {
                showErrorDialogFor(e);
            }
        });
        var close = new MenuItem("Close");
        close.setOnAction(event -> state.reset());

        var exit = new MenuItem("Exit");
        exit.setOnAction(event -> stage.close());

        var about = new MenuItem("About");
        about.setOnAction(event -> showAboutDialog());

        return new MenuBar(
                new Menu("File", null, open, close, new SeparatorMenuItem(), exit),
                new Menu("Help", null, about)
        );
    }

    private void showErrorDialogFor(Exception e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(APP_NAME);
        alert.setHeaderText("An error has been encountered");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void showAboutDialog() {
        var about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle(APP_NAME);
        about.setHeaderText(APP_NAME);
        about.setContentText("https://github.com/idegtiarenko/json-viewer");
        about.showAndWait();
    }

    private JsonViewerComponents createJsonViewComponents(ObservableValue<JsonViewerState> state) {

        var path = new Text();

        var preview = new TextArea();
        preview.setEditable(false);
        HBox.setHgrow(preview, Priority.ALWAYS);

        var tree = new TreeTableView<Node>();
        HBox.setHgrow(tree, Priority.ALWAYS);
        tree.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        tree.getColumns().addAll(
                createColumn("name", 0.45, Node::getName),
                createColumn("direct" + System.lineSeparator() + "children", 0.15, Node::getChildrenCount),
                createLabeledProgressBarColumn("recursive" + System.lineSeparator() + "children", 0.2, node -> {
                    var totalSize = state.getValue().node().getRecursiveChildrenCount();
                    var currentSize = node.getRecursiveChildrenCount();
                    return new ProgressAndLabel(currentSize, totalSize, Integer.toString(currentSize));
                }),
                createLabeledProgressBarColumn("size", 0.2, node -> {
                    var totalSize = state.getValue().node().getSize();
                    var currentSize = node.getSize();
                    return new ProgressAndLabel(currentSize, totalSize, sizeToString(currentSize));
                })
        );

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                path.setText(Json.toAbbreviatedJsonPath(newValue, 256));
                preview.setText(Json.toAbbreviatedFormattedString(newValue.getValue(), 1024));
            } else {
                path.setText("");
                preview.setText("");
            }
        });

        state.addListener((observable, oldValue, newValue) -> tree.setRoot(newValue != null ? new JsonNodeTreeItem(newValue.node()) : null));

        return new JsonViewerComponents(tree, path, preview);
    }

    private <T> TreeTableColumn<Node, T> createColumn(String name, double widthRatio, Function<Node, T> extractor) {
        TreeTableColumn<Node, T> column = new TreeTableColumn<>(name);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue().getValue())));
        column.setMaxWidth(widthRatio * Integer.MAX_VALUE);
        return column;
    }

    private TreeTableColumn<Node, ProgressAndLabel> createLabeledProgressBarColumn(String name, double widthRatio, Function<Node, ProgressAndLabel> extractor) {
        var column = new TreeTableColumn<Node, ProgressAndLabel>(name);
        column.setCellValueFactory(param -> {
            var node = param.getValue().getValue();
            return new SimpleObjectProperty<>(extractor.apply(node));
        });
        column.setCellFactory(param -> new LabeledProgressBarTreeTableCell<>());
        column.setMaxWidth(widthRatio * Integer.MAX_VALUE);
        return column;
    }

    private SplitPane createSplitPane(javafx.scene.Node a, javafx.scene.Node b) {
        var split = new SplitPane(a, b);
        VBox.setVgrow(split, Priority.ALWAYS);
        return split;
    }

    private Optional<File> getInitialFile() {
        return getParameters().getRaw().stream().map(File::new).filter(File::exists).findFirst();
    }

    @RequiredArgsConstructor
    private static class JsonViewerComponents {
        private final TreeTableView<Node> tree;
        private final Text path;
        private final TextArea preview;
    }
}
