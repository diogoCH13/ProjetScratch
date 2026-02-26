package scratch.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;


public class MainView extends BorderPane /*VBox*/ {
    private final double TURTLE_HEAD_SIZE = 2;
    private final double TURTLE_SIZE = 20;

    private final MainViewModel vm;
    private Polygon turtle;
    private final Circle headTurtle = new Circle(TURTLE_HEAD_SIZE);

    private final Label palettLabel = new Label("Palette d'actions");
    private final Label programLabel = new Label("Programme");
    private final Label sceneLabel = new Label("Scene");
    private final Label moveForwardLabel = new Label(" Avancer de ");
    private final Label turnLeft = new Label(" Tourner à gauche de ");
    private final Label turnRight = new Label(" Tourner à droite de ");
    private final Label penUp = new Label(" Lever stylo ");
    private final Label penDown = new Label(" Abaisser stylo ");

    private final Button add = new Button("Ajouter au programme");
    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");
    private final Button loadBtn = new Button("Charger");
    private final Button executeBtn = new Button("Exécuter");


//    private final ListView<ActionType> palletListView = new ListView<>();
    private final ListView<Action> actionListView = new ListView<>();
    private final ListView<Label> labelListView = new ListView<>();
    private final ObservableList<Label> tabLabel = FXCollections.observableArrayList(
            moveForwardLabel,
            turnLeft,
            turnRight,
            penUp,
            penDown
    );

    private final MenuBar menuBar = new MenuBar(new Menu("File"));
    private final Pane paneTurtle = new Pane();
    private final VBox menuBox = new VBox(10);
    private final VBox palletBox = new VBox(10);
    private final VBox programBox = new VBox(10);
    private final VBox sceneBox = new VBox(10);
    private final HBox buttonBox = new HBox(10);

    public MainView(MainViewModel vm) {
        this.vm = vm;

        configureMenuBox();
        configLeft();
        configCenter();
        configRight();

        this.setTop(menuBox);
        this.setLeft(palletBox);
        this.setCenter(programBox);
        this.setRight(sceneBox);

//        this.getChildren().add(layoutControls());

        beautify();

    }

    private void configureMenuBox(){
        menuBox.getChildren().addAll(menuBar);
    }

    public void configLeft(){
        labelListView.setItems(tabLabel);
        palletBox.getChildren().addAll(palettLabel, labelListView, add);
    }

    public void configCenter(){
        programBox.getChildren().addAll(programLabel, actionListView, configureButtonBox(), configureDetailSection());
    }

    public void configRight(){
        drawTurtle(vm.getPoint().getX(),vm.getPoint().getY(),0);
        paneTurtle.getChildren().addAll(configureSceneGrid(), turtle, headTurtle);
        sceneBox.getChildren().addAll(sceneLabel, paneTurtle, configureSceneButton());
    }

    private void configureButtonSizes(){
        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);
    }

    private HBox configureButtonBox(){
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        configureButtonSizes();
        buttonBox.getChildren().addAll(upBtn, downBtn, duplicateBtn, deleteBtn, clearBtn);
        return buttonBox;
    }

    private VBox configureDetailSection() {
        VBox TitledPaneBox = new VBox();
        TitledPaneBox.setPadding(new Insets(10));
        TitledPaneBox.setFillWidth(false);
        Label infoLabel = new Label("(aucune action sélectionnée)");
        TitledPane detailPane = new TitledPane();

        detailPane.setPrefWidth(460);
        detailPane.setText("Détail de l'action");

        TextField valueField = new TextField();
        valueField.setPrefWidth(40);
        valueField.setMaxWidth(40);

        HBox detailContent = new HBox(10);
        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(10));
        detailContent.getChildren().addAll(infoLabel, valueField);

        detailPane.setContent(detailContent);
        detailPane.setExpanded(true);

        TitledPaneBox.getChildren().add(detailPane);

        return TitledPaneBox;
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

    public void beautify(){
        palletBox.setPadding(new Insets(16, 16, 12, 16));
        programBox.setPadding(new Insets(16, 16, 12, 16));
        sceneBox.setPadding(new Insets(16, 16, 12, 16));
//        sceneListView.setPrefSize(500,500);
//        palletListView.setStyle("-fx-control-inner-background: lightblue;");
//        palletListView.setItems(vm.getPalettAction());

        Circle circleLabel = new Circle(5);
        circleLabel.setFill(Color.BLUE);
        moveForwardLabel.setStyle("-fx-text-fill: blue;");
        moveForwardLabel.setGraphic(circleLabel);

        Circle circleLabel2 = new Circle(5);
        circleLabel2.setFill(Color.RED);
        turnLeft.setStyle("-fx-text-fill: red;");
        turnLeft.setGraphic(circleLabel2);

        Circle circleLabel3 = new Circle(5);
        circleLabel3.setFill(Color.RED);
        turnRight.setStyle("-fx-text-fill: red;");
        turnRight.setGraphic(circleLabel3);

        Circle circleLabel4 = new Circle(5);
        circleLabel4.setFill(Color.GREEN);
        penUp.setStyle("-fx-text-fill: green;");
        penUp.setGraphic(circleLabel4);

        Circle circleLabel5 = new Circle(5);
        circleLabel5.setFill(Color.GREEN);
        penDown.setStyle("-fx-text-fill: green;");
        penDown.setGraphic(circleLabel5);

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

}
