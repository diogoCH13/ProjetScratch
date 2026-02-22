package scratch.viewmodel;

import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.Command;
import scratch.model.Cursor;

public class MainViewModel {
    private final Action action;

    public MainViewModel(Action action){
        this.action = action;
    }

    public ObservableList<Command> getPalettAction(){
        return action.getPalettActions();
    }
}
