module com.elitedesk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.elitedesk to javafx.fxml;
    opens com.elitedesk.service to com.fasterxml.jackson.databind;
    opens com.elitedesk.model to com.fasterxml.jackson.databind;

    exports com.elitedesk;
}