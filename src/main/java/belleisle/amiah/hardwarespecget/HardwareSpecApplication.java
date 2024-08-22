package belleisle.amiah.hardwarespecget;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class HardwareSpecApplication extends Application {

    protected static Stage rootStage = null;
    protected static final URI APP_ICON_URI = FileUtil.getResourceFile( "app_icon.png", false)
                                              .orElse(new File("")).toURI();
    protected static final URI ADD_IMAGE_ICON_URI = FileUtil.getResourceFile( "add_image_icon.png", false)
                                                    .orElse(new File("")).toURI();
    public static final Image DEFAULT_ICON = new Image(ADD_IMAGE_ICON_URI.toString());
    public static final String APP_TITLE = "Hardware Specifications";

    @Override
    public void start(Stage stage) throws IOException {

        /* =================== *
         * Initial Stage Setup *
         * =================== */

        rootStage = stage;
        VBox rootPane = new VBox();
        Scene scene = new Scene(rootPane, 320, 240);
        stage.setTitle(APP_TITLE);
        stage.getIcons().add(new Image(APP_ICON_URI.toString()));
        stage.setMinWidth(500);
        stage.setMinHeight(300);
        stage.setScene(scene);

        /* ======================= *
         * Pane Creation and Setup *
         * ======================= */

        // Declaring and init panes
        HBox mainContentHbox = new HBox(); // used to hold the left and right panes
        VBox rightMainPane = new VBox(); // right pane in mainContentHbox; holds rightScrollPane
        VBox rightScrollPaneVbox = new VBox(); // Vbox inside rightScrollPane
        ScrollPane rightScrollPane = new ScrollPane(rightScrollPaneVbox); // holds content in rightMainPane
        VBox leftMainPane = new VBox(); // left pane in mainContentHbox
        HBox leftOptionsHbox = new HBox(); // top pane in leftMainPane to hold option buttons and etc
        VBox leftImageVbox = new VBox(); // second pane in leftMainPane, holds OS icon
        VBox leftScrollPaneVbox = new VBox(); // Vbox inside leftScrollPane
        ScrollPane leftScrollPane = new ScrollPane(leftScrollPaneVbox); // holds content in bottom of leftMainPane
        // Organizing Panes
        rootPane.getChildren().add(mainContentHbox);
        mainContentHbox.getChildren().add(leftMainPane);
        mainContentHbox.getChildren().add(rightMainPane);
        rightMainPane.getChildren().add(rightScrollPane);
        leftMainPane.getChildren().add(leftOptionsHbox);
        leftMainPane.getChildren().add(leftImageVbox);
        leftMainPane.getChildren().add(leftScrollPane);
        // Setting Pane Properties
        rightScrollPaneVbox.setPadding(new Insets(0, 0, 0, 5));
        leftScrollPaneVbox.setPadding(new Insets(0, 0, 0, 5));
        leftOptionsHbox.setPadding(new Insets(5));
        leftOptionsHbox.setSpacing(3);
        leftImageVbox.setPadding(new Insets(5));
        leftImageVbox.setAlignment(Pos.CENTER);

        /* ========================== *
         * Control Creation and Setup *
         * ========================== */

        // Creating controls
        Button toggleEditModeButton = new Button("Edit Visibility");
        Button replaceIconButton = new Button("Set Icon");
        Button saveButton = new Button("Save");
        ImageView mainIcon = new ImageView();
        // Adding Controls
        leftOptionsHbox.getChildren().add(toggleEditModeButton);
        leftOptionsHbox.getChildren().add(replaceIconButton);
        leftOptionsHbox.getChildren().add(saveButton);
        leftImageVbox.getChildren().add(mainIcon);
        // Setting control properties
        // mainIcon Properties
        mainIcon.setImage(DEFAULT_ICON);
        mainIcon.setPreserveRatio(true);
        mainIcon.setCursor(Cursor.HAND);
        mainIcon.setFitHeight(80);
        mainIcon.setFitWidth(80);
        // mainIcon bindings to fit parent
        DoubleBinding smallestDimension = (DoubleBinding) Bindings.min(leftImageVbox.heightProperty(), leftImageVbox.widthProperty());
        mainIcon.scaleXProperty().bind(smallestDimension.divide(mainIcon.fitWidthProperty().add(10)));
        mainIcon.scaleYProperty().bind(smallestDimension.divide(mainIcon.fitHeightProperty().add(10)));

        /* =================== *
         * Creating Node Lists *
         * =================== */

        NodeList rightNodeList = new NodeList(rightScrollPaneVbox.getChildren());

        rightNodeList.createElement("CPU", HardwareCollector.getCPU(), "#CC44AA");
        rightNodeList.createElement("GPU", HardwareCollector.getGPU(), "#5599DD");
        rightNodeList.createElement("Memory", "454 / 34213213", "#4CA8AF");

        NodeList leftNodeList = new NodeList(leftScrollPaneVbox.getChildren());

        leftNodeList.createElement("Operating System", HardwareCollector.getOS(), "#CC44AA");
        leftNodeList.createElement("Kernel", HardwareCollector.getKernel(), "#CC44AA");
        leftNodeList.createElement("Username", "", "#EE4487");

        /* ========= *
         * Listeners *
         * ========= */

        // Resize panes with stage size
        stage.widthProperty().addListener(event -> {
            // Make the right pane take up half of the right side
            rightMainPane.setPrefWidth(stage.getWidth()/2);
            rightMainPane.relocate(stage.getWidth()/2,0);
            // Make the left pane take up half of the left side
            leftMainPane.setPrefWidth(stage.getWidth()/2);
        });
        stage.heightProperty().addListener(event -> {
            rightMainPane.setPrefHeight(stage.getHeight());
            leftMainPane.setPrefHeight(stage.getHeight());
            rightScrollPane.setPrefHeight(stage.getHeight());

            leftScrollPane.setPrefHeight(stage.getHeight() / 2);
            leftScrollPane.relocate(0, stage.getHeight() / 2);

            leftImageVbox.setPrefHeight((stage.getHeight() / 2) - leftOptionsHbox.getHeight());
            leftImageVbox.relocate(0, leftOptionsHbox.getHeight());
        });

        /* =============== *
         * Control Actions *
         * =============== */

        // Setting control actions
        toggleEditModeButton.setOnAction(event -> {
            rightNodeList.toggleEditMode();
            leftNodeList.toggleEditMode();
        });

        replaceIconButton.setOnAction(event -> {
            Optional<File> file = FileUtil.getImageFileFromUser(stage);
            if (file.isPresent()) {
                mainIcon.setImage(new Image(file.get().toURI().toString()));
                mainIcon.setCursor(Cursor.DEFAULT);
            }
        });

        mainIcon.setOnMouseClicked(event -> {
            if (mainIcon.getImage().equals(DEFAULT_ICON)) {
                Optional<File> file = FileUtil.getImageFileFromUser(stage);
                if (file.isPresent()) {
                    mainIcon.setImage(new Image(file.get().toURI().toString()));
                    mainIcon.setCursor(Cursor.DEFAULT);
                }
            }
        });

        saveButton.setOnAction(event -> {
            // Save the image and node lists and store whether they were successful
            boolean savedImg = FileUtil.saveImage(mainIcon.getImage().getUrl());
            boolean savedLists = FileUtil.saveNodeLists(leftNodeList, rightNodeList);
            // Tell the user that their data was saved successfully
            if (savedImg && savedLists) {
                new AlertBuilder(Alert.AlertType.INFORMATION)
                        .setWindowTitle("Saved Successfully")
                        .setHeaderText("Saved Successfully")
                        .setMessage("Saved Successfully")
                        .build().showAndWait();
            }
        });

        /* ========== *
         * Show Stage *
         * ========== */

        stage.show();

        /* ========= *
         * Load Data *
         * ========= */
        FileUtil.loadImage(mainIcon);
    }

    public static void main(String[] args) {
        launch();
    }

}