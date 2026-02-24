package scratch.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;


public class MainView extends BorderPane /*VBox*/ {
    private final MainViewModel vm;

    private final Label palettLabel = new Label("Palette d'actions");
    private final Label programLabel = new Label("Programme");
    private final Label sceneLabel = new Label("Scene");

    private final Button add = new Button("Ajouter au programme");
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
        palletBox.getChildren().addAll(palettLabel, palletListView, add);
    }

    public void configCenter(){
        programBox.getChildren().addAll(programLabel, actionListView, configureButtonBox(), configureDetailSection());
    }

    public void configRight(){
        sceneBox.getChildren().addAll(sceneLabel, configureSceneGrid(), configureSceneButton());
    }

//    private VBox layoutControls(){
//        VBox root = new VBox();
//        root.getChildren().addAll(configureMenuBox(), ConfigureMainView(), configureDetailSection());
//        return root;
//
//    }

//    private HBox ConfigureMainView(){
//        HBox mainView = new HBox();
//        mainView.setSpacing(20);
//        mainView.getChildren().add(ConfigureProgramBox());
//        return mainView;
//    }

//    private VBox ConfigureProgramBox(){
//        programBox.setPadding(new Insets(10));
//        programBox.setSpacing(15);
//        programBox.setPrefWidth(480);
//        programBox.getChildren().addAll(programLabel, actionListView, configureButtonBox());
//        return programBox;
//    }

    private void ButtonsBox(){
        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);
    }

    private HBox configureButtonBox(){
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        ButtonsBox();
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

    public HBox configureSceneButton(){
        HBox root = new HBox();
        root.getChildren().addAll(loadBtn, executeBtn);
        root.setAlignment(Pos.CENTER);
        return root;
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

        return gc.getCanvas();
    }

    public void beautify(){
        palletBox.setPadding(new Insets(16, 16, 12, 16));
        programBox.setPadding(new Insets(16, 16, 12, 16));
        sceneBox.setPadding(new Insets(16, 16, 12, 16));
//        sceneListView.setPrefSize(500,500);
        
        palletListView.setItems(vm.getPalettAction());
    }


}
