package belleisle.amiah.hardwarespecget;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;


public class NodeList {

    private ObservableList<Node> nodeList = null;


    public NodeList(ObservableList<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void createElementInList(NodeInfo info) {

        /* ========================= *
         * Create Panes and Controls *
         * ========================= */

        Label titleLabel = new Label(info.getTitle());
        Label contentLabel = new Label(info.getContent());
        ImageView icon = new ImageView();
        Button moveUpButton = new Button("⏶");
        Button moveDownButton = new Button("⏷");
        ToggleButton visibilityToggleButton = new ToggleButton("On");
        // If the node is off, make the toggle button be off as well
        if (!info.getIsShown()) {
            visibilityToggleButton.setText("Off");
            visibilityToggleButton.setSelected(true);
        }

        HBox buttonBox = new HBox(visibilityToggleButton, moveDownButton, moveUpButton);
        HBox titleBox = new HBox(icon, titleLabel, buttonBox);
        VBox rootNode = new VBox(titleBox, contentLabel);

        rootNode.setUserData(info);

        // Properties and bindings
        BooleanBinding isHiddenAndNotEditing = info.getIsShownProperty().not().and(HardwareSpecApplication.IS_EDIT_MODE.not());

        /* ==================================== *
         * Set properties of controls and panes *
         * ==================================== */

        // Pane spacing and properties
        rootNode.setSpacing(2);
        titleBox.setSpacing(5);
        buttonBox.setSpacing(2);
        VBox.setMargin(rootNode, new Insets(5));
        VBox.setMargin(titleBox, new Insets(5));
        titleBox.setPadding(new Insets(0, 0, 4, 0));

        // Title properties
        titleLabel.setPrefHeight(30);
        titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        // Content Properties
        contentLabel.setPadding(new Insets(0, 0, 6, 5));
        // Image Properties
        icon.setImage(new Image(
                FileUtil.getResourceFile("app_icon.png", false)
                        .orElse(new File(HardwareSpecApplication.APP_ICON_URI.getPath())).toURI().toString()
        ));
        icon.setPreserveRatio(true);
        icon.setFitHeight(30);
        icon.setFitWidth(30);
        // Toggle button properties
        visibilityToggleButton.setMinWidth(40);

        // Setting control ids (used to lookup nodes to save their values)
        titleLabel.setId("title");
        contentLabel.setId("content");
        icon.setId("icon");
        visibilityToggleButton.setId("visibility-toggle");

        /* ======== *
         * Bindings *
         * ======== */

        rootNode.visibleProperty().bind(isHiddenAndNotEditing.not());
        rootNode.managedProperty().bind(isHiddenAndNotEditing.not());
        visibilityToggleButton.managedProperty().bind(HardwareSpecApplication.IS_EDIT_MODE);
        visibilityToggleButton.visibleProperty().bind(HardwareSpecApplication.IS_EDIT_MODE);

        /* ============================= *
         * Listeners and Control Actions *
         * ============================= */

        // Scale vbox to fit scroll pane width (minus 33 to account for scrollbar width)
        HardwareSpecApplication.rootStage.widthProperty().addListener(event -> {
            rootNode.setPrefWidth((HardwareSpecApplication.rootStage.getWidth() / 2) - 33);
        });
        // Scale name to fit the space between the icon and the right buttons
        titleBox.widthProperty().addListener(event -> {
            titleLabel.setPrefWidth(((HardwareSpecApplication.rootStage.getWidth() / 2) - 33)
                                - icon.getFitWidth()
                                - buttonBox.widthProperty().get());
        });
        // Change the text of the visibility toggle depending on state
        visibilityToggleButton.selectedProperty().addListener(event -> {
            if (visibilityToggleButton.selectedProperty().get()) {
                visibilityToggleButton.setText("Off");
                info.setIsShown(false);
            }
            else {
                visibilityToggleButton.setText("On");
                info.setIsShown(true);
            }
        });

        moveUpButton.setOnAction(event -> swapNodes(nodeList.indexOf(rootNode), NodeSwapDirection.UP));
        moveDownButton.setOnAction(event -> swapNodes(nodeList.indexOf(rootNode), NodeSwapDirection.DOWN));

        /* ======= *
         * Set CSS *
         * ======= */

        rootNode.setStyle("-fx-background-color: " + info.getMainColor() + ";"
                + "-fx-background-insets: -6 -4 -2 -4;"
                + "-fx-background-radius: 5");

        titleBox.setStyle("-fx-background-color: " + "#8b8e8f" + ";"
                + "-fx-background-insets: -6 -4 -2 -4;"
                + "-fx-background-radius: 3.75;");

        /* ===================== *
         * Add element to parent *
         * ===================== */

        nodeList.add(rootNode);
    }

    // Used for the swapNodes method
    private enum NodeSwapDirection {
        UP,
        DOWN
    }

    // Used for the swapNodes method
    private boolean isNodeInBounds(int i) {
        return i >= 0 && i <= nodeList.size();
    }

    private void swapNodes(int i, NodeSwapDirection dir){
        // Ensure both nodes are in bounds
        if (isNodeInBounds(i)) {
            // Remove and store node from list (list doesn't allow duplicates so this is necessary)
            Node iTemp = nodeList.remove(i);

            // Get the proper index to put the node in
            int insertionIndex = 0;

            if (dir == NodeSwapDirection.UP) {
                insertionIndex = i - 1;
                // Loop around to the end of the list if out of bounds
                if (insertionIndex < 0) {
                    insertionIndex = nodeList.size();
                }
                // While insertion index hasn't looped back to i,
                // and the insertion index isn't at the list end (no node is at that index)
                // and the node at insertion index is invalid
                // and edit mode is not enabled
                while (insertionIndex != i && insertionIndex != nodeList.size()
                        && !nodeList.get(insertionIndex).managedProperty().get() && !HardwareSpecApplication.IS_EDIT_MODE.get()) {

                    // If the node at the index isn't managed (is hidden), skip over it
                    if (!nodeList.get(insertionIndex).managedProperty().get() && !HardwareSpecApplication.IS_EDIT_MODE.get()) {
                        insertionIndex--;
                        if (insertionIndex < 0) {
                            insertionIndex = nodeList.size();
                        }
                    }
                    // Exit while loop if the node is valid
                    else {
                        break;
                    }
                }
            }
            else if (dir == NodeSwapDirection.DOWN) {
                insertionIndex = i + 1;
                // Loop around to the end of the list if out of bounds
                if (insertionIndex > nodeList.size()) {
                    insertionIndex = 0;
                }
                // While insertion index hasn't looped back to i,
                // and the insertion index isn't at the beginning (no node is at -1)
                // and the node at insertion index minus 1 is unmanaged (list is shifted left, due to above removal)
                // and edit mode is not enabled
                while (insertionIndex != i && insertionIndex != 0
                        && !nodeList.get(insertionIndex-1).managedProperty().get() && !HardwareSpecApplication.IS_EDIT_MODE.get()) {

                    // If the node at the index isn't managed (is hidden), skip over it
                    if (!nodeList.get(insertionIndex-1).managedProperty().get() && !HardwareSpecApplication.IS_EDIT_MODE.get()) {
                        insertionIndex++;
                        if (insertionIndex > nodeList.size()) {
                            insertionIndex = 0;
                        }
                    }
                    // Exit while loop if the node is valid
                    else {
                        break;
                    }
                }
            }
            // Finally, add the node to the list
            nodeList.add(insertionIndex, iTemp);
        }
    }

    public ObservableList<Node> getNodeList() {
        return nodeList;
    }

}
