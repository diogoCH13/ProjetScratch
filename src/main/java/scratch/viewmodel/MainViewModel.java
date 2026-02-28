package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;

public class MainViewModel {
    private final Cursor cursor;
    private final Program program;

    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
    private final StringProperty actionDetails = new SimpleStringProperty("(aucune action sélectionnée)");
    private final IntegerProperty actionValue = new SimpleIntegerProperty();

    private final BooleanBinding canUp = actionIndex.greaterThan(0);

    public MainViewModel(Cursor cursor) {
        this.cursor = cursor;
        this.program = new Program();
    }

    public IntegerProperty catalogIndexProperty() {
        return catalogIndex;
    }

    public IntegerProperty actionIndexProperty() {
        return actionIndex;
    }

    public StringProperty actionDetailsProperty() {
        return actionDetails;
    }

    public IntegerProperty actionValueProperty(){
        return actionValue;
    }

    public ObservableList<ActionType> getPalettAction() {
        return ActionsCatalog.getPalettActions();
    }

    public ObservableList<Action> getProgramActions() {
        return program.getActions();
    }

    public Point getPoint() {
        return cursor.getPosition();
    }

    public void addAction() {
        int i = catalogIndex.get();
        program.addAction(i);
    }

    public void deleteAction() {
        int i = actionIndex.get();
        if (i >= 0) {
            program.deleteAction(i);
            updateActionDetails();
        }
    }

    public void upAction() {
        int i = actionIndex.get();
        program.up(i);
        if (i > 0) {
            actionIndex.set(i - 1);
        }
    }

    public BooleanBinding canUp() {
        return canUp;
    }

    public void downAction() {
        int i = actionIndex.get();
        if (i >= 0 && i < getProgramActions().size() - 1) {
            program.down(i);
            actionIndex.set(i + 1);
        }
    }

    public BooleanBinding canDown() {
        return Bindings.createBooleanBinding(() -> {
            int idx = actionIndex.get();
            return idx >= 0 && idx < getProgramActions().size() - 1;
        }, getProgramActions(), actionIndex);
    }

    public void duplicateAction(Action action) {
        program.duplicate(action);
    }

    public BooleanBinding canDuplicate(){
        return Bindings.createBooleanBinding(() -> !getProgramActions().isEmpty(), getProgramActions());
    }

    public void clearAction() {
        program.removeAll();
        actionIndex.set(-1);
    }

    public void updateActionDetails() {
        int idx = actionIndex.get();

        if (!getProgramActions().isEmpty() && idx >= 0 && idx < getProgramActions().size()) {
            Action action = getProgramActions().get(idx);

            switch (action.actionType()) {
                case MOVE_FORWARD:
                    actionDetails.set("Avancer de");
                    actionValue.setValue(action.getValue());
                    break;
                case TURN_LEFT:
                    actionDetails.set("Angle vers la gauche");
                    actionValue.setValue(action.getValue());
                    break;
                case TURN_RIGHT:
                    actionDetails.set("Angle vers la droite");
                    actionValue.setValue(action.getValue());
                    break;
                case PEN_DOWN:
                    actionDetails.set("Abaisser stylo");
                    actionValue.setValue(0);
                    break;
                case PEN_UP:
                    actionDetails.set("Lever stylo");
                    actionValue.setValue(0);
            }

        }
        else {
            actionDetails.set("(aucune action sélectionnée)");
            actionValue.setValue(0);
        }
    }

}
