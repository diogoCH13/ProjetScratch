package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActionsCatalog {

    private static final ObservableList<ActionType> basicActions = FXCollections.observableArrayList(
            ActionType.VAR_DECLARATION,
            ActionType.VAR_ASSIGNMENT,
            ActionType.INCREMENT_VARIABLE,
            ActionType.MOVE_FORWARD,
            ActionType.TURN_LEFT,
            ActionType.TURN_RIGHT,
            ActionType.REPEAT,
            ActionType.END_REPEAT,
            ActionType.PEN_UP,
            ActionType.PEN_DOWN
    );

    private static final ObservableList<ActionType> advancedActions =
            FXCollections.observableArrayList(ActionType.DRAW_RECTANGLE);

    public static ObservableList<ActionType> getPalettActions() {
        return FXCollections.unmodifiableObservableList(basicActions);
    }

    public static ObservableList<ActionType> getBasicActions() {
        return FXCollections.unmodifiableObservableList(basicActions);
    }

    public static ObservableList<ActionType> getAdvancedActions() {
        return FXCollections.unmodifiableObservableList(advancedActions);
    }

    public static ActionType getAction(int index){
        if (index <= -1 || index > basicActions.size() - 1){
            return null;
        }
        return basicActions.get(index);
    }
}

