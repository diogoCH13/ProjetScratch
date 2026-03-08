package scratch.view;

import javafx.application.Platform;
import javafx.beans.Observable;
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
import javafx.stage.FileChooser;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;

import java.io.File;


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
    private final Label errorLabel = new Label("Erreur valeur");
    private final Label sceneLabel = new Label("Scene");

    private final Button addBtn = new Button("Ajouter au programme");
    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");
    private final Button loadBtn = new Button("Charger");
    private final Button executeBtn = new Button("Exécuter");

    private final MenuItem newItem  = new MenuItem("New...");
    private final MenuItem openItem = new MenuItem("Open...");
    private final MenuItem saveItem = new MenuItem("Save As...");
    private final MenuItem exitItem = new MenuItem("Exit");


    private final ListView<ActionType> palletListView = new ListView<>();
    private final ListView<Action> actionListView = new ListView<>();

    private final Pane paneTurtle = new Pane();
    private final VBox menuBox = new VBox(10);
    private final VBox palletBox = new VBox(10);
    private final VBox programBox = new VBox(10);
    private final VBox sceneBox = new VBox(10);
    private final HBox buttonBox = new HBox(10);

    private Canvas canvas = new Canvas(500, 500);
    private GraphicsContext gc = canvas.getGraphicsContext2D();

    public MainView(MainViewModel vm) {
        this.vm = vm;
        beautify();

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
    }

    private void configureMenuBox(){
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);

        MenuBar menuBar = new MenuBar(fileMenu);
        menuBox.getChildren().add(menuBar);
    }

    public void configLeft(){
        palletBox.getChildren().addAll(palettLabel, palletListView, addBtn);
    }

    public void configCenter(){
        programBox.getChildren().addAll(programLabel, actionListView, configureButtonBox(), configureDetailSection());
    }

    public void configRight(){
        drawTurtle(vm.getPoint().getX(),vm.getPoint().getY(),0);
        paneTurtle.getChildren().addAll(configureSceneGrid(), turtle, headTurtle, canvas);
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

        VBox detail = new VBox();
        HBox detailContent = new HBox(10);

        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(10));
        detailContent.getChildren().addAll(detailLabel, valueField, detailLabel2);

        errorLabel.setPadding(new Insets(0,0,0,10));
        detail.getChildren().addAll(detailContent, errorLabel);

        detailPane.setContent(detail);
        detailPane.setExpanded(true);

        TitledPaneBox.getChildren().addAll(detailPane);

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
        errorLabel.setTextFill(Color.INDIANRED);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        gc.setStroke(Color.RED);

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
            vm.duplicateAction(vm.getProgramActions().get(vm.actionIndexProperty().get()));
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
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        executeBtn.setOnAction(e->{
            if (!vm.isRunning()){
                vm.execute();
            }else {
                vm.next();
            }
        });

        newItem.setOnAction(e->{
            vm.newFile();
        });

        exitItem.setOnAction(e->{
            Platform.exit();
        });

        openItem.setOnAction(e->{
            openFile();
        });

        saveItem.setOnAction(e->{
            saveFile();
        });
    }

    private void configButtonsDisabling() {
        upBtn.disableProperty().bind(vm.canUp().not());
        addBtn.disableProperty().bind(vm.canAdd().not());
        deleteBtn.disableProperty().bind(vm.canRemove().not());
        clearBtn.disableProperty().bind(vm.canClearAll().not());
        valueField.disableProperty().bind(vm.disableTextFieldByActionType().not());
        errorLabel.visibleProperty().bind(vm.errorValueProperty());
        errorLabel.managedProperty().bind(vm.errorValueProperty());
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
        });

        vm.actionIndexProperty().addListener((observableValue, number, t1) -> {
            actionListView.getSelectionModel().select(t1.intValue());
        });

        vm.catalogIndexProperty().addListener((observableValue, number, t1) -> {
            palletListView.getSelectionModel().select(t1.intValue());
        });

        actionListView.getItems().addListener((Observable obs) -> {
            if (vm.isValidIndex()){
                int value = (int)vm.getProgramActions().get(vm.actionIndexProperty().get()).getValue();
                vm.actionValueProperty().setValue(String.valueOf(Math.abs(value)));
            }
            else{
                vm.actionValueProperty().set("");
                vm.errorValueProperty().set(false);
            }
        });
    }

    private void configureBindingsAndListeners(){
        detailLabel.textProperty().bind(vm.actionDetailsBinding());
        valueField.textProperty().bindBidirectional(vm.actionValueProperty());
        detailLabel2.textProperty().bind(vm.actionDetailValueBindings());

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
        if (vm.isRunning()){
            if (vm.getCursor().isPenDown()){
                double  xP2 = vm.cursorXProperty().get() - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(vm.cursorAngelProperty().get())),
                        yP2 = vm.cursorYProperty().get() + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(vm.cursorAngelProperty().get())),
                        xP3 = vm.cursorXProperty().get() + TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.cos(Math.toRadians(vm.cursorAngelProperty().get())),
                        yP3 = vm.cursorYProperty().get() - TURTLE_SIZE * Math.sin(Math.toRadians(30)) * Math.sin(Math.toRadians(vm.cursorAngelProperty().get()));

                Point start = new Point((xP2 + xP3) / 2, (yP2 + yP3) / 2);
                Point end = new Point(vm.cursorXProperty().get(), vm.cursorYProperty().get());

                vm.addSegment(start, end);

                for (int i = 0; i < vm.getWorld().getSegments().size(); ++i){
                    gc.strokeLine(
                            vm.getWorld().getSegments().get(i).getStart().getX(),
                            vm.getWorld().getSegments().get(i).getStart().getY(),
                            vm.getWorld().getSegments().get(i).getEnd().getX(),
                            vm.getWorld().getSegments().get(i).getEnd().getY()
                    );
                }
            }
        }
        else {
            vm.clearSegment();
        }
    }

    private void openFile(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir");
        fileChooser.setInitialDirectory(new File("scratch"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
        );

        File file = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (file != null){
            vm.openFile(file);
        }
    }


    private void saveFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer sous");
        fileChooser.setInitialDirectory(new File("scratch"));

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
        );

        File file = fileChooser.showSaveDialog(this.getScene().getWindow());

        if (file != null) {
            vm.saveFile(file);
        }
    }

    private void render() {

    }

}
