package scratch.viewmodel;

import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.Command;
import scratch.model.Cursor;
import scratch.model.Point;

public class MainViewModel {
    private final Action action;

    public MainViewModel(Action action){
        this.action = action;
    }

    public ObservableList<Command> getPalettAction(){
        return action.getPalettActions();
    }

    public Point getPoint(){
        return action.getCursor().getPosition();
    }
}
