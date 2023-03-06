package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {
    @FXML
    private Label welcomeText;

    @FXML
    private Button playButton;

    @FXML
    protected void onButtonClick() {
        welcomeText.setText("Welcome to Asteroids!");
        playButton.setVisible(false);
    }
}