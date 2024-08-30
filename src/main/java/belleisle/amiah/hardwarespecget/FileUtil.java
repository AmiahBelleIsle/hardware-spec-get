package belleisle.amiah.hardwarespecget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class FileUtil {

    // Used to ensure that the user can only select supported image files
    private static final FileChooser.ExtensionFilter IMAGE_FILTER = new FileChooser.ExtensionFilter(
            "Image File (*.png, *.jpg, *jpeg, *.jpe, *.gif, *.bmp)",
            "*.png", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.bmp");

    /**
     * Opens a file dialogue and returns the file selected by the user.
     *
     * @param stage The JavaFX Stage to associate with the file select dialogue
     * @return An Optional containing a valid file or empty.
     */
    public static Optional<File> getImageFileFromUser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(HardwareSpecApplication.APP_TITLE + " - Select File");
        // Rarely, some systems will return ? for their home directory.
        // Use JavaFX default directory if home directory can't be found.
        if (!System.getProperty("user.home").equals("?")) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        // Apply image filter so user can only select supported image files
        fileChooser.getExtensionFilters().add(IMAGE_FILTER);

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return Optional.of(file);
        }
        return Optional.empty();
    }

    /**
     * Gets an image file and returns it as an {@link Image} object.
     *
     * @param fileName Name of image file in the resource folder
     * @return An {@link Image} with the specified resource, or an empty image
     *         if it couldn't find the resource.
     */
    public static Image getImage(String fileName) {
        // Get the resource to display
        InputStream is = FileUtil.class.getResourceAsStream(fileName);
        // If it can't be found return an image with nothing
        if (is == null) {
            return new Image("");
        }
        // Else return the image with the resource
        return new Image(is);
    }

    /**
     * Gets the specified save file. Will attempt to create the file if it
     * doesn't exist.
     *
     * @param fileName The name of the file to get
     * @return An {@link Optional Optional} containing a {@link File File}, or an empty
     *         if it is unable to get or create the file.
     */
    public static Optional<File> getSaveFile(String fileName) {
        // Save data path will be null if it doesn't exist and it can't create it.
        String root = getSaveDataPath();
        if (root == null) {
            return Optional.empty();
        }
        File file = new File(root + fileName);
        // Try to create file if it doesn't exist
        if (ensureFileExists(file)) {
            return Optional.of(file);
        }
        return Optional.empty();
    }

    /**
     * Gets the path to the save data directory. Does not
     * verify if it exists.
     *
     * <p>This method is intended to be used with JLink only.
     * Do not use this method otherwise.</p>
     *
     * @return The path to save data directory
     */
    private static String getSaveDataPath() {
        // When JLinked, this will return the location of root dir the binary is in. E.g., if app
        // binary is at /home/$USER/Desktop/app/bin/app, the below will return /home/$USER/Desktop/app
        String root = System.getProperty("java.home");
        // Add file separator if needed
        if (!root.endsWith(File.separator)) {
            root += File.separator;
        }
        return root + "save-data" + File.separator;
    }

    /**
     * Verifies if a file and directories exists. If it doesn't exist, this method
     * will attempt to create it. It will produce an {@code error} {@link Alert Alert}
     * if it fails to do so.
     *
     * @param file The file to verify exists
     * @return {@code true} if file exists, and was able to create it if it didn't exist,
     *         or {@code false} if it was unable to access the file or create it.
     */
    private static boolean ensureFileExists(File file) {
        try {
            // If file or dir doesn't exist, try creating it
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        }
        catch (IOException | SecurityException e) {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to create the following directories and/or file: " + file.getAbsolutePath())
                    .build()
                    .showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Saves the provided imageURL into the file "savedata/image.json"
     *
     * @param image A JavaFX Image object
     * @return true if able to write to file, false otherwise
     */
    public static boolean saveImage(Image image) {
        // Don't save the image if it is the default image
        if (image.equals(HardwareSpecApplication.DEFAULT_ICON)) {
            return false;
        }

        // Getting the file
        Optional<File> jsonFileOptional = getSaveFile("image.json");
        File jsonFile;
        if (jsonFileOptional.isPresent()) {
            jsonFile = jsonFileOptional.get();
        }
        else {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to find the save file.")
                    .build()
                    .showAndWait();
            return false;
        }

        // Create JSON and write it to file
        try (BufferedWriter file = new BufferedWriter(new FileWriter(jsonFile))) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("image", image.getUrl());

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);

            file.write(json);
            file.flush();
        }
        catch (IOException e) {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to write the set icon to the save file.")
                    .build()
                    .showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Reads from the file "savedata/image.json", and loads the
     * stored image into an imageView
     *
     * @param imgView An imageView to load the image into
     * @return true if able to read from file, false otherwise
     */
    public static boolean loadImage(ImageView imgView) {
        // Get the file to load from
        // Getting the file
        Optional<File> jsonFileOptional = getSaveFile("image.json");
        File jsonFile;
        if (jsonFileOptional.isPresent()) {
            jsonFile = jsonFileOptional.get();
        }
        else {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to find the save file.")
                    .build()
                    .showAndWait();
            return false;
        }

        try (BufferedReader file = new BufferedReader(new FileReader(jsonFile))) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(file);
            // If file doesn't contain field, don't try to load
            if (node.get("image") != null) {
                imgView.setImage(new Image(node.get("image").asText()));
                // Set default cursor when loading image (because only the default image can be clicked on)
                imgView.setCursor(Cursor.DEFAULT);
            }
        }
        catch (IOException e) {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to load the save file to get the saved icon.")
                    .build()
                    .showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Converts the left and right lists into JSON format and saves
     * them in "/savedata/entries.json"
     *
     * @param left The left list
     * @param right The right list
     * @return true if able to write to file, false otherwise
     */
    public static boolean saveNodeLists(NodeList left, NodeList right) {
        // Get the file to write to
        // Getting the file
        Optional<File> jsonFileOptional = getSaveFile("entries.json");
        File jsonFile;
        if (jsonFileOptional.isPresent()) {
            jsonFile = jsonFileOptional.get();
        }
        else {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to find the save file.")
                    .build()
                    .showAndWait();
            return false;
        }

        // Create JSON and write it to file
        try (BufferedWriter file = new BufferedWriter(new FileWriter(jsonFile))) {
            // Create JSON objects and arrays
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode leftListNode = mapper.createArrayNode();
            ArrayNode rightListNode = mapper.createArrayNode();
            rootNode.set("left-list", leftListNode);
            rootNode.set("right-list", rightListNode);
            // Fill the arrays
            for (Node n : left.getNodeList()) {
                // Put data all nodes will have
                ObjectNode tempNode = mapper.createObjectNode()
                        .put("type", ((NodeInfo) n.getUserData()).getType().getValueAsString())
                        .put("index",((NodeInfo) n.getUserData()).getIndex())
                        .put("shown", ((NodeInfo) n.getUserData()).getIsShown())
                        .put("background-color", ((NodeInfo) n.getUserData()).getMainColor());
                // Save userdata if the node has userdata
                if ( ((NodeInfo) n.getUserData()).getType() == NodeInfo.HardwareType.USERDATA) {
                    tempNode.put("user-title", ((NodeInfo) n.getUserData()).getUserTitle());
                    tempNode.put("user-content", ((NodeInfo) n.getUserData()).getUserContent());
                }
                // Add to the right list array
                leftListNode.add(tempNode);
            }
            for (Node n : right.getNodeList()) {
                // Put data all nodes will have
                ObjectNode tempNode = mapper.createObjectNode()
                        .put("type", ((NodeInfo) n.getUserData()).getType().getValueAsString())
                        .put("index",((NodeInfo) n.getUserData()).getIndex())
                        .put("shown", ((NodeInfo) n.getUserData()).getIsShown())
                        .put("background-color", ((NodeInfo) n.getUserData()).getMainColor());
                // Save userdata if the node has userdata
                if ( ((NodeInfo) n.getUserData()).getType() == NodeInfo.HardwareType.USERDATA) {
                    tempNode.put("user-title", ((NodeInfo) n.getUserData()).getUserTitle());
                    tempNode.put("user-content", ((NodeInfo) n.getUserData()).getUserContent());
                }
                // Add to the right list array
                rightListNode.add(tempNode);
            }

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            file.write(json);
            file.flush();
        }
        catch (IOException e) {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to write the hardware information to the save file.")
                    .build()
                    .showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Loads Json data and creates NodeInfo objects from them,
     * then puts them in a NodeList
     *
     * @param left The left list to load into
     * @param right The right list to load into
     * @return true if able to load from file, false otherwise
     */
    public static boolean loadNodeLists(NodeList left, NodeList right) {
        // Get file to load from
        // Getting the file
        Optional<File> jsonFileOptional = getSaveFile("entries.json");
        File jsonFile;
        if (jsonFileOptional.isPresent()) {
            jsonFile = jsonFileOptional.get();
        }
        else {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to find the save file.")
                    .build()
                    .showAndWait();
            return false;
        }

        try (BufferedReader file = new BufferedReader(new FileReader(jsonFile))) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node  = mapper.readTree(file);

            // Only load nodes if present and of proper type
            if ((node.get("left-list") != null) && (node.get("right-list") != null)
                    && (node.get("left-list").getNodeType() == JsonNodeType.ARRAY)
                    && (node.get("right-list").getNodeType() == JsonNodeType.ARRAY)) {
                // Load in the left list
                for (JsonNode n : node.get("left-list")) {
                    NodeInfo newNode = new NodeInfo(
                            NodeInfo.HardwareType.stringToValue(n.get("type").asText()),
                            n.get("index").asInt(),
                            n.get("shown").asBoolean());
                    // Set user data if the node contains user data
                    if (newNode.getType() == NodeInfo.HardwareType.USERDATA) {
                        newNode.setUserTitle(n.get("user-title").asText());
                        newNode.setUserTitle(n.get("user-content").asText());
                    }
                    // Add the node to the list
                    left.createElementInList(newNode);
                }
                // Load in the right list
                for (JsonNode n : node.get("right-list")) {
                    NodeInfo newNode = new NodeInfo(
                            NodeInfo.HardwareType.stringToValue(n.get("type").asText()),
                            n.get("index").asInt(),
                            n.get("shown").asBoolean());
                    // Set user data if the node contains user data
                    if (newNode.getType() == NodeInfo.HardwareType.USERDATA) {
                        newNode.setUserTitle(n.get("user-title").asText());
                        newNode.setUserTitle(n.get("user-content").asText());
                    }
                    // Add the node to the list
                    right.createElementInList(newNode);
                }
            }
            else {
                // Failed to load nodes
                return false;
            }

        }
        catch (IOException e) {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to load the save file to get the saved entries.")
                    .build()
                    .showAndWait();
            return false;
        }
        return true;
    }

}
