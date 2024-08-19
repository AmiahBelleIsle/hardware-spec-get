package belleisle.amiah.hardwarespecget;

import javafx.scene.control.Alert;

public class AlertBuilder {

    private Alert.AlertType type;
    private String title;
    private String msg;

    public AlertBuilder(Alert.AlertType type) {
        this.type = type;
    }

    public AlertBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertBuilder setMessage(String msg) {
        this.msg = msg;
        return this;
    }

    public Alert build() {
        Alert alert = new Alert(type);

        if (title != null) {
            alert.setTitle(title);
        }
        if (msg != null) {
            alert.setContentText(msg);
        }

        return alert;
    }

}
