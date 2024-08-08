module belleisle.amiah.hardwarespecget {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.xml;

    opens belleisle.amiah.hardwarespecget to javafx.fxml;
    exports belleisle.amiah.hardwarespecget;
}