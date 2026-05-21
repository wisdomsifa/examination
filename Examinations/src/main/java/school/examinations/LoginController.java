package school.examinations;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private Button cancel;
    @FXML private Button login;
    @FXML private TextField user;
    @FXML private PasswordField pass;
    @FXML private Label error;

    // (?=.*[A-Z]) -> uppercase, (?=.*\d) -> digit, (?=.*[\W_]) -> special char
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{1,}$";

    @FXML
    public void handleRegister(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register Page");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Cancel button -> just clears the fields and tests the DB connection.
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        user.clear();
        pass.clear();
        try (Connection conn = Connect.connection()) {
            if (conn != null) {
                System.out.println("Connection Successful");
            } else {
                System.out.println("Connection Failed");
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
    }

    /**
     * Login button -> authenticate against the MySQL `users` table.
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = user.getText() == null ? "" : user.getText().trim();
        String password = pass.getText() == null ? "" : pass.getText();

        if (username.isEmpty() || password.isEmpty()) {
            error.setText("Please fill all the fields");
            error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            error.setVisible(true);
            return;
        }

        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = Connect.connection()) {
            if (conn == null) {
                error.setText("Could not connect to the database.");
                error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                error.setVisible(true);
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (storedPassword.equals(password)) {
                            error.setText("Login successful!");
                            error.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            error.setVisible(true);
                            System.out.println("User '" + username + "' logged in.");
                        } else {
                            error.setText("Incorrect password.");
                            error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            error.setVisible(true);
                        }
                    } else {
                        error.setText("No account found for that username.");
                        error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        error.setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            error.setText("Database error: " + e.getMessage());
            error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            error.setVisible(true);
        }
    }

    public void initialize() {
        if (pass != null) {
            pass.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    error.setText("Password is required");
                    error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else if (newValue.matches(PASSWORD_PATTERN)) {
                    error.setText("Password is valid!");
                    error.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    error.setText("Invalid password structure");
                    error.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            });
        } else {
            System.out.println("Error: 'pass' field was not injected!");
        }
    }
}
