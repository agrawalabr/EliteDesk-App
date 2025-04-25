module com.elitedesk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.elitedesk to javafx.fxml;

    exports com.elitedesk;
}