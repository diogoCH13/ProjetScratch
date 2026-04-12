package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import scratch.model.Segment;
import scratch.model.VarDeclaration;
import scratch.viewmodel.MainViewModel;

public class SceneTurtleView extends VBox {
    private final MainViewModel vm;
    private final TableView<VarDeclaration> tableView = new TableView<>();
    private final Label varibaleLabel = new Label("Variables");
    Button stopBtn = new Button("Arrêter");
    private final Slider slider = new Slider(0.01, 5, 1);

    private final double TURTLE_HEAD_SIZE = 2;
    private final double TURTLE_SIZE = 20;

    private Polygon turtle;
    private final Circle headTurtle = new Circle(TURTLE_HEAD_SIZE);
    private final Label sceneLabel = new Label("Scene");
    private final Label turtleLabel = new Label();

    private final Button loadBtn = new Button("Charger");
    private final Button executeBtn = new Button("Exécuter");

    private final RadioButton autoRadioBtn = new RadioButton("Exécution automatique");
    private final RadioButton manuelRadioBtn = new RadioButton("Exécution manuelle");

    private final Pane paneTurtle = new Pane();
    private final VBox menuBox = new VBox(10);

    private final VBox sceneBox = new VBox(10);

//    private Canvas canvas = new Canvas(500, 500);
    private Canvas canvas = new Canvas(380, 380);

    private GraphicsContext gc = canvas.getGraphicsContext2D();

    public SceneTurtleView(MainViewModel vm) {
        this.vm = vm;

        beautify();
        configActions();
        configButtonsDisabling();
        configureBindingsAndListeners();
    }

    private double toScreenX(double x) {
        return canvas.getWidth() / 2 + x;
    }

    private double toScreenY(double y) {
        return canvas.getHeight() / 2 - y;
    }

//    public void beautify(){
////        paneTurtle.setPrefSize(380, 380);
////        paneTurtle.setMinSize(380, 380);
////        paneTurtle.setMaxSize(380, 380);
//        VBox tableBox = configTableBox();
//        HBox buttonsBox = configureSceneButton();
//        paneTurtle.setPrefHeight(380);
//        drawTurtle(vm.getPoint().getX(),vm.getPoint().getY(),0);
//        paneTurtle.getChildren().addAll(configureSceneGrid(), turtle, headTurtle, canvas);
//        getChildren().addAll(sceneLabel, paneTurtle, configTableBox(), configureSceneButton());
////        setSpacing(0);
//        VBox.setMargin(tableBox, new Insets(12, 0, 0, 0));
//        VBox.setMargin(buttonsBox, new Insets(12, 0, 0, 0));
//
//        setPadding(new Insets(16, 16, 12, 16));
//        gc.setStroke(Color.RED);
//    }

    public void beautify() {
        drawTurtle(vm.getPoint().getX(), vm.getPoint().getY(), 0);
        paneTurtle.getChildren().addAll(configureSceneGrid(), turtle, headTurtle, canvas);

        VBox sceneBlock = new VBox(0);
        sceneBlock.getChildren().addAll(sceneLabel, paneTurtle);

        VBox tableBox = configTableBox();
        HBox buttonsBox = configureSceneButton();
        HBox radioBox = configRadioBox();

        getChildren().addAll(sceneBlock, tableBox, radioBox, buttonsBox, configSliderBox());

        setPadding(new Insets(16, 16, 12, 16));
        setSpacing(12);

        gc.setStroke(Color.RED);
    }

    private void drawTurtle(double x, double y, double angle) {
        // angle on degrees
        double xP1 = toScreenX(x) + TURTLE_SIZE * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(angle + 90)),
                yP1 = toScreenY(y) - TURTLE_SIZE * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(angle + 90)),
                xP2 = toScreenX(x) - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle)),
                yP2 = toScreenY(y) + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle)),
                xP3 = toScreenX(x) + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle)),
                yP3 = toScreenY(y) - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle));

        this.turtle = new Polygon(xP1, yP1, xP2, yP2, xP3, yP3); // draw the body (triangle)
        this.turtle.setFill(Color.DEEPSKYBLUE); // Color of the body
        this.turtle.setStroke(Color.BLACK);
        this.headTurtle.setTranslateX(xP1); // move the head of the turtle (circle)
        this.headTurtle.setTranslateY(yP1);
    }

//    private void drawTurtle(double x, double y, double angle) {
//        double screenX = toScreenX(x);
//        double screenY = toScreenY(y);
//        // angle on degrees
//        double xP1 = screenX + TURTLE_SIZE * Math.sin(Math.toRadians(60)) * Math.cos(Math.toRadians(angle + 90)),
//                yP1 = screenY - TURTLE_SIZE * Math.sin(Math.toRadians(60)) * Math.sin(Math.toRadians(angle + 90)),
//                xP2 = screenX - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle)),
//                yP2 = screenY + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle)),
//                xP3 = screenX + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(angle)),
//                yP3 = screenY - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(angle));
//
//        this.turtle = new Polygon(xP1, yP1, xP2, yP2, xP3, yP3); // draw the body (triangle)
//        this.turtle.setFill(Color.DEEPSKYBLUE); // Color of the body
//        this.turtle.setStroke(Color.BLACK);
//        this.headTurtle.setTranslateX(xP1); // move the head of the turtle (circle)
//        this.headTurtle.setTranslateY(yP1);
//    }

