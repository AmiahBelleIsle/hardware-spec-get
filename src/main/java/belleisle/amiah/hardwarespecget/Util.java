package belleisle.amiah.hardwarespecget;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public abstract class Util {


    private static final FileChooser.ExtensionFilter IMAGE_FILTER = new FileChooser.ExtensionFilter(
            "Image File (*.png, *.jpg, *jpeg, *.jpe, *.gif, *.bmp)",
            "*.png", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.bmp");


    public static Optional<File> getImageFileFromUser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(HardwareSpecApplication.APP_TITLE + " - Select File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().add(IMAGE_FILTER);

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return Optional.of(file);
        }
        return Optional.empty();
    }

}
