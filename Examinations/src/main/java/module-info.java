module school.examinations {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens school.examinations to javafx.fxml;
    exports school.examinations;
}