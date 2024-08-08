package belleisle.amiah.hardwarespecget;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class NodeList {

    private ObservableList<Node> nodeList = null;
    private SimpleBooleanProperty isEditMode = new SimpleBooleanProperty(false);


    public NodeList(ObservableList<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void createElement(String title, String desc, String mainBackgroundColor) {

        /* ========================= *
         * Create Panes and Controls *
         * ========================= */

        // vbox is the root of the element
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Label description = new Label(desc);
        ImageView icon = new ImageView();
        Label name = new Label(title);
        HBox buttonBox = new HBox();
        Button moveUp = new Button("⏶");
        Button moveDown = new Button("⏷");
        ToggleButton toggleVisibility = new ToggleButton("On");
        // Properties and bindings
        SimpleBooleanProperty isHidden = new SimpleBooleanProperty(false);
        BooleanBinding isHiddenAndNotEditing = isHidden.and(isEditMode.not());

        /* ==================================== *
         * Set properties of controls and panes *
         * ==================================== */

        // Pane spacing and properties
        vbox.setSpacing(2);
        hbox.setSpacing(5);
        buttonBox.setSpacing(2);
        VBox.setMargin(vbox, new Insets(5));
        VBox.setMargin(hbox, new Insets(5));

        hbox.setPadding(new Insets(0, 0, 4, 0));
        // This is the main background
        vbox.setStyle("-fx-background-color: " + mainBackgroundColor + ";"
                    + "-fx-background-insets: -6 -4 -2 -4;"
                    + "-fx-background-radius: 5");
        // This is the background for the title, icon, buttons, etc
        hbox.setStyle("-fx-background-color: " + "#FF22CC" + ";"
                    + "-fx-background-insets: -6 -4 -2 -4;"
                    + "-fx-background-radius: 3.75;");
        // Vbox visibility bindings
        vbox.visibleProperty().bind(isHiddenAndNotEditing.not());
        vbox.managedProperty().bind(isHiddenAndNotEditing.not());
        // Toggle Button
        toggleVisibility.managedProperty().bind(isEditMode);
        toggleVisibility.visibleProperty().bind(isEditMode);
        toggleVisibility.setMinWidth(40);
        // Image Properties
        icon.setImage(new Image(NodeList.class.getResourceAsStream("app_icon.png")));
        icon.setPreserveRatio(true);
        icon.setFitHeight(30);
        icon.setFitWidth(30);
        // Name Label Properties
        name.setPrefHeight(30);
        name.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        // Description Properties
        description.setPadding(new Insets(0, 0, 6, 5));

        // Add to child Hbox
        buttonBox.getChildren().add(toggleVisibility);
        buttonBox.getChildren().add(moveDown);
        buttonBox.getChildren().add(moveUp);

        // Add to parent Hbox
        hbox.getChildren().add(icon);
        hbox.getChildren().add(name);
        hbox.getChildren().add(buttonBox);

        // Add to root Vbox
        vbox.getChildren().add(hbox);
        vbox.getChildren().add(description);


        /* ========= *
         * Listeners *
         * ========= */

        // Scale vbox to fit scroll pane width (minus 33 to account for scrollbar width)
        HardwareSpecApplication.rootStage.widthProperty().addListener(event -> {
            vbox.setPrefWidth((HardwareSpecApplication.rootStage.getWidth() / 2) - 33);
        });
        // Scale name to fit the space between the icon and the right buttons
        hbox.widthProperty().addListener(event -> {
            name.setPrefWidth(((HardwareSpecApplication.rootStage.getWidth() / 2) - 33)
                                - icon.getFitWidth()
                                - buttonBox.widthProperty().get());
        });
        // Change the text of the visibility toggle depending on state
        toggleVisibility.selectedProperty().addListener(event -> {
            if (toggleVisibility.selectedProperty().get()) {
                toggleVisibility.setText("Off");
                isHidden.set(true);
            }
            else {
                toggleVisibility.setText("On");
                isHidden.set(false);
            }
        });

        /* =============== *
         * Control Actions *
         * =============== */

        moveUp.setOnAction(event -> swapNodes(nodeList.indexOf(vbox), NodeSwapDirection.UP));
        moveDown.setOnAction(event -> swapNodes(nodeList.indexOf(vbox), NodeSwapDirection.DOWN));

        /* ===================== *
         * Add element to parent *
         * ===================== */

        nodeList.add(vbox);
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
                        && !nodeList.get(insertionIndex).managedProperty().get() && !getEditMode()) {

                    // If the node at the index isn't managed (is hidden), skip over it
                    if (!nodeList.get(insertionIndex).managedProperty().get() && !getEditMode()) {
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
                        && !nodeList.get(insertionIndex-1).managedProperty().get() && !getEditMode()) {

                    // If the node at the index isn't managed (is hidden), skip over it
                    if (!nodeList.get(insertionIndex-1).managedProperty().get() && !getEditMode()) {
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

    public void setEditMode(boolean b) {
        isEditMode.set(b);
    }

    public void toggleEditMode() {
        isEditMode.set(!isEditMode.get());
    }

    public boolean getEditMode() {
        return isEditMode.get();
    }

    public ObservableList<Node> getNodeList() {
        return nodeList;
    }

}
