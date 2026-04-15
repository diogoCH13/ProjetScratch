package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import scratch.model.Action;
import scratch.model.ActionType;
import scratch.viewmodel.MainViewModel;

public class ActionsListView extends VBox {
    private final MainViewModel vm;

    private final TextField valueField = new TextField();
    private final TextField varField = new TextField();

    private final Label programLabel = new Label("Programme");
    private final Label detailLabel = new Label("aucune action sélectionnées");
    private final Label detailLabel2 = new Label();
    private final Label errorLabel = new Label("Erreur valeur");
    private final Label runtimeLabel = new Label("Runtime Error");

    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");
    private final Button incrementBtn = new Button("+");
    private final Button decrementBtn = new Button("-");

    private final ListView<Action> actionListView = new ListView<>();
    private final HBox buttonBox = new HBox(10);

    public ActionsListView(MainViewModel vm){
        this.vm = vm;

        beautify();
        loadData();
        configActions();
        configButtonsDisabling();
        configSelectionModels();
        configureBindingsAndListeners();
    }

    private void beautify(){
        getChildren().addAll(programLabel, actionListView, configureButtonBox(), configureDetailSection());
        setPadding(new Insets(16, 16, 12, 16));
        beautifyValueField();
        errorLabel.setTextFill(Color.INDIANRED);
        runtimeLabel.setFont(new Font(30));
        runtimeLabel.setTextFill(Color.RED);


        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);
        valueField.setPrefWidth(40);
        valueField.setMaxWidth(40);
        varField.setPrefWidth(50);
        varField.setMaxWidth(50);

        varField.setVisible(false);
        varField.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        incrementBtn.setVisible(false);
        incrementBtn.setManaged(false);
        decrementBtn.setVisible(false);
        decrementBtn.setManaged(false);
        runtimeLabel.setVisible(false);
        runtimeLabel.setManaged(false);

        actionListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Action action, boolean empty) {
                super.updateItem(action, empty);

                if (empty || action == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Circle circle = new Circle(5);
                    Color color = action.actionType().getColor();

                    int index = getIndex();
                    int level = vm.getIndentationLevel(index);

                    Label label = new Label(action.toString());
                    label.setTextFill(color);

                    HBox box = new HBox(5);
                    box.getChildren().addAll(circle, label);

                    // Décalage à gauche selon level
                    box.setPadding(new Insets(0, 0, 0, level * 20));
                    box.setAlignment(Pos.CENTER_LEFT);

                    circle.setFill(color);

                    setText(null);
                    setGraphic(box);
                }
            }
        });
    }

    private HBox configureButtonBox(){
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10,0,0,0));
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

        VBox detail = new VBox();
        HBox detailContent = new HBox(10);

        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(10));
        detailContent.getChildren().addAll(detailLabel, valueField, detailLabel2, varField, incrementBtn, decrementBtn);

        errorLabel.setPadding(new Insets(0,0,0,10));
        detail.getChildren().addAll(detailContent, errorLabel, runtimeLabel);

        detailPane.setContent(detail);
        detailPane.setExpanded(true);

        TitledPaneBox.getChildren().addAll(detailPane);

        return TitledPaneBox;
    }

    private void loadData() {
        actionListView.setItems(vm.getProgramActions());
    }

    private void configActions() {
        deleteBtn.setOnAction(e -> {
            vm.deleteAction();
        });

        upBtn.setOnAction(e -> {
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

        incrementBtn.setOnAction(e -> {
            boolean increment = true;
            vm.increment(increment);
        });

        decrementBtn.setOnAction(e -> {
            boolean increment = false;
            vm.increment(increment);
        });
    }

    private void configButtonsDisabling() {
        upBtn.disableProperty().bind(vm.canUp().not());
        deleteBtn.disableProperty().bind(vm.canRemove().not());
        clearBtn.disableProperty().bind(vm.canClearAll().not());
        downBtn.disableProperty().bind(vm.canDown().not());
        duplicateBtn.disableProperty().bind(vm.canDuplicate().not());
        incrementBtn.visibleProperty().bind((vm.disableIncrement()));
        incrementBtn.managedProperty().bind((vm.disableIncrement()));
        decrementBtn.visibleProperty().bind((vm.disableIncrement()));
        decrementBtn.managedProperty().bind((vm.disableIncrement()));

        valueField.disableProperty().bind(vm.disableTextFieldByActionType().not());
        varField.visibleProperty().bind(vm.disableVarField());
        varField.managedProperty().bind(vm.disableVarField());
        errorLabel.visibleProperty().bind(vm.errorValueProperty());
        errorLabel.managedProperty().bind(vm.errorValueProperty());
        runtimeLabel.visibleProperty().bind(vm.runtimeValueProperty());
        runtimeLabel.managedProperty().bind(vm.runtimeValueProperty());
    }

    private void configSelectionModels() {
        actionListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.actionIndexProperty().set(t1.intValue());
            if (t1.intValue() >= 0 && t1.intValue() < vm.getProgramActions().size()){
                Action action = vm.getProgramActions().get(t1.intValue());
                valueField.visibleProperty().set(action.actionType() != ActionType.PEN_UP && action.actionType() != ActionType.PEN_DOWN);
            }
        });

        vm.actionIndexProperty().addListener((observableValue, number, t1) -> {
            actionListView.getSelectionModel().select(t1.intValue());
            beautifyValueField();
        });
    }

    private void configureBindingsAndListeners() {
        detailLabel.textProperty().bind(vm.actionDetailsBinding());
        valueField.textProperty().bindBidirectional(vm.actionValueProperty());
        detailLabel2.textProperty().bind(vm.actionDetailValueBindings());
        varField.textProperty().bindBidirectional(vm.actionVarProperty());
    }

    private void beautifyValueField(){
        if (vm.isValidIndex()){
            Action action = vm.getProgramActions().get(vm.actionIndexProperty().get());
            if (action.actionType() == ActionType.VAR_DECLARATION || action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE){
                valueField.setPrefWidth(60);
                valueField.setMaxWidth(60);
            }

            else{
                valueField.setPrefWidth(40);
                valueField.setMaxWidth(40);
            }
        }
    }
}
