package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
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

    public PalettActionsView(PalettListViewModel vm){
        this.vm = vm;
        beautify();
        loadData();
        configActions();
        configSelectionModels();

    }

    private void beautify(){
        getChildren().addAll(palettLabel, palletListView, addBtn);
        setPadding(new Insets(16, 16, 12, 16));

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
                        case REPEAT, END_REPEAT, VAR_DECLARATION, VAR_ASSIGNMENT, INCREMENT_VARIABLE:
                            setTextFill(Color.DARKTURQUOISE);
                            circle.setFill(Color.DARKTURQUOISE);
                            break;

                        default:
                            setTextFill(Color.GREEN);
                            circle.setFill(Color.GREEN);
                    }
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
        palletListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            vm.catalogIndexProperty().set(t1.intValue());
        });

        vm.catalogIndexProperty().addListener((observableValue, number, t1) -> {
            palletListView.getSelectionModel().select(t1.intValue());
        });
    }
}
