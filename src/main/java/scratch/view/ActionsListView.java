package scratch.view;

import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import scratch.model.Action;
import scratch.viewmodel.ActionListViewModel;
import scratch.viewmodel.ActionParameterViewModel;

public class ActionsListView extends VBox {
    private final ActionListViewModel vm;

    private final Label programLabel = new Label("Programme");
    private final Label detailLabel = new Label("");
    private final Label errorLabel = new Label("Error");
    private final Label runtimeLabel = new Label("Runtime Error");

    private final Button upBtn = new Button("Monter");
    private final Button downBtn = new Button("Descendre");
    private final Button duplicateBtn = new Button("Dupliquer");
    private final Button deleteBtn = new Button("Supprimer");
    private final Button clearBtn = new Button("Vider tout");

    private final ListView<Action> actionListView = new ListView<>();
    private final HBox buttonBox = new HBox(10);
    private final HBox parameterContent = new HBox(10);

    public ActionsListView(ActionListViewModel vm){
        this.vm = vm;

        beautify();
        loadData();
        configActions();
        configButtonsDisabling();
        configureBindingsAndListeners();
    }

    private void beautify(){
        getChildren().addAll(programLabel, actionListView, configureButtonBox(), configureDetailSection());
        setPadding(new Insets(16, 16, 12, 16));

        errorLabel.setTextFill(Color.INDIANRED);
        runtimeLabel.setFont(new Font(30));
        runtimeLabel.setTextFill(Color.RED);


        upBtn.setPrefWidth(80);
        downBtn.setPrefWidth(80);
        duplicateBtn.setPrefWidth(80);
        deleteBtn.setPrefWidth(80);
        clearBtn.setPrefWidth(80);

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
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

        detailPane.setPrefWidth(700);
        detailPane.setText("Détail de l'action");

        VBox detail = new VBox();

        parameterContent.setAlignment(Pos.CENTER_LEFT);
        parameterContent.setPadding(new Insets(10));
        parameterContent.getChildren().addAll(detailLabel);

        errorLabel.setPadding(new Insets(0,0,0,10));
        detail.getChildren().addAll(parameterContent, errorLabel, runtimeLabel);

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
        downBtn.disableProperty().bind(vm.canDown().not());
        duplicateBtn.disableProperty().bind(vm.canDuplicate().not());

        errorLabel.visibleProperty().bind(vm.errorValueProperty());
        errorLabel.managedProperty().bind(vm.errorValueProperty());
        runtimeLabel.visibleProperty().bind(vm.runtimeValueProperty());
        runtimeLabel.managedProperty().bind(vm.runtimeValueProperty());
    }

    private void configureBindingsAndListeners() {
        detailLabel.textProperty().bind(vm.actionDetailsBinding());

        vm.getParameters().addListener((InvalidationListener) e-> {
            rebuildParameterFields();
        });

        actionListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.actionIndexProperty().set(t1.intValue());
        });

        vm.actionIndexProperty().addListener((observableValue, number, t1) -> {
            actionListView.getSelectionModel().select(t1.intValue());
        });;
    }

    private void rebuildParameterFields() {
        parameterContent.getChildren().clear();
        parameterContent.getChildren().add(detailLabel);

        for (ActionParameterViewModel parameter : vm.getParameters()) {
            Label prefix = new Label(parameter.prefix());

            TextField valueField = new TextField();
            valueField.setPrefWidth(60);
            valueField.textProperty().bindBidirectional(parameter.valueProperty());

            Label suffix = new Label(parameter.suffix());

            Button plus = new Button("+");
            Button minus = new Button("-");

            plus.visibleProperty().bind(parameter.canIncrement());
            plus.managedProperty().bind(parameter.canIncrement());

            minus.visibleProperty().bind(parameter.canIncrement());
            minus.managedProperty().bind(parameter.canIncrement());

            plus.setOnAction(e -> parameter.increment(1));
            minus.setOnAction(e -> parameter.increment(-1));

            parameterContent.getChildren().addAll(prefix, valueField, suffix, plus, minus);
        }
    }
}
