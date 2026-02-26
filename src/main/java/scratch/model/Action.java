package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Action {


    private final Cursor cursor = new Cursor(new Point(250,250));
    private final ObservableList<ActionType> palettActions = FXCollections.observableArrayList(
            ActionType.MOVE_FORWARD,
            ActionType.TURN_LEFT,
            ActionType.TURN_RIGHT,
            ActionType.PEN_UP,
            ActionType.PEN_DOWN
    );

    public abstract void execute(Cursor cursor);

}