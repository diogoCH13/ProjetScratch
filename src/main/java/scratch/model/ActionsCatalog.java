package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActionsCatalog {

    private static final ObservableList<ActionType> palettActions =
            FXCollections.observableArrayList(ActionType.values());

    public static ObservableList<ActionType> getPalettActions() {
        return FXCollections.unmodifiableObservableList(palettActions);
    }

    public static ActionType getAction(int index){
        if (index <= -1 || index > palettActions.size() - 1){
            return null;
        }
        return palettActions.get(index);
    }
}

