package scratch.viewmodel;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;

public class MainViewModel {
    private final Cursor cursor;
    private final Program program;

    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canAdd = catalogIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);

    public MainViewModel(Cursor cursor){
        this.cursor = cursor;
        this.program = new Program();
    }

    public IntegerProperty catalogIndexProperty() {
        return catalogIndex;
    }

    public IntegerProperty actionIndexProperty() {
        return actionIndex;
    }

    public ObservableList<ActionType> getPalettAction(){
        return ActionsCatalog.getPalettActions();
    }

    public ObservableList<Action>getProgramActions(){
        return program.getActions();
    }

    public Point getPoint(){
        return cursor.getPosition();
    }

    public void addAction(){
        int i = catalogIndex.get();
        program.addAction(i);
    }

    public void deleteAction(){
        int i = actionIndex.get();
        program.deleteAction(i);
    }

    public void upAction(){
        int i = actionIndex.get();
        program.up(i);
        if (i  > 0){
            actionIndex.set(i - 1);
        }
    }

    public BooleanBinding canUp() {
        return canUp;
    }

    public BooleanBinding canAdd(){
        return canAdd;
    }

    public BooleanBinding canRemove(){
        return canRemove;
    }
}
