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
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField fname;
    @FXML private TextField lname;
    @FXML private TextField email;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField confirm;
    @FXML private Button register;
    @FXML private Button cancel;
    @FXML private Label emailError;
    @FXML private Label statusMessage;

    // RFC 5322 simplified email regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    // Same rules used on the login screen:
    //   At least one uppercase, one digit, one special char.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{1,}$");

    public void initialize() {
        // Live email validation as the user types
        if (email != null && emailError != null) {
            email.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    emailError.setText("");
                    emailError.setStyle("");
                    email.setStyle("");
                } else if (validateEmail(newValue)) {
                    emailError.setText("Valid email address!");
                    emailError.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
                    email.setStyle("-fx-border-color: green; -fx-border-width: 1px;");
                } else {
                    emailError.setText("Invalid email format.");
                    emailError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
                    email.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                }
            });
        }
    }

    /**
     * Cancel button -> go back to the Login screen.
     */
    @FXML
    public void handleCancel(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        stage.setTitle("Login Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Register button -> validate then INSERT into MySQL.
     */
    @FXML
    public void handleRegister(ActionEvent event) {
        String firstName = safe(fname.getText());
        String lastName  = safe(lname.getText());
        String mail      = safe(email.getText());
        String user      = safe(username.getText());
        String pass      = password.getText();
        String confirmPw = confirm.getText();

        // ---- Client-side validation ---------------------------------------
        if (firstName.isEmpty() || lastName.isEmpty() || mail.isEmpty()
                || user.isEmpty() || pass.isEmpty() || confirmPw.isEmpty()) {
            showStatus("Please fill in all the fields.", false);
            return;
        }
        if (!validateEmail(mail)) {
            showStatus("Please enter a valid email address.", false);
            return;
        }
        if (!PASSWORD_PATTERN.matcher(pass).matches()) {
            showStatus("Password must contain an uppercase letter, a digit and a special character.", false);
            return;
        }
        if (!pass.equals(confirmPw)) {
            showStatus("Passwords do not match.", false);
            return;
        }

        // ---- Persist to MySQL ---------------------------------------------
        String sql = "INSERT INTO users (first_name, last_name, email, username, password) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Connect.connection()) {
            if (conn == null) {
                showStatus("Could not connect to the database. Check your settings.", false);
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, mail);
                ps.setString(4, user);
                ps.setString(5, pass);

                int rows = ps.executeUpdate();
                if (rows == 1) {
                    showStatus("Registration successful! Redirecting to login...", true);
                    clearForm();
                    // Small delay so the user can read the message
                    new Thread(() -> {
                        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                        javafx.application.Platform.runLater(() -> {
                            try { goToLogin(event); } catch (IOException ex) { ex.printStackTrace(); }
                        });
                    }).start();
                } else {
                    showStatus("Registration failed. Please try again.", false);
                }
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            // Triggered by the UNIQUE constraint on email / username
            String msg = dup.getMessage() != null ? dup.getMessage().toLowerCase() : "";
            if (msg.contains("email")) {
                showStatus("That email is already registered.", false);
            } else if (msg.contains("username")) {
                showStatus("That username is already taken.", false);
            } else {
                showStatus("That account already exists.", false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showStatus("Database error: " + e.getMessage(), false);
        }
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------
    private boolean validateEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }

    private void showStatus(String text, boolean success) {
        if (statusMessage == null) return;
        statusMessage.setText(text);
        statusMessage.setStyle(success
                ? "-fx-text-fill: green; -fx-font-weight: bold;"
                : "-fx-text-fill: red;   -fx-font-weight: bold;");
    }

    private void clearForm() {
        fname.clear();
        lname.clear();
        email.clear();
        username.clear();
        password.clear();
        confirm.clear();
    }

    private void goToLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        stage.setTitle("Login Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
