package belleisle.amiah.hardwarespecget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Optional;

public abstract class FileUtil {

    // Used to ensure that the user can only select supported image files
    private static final FileChooser.ExtensionFilter IMAGE_FILTER = new FileChooser.ExtensionFilter(
            "Image File (*.png, *.jpg, *jpeg, *.jpe, *.gif, *.bmp)",
            "*.png", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.bmp");

    /**
     * Gets a file from the resources folder and creates it if it doesn't exist.
     *
     * @param fileName The file path starting in the resources folder
     * @return An optional containing a valid file or empty.
     */
    public static Optional<File> getResourceFile(String fileName, boolean makeFile) {
        // Get path to resources
        URL url = FileUtil.class.getResource("");
        File file;

        // Should never be null, but check just in case
        if (url != null && url.getFile() != null) {
            String path = url.getFile();
            // Insert a slash if needed
            if (!fileName.startsWith("/")) {
                file = new File(path + "/" + fileName);
            }
            else {
                file = new File(path + fileName);
            }

            // Check if file exists and create it if not
            try {
                if (!file.exists() && makeFile) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // Don't make the file if not wanted
                else if (!file.exists() && !makeFile) {
                    return Optional.empty();
                }
            }
            catch (IOException e) {
                AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                        .setWindowTitle("Error")
                        .setHeaderText("An Error has Occurred")
                        .setMessage("A file or directory didn't exist and was unable to be created.")
                        .build()
                        .showAndWait();
                return Optional.empty();
            }
            catch (SecurityException e) {
                AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                        .setWindowTitle("Error")
                        .setHeaderText("An Error has Occurred")
                        .setMessage("A security manager is preventing the creation of the file \"" + fileName + "\".")
                        .build()
                        .showAndWait();
                return  Optional.empty();
            }
        }
        else {
            // Returning empty if resources directory can't be found for some reason
            return Optional.empty();
        }
        return Optional.of(file);
    }

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
     * Saves the provided imageURL into the file "savedata/image.json"
     *
     * @param imageURL The image url returned from calling toURL() on a JavaFX image
     * @return true if able to write to file, false otherwise
     */
    public static boolean saveImage(String imageURL) {
        // Get the file to write to
        Optional<File> fileOptional = getResourceFile("/savedata/image.json", true);
        File jsonFile;

        if (fileOptional.isPresent()) {
            jsonFile = fileOptional.get();
        }
        else {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to locate the save file and save the set icon.")
                    .build()
                    .showAndWait();
            return false;
        }

        // Create JSON and write it to file
        try (BufferedWriter file = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("image", imageURL);

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
        // Get the file path to load from
        Optional<File> fileOptional = getResourceFile("/savedata/image.json", true);
        File jsonFile;

        if (fileOptional.isPresent()) {
            jsonFile = fileOptional.get();
        }
        else {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to access the save file to load the saved icon.")
                    .build()
                    .showAndWait();
            return false;
        }

        try (BufferedReader file = new BufferedReader(new FileReader(jsonFile))) {
            // Read the JSON file in as a string
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = file.readLine()) != null) {
                sb.append(line);
            }
            // Don't load image if file is empty
            if (!sb.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(sb.toString());
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

    public static boolean saveNodeLists(NodeList left, NodeList right) {
        // Get the file to write to
        File jsonFile;
        try {
            jsonFile = getResourceFile("/savedata/entries.json", true).orElseThrow();
        }
        catch (Exception e) {
            AlertBuilder.makeBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to locate the save file and save the hardware information.")
                    .build()
                    .showAndWait();
            return false;
        }
        // Create JSON and write it to file
        try (BufferedWriter file = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
            // Create JSON objects and arrays
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode leftListNode = mapper.createArrayNode();
            ArrayNode rightListNode = mapper.createArrayNode();
            rootNode.set("left-list", leftListNode);
            rootNode.set("right-list", rightListNode);
            // Fill the arrays
            for (Node n : left.getNodeList()) {
                leftListNode.add(
                        mapper.createObjectNode()
                        .put("name", ((Label) n.lookup("#name")).getText())
                        .put("description",((Label) n.lookup("#description")).getText())
                        .put("icon", ((ImageView) n.lookup("#icon")).getImage().getUrl())
                        .put("invisible", ((ToggleButton) n.lookup("#toggleVisibility")).getToggleGroup().getToggles().getFirst().isSelected())
                        .put("main-color", n.getUserData().toString())
                );
            }
            for (Node n : right.getNodeList()) {
                rightListNode.add(
                        mapper.createObjectNode()
                        .put("name", ((Label) n.lookup("#name")).getText())
                        .put("description",((Label) n.lookup("#description")).getText())
                        .put("icon", ((ImageView) n.lookup("#icon")).getImage().getUrl())
                        .put("invisible", ((ToggleButton) n.lookup("#toggleVisibility")).getToggleGroup().getToggles().getFirst().isSelected())
                        .put("main-color", n.getUserData().toString())
                );
            }

            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            System.out.println(jsonString);
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


}
