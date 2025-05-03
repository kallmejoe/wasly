module com.example.foodorderingsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.foodorderingsystem to javafx.fxml;
    exports com.example.foodorderingsystem;

    opens com.example.foodorderingsystem.PresentationLayer to javafx.fxml;
    exports com.example.foodorderingsystem.PresentationLayer;

    exports com.example.foodorderingsystem.BusinessLayer;
    opens com.example.foodorderingsystem.BusinessLayer to javafx.fxml;
    exports com.example.foodorderingsystem.DataAccessLayer;
    opens com.example.foodorderingsystem.DataAccessLayer to javafx.fxml;
}
