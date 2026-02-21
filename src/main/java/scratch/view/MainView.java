package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.Action;
import scratch.viewmodel.MainViewModel;


public class MainView extends VBox {
    private final MainViewModel vm;

    private final Label programLabel = new Label("Programme");
    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");

    private final ListView<Action> actionListView = new ListView<>();

    private final VBox programBox = new VBox();
    private final HBox buttonBox = new HBox();
    private final VBox menuBox = new VBox();



    public MainView(MainViewModel vm) {
        this.vm = vm;
        this.getChildren().add(layoutControls());
    }

    private VBox layoutControls(){
        VBox root = new VBox();
        root.getChildren().addAll(configureMenuBox(), ConfigureMainView(), configureDetailSection());
        return root;

    }

    private HBox ConfigureMainView(){
        HBox mainView = new HBox();
        mainView.setSpacing(20);
        mainView.getChildren().add(ConfigureProgramBox());
        return mainView;
    }

    private VBox ConfigureProgramBox(){
        programBox.setPadding(new Insets(10));
        programBox.setSpacing(15);
        programBox.setPrefWidth(480);
        programBox.getChildren().addAll(programLabel, actionListView, configureButtonBox());
        return programBox;
    }

    private HBox configureButtonBox(){
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        ButtonsBox();
        buttonBox.getChildren().addAll(upBtn, downBtn, duplicateBtn, deleteBtn, clearBtn);
        return buttonBox;
    }

    private void ButtonsBox(){
        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);
    }

    private VBox configureMenuBox(){
        Menu menu = new Menu("File");
        MenuBar menuBar = new MenuBar(menu);
        menuBox.getChildren().add(menuBar);
        return menuBox;
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


}
