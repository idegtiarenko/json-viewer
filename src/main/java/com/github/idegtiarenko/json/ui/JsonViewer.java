package com.github.idegtiarenko.json.ui;

import com.github.idegtiarenko.json.Json;
import com.github.idegtiarenko.json.Node;
import com.github.idegtiarenko.json.ui.components.BackgroundTaskExecutor;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static com.github.idegtiarenko.json.FileSystem.sizeToString;
import static com.github.idegtiarenko.json.ui.components.NodeUtils.fillHeight;
import static com.github.idegtiarenko.json.ui.components.NodeUtils.fillWidth;
import static javafx.scene.control.TreeTableView.CONSTRAINED_RESIZE_POLICY;

public class JsonViewer extends Application {

    private static final String APP_NAME = "Json viewer";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        var state = new MutableObservableValue<JsonViewerState>();
        var executor = new BackgroundTaskExecutor();

        var root = new VBox(
                createMenu(stage, state, executor),
                createJsonViewer(state),
                executor.getLabeledProgressBar()
        );

        stage.setTitle(APP_NAME);
        stage.setScene(new Scene(root));
        stage.show();

        getInitialFile().ifPresent(file -> openFile(state, executor, file));
    }

    private MenuBar createMenu(Stage stage, MutableObservableValue<JsonViewerState> state, BackgroundTaskExecutor executor) {

        var open = new MenuItem("Open");
        open.setOnAction(event -> openFile(state, executor, new FileChooser().showOpenDialog(stage)));
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

    private void openFile(MutableObservableValue<JsonViewerState> state, BackgroundTaskExecutor backgroundTaskExecutor, File file) {
        backgroundTaskExecutor.submit(new BackgroundTaskExecutor.Task<JsonViewerState>() {
            @Override
            public String getName() {
                return "Loading json file";
            }

            @Override
            public int getTotalSize() {
                return file != null ? (int) file.length() : 0;
            }

            @Override
            public JsonViewerState execute(IntConsumer onProgress) {
                return JsonViewerState.from(file, onProgress);
            }

            @Override
            public void onSuccess(JsonViewerState result) {
                state.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                state.setValue(null);
                showErrorDialogFor(e);
            }
        });
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

    private VBox createJsonViewer(ObservableValue<JsonViewerState> state) {

        var path = new Text();

        var preview = fillWidth(new TextArea());
        preview.setEditable(false);

        var tree = fillWidth(new TreeTableView<Node>());
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

        return fillHeight(new VBox(
                path,
                fillHeight(new SplitPane(tree, preview))
        ));
    }

    private <T> TreeTableColumn<Node, T> createColumn(String name, double widthRatio, Function<Node, T> extractor) {
        TreeTableColumn<Node, T> column = new TreeTableColumn<>(name);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue().getValue())));
        column.setMaxWidth(widthRatio * Double.MAX_VALUE);
        return column;
    }

    private TreeTableColumn<Node, ProgressAndLabel> createLabeledProgressBarColumn(String name, double widthRatio, Function<Node, ProgressAndLabel> extractor) {
        var column = new TreeTableColumn<Node, ProgressAndLabel>(name);
        column.setCellValueFactory(param -> {
            var node = param.getValue().getValue();
            return new SimpleObjectProperty<>(extractor.apply(node));
        });
        column.setCellFactory(param -> new LabeledProgressBarTreeTableCell<>());
        column.setMaxWidth(widthRatio * Double.MAX_VALUE);
        return column;
    }

    private Optional<File> getInitialFile() {
        return getParameters().getRaw().stream().map(File::new).filter(File::exists).findFirst();
    }
}
