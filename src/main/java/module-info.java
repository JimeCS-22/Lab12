module ucr.lab12 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ucr.lab12 to javafx.fxml;
    exports ucr.lab12;
    exports Controller;
    opens Controller to javafx.fxml;
}