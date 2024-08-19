package belleisle.amiah.hardwarespecget;

import com.fasterxml.jackson.jr.ob.JSON;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public abstract class Util {

    private static final FileChooser.ExtensionFilter IMAGE_FILTER = new FileChooser.ExtensionFilter(
            "Image File (*.png, *.jpg, *jpeg, *.jpe, *.gif, *.bmp)",
            "*.png", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.bmp");


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

    // Takes in a string and removes redundant information
    public static String beautifyName(String name) {
        int searchIndex = 0;
        while (searchIndex != -1) {
            searchIndex = name.indexOf('(', searchIndex);

            if (name.startsWith("(R)", searchIndex)) {
                name = name.substring(0, searchIndex) + name.substring(searchIndex + 3);
                searchIndex++;
            }
            else if (name.startsWith("(TM)", searchIndex)) {
                name = name.substring(0, searchIndex) + name.substring(searchIndex + 4);
                searchIndex++;
            }
        }

        return name;
    }

    public static String deepExtractFromBrackets(String name) {
        int beginIndex = 0;
        int endIndex = name.length();

        beginIndex = name.indexOf('[', beginIndex);
        endIndex = name.lastIndexOf(']', endIndex);

        if ((beginIndex != -1 && endIndex != -1) && beginIndex < endIndex) {
            name = name.substring(beginIndex + 1, endIndex);
            return deepExtractFromBrackets(name);
        }
        else {
            return name;
        }
    }

    /**
     * Gets a file from the resources folder and creates it if it doesn't exist.
     *
     * @param fileName The file path starting in the resources folder
     * @return An optional containing a valid file or empty.
     * @exception IOException If the resource folder cannot be found
     */
    private static Optional<File> getResourceFile(String fileName) throws IOException {
        // Get path to resources
        URL url = Util.class.getResource("");
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
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }
            catch (IOException e) {
                new AlertBuilder(Alert.AlertType.ERROR)
                        .setWindowTitle("Error")
                        .setHeaderText("An Error has Occurred")
                        .setMessage("A file or directory didn't exist and was unable to be created.")
                        .build()
                        .showAndWait();
                return Optional.empty();
            }
            catch (SecurityException e) {
                new AlertBuilder(Alert.AlertType.ERROR)
                        .setWindowTitle("Error")
                        .setHeaderText("An Error has Occurred")
                        .setMessage("A security manager is preventing the creation of the file \"" + fileName + "\".")
                        .build()
                        .showAndWait();
                return  Optional.empty();
            }
        }
        else {
            throw new IOException("Unable to find resources folder");
        }
        return Optional.of(file);
    }

    /**
     * Saves the provided imageURL into the file "savedata/image.json"
     *
     * @param imageURL The image url returned from calling toURL() on a JavaFX image
     * @return true if able to write to file, false otherwise
     */
    public static boolean saveImage(String imageURL) {
        // Get the file to write to
        File jsonFile;
        try {
            jsonFile = getResourceFile("/savedata/image.json").orElseThrow();
        }
        catch (Exception e) {
            new AlertBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to locate the save file.")
                    .build()
                    .showAndWait();
            return false;
        }
        // Create JSON and write it to file
        try (BufferedWriter file = new BufferedWriter(new FileWriter(jsonFile.getPath()))) {
            String json = JSON.std
                    .with(JSON.Feature.PRETTY_PRINT_OUTPUT)
                    .composeString()
                    .startObject()
                    .put("image", imageURL)
                    .end()
                    .finish();
            file.write(json);
            file.flush();
        }
        catch (IOException e) {
            new AlertBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to write to the save file.")
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
        File jsonFile;
        try {
            jsonFile = getResourceFile("/savedata/image.json").orElseThrow();
        }
        catch (Exception e) {
            new AlertBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to access the save file.")
                    .build()
                    .showAndWait();
            return false;
        }
        // Read the JSON file in as a string
        try (BufferedReader file = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = file.readLine()) != null) {
                sb.append(line);
            }
            // Convert string to map to get image value
            Map<String,Object> map = JSON.std.mapFrom(sb.toString());
            imgView.setImage(new Image(String.valueOf(map.get("image"))));
            // Set default cursor when loading image (because only the default image can be clicked on)
            imgView.setCursor(Cursor.DEFAULT);
        }
        catch (IOException e) {
            new AlertBuilder(Alert.AlertType.ERROR)
                    .setWindowTitle("Error")
                    .setHeaderText("An Error has Occurred")
                    .setMessage("Unable to load the save file.")
                    .build()
                    .showAndWait();
            return false;
        }
        return true;
    }


}
