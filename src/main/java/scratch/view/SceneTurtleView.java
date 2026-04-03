package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import scratch.model.Segment;
import scratch.viewmodel.MainViewModel;

public class SceneTurtleView extends VBox {
    private final MainViewModel vm;

    private final double TURTLE_HEAD_SIZE = 2;
    private final double TURTLE_SIZE = 20;

    private Polygon turtle;
    private final Circle headTurtle = new Circle(TURTLE_HEAD_SIZE);

    private final Label sceneLabel = new Label("Scene");

    private final Button loadBtn = new Button("Charger");
    private final Button executeBtn = new Button("Exécuter");

    private final Pane paneTurtle = new Pane();
    private final VBox menuBox = new VBox(10);

    private final VBox sceneBox = new VBox(10);

    private Canvas canvas = new Canvas(500, 500);
    private GraphicsContext gc = canvas.getGraphicsContext2D();

    public SceneTurtleView(MainViewModel vm) {
        this.vm = vm;

        beautify();
        configActions();
        configButtonsDisabling();
        configureBindingsAndListeners();
    }

    public void beautify(){
        drawTurtle(vm.getPoint().getX(),vm.getPoint().getY(),0);
        paneTurtle.getChildren().addAll(configureSceneGrid(), turtle, headTurtle, canvas);
        getChildren().addAll(sceneLabel, paneTurtle, configureSceneButton());

        setPadding(new Insets(16, 16, 12, 16));
        gc.setStroke(Color.RED);
    }

    private void drawTurtle(double x, double y, double angle) {
        // angle on degrees
        double xP1 = x + TURTLE_SIZE * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(angle + 90)),
                yP1 = y - TURTLE_SIZE * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(angle + 90)),
                xP2 = x - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle)),
                yP2 = y + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle)),
                xP3 = x + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle)),
                yP3 = y - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle));

        this.turtle = new Polygon(xP1, yP1, xP2, yP2, xP3, yP3); // draw the body (triangle)
        this.turtle.setFill(Color.DEEPSKYBLUE); // Color of the body
        this.turtle.setStroke(Color.BLACK);
        this.headTurtle.setTranslateX(xP1); // move the head of the turtle (circle)
        this.headTurtle.setTranslateY(yP1);
    }

    public Canvas configureSceneGrid(){
        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int width = 500;
        int height = 500;
        int cellSize = 50;

        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
        gc.setStroke(Color.LIGHTBLUE);

        for (int xStart = cellSize; xStart < width; xStart += cellSize){
            gc.strokeLine(xStart, 0, xStart, height);

        }

        for (int yStart = cellSize; yStart < height; yStart += cellSize){
            gc.strokeLine(0, yStart, width, yStart);
        }

        return canvas;
    }

    public HBox configureSceneButton(){
        HBox root = new HBox();
        root.getChildren().addAll(loadBtn, executeBtn);
        root.setAlignment(Pos.CENTER);
        return root;
    }

    private void configActions() {
        loadBtn.setOnAction(e->{
            if (!vm.isProgramLoaded()) {
                vm.loadProgram();

            } else {
                vm.reset();
            }
        });

        executeBtn.setOnAction(e->{
            if (!vm.isRunning()){
                vm.execute();
            }else {
                vm.next();
            }
        });
    }

    private void configButtonsDisabling() {
        loadBtn.disableProperty().bind(vm.canLoad().not());
//        executeBtn.disableProperty().bind(vm.canExecute().not().or(vm.isExecutionFinished()));

        executeBtn.disableProperty().bind(
                vm.canExecute().not()
                        .or(vm.executionFinishedProperty())
        );
    }

    private void configureBindingsAndListeners() {
        loadBtn.textProperty().bind(
                Bindings.when(vm.programLoadedProperty())
                        .then("Ré-initialiser")
                        .otherwise("Charger")
        );

        executeBtn.textProperty().bind(
                Bindings.when(vm.runningProperty())
                        .then("Suivant")
                        .otherwise("Exécuter")
        );

        vm.cursorXProperty().addListener((observableValue, number, t1) -> {
            redrawTurtle();
//            drawTurtle(vm.cursorXProperty().get(), vm.cursorYProperty().get(), vm.cursorAngelProperty().get());
        });

        vm.cursorYProperty().addListener((observableValue, number, t1) -> {
            redrawTurtle();
//            drawTurtle(vm.cursorXProperty().get(), vm.cursorYProperty().get(), vm.cursorAngelProperty().get());
        });

        vm.cursorAngelProperty().addListener((observableValue, number, t1) -> {
//            drawTurtle(vm.cursorXProperty().get(), vm.cursorYProperty().get(), vm.cursorAngelProperty().get());
            redrawTurtle();
        });
    }

    private void redrawTurtle(){
        paneTurtle.getChildren().remove(turtle);
        drawTurtle(vm.cursorXProperty().get(), vm.cursorYProperty().get(), vm.cursorAngelProperty().get());
        paneTurtle.getChildren().add(turtle);
        drawSegments();
    }

    public void drawSegments(){

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Segment segment : vm.getWorld().getSegments()) {
            gc.strokeLine(
                    segment.getStart().getX(),
                    segment.getStart().getY(),
                    segment.getEnd().getX(),
                    segment.getEnd().getY()
            );
        }
    }

}
