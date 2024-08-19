package belleisle.amiah.hardwarespecget;

import javafx.scene.control.Alert;

public class AlertBuilder {

    private Alert.AlertType type;
    private String windowTitle;
    private String messageText;
    private String headerText;

    public AlertBuilder(Alert.AlertType type) {
        this.type = type;
    }

    public AlertBuilder setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
        return this;
    }

    public AlertBuilder setHeaderText(String headerText) {
        this.headerText = headerText;
        return this;
    }

    public AlertBuilder setMessage(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public Alert build() {
        Alert alert = new Alert(type);

        if (windowTitle != null) {
            alert.setTitle(windowTitle);
        }
        if (messageText != null) {
            alert.setContentText(messageText);
        }
        if (headerText != null) {
            alert.setHeaderText(headerText);
        }

        return alert;
    }

}
