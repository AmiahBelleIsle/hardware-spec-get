package belleisle.amiah.hardwarespecget;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HardwareSpecApplication extends Application {

    public static Stage rootStage = null;

    @Override
    public void start(Stage stage) throws IOException {

        /* =================== *
         * Initial Stage Setup *
         * =================== */

        rootStage = stage;
        VBox rootPane = new VBox();
        Scene scene = new Scene(rootPane, 320, 240);
        stage.setTitle("Hardware Specifications");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("app_icon.png")));
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
        leftImageVbox.setPadding(new Insets(5));
        leftImageVbox.setAlignment(Pos.CENTER);

        /* ========================== *
         * Control Creation and Setup *
         * ========================== */

        // Creating controls
        Button toggleEditModeButton = new Button("Edit Visibility");
        ImageView mainIcon = new ImageView();
        // Adding Controls
        leftOptionsHbox.getChildren().add(toggleEditModeButton);
        leftImageVbox.getChildren().add(mainIcon);
        // Setting control properties
        // mainIcon Properties
        mainIcon.setImage(new Image(NodeList.class.getResourceAsStream("add_image_simple_icon.png")));
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

        rightNodeList.createElement("CPU", "Intel i5 10400 @ 2.90 GHz", "#CC44AA");
        rightNodeList.createElement("GPU", "Nvidia 3060 RTX", "#5599DD");
        rightNodeList.createElement("Memory", "454 / 34213213", "#4CA8AF");

        NodeList leftNodeList = new NodeList(leftScrollPaneVbox.getChildren());

        leftNodeList.createElement("Operating System", "Linux\nDebian", "#CC44AA");
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

        /* ========== *
         * Show Stage *
         * ========== */
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}