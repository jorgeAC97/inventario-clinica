module com.clinica {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens com.clinica to javafx.fxml;
    opens com.clinica.Controladores to javafx.fxml;
    exports com.clinica;
    exports com.clinica.Controladores;
}
