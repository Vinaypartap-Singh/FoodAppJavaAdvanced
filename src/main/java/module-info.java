module com.example.foodappjavaadvanced {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.example.foodappjavaadvanced to javafx.fxml;
    exports com.example.foodappjavaadvanced;
}