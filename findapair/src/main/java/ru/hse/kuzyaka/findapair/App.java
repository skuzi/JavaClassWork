package ru.hse.kuzyaka.findapair;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Class for gui of this game **/
public class App extends Application {
    private static final double MAX_WIDTH = 500;
    private static final double MAX_HEIGHT = 500;
    private static int boardSize;
    private static int buttonShown;
    private static Button[][] buttons;
    private static Stage stage;

    /**
     * Initializes a game
     *
     * @param args command line arguments, must be single even number. If it's not true, ends with return code 1
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Number of argument must be 1");
            System.exit(1);
        }
        try {
            boardSize = Integer.parseInt(args[0]);
            if (boardSize % 2 != 0) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Argument must be an even number");
            System.exit(1);
        }
        boardSize = Integer.parseInt(args[0]);
        BoardLogic.init(boardSize);
        Application.launch(args);
    }

    /** {@inheritDoc} **/
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("Find a pair");
        primaryStage.setHeight(MAX_HEIGHT);
        primaryStage.setWidth(MAX_WIDTH);
        var field = new GridPane();
        buttons = new Button[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            var column = new ColumnConstraints();
            column.setPercentWidth(100.0 / boardSize);
            var row = new RowConstraints();
            row.setPercentHeight(100.0 / boardSize);
            field.getRowConstraints().add(row);
            field.getColumnConstraints().add(column);
        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new Button("");
//                buttons[i][j].setMaxSize(MAX_WIDTH / boardSize, MAX_HEIGHT / boardSize);
                buttons[i][j].setMaxSize(MAX_WIDTH, MAX_HEIGHT);
                buttons[i][j].setOnMouseClicked(this::mouseClicked);
                buttons[i][j].setUserData(new Pair<>(i, j));
                field.add(buttons[i][j], i, j);
            }
        }
        var scene = new Scene(field, MAX_WIDTH, MAX_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mouseClicked(MouseEvent mouseEvent) {
        var button = (Button) mouseEvent.getSource();
        @SuppressWarnings("unchecked")
        var position = (Pair<Integer, Integer>) button.getUserData();

        if (!BoardLogic.isOpened(position) || buttonShown == 2) {
            return;
        }

        BoardLogic.open(position);
        button.setText(String.valueOf(BoardLogic.getNumber(position)));
        buttonShown++;
        if (BoardLogic.currentlyOpened() == 2) {
            var firstPosition = BoardLogic.getFirstOpened();
            var secondPosition = BoardLogic.getSecondOpened();
            if (BoardLogic.checkEqual()) {
                buttons[firstPosition.getFirst()][firstPosition.getSecond()].setDisable(true);
                buttons[secondPosition.getFirst()][secondPosition.getSecond()].setDisable(true);
                buttonShown = 0;
            } else {
                new Timeline(new KeyFrame(Duration.millis(500), v -> {
                    buttons[firstPosition.getFirst()][firstPosition.getSecond()].setText("");
                    buttons[secondPosition.getFirst()][secondPosition.getSecond()].setText("");
                    buttonShown = 0;
                })).play();
            }
            BoardLogic.close();
        }

        if (BoardLogic.isFinished()) {
            var alert = new Alert(Alert.AlertType.INFORMATION, "YATTA!");
            Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            exitButton.setText("Exit");
            exitButton.setOnTouchPressed(event -> Platform.exit());
            alert.setTitle("Game over");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(stage);
            alert.setOnHidden(event -> Platform.exit());
            alert.show();
        }
    }
}
