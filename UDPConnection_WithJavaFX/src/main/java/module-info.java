module com.example.javafxudpconnection {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.javafxudpconnection to javafx.fxml;
    exports com.example.javafxudpconnection;
}