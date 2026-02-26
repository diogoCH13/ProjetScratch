package scratch.viewmodel;

import javafx.collections.ObservableList;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;

public class MainViewModel {
    private final Cursor cursor;

    public MainViewModel(Cursor cursor){
        this.cursor = cursor;

    }

    public ObservableList<ActionType> getPalettAction(){
        return ActionsCatalog.getPalettActions();
    }

    public Point getPoint(){
        return cursor.getPosition();
    }
}
