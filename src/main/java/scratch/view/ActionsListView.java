package scratch.view;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");
    private final Button increment = new Button("+");
    private final Button decrement = new Button("-");

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

        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);

        errorLabel.setTextFill(Color.INDIANRED);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

//        actionListView.setCellFactory(lv -> new ListCell<>() {
//            @Override
//            protected void updateItem(Action action, boolean empty) {
//                super.updateItem(action, empty);
//
//                if (empty || action == null) {
//                    setText(null);
//                    setGraphic(null);
//                } else {
//                    Circle circle = new Circle(5);
//                    Color color = action.actionType().getColor();
//                    int index = getIndex();
//                    int level = vm.getIndentationLevel(index);
//                    if (level == 0){
//                        setText(action.toString());
//                        setTextFill(color);
//                    }else if (level > 0){
//                        String res = "";
//                        for(int i = 0; i < level; ++i){
//
//                            res += "\t";
//                        }
//                        setText(res + action.toString());
//                        setTextFill(color);
//                    }
//                    circle.setFill(color);
//                    setGraphic(circle);
//                }
//            }
//        });


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

        valueField.setPrefWidth(40);
        valueField.setMaxWidth(40);

        varField.setPrefWidth(50);
        varField.setMaxWidth(50);

        varField.setVisible(false);
        varField.setManaged(false);

        increment.setVisible(false);
        increment.setManaged(false);

        decrement.setVisible(false);
        decrement.setManaged(false);

        VBox detail = new VBox();
        HBox detailContent = new HBox(10);

        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(10));
        detailContent.getChildren().addAll(detailLabel, valueField, detailLabel2, varField, increment, decrement);

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
        varField.visibleProperty().bind(vm.disableVarField());
        varField.managedProperty().bind(vm.disableVarField());
        increment.visibleProperty().bind(vm.disableVarField());
        increment.managedProperty().bind(vm.disableVarField());
        decrement.visibleProperty().bind(vm.disableVarField());
        decrement.managedProperty().bind(vm.disableVarField());
    }

    private void configSelectionModels() {
        actionListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.actionIndexProperty().set(t1.intValue());
        });

        vm.actionIndexProperty().addListener((observableValue, number, t1) -> {
            actionListView.getSelectionModel().select(t1.intValue());
            beautifyValueField();
        });

//        actionListView.getItems().addListener((Observable obs) -> {
//            if (vm.isValidIndex()){
//                Action action = vm.getProgramActions().get(vm.actionIndexProperty().get());
//                if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE) {
//                    vm.actionValueProperty().set(action.getVariableTargetName());
//                }
//                else{
//                    vm.actionValueProperty().set(action.getExpression().toString());
//                }
//                beautifyValueField();
//            }
//            else{
//                vm.actionValueProperty().set("");
//                vm.errorValueProperty().set(false);
//            }
//        });
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
