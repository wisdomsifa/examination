package school.examinations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Lightweight toast / pop-up message that auto-dismisses after a few seconds.
 *
 * Usage:
 *   Toast.success(ownerWindow, "Registration successful!");
 *   Toast.error  (ownerWindow, "Username already exists.");
 *   Toast.info   (ownerWindow, "Please fill all the fields.");
 */
public final class Toast {

    public enum Type {
        SUCCESS("#2ecc71"),
        ERROR  ("#e74c3c"),
        INFO   ("#3498db");

        final String color;
        Type(String c) { this.color = c; }
    }

    private static final double DEFAULT_SECONDS = 2.5;

    private Toast() {}

    public static void success(Window owner, String message) {
        show(owner, message, Type.SUCCESS, DEFAULT_SECONDS);
    }

    public static void error(Window owner, String message) {
        show(owner, message, Type.ERROR, DEFAULT_SECONDS);
    }

    public static void info(Window owner, String message) {
        show(owner, message, Type.INFO, DEFAULT_SECONDS);
    }

    public static void show(Window owner, String message, Type type, double seconds) {
        Platform.runLater(() -> {
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            if (owner != null) stage.initOwner(owner);

            Label label = new Label(message);
            label.setWrapText(true);
            label.setMaxWidth(360);
            label.setStyle(
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 20 12 20;" +
                    "-fx-background-color: " + type.color + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 10, 0.2, 0, 2);"
            );

            StackPane root = new StackPane(label);
            root.setStyle("-fx-background-color: transparent;");
            root.setAlignment(Pos.CENTER);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.show();

            // Position toast at the top-center of the owner window (or screen).
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2.0);
                stage.setY(owner.getY() + 40);
            }

            // Fade out, then close
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(seconds),
                            new KeyValue(stage.getScene().getRoot().opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(seconds + 0.6),
                            new KeyValue(stage.getScene().getRoot().opacityProperty(), 0.0))
            );
            timeline.setOnFinished(e -> stage.close());
            timeline.play();
        });
    }
}
