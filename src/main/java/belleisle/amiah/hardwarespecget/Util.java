package belleisle.amiah.hardwarespecget;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class Util {

    public static void fitImageToPane(ImageView img, double padding) {
        if (img.getParent() instanceof Pane pane) {
            // Get dimension that the image can fit in
            double smallestDimension = Math.min(pane.getWidth(), pane.getHeight());

            img.setScaleX(smallestDimension / (img.getFitWidth() + padding));
            img.setScaleY(smallestDimension / (img.getFitHeight() + padding));
        }
    }



}
