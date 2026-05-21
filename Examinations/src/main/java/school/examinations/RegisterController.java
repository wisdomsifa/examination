package school.examinations;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class RegisterController {
    @FXML private TextField email;
    @FXML private Label emailError;
    // RFC 5322 official standard regex for email validation
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public void initialize(){
        // Add listener for real-time validation as the user types
        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                emailError.setText("");
                emailError.setStyle(""); // Reset to default style
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
    /**
     * Validates the input string against the email regex pattern.
     */
    private boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
