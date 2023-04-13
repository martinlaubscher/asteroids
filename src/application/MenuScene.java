package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.scene.layout.VBox;
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
	private final static String LOGO = "/resources/Preview_1.gif";

	private TextField textBox;
	private MenuButtonFeature saveButton;

	private MenuSubScene helpSubscene;
	private MenuSubScene scoreSubscene;
	private MenuSubScene howToPlaySubscene;
	private MenuSubScene playSubscene;

	private MenuSubScene sceneToHide;

	List<MenuButtonFeature> menuButtons;
	private MenuSubScene highScoreSubScene;
	private MenuSubScene rulesSubScene;

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
		howToPlaySubscene = new MenuSubScene();
		mainPane.getChildren().add(howToPlaySubscene);

		createStartGameSubscene();
		createHighScoreSubScene();
		createRulesSubScene();

	}

	private void createHighScoreSubScene() {
		highScoreSubScene = new MenuSubScene();
		mainPane.getChildren().add(highScoreSubScene);
		StartGameLabel startGameLabel = new StartGameLabel("CHAMPIONS");
		startGameLabel.setLayoutX(110);
		startGameLabel.setLayoutY(25);
		highScoreSubScene.getPane().getChildren().add(startGameLabel);
		highScoreSubScene.getPane().getChildren().add(createTextboxForScore());
		
	}


	private void createStartGameSubscene() {
		playSubscene = new MenuSubScene();
		mainPane.getChildren().add(playSubscene);

		StartGameLabel startGameLabel = new StartGameLabel("ENTER YOUR NAME");
		startGameLabel.setLayoutX(110);
		startGameLabel.setLayoutY(25);
		playSubscene.getPane().getChildren().add(startGameLabel);
		playSubscene.getPane().getChildren().add(createTextboxForName());
		playSubscene.getPane().getChildren().add(createButtonToStart());

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

	private HBox createTextboxForScore() {
		// Create a new TextArea to display the scores
         TextArea scoresTextArea = new TextArea();
         scoresTextArea.setEditable(false);
         scoresTextArea.setPrefSize(MENU_BUTTON_HEIGHT, MENU_BUTTON_LENGTH);

         // Read the scores from the file and append them to the TextArea
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

         // Show the scoreSubscene with the TextArea containing the scores
         HBox scoreBox = new HBox(scoresTextArea);
         scoreBox.setLayoutX(100);
         scoreBox.setLayoutY(100);
		 return scoreBox;
	}


	private HBox createTextboxForName() {
		HBox box = new HBox();
		box.setSpacing(60);
		textBox = new TextField();
		textBox.setLayoutX(50);
		textBox.setLayoutY(25);
		saveButton = new MenuButtonFeature("SAVE");
		saveButton.setLayoutX(50);
		saveButton.setLayoutY(25);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				saveTextToFile(textBox.getText());
			}
		});
		box.getChildren().addAll(textBox, saveButton);

		box.setLayoutX(100);
		box.setLayoutY(100);
		return box;
	}
	
	private HBox createTextboxForRules() {
		 TextArea rulesTextArea = new TextArea();
		 rulesTextArea.setEditable(false);
		 rulesTextArea.setPrefSize(430, 250);
		 rulesTextArea.setStyle("-fx-font-family: 'Monotype Corsiva'; -fx-font-size: 12; -fx-font-weight: bold;");
		 
		// Read the rules from the file and append them to the TextArea
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

         // Show the ruleSubscene with the TextArea containing the scores
         HBox rulesBox = new HBox(rulesTextArea);
         scoreSubscene.setRoot(rulesBox);
         rulesBox.setLayoutX(100);
         rulesBox.setLayoutY(100);
		 return rulesBox;
	}

	private void saveTextToFile(String text) {
		try {
			File outputFile = new File("src/resources/scores.txt");
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			FileWriter writer = new FileWriter(outputFile);
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}

	private MenuButtonFeature createButtonToStart() {
		MenuButtonFeature startButton = new MenuButtonFeature("START");
		startButton.setLayoutX(350);
		startButton.setLayoutY(300);

		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				GameController gameController = new GameController();
				gameController.startGame();

			}
		});

		return startButton;
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

	private void CreateButtons() {
		createStartButton();
		createScoresButton();
		createHowToPlayButton();
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

	private void createHowToPlayButton() {

		MenuButtonFeature creditsButton = new MenuButtonFeature("RULES");
		AddMenuButtons(creditsButton);

		creditsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showSubScene(rulesSubScene);

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
		/*ImageView logo = new ImageView(LOGO);
		logo.setLayoutX(580);
		logo.setLayoutY(50);*/
		StartGameLabel startGameLabel = new StartGameLabel("ASTEROIDS");
		startGameLabel.setLayoutX(350);
		startGameLabel.setLayoutY(25);
		
		//highScoreSubScene.getPane().getChildren().add(startGameLabel);

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
		logo.setLayoutX(800);
		//logo.setLayoutY(10);
		
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