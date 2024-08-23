package belleisle.amiah.hardwarespecget;

import javafx.scene.control.Alert;

public class AlertBuilder {

    private Alert.AlertType type;
    private String windowTitle;
    private String messageText;
    private String headerText;

    // Used by makeBuilder
    private AlertBuilder(Alert.AlertType type) {
        this.type = type;
    }

    /**
     * Creates a new AlertBuilder instance with the specified alert type.
     *
     * @param type The alert type
     * @return A new AlertBuilder instance
     * @see Alert
     * @see javafx.scene.control.Alert.AlertType
     */
    public static AlertBuilder makeBuilder(Alert.AlertType type) {
        return new AlertBuilder(type);
    }

    /**
     * Sets the text of the title bar. If unset its default value is "Message".
     *
     * @param windowTitle The text to be displayed on the title bar
     * @return this
     * @see Alert
     */
    public AlertBuilder setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
        return this;
    }

    /**
     * Sets the heading text. This is the large text above the message.
     * If unset its default value is "Message".
     *
     * @param headerText The text to be displayed as the header
     * @return this
     * @see Alert
     */
    public AlertBuilder setHeaderText(String headerText) {
        this.headerText = headerText;
        return this;
    }

    /**
     * Sets the message text. This is the small text under the heading.
     * If unset its default value is an empty string.
     *
     * @param messageText The text to be displayed as the message
     * @return this
     * @see Alert
     */
    public AlertBuilder setMessage(String messageText) {
        this.messageText = messageText;
        return this;
    }

    /**
     * Constructs and returns an Alert. This method does not
     * show the Alert it returns.
     *
     * @return Alert
     * @see Alert
     */
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
