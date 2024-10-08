package belleisle.amiah.hardwarespecget;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.Optional;

public class HardwareSpecApplication extends Application {

    protected static Stage rootStage = null;
    public static Image DEFAULT_ICON;
    public static final String APP_TITLE = "Hardware Specifications";
    public static final SimpleBooleanProperty IS_EDIT_MODE = new SimpleBooleanProperty(false);

    @Override
    public void start(Stage stage) throws IOException {

        /* ============================ *
         * Initial Stage and Pane Setup *
         * ============================ */

        // Left side of application
        HBox leftOptionsHbox = new HBox(); // top pane in leftMainPane to hold option buttons and etc
        VBox leftImageVbox = new VBox(); //  middle pane in leftMainPane, holds an icon
        VBox leftScrollPaneVbox = new VBox(); // Vbox inside leftScrollPane, holds hardware info nodes
        ScrollPane leftScrollPane = new ScrollPane(leftScrollPaneVbox); // bottom pane in leftMainPane
        VBox leftMainPane = new VBox(leftOptionsHbox, leftImageVbox, leftScrollPane); // Pane holding left side of app
        // Right side of application
        VBox rightScrollPaneVbox = new VBox(); // Vbox inside rightScrollPane, holds hardware info nodes
        ScrollPane rightScrollPane = new ScrollPane(rightScrollPaneVbox); // This is the primary content on the right
        VBox rightMainPane = new VBox(rightScrollPane); // Pane holding right side of app
        // Main panes holding content
        HBox mainContentHbox = new HBox(leftMainPane, rightMainPane); // used to hold the left and right panes
        VBox rootPane = new VBox(mainContentHbox);

        // Stage set up
        rootStage = stage;
        Scene scene = new Scene(rootPane, 820, 540);
        stage.setTitle(APP_TITLE);
        stage.getIcons().add(FileUtil.getImage("app_icon.png"));
        stage.setMinWidth(500);
        stage.setMinHeight(300);
        stage.setScene(scene);

        // Setting Pane Properties
        rightScrollPaneVbox.setPadding(new Insets(0, 0, 0, 5));
        leftScrollPaneVbox.setPadding(new Insets(0, 0, 0, 5));
        leftOptionsHbox.setPadding(new Insets(5));
        leftOptionsHbox.setSpacing(3);
        leftImageVbox.setPadding(new Insets(5));
        leftImageVbox.setAlignment(Pos.CENTER);

        // Setting pane colors
        rightScrollPane.setStyle("-fx-background: " + "#393a3e" + ";" + "-fx-background-color: " + "#393a3e" + ";" );
        leftScrollPane.setStyle("-fx-background: " + "#393a3e" + ";" + "-fx-background-color: " + "#393a3e" + ";" );
        rootPane.setStyle("-fx-background: " + "#393a3e" + ";");

        /* ========================== *
         * Control Creation and Setup *
         * ========================== */

        // Creating controls
        Button toggleEditModeButton = new Button("Edit Visibility");
        Button replaceIconButton = new Button("Set Icon");
        Button saveButton = new Button("Save");
        Button recollectButton = new Button("Recollect Info");
        ImageView mainIcon = new ImageView();
        // Adding Controls
        leftOptionsHbox.getChildren().addAll(toggleEditModeButton, replaceIconButton, saveButton, recollectButton);
        leftImageVbox.getChildren().add(mainIcon);
        // Setting control properties
        // mainIcon Properties
        DEFAULT_ICON = FileUtil.getImage("add_image_icon.png");
        mainIcon.setImage(DEFAULT_ICON);
        mainIcon.setPreserveRatio(true);
        mainIcon.setCursor(Cursor.HAND);
        mainIcon.setFitHeight(80);
        mainIcon.setFitWidth(80);
        // mainIcon bindings to fit parent
        DoubleBinding smallestDimension = (DoubleBinding) Bindings.min(leftImageVbox.heightProperty(), leftImageVbox.widthProperty());
        // Adding to the width and height creates padding
        mainIcon.scaleXProperty().bind(smallestDimension.divide(mainIcon.fitWidthProperty().add(10)));
        mainIcon.scaleYProperty().bind(smallestDimension.divide(mainIcon.fitHeightProperty().add(10)));

        /* =================== *
         * Creating Node Lists *
         * =================== */

        // These link the children of a pane to a NodeList object
        NodeList rightNodeList = new NodeList(rightScrollPaneVbox.getChildren());
        NodeList leftNodeList = new NodeList(leftScrollPaneVbox.getChildren());

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
        toggleEditModeButton.setOnAction(event -> IS_EDIT_MODE.set(!IS_EDIT_MODE.get()));

        replaceIconButton.setOnAction(event -> {
            Optional<File> file = FileUtil.getImageFileFromUser(stage);
            if (file.isPresent()) {
                mainIcon.setImage(new Image(file.get().toURI().toString()));
                mainIcon.setCursor(Cursor.DEFAULT);
            }
        });

        saveButton.setOnAction(event -> {
            // Save the image and node lists and store whether they were successful
            boolean savedImg = FileUtil.saveImage(mainIcon.getImage());
            boolean savedLists = FileUtil.saveNodeLists(leftNodeList, rightNodeList);
            // Create alert to show to user.
            Alert msg = AlertBuilder.makeBuilder(Alert.AlertType.INFORMATION)
                    .setWindowTitle("Save Status")
                    .setHeaderText("Saved Successfully")
                    .setMessage("Saved all preferences successfully")
                    .build();
            // If one type of save was successful, but not both
            if ((savedImg || savedLists) && !(savedImg && savedLists)) {
                 msg.setAlertType(Alert.AlertType.WARNING);
                 msg.setHeaderText("Partially Saved");
                 if (savedImg) {
                     msg.setContentText("Saved icon successfully. Unable to save hardware information.");
                 }
                 else {
                     msg.setContentText("Saved hardware information successfully. Unable to save icon.");
                 }
            }
            // If neither were successful
            else if (!(savedImg && savedLists)) {
                msg.setAlertType(Alert.AlertType.ERROR);
                msg.setHeaderText("Save Error");
                msg.setContentText("Unable to save preferences.");
            }
            // Finally show the message
            msg.showAndWait();
        });

        recollectButton.setOnAction(event -> {
            leftNodeList.clearNonUserNodes();
            leftNodeList.addAllNodes(HardwareCollector.collectSystemInfo());
            rightNodeList.clearNonUserNodes();
            rightNodeList.addAllNodes(HardwareCollector.collectHardware());
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

        /* ========== *
         * Show Stage *
         * ========== */

        stage.show();

        /* ========= *
         * Load Data *
         * ========= */

        if (!FileUtil.loadNodeLists(leftNodeList, rightNodeList)) {
            leftNodeList.addAllNodes(HardwareCollector.collectSystemInfo());
            rightNodeList.addAllNodes(HardwareCollector.collectHardware());
        }
        FileUtil.loadImage(mainIcon);
    }

    public static void main(String[] args) {
        launch();
    }

}