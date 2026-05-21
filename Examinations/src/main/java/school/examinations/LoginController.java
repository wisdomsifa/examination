package school.examinations;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class LoginController {
    @FXML private Button cancel;
    @FXML private Button login;
    @FXML private TextField user;
    @FXML private PasswordField pass;
    @FXML private Label error;

    // Regex breakdown:
    // (?=.*[A-Z]) -> At least one uppercase letter
    // (?=.*\d)    -> At least one digit
    // (?=.*[\W_]) -> At least one special character (non-word character or underscore)
    // .{1,}       -> At least 1 character long total
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{1,}$";
    @FXML
    public void handleRegister(ActionEvent event) throws IOException
    {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Register Page");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        user.clear();
        pass.clear();
        Connection conn = Connect.connection();
        if(conn!=null)
            System.out.println("Connection Successful");
        else
            System.out.println("Connection Failed");
    }
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = user.getText();
        String password = pass.getText();
        if(user.getText().equals("") && pass.getText().equals("")) {
            error.setText("Please fill all the fields");
            error.setVisible(true);
        }
    }
    public void initialize() {
        if(pass != null) {
            // Add a listener to validate the password in real-time as the user types
            pass.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    error.setText("Password is required");
                    error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else if (newValue.matches(PASSWORD_PATTERN)) {
                    error.setText("✔ Password is valid!");
                    error.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    error.setText("❌ Invalid password structure");
                    error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            });
        }
        else
            System.out.println("Error: 'pass' field was not injected!");
    }

}
