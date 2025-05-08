module com.elitedesk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.elitedesk to javafx.fxml, javafx.graphics;
    opens com.elitedesk.service to com.fasterxml.jackson.databind, javafx.fxml;
    opens com.elitedesk.model to com.fasterxml.jackson.databind, javafx.fxml;
    opens com.elitedesk.config to javafx.fxml;

    exports com.elitedesk;
    exports com.elitedesk.service;
    exports com.elitedesk.model;
    exports com.elitedesk.config;
}