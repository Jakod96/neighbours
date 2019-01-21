import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.omg.IOP.ExceptionDetailMessage;
import sun.jvm.hotspot.code.ExceptionBlob;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.*;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    // Enumeration type for the state of an Actor
    enum State {
        UNSATISFIED,
        SATISFIED
    }

    class Actor {
        final Color color;    // Color an existing JavaFX class
        State state;

        Actor(Color color) {
            this.color = color;
        }   // Constructor
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    Boolean isnotVacant (int x, int y, Actor[][] world){
        if(world[x][y] == null){
            return false;
        }
        else{
            return true;
        }
    }

    boolean moveStatus(int x, int y, Actor[][] world, int threshold){
        int reds = 0, blues = 0;
        int total = 0;
        double percantage;
        Color self = Color.DARKGREEN;
        for (int x1 = -1; x1<=1; x1++){
            for (int y1 = -1; y<=1; y1++){
                if(x1==0 && y1==0){
                    self = world[x][y].color;
                    continue;
                }
                if (isValidLocation(world.length, x+x1, y+y1)){
                    if(isnotVacant(x+x1,y+y1,world)){
                        Color lot = world[x+x1][y+y1].color;
                        if (lot == Color.RED) {reds=reds+1;}
                        if (lot == Color.BLUE) {blues=blues+1;}
                    }

                }
            }
        }
        total = reds + blues;
        if (self == Color.RED){
            percantage = reds/total;

        }
        else if (self == Color.BLUE){
            percantage = reds/total;

        }
        else {
            out.print("Cancer");
            throw new ExceptionInInitializerError();
        }

        if (percantage<threshold){
            return true;
        }
        return false;
    }

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;
        Actor[][] newWorld;

        // TODO update the world
    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST!
        Random rand = new Random();
        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 900;   // Should also try 90 000
        int length = (int)Math.floor(sqrt((double)nLocations));
        world = new Actor[length][length];
        // TODO create initialize the world
        for (int x=0; x<length; x++){
            for (int y=0; y<length; y++){
                double randDouble = rand.nextDouble();
                Color actorColor;
                if (randDouble <dist[0]){
                    actorColor = Color.RED;
                }
                else if ((randDouble >= dist[0]) && (randDouble < (dist[0]+dist[1]))){
                    actorColor = Color.BLUE;
                }
                else{
                    world[x][y] = null;
                    continue;
                }

                world[x][y] = new Actor(actorColor);
            }

        }
        // Should be last
        fixScreenSize(nLocations);
    }

    // -------------- Methods -----------------------

    // TODO Add methods to use

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }


    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null},
                {null, new Actor(Color.BLUE), null},
                {new Actor(Color.RED), null, new Actor(Color.BLUE)}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));

        // TODO Add tests here

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 400;   // Size for window
    double height = 400;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
