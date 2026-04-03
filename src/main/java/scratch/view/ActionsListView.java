package scratch.view;

import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.Action;
import scratch.viewmodel.MainViewModel;

public class ActionsListView extends VBox {
    private final MainViewModel vm;

    private final TextField valueField = new TextField();

    private final Label programLabel = new Label("Programme");
    private final Label detailLabel = new Label("(aucune action sélectionnée)");
    private final Label detailLabel2 = new Label();
    private final Label errorLabel = new Label("Erreur valeur");

    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");

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

        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);

        errorLabel.setTextFill(Color.INDIANRED);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

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

                    setText(action.toString());
                    setTextFill(color);

                    circle.setFill(color);
                    setGraphic(circle);
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
    }

    private void configButtonsDisabling() {
        upBtn.disableProperty().bind(vm.canUp().not());
        deleteBtn.disableProperty().bind(vm.canRemove().not());
        clearBtn.disableProperty().bind(vm.canClearAll().not());
        valueField.disableProperty().bind(vm.disableTextFieldByActionType().not());
        errorLabel.visibleProperty().bind(vm.errorValueProperty());
        errorLabel.managedProperty().bind(vm.errorValueProperty());
        downBtn.disableProperty().bind(vm.canDown().not());
        duplicateBtn.disableProperty().bind(vm.canDuplicate().not());

    }

    private void configSelectionModels() {
        actionListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.actionIndexProperty().set(t1.intValue());
        });

        vm.actionIndexProperty().addListener((observableValue, number, t1) -> {
            actionListView.getSelectionModel().select(t1.intValue());
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

    private void configureBindingsAndListeners() {
        detailLabel.textProperty().bind(vm.actionDetailsBinding());
        valueField.textProperty().bindBidirectional(vm.actionValueProperty());
        detailLabel2.textProperty().bind(vm.actionDetailValueBindings());
    }
}
