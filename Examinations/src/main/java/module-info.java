module school.examinations {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens school.examinations to javafx.fxml;
    exports school.examinations;
}
