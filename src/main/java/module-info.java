module belleisle.amiah.hardwarespecget {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.xml;
    requires com.github.oshi;
    requires com.fasterxml.jackson.databind;

    opens belleisle.amiah.hardwarespecget to javafx.fxml;
    exports belleisle.amiah.hardwarespecget;
}