module com.mycompany.myeditor {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.myeditor to javafx.fxml;
    exports com.mycompany.myeditor;
}
