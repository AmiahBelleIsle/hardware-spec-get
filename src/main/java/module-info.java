module belleisle.amiah.hardwarespecget {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.xml;
    requires com.github.oshi;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens belleisle.amiah.hardwarespecget to javafx.fxml;
    exports belleisle.amiah.hardwarespecget;
}