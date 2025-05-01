module com.example.foodorderingsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.foodorderingsystem to javafx.fxml;
    exports com.example.foodorderingsystem;
}