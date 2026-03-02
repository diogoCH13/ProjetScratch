package scratch.view;

import javafx.beans.binding.Bindings;
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

    private final TextField valueField = new TextField();

    private final Label palettLabel = new Label("Palette d'actions");
    private final Label programLabel = new Label("Programme");
    private final Label detailLabel = new Label("(aucune action sélectionnée)");
    private final Label detailLabel2 = new Label();
    private final Label sceneLabel = new Label("Scene");

    private final Button addBtn = new Button("Ajouter au programme");
    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");
    private final Button loadBtn = new Button("Charger");
    private final Button executeBtn = new Button("Exécuter");


    private final ListView<ActionType> palletListView = new ListView<>();
    private final ListView<Action> actionListView = new ListView<>();

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

        loadData();
        configActions();
        configSelectionModels();
        configButtonsDisabling();
        configureBindingsAndListeners();

//        this.getChildren().add(layoutControls());

        beautify();

    }

    private void configureMenuBox(){
        menuBox.getChildren().addAll(menuBar);
    }

    public void configLeft(){
        palletBox.getChildren().addAll(palettLabel, palletListView, addBtn);
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
        TitledPane detailPane = new TitledPane();

        detailPane.setPrefWidth(460);
        detailPane.setText("Détail de l'action");

        valueField.setPrefWidth(40);
        valueField.setMaxWidth(40);

        HBox detailContent = new HBox(10);
        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(10));
        detailContent.getChildren().addAll(detailLabel, valueField, detailLabel2);

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

    public void beautify() {
        palletBox.setPadding(new Insets(16, 16, 12, 16));
        programBox.setPadding(new Insets(16, 16, 12, 16));
        sceneBox.setPadding(new Insets(16, 16, 12, 16));
//        sceneListView.setPrefSize(500,500);
//        palletListView.setStyle("-fx-control-inner-background: lightblue;");

        palletListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ActionType action, boolean empty) {
                super.updateItem(action, empty);
                Circle circle = new Circle(5);

                if (empty) {
                    setText(null);
                }
                else {
                    setGraphic(circle);
                    setText(action.toString());
                    switch (action) {
                        case TURN_LEFT, TURN_RIGHT :
                            setTextFill(Color.RED);
                            circle.setFill(Color.RED);
                            break;

                        case MOVE_FORWARD:
                            setTextFill(Color.BLUE);
                            circle.setFill(Color.BLUE);
                            break;

                        default:
                            setTextFill(Color.GREEN);
                            circle.setFill(Color.GREEN);
                    }
                }
            }
        });


        actionListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Action action, boolean empty) {
                super.updateItem(action, empty);
                Circle circle = new Circle(5);

                if (empty || action == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setGraphic(circle);
                    setText(action.toString());
                    if(action instanceof Turn){
                        setTextFill(Color.RED);
                        circle.setFill(Color.RED);
                    } else if (action instanceof MoveForward) {
                        setTextFill(Color.BLUE);
                        circle.setFill(Color.BLUE);
                    }else {
                        setTextFill(Color.GREEN);
                        circle.setFill(Color.GREEN);
                    }
                }
            }
        });
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


    private void loadData() {
        palletListView.setItems(vm.getPalettAction());
        actionListView.setItems(vm.getProgramActions());
    }

    private void configActions() {
        addBtn.setOnAction(e->{
            vm.addAction();
        });

        palletListView.setOnMouseClicked(e->{
            if (e.getClickCount() >= 2){
                vm.addAction();
            }
        });

        deleteBtn.setOnAction(e->{
            vm.deleteAction();
        });

        upBtn.setOnAction(e->{
            vm.upAction();
        });

        downBtn.setOnAction(e -> {
            vm.downAction();
        });

        duplicateBtn.setOnAction(e -> {
            vm.duplicateAction(actionListView.getSelectionModel().getSelectedItem());
        });

        clearBtn.setOnAction(e -> {
            vm.clearAction();
        });

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
        upBtn.disableProperty().bind(vm.canUp().not());
        addBtn.disableProperty().bind(vm.canAdd().not());
        deleteBtn.disableProperty().bind(vm.canRemove().not());
        loadBtn.disableProperty().bind(vm.canLoad().not());
        executeBtn.disableProperty().bind(vm.canExecute().not().or(vm.isExecutionFinished()));
        downBtn.disableProperty().bind(vm.canDown().not());
        duplicateBtn.disableProperty().bind(vm.canDuplicate().not());


    }

    private void configSelectionModels() {
        palletListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.catalogIndexProperty().set(t1.intValue());
        });

        actionListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
          vm.actionIndexProperty().set(t1.intValue());

          vm.updateActionDetails();
        });

        vm.actionIndexProperty().addListener((observableValue, number, t1) -> {
            actionListView.getSelectionModel().select(t1.intValue());
        });
    }

    private void configureBindingsAndListeners(){
        detailLabel.textProperty().bind(vm.actionDetailsProperty());


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


    }

}
