package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.MenuButtonFeature;
import model.MenuSubScene;
import model.StartGameLabel;

public class MenuScene {

	private static final int HEIGHT = 768;
	private static final int WIDTH = 1024;
	private AnchorPane mainPane;
	private Scene mainScene;
	private Stage mainStage;

	private final static int MENU_BUTTON_LENGTH = 100;
	private final static int MENU_BUTTON_HEIGHT = 150;

	private final static String BACKGROUND_IMAGE = "/resources/deep_blue.png";
	private final static String LOGO = "/resources/Preview.gif";

	private MenuSubScene helpSubscene;
	private MenuSubScene scoreSubscene;
	private MenuSubScene playSubscene;

	private MenuSubScene sceneToHide;

	List<MenuButtonFeature> menuButtons;
	private MenuSubScene highScoreSubScene;
	private MenuSubScene rulesSubScene;
	private MenuSubScene controlSubscene;
	public String name;

	// Constructor
	public MenuScene() {
		menuButtons = new ArrayList<>();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane, WIDTH, HEIGHT);
		mainStage = new Stage();
		mainStage.setScene(mainScene);
		createSubScenes();
		CreateButtons();
		createBackground();
		createLogo();
		createLogo2();

	}

	private void showSubScene(MenuSubScene subScene) {
		if (sceneToHide != null) {
			sceneToHide.moveSubScene();
		}

		subScene.moveSubScene();
		sceneToHide = subScene;
	}

	private void createSubScenes() {

		helpSubscene = new MenuSubScene();
		mainPane.getChildren().add(helpSubscene);
		scoreSubscene = new MenuSubScene();
		mainPane.getChildren().add(scoreSubscene);
		rulesSubScene = new MenuSubScene();
		mainPane.getChildren().add(rulesSubScene);
		controlSubscene = new MenuSubScene();
		mainPane.getChildren().add(controlSubscene);

		createStartGameSubscene();
		createHighScoreSubScene();
		createRulesSubScene();
		createControlsSubScene();

	}

	public void createHighScoreSubScene() {
		highScoreSubScene = new MenuSubScene();
		mainPane.getChildren().add(highScoreSubScene);
		StartGameLabel startGameLabel = new StartGameLabel("CHAMPIONS");
		startGameLabel.setLayoutX(110);
		startGameLabel.setLayoutY(25);
		highScoreSubScene.getPane().getChildren().add(startGameLabel);
		highScoreSubScene.getPane().getChildren().add(createTextboxForScore());

	}

	public void createStartGameSubscene() {
		playSubscene = new MenuSubScene();
		mainPane.getChildren().add(playSubscene);

		StartGameLabel startGameLabel = new StartGameLabel("ENTER YOUR NAME");
		startGameLabel.setLayoutX(110);
		startGameLabel.setLayoutY(25);
		playSubscene.getPane().getChildren().add(startGameLabel);

		TextField nameTextField = createTextboxForName();
		playSubscene.getPane().getChildren().add(nameTextField);

		MenuButtonFeature startButton = new MenuButtonFeature("START");
		startButton.setLayoutX(350);
		startButton.setLayoutY(300);

		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				name = nameTextField.getText().trim();
				if (!name.isEmpty()) {
					GameController gameController = new GameController(name);
					gameController.startGame();
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Enter your Name!");

					alert.showAndWait();
				}
			}
		});
		playSubscene.getPane().getChildren().add(startButton);
	}

	private void createRulesSubScene() {
		rulesSubScene = new MenuSubScene();
		mainPane.getChildren().add(rulesSubScene);
		StartGameLabel startGameLabel = new StartGameLabel("RULES");
		startGameLabel.setLayoutX(110);
		startGameLabel.setLayoutY(25);
		rulesSubScene.getPane().getChildren().add(startGameLabel);
		rulesSubScene.getPane().getChildren().add(createTextboxForRules());

	}

	private void createControlsSubScene() {
		controlSubscene = new MenuSubScene();
		mainPane.getChildren().add(controlSubscene);
		StartGameLabel startGameLabel = new StartGameLabel("CONTROLS");
		startGameLabel.setLayoutX(110);
		startGameLabel.setLayoutY(25);
		controlSubscene.getPane().getChildren().add(startGameLabel);
		controlSubscene.getPane().getChildren().add(createTextboxForControls());

	}

	public HBox createTextboxForScore() {
		TextArea scoresTextArea = new TextArea();
		scoresTextArea.setEditable(false);
		scoresTextArea.setPrefSize(400, 250);
		scoresTextArea.setFont(Font.font("Cambria", FontWeight.BOLD, 18));
		try {
			File scoresFile = new File("src/resources/scores.txt");
			Scanner scanner = new Scanner(scoresFile);
			while (scanner.hasNextLine()) {
				scoresTextArea.appendText(scanner.nextLine() + "\n");
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		HBox scoreBox = new HBox(scoresTextArea);
		scoreBox.setLayoutX(100);
		scoreBox.setLayoutY(100);
		return scoreBox;
	}

	private TextField createTextboxForName() {
		TextField nameField = new TextField();
		nameField.setPromptText("your name");
		nameField.setFont(Font.font("Cambria", FontWeight.BOLD, 28));
		nameField.setLayoutX(100);
		nameField.setLayoutY(150);
		nameField.setPrefWidth(200);

		nameField.setOnAction(e -> {
			name = nameField.getText();
		});

		return nameField;
	}

	private HBox createTextboxForRules() {
		TextArea rulesTextArea = new TextArea();
		rulesTextArea.setEditable(false);
		rulesTextArea.setPrefSize(430, 250);
		rulesTextArea.setStyle("-fx-font-family: 'Cambria'; -fx-font-size: 11; -fx-font-weight: bold;");

		try {
			File scoresFile = new File("src/resources/rules.txt");
			Scanner scanner = new Scanner(scoresFile);
			while (scanner.hasNextLine()) {
				rulesTextArea.appendText(scanner.nextLine() + "\n");
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		HBox rulesBox = new HBox(rulesTextArea);
		scoreSubscene.setRoot(rulesBox);
		rulesBox.setLayoutX(100);
		rulesBox.setLayoutY(100);
		return rulesBox;
	}

	private Node createTextboxForControls() {
		TextArea controlsTextArea = new TextArea();
		controlsTextArea.setEditable(false);
		controlsTextArea.setPrefSize(400, 250);
		controlsTextArea.setFont(Font.font("Cambria", FontWeight.BOLD, 12));
		try {
			File scoresFile = new File("src/resources/controls.txt");
			Scanner scanner = new Scanner(scoresFile);
			while (scanner.hasNextLine()) {
				controlsTextArea.appendText(scanner.nextLine() + "\n");
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		HBox scoreBox = new HBox(controlsTextArea);
		scoreBox.setLayoutX(100);
		scoreBox.setLayoutY(100);
		return scoreBox;
	}

	public Stage getMainStage() {
		return mainStage;
	}

	private void AddMenuButtons(MenuButtonFeature button) {
		button.setLayoutX(MENU_BUTTON_LENGTH);
		button.setLayoutY(MENU_BUTTON_HEIGHT + menuButtons.size() * 100);
		menuButtons.add(button);
		mainPane.getChildren().add(button);
	}

	// Create button for menu screen
	private void CreateButtons() {
		createStartButton();
		createScoresButton();
		createRulesButton();
		createControlsButton();
		createExitButton();
	}

	private void createStartButton() {
		MenuButtonFeature startButton = new MenuButtonFeature("PLAY");
		AddMenuButtons(startButton);

		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showSubScene(playSubscene);

			}
		});
	}

	private void createScoresButton() {
		MenuButtonFeature scoresButton = new MenuButtonFeature("SCORES");
		AddMenuButtons(scoresButton);

		scoresButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showSubScene(highScoreSubScene);

			}
		});
	}

	private void createRulesButton() {

		MenuButtonFeature rulesButton = new MenuButtonFeature("RULES");
		AddMenuButtons(rulesButton);

		rulesButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showSubScene(rulesSubScene);

			}
		});
	}

	private void createControlsButton() {
		MenuButtonFeature controlsButton = new MenuButtonFeature("CONTROLS");
		AddMenuButtons(controlsButton);

		controlsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showSubScene(controlSubscene);

			}
		});

	}

	private void createExitButton() {
		MenuButtonFeature exitButton = new MenuButtonFeature("EXIT");
		AddMenuButtons(exitButton);

		exitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				mainStage.close();

			}
		});

	}

	private void createBackground() {
		Image backgroundImage = new Image(BACKGROUND_IMAGE, 256, 256, false, false);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
				BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		mainPane.setBackground(new Background(background));
	}

	private void createLogo() {
		StartGameLabel startGameLabel = new StartGameLabel("ASTEROIDS");
		startGameLabel.setLayoutX(350);
		startGameLabel.setLayoutY(25);

		startGameLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				startGameLabel.setEffect(new DropShadow());

			}
		});

		startGameLabel.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				startGameLabel.setEffect(null);

			}
		});

		mainPane.getChildren().add(startGameLabel);

	}

	private void createLogo2() {
		ImageView logo = new ImageView(LOGO);
		logo.setFitHeight(150);
		logo.setFitWidth(150);
		logo.setLayoutX(700);

		logo.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				logo.setEffect(new DropShadow());

			}
		});

		logo.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				logo.setEffect(null);

			}
		});

		mainPane.getChildren().add(logo);

	}
}
