package gurdle.gui;

import gurdle.CharChoice;
import gurdle.Model;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import util.Observer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * The graphical user interface to the Wordle game model in
 * {@link Model}.
 *
 * @author James Prendergast
 */
public class Gurdle extends Application implements Observer< Model, String > {

    private Model model;

    //2d array of guesses if you have to
    private Text[][] guessGrid;
    private Rectangle[][] recGrid;
    private HashMap keymap;

    private Label GuessLabel;

    private Label gameSateLabel;




    @Override public void init() {
        this.guessGrid = new Text[6][5];
        this.recGrid = new Rectangle[6][5];
        //this.keymap = new Map<String, Button>;
        this.model = new Model();
        this.model.addObserver(this);
    }

    @Override
    public void start( Stage mainStage ) {

        BorderPane Border = new BorderPane();
        Scene scene = new Scene(Border);
        Border.setPrefSize(800,800);
        Border.setPadding(new Insets(0,20,0,20));


        GridPane wordGrid = new GridPane();
        wordGrid.setAlignment(Pos.CENTER);
        Border.setCenter(wordGrid);
        //this.wordGrid = wordGrid;

        wordGrid.setVgap(2);
        wordGrid.setHgap(1);
        for(int r =0;r < 6;r++){
            for(int c = 0; c < 5;c++){
                Rectangle rec = new Rectangle(60,60);
                rec.setFill(Color.WHITE);
                rec.setStroke(Color.BLACK);

                Text txt = new Text("");
                txt.setFill(Color.BLACK);

                StackPane stack = new StackPane(rec,txt);
                guessGrid[r][c] = txt;
                recGrid[r][c] = rec;
                wordGrid.add(stack,c,r);
            }
        }


        FlowPane fp = new FlowPane();
        fp.setAlignment(Pos.CENTER);
        Label guess = new Label("Guesses: 0");
        this.GuessLabel = guess;
        fp.getChildren().add(guess);
        Label GState = new Label("Make A Guess!");
        this.gameSateLabel = GState;
        fp.getChildren().add(GState);
        Label Secret = new Label("Secret: N/A");
        fp.getChildren().add(Secret);

        fp.setHgap(20);
        Border.setTop(fp);


        BorderPane Botpane = new BorderPane();
        Border.setBottom(Botpane);

        GridPane letters = new GridPane();
        letters.setHgap(.5);
        letters.setVgap(.5);
        letters.setAlignment(Pos.CENTER);
        String qwertyString = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String[] QWERTY = qwertyString.split("");
        int letternum = 0;
        for(int r = 0; r<3;r++){
            int c = 0;
            while(!((r==0&&c==10)||(r==1&&c==9)||(r==2&&c==7))) {
                Button button = new Button();
                button.setText(QWERTY[letternum]);

                button.setStyle("-fx-background-color: #e6e6e6; -fx-border-color: #000000; ");
                button.setPrefSize(50, 70);
                button.setOnAction( event -> model.enterNewGuessChar(button.getText().charAt(0)));
                letters.add(button, c, r);
                c++;
                letternum++;
            }

        }
        Botpane.setCenter(letters);

        FlowPane botbot = new FlowPane();
        botbot.setAlignment(Pos.CENTER);
        Button newgame = new Button();
        newgame.setOnAction( event -> {
            model.newGame();
            Secret.setText("Secret: N/A");
        });
        newgame.setPrefSize(130,80);
        newgame.setText("NEW GAME");

        Button cheat = new Button();
        cheat.setPrefSize(110,80);
        cheat.setText("CHEAT");
        cheat.setOnAction( event -> Secret.setText("Secret: " + model.secret()));

        botbot.getChildren().add(cheat);
        botbot.getChildren().add(newgame);
        Botpane.setBottom(botbot);


        Button enter = new Button();
        enter.setPrefSize(110,80);
        enter.setText("RETURN");
        enter.setOnAction( event -> model.confirmGuess());
        Botpane.setRight(enter);


        mainStage.setScene( scene );
        mainStage.setTitle("Wordle!");
        mainStage.show();
        this.model.newGame();
    }

    @Override
    public void update( Model model, String message ) {
        /**
         * i know i had to change to keyboard too but i had troubles thinking about it
         * i know i had to do a hashmap or something but its still fully functional
         *  also i didnt know how to get the game status statements instead of just the status
         *  overrall pretty successful though
         */
        //update letter grid
        for(int r =0; r < 6;r++){
            for(int c = 0;c<5;c++){
                guessGrid[r][c].setText(model.get(r,c).toString());
                if(model.get(r,c).getStatus() == CharChoice.Status.EMPTY){
                    recGrid[r][c].setFill(Color.WHITE);
                }else if(model.get(r,c).getStatus() == CharChoice.Status.RIGHT_POS){
                    recGrid[r][c].setFill(Color.GREEN);
                }else if(model.get(r,c).getStatus() == CharChoice.Status.WRONG_POS){
                    recGrid[r][c].setFill(Color.rgb(156,142,36));
                }else{
                    recGrid[r][c].setFill(Color.GREY);
                }

            }
        }

        //update guesses
        GuessLabel.setText("Guesses: " + model.numAttempts());

        //update gamestate
        gameSateLabel.setText(model.gameState().toString());
    }

    public static void main( String[] args ) {
        if ( args.length > 1 ) {
            System.err.println( "Usage: java Gurdle [1st-secret-word]" );
        }
        Application.launch( args );
    }
}
