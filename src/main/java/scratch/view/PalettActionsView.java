package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.ActionType;
import scratch.viewmodel.PalettListViewModel;

public class PalettActionsView extends VBox {

    private final ListView<ActionType> palletListView = new ListView<>();
    private final PalettListViewModel vm;

    private final Label palettLabel = new Label("Palette d'actions");
    private final Button addBtn = new Button("Ajouter au programme");
    private final CheckBox advancedModeCheckBox = new CheckBox("Mode avancé");

    public PalettActionsView(PalettListViewModel vm){
        this.vm = vm;
        beautify();
        loadData();
        configActions();
        configSelectionModels();

    }

    private void beautify(){
        getChildren().addAll(palettLabel, palletListView, addBtn, advancedModeCheckBox);
        setPadding(new Insets(16, 16, 12, 16));
        advancedModeCheckBox.setPadding(new Insets(10, 10, 8, 10));

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
                    setTextFill(action.getColor());
                    circle.setFill(action.getColor());
                }
            }
        });
    }

    private void loadData(){
        palletListView.setItems(vm.getPalettAction());
    }

    private void configActions(){
        palletListView.setOnMouseClicked(e->{
            if (e.getClickCount() >= 2){
                vm.addAction();
            }
        });

        addBtn.setOnAction(e->{
            vm.addAction();
        });
    }

    private void configSelectionModels() {
        advancedModeCheckBox.selectedProperty().bindBidirectional(vm.advancedModeProperty());

        palletListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.catalogIndexProperty().set(t1.intValue());
        });

        vm.catalogIndexProperty().addListener((observableValue, number, t1) -> {
            palletListView.getSelectionModel().select(t1.intValue());
        });
    }
}
