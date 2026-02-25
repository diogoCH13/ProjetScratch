package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Action implements Command{

    private final Cursor cursor = new Cursor(new Point());
    private final ObservableList<Command> palettActions = FXCollections.observableArrayList(
            new MoveForward(),
            new TurnLeft(),
            new TurnRight(),
            new PenUp(),
            new PenDown()
    );

    public Cursor getCursor(){
        return cursor;
    }
    public ObservableList<Command> getPalettActions(){
        return FXCollections.unmodifiableObservableList(palettActions);
    }

    @Override
    public void execute() {

    }
}