//    public Canvas configureSceneGrid(){
//        Canvas canvas = new Canvas(500, 500);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//
//        int width = 500;
//        int height = 500;
//        int cellSize = 50;
//
//        gc.setStroke(Color.BLACK);
//        gc.strokeRect(0, 0, width, height);
//        gc.setStroke(Color.LIGHTBLUE);
//
//        for (int xStart = cellSize; xStart < width; xStart += cellSize){
//            gc.strokeLine(xStart, 0, xStart, height);
//
//        }
//
//        for (int yStart = cellSize; yStart < height; yStart += cellSize){
//            gc.strokeLine(0, yStart, width, yStart);
//        }
//
//        return canvas;
//    }

    public Canvas configureSceneGrid(){
        Canvas canvas = new Canvas(380, 380);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int width = 380;
        int height = 380;
        int cellSize = 38;

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
        confingStopBtn();
        root.getChildren().addAll(loadBtn, executeBtn, stopBtn);
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
//            if (!vm.isRunning()){
//                vm.execute();
//            }else {
//                vm.next();
//            }
            if (!vm.isRunning()){
                vm.execute();

                if (vm.isAutoMode()){
                    vm.autoNext();
                }
            }
            else{
                if (!vm.isAutoMode()){
                    vm.next();
                }
            }

        });

        stopBtn.setOnAction(e->{
            vm.stopAutoMode();
        });

        autoRadioBtn.setOnAction(e->{
            vm.autoModeProperty().set(true);
        });

        manuelRadioBtn.setOnAction(e->{
            vm.autoModeProperty().set(false);
        });


    }

    private void configButtonsDisabling() {
        loadBtn.disableProperty().bind(vm.canLoad().not().or(vm.errorValueProperty()).or(vm.varDeclarationIsBefore().not()));
//        executeBtn.disableProperty().bind(vm.canExecute().not().or(vm.isExecutionFinished()));

        executeBtn.disableProperty().bind(
                vm.canExecute().not()
                        .or(vm.executionFinishedProperty())
        );

        stopBtn.disableProperty().bind(
                vm.runningProperty().not()
        );
    }

    private void configureBindingsAndListeners() {
        loadBtn.textProperty().bind(
                Bindings.when(vm.programLoadedProperty())
                        .then("Ré-initialiser")
                        .otherwise("Charger")
        );

//        executeBtn.textProperty().bind(
//                Bindings.when(vm.runningProperty())
//                        .then("Suivant")
//                        .otherwise("Exécuter")
//        );


        executeBtn.textProperty().bind(
                Bindings.when(vm.runningProperty().and(vm.autoModeProperty().not()))
                        .then("Suivant")
                        .otherwise("Exécuter")
        );

        slider.visibleProperty().bind(vm.autoModeProperty());
        slider.managedProperty().bind(vm.autoModeProperty());

        stopBtn.visibleProperty().bind(vm.autoModeProperty());
        stopBtn.managedProperty().bind(vm.autoModeProperty());


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

        slider.valueProperty().addListener((observableValue, number, t1) -> {
            vm.setTime(t1.doubleValue());
        });




    }

    private void redrawTurtle(){
        paneTurtle.getChildren().remove(turtle);
        drawTurtle(vm.cursorXProperty().get(), vm.cursorYProperty().get(), vm.cursorAngelProperty().get());
        paneTurtle.getChildren().add(turtle);
        drawSegments();
    }

//    public void drawSegments(){
//
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//
//        for (Segment segment : vm.getWorld().getSegments()) {
//            gc.strokeLine(
//                    segment.getStart().getX(),
//                    segment.getStart().getY(),
//                    segment.getEnd().getX(),
//                    segment.getEnd().getY()
//            );
//        }
//    }

    public void drawSegments(){

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Segment segment : vm.getWorld().getSegments()) {
            gc.strokeLine(
                    toScreenX(segment.getStart().getX()),
                    toScreenY(segment.getStart().getY()),
                    toScreenX(segment.getEnd().getX()),
                    toScreenY(segment.getEnd().getY())
            );
        }
    }

    public void configturtleInfo(){
        turtleLabel.textProperty().bind(
                Bindings.createStringBinding(() ->
                                String.format(
                                        "Tortue : x = %.1f, y = %.1f, direction = %.1f°",
                                        vm.cursorXProperty().get(),
                                        vm.cursorYProperty().get(),
                                        vm.cursorAngelProperty().get()
                                ),
                        vm.cursorXProperty(),
                        vm.cursorYProperty(),
                        vm.cursorAngelProperty()
                )
        );
    }

    public VBox configTable(){
        VBox tableBox = new VBox();
        TableColumn<VarDeclaration, String> firstColumn = new TableColumn<>("Nom");
        TableColumn<VarDeclaration, String> SecondColumn = new TableColumn<>("Valeur");
        tableView.getColumns().addAll(firstColumn, SecondColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.setPrefWidth(150);
        tableView.setMaxHeight(150);
        tableBox.getChildren().add(tableView);
        return tableBox;
    }

    public VBox configTableBox(){
        configturtleInfo();

        VBox detailTableBox = new VBox();
        detailTableBox.setPadding(new Insets(10));
        detailTableBox.setBorder(Border.stroke(Color.GRAY));
        detailTableBox.getChildren().addAll(turtleLabel, varibaleLabel, configTable());
        return detailTableBox;
    }

    public HBox configRadioBox(){
        HBox radioBox = new HBox(10);
        radioBox.setAlignment(Pos.CENTER);
        ToggleGroup group = new ToggleGroup();
        autoRadioBtn.setToggleGroup(group);
        manuelRadioBtn.setToggleGroup(group);

        manuelRadioBtn.setSelected(true);
        radioBox.getChildren().addAll(autoRadioBtn, manuelRadioBtn);
        return radioBox;
    }

    public void confingStopBtn(){
        stopBtn.setVisible(false);
        stopBtn.setManaged(false);
    }

    public VBox configSliderBox(){
        VBox sliderBox = new VBox();
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(0.1);

        sliderBox.getChildren().add(slider);
        return sliderBox;

    }


}
