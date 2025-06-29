module hr.tvz.java.freelance.freelancemanagementtool {
    // We declare that our module needs these other modules to function.
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;

    // This is the FIX:
    // The automatic module name for the BCrypt library is 'bcrypt'.
    requires bcrypt;

    // The 'opens' keyword allows the 'javafx.fxml' module to use reflection
    // to access our controller classes at runtime.
    opens hr.tvz.java.freelance.freelancemanagementtool.controller to javafx.fxml;

    // We also need to export our main package so JavaFX can launch the application.
    exports hr.tvz.java.freelance.freelancemanagementtool;
    // Exporting our model is good practice in case other views need them directly.
    exports hr.tvz.java.freelance.freelancemanagementtool.model;
    exports hr.tvz.java.freelance.freelancemanagementtool.enums;
}