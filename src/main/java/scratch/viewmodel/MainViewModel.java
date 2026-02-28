package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;

public class MainViewModel {
    private final Cursor cursor;
    private final Program program;
    private final   CursorWorld world;

    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty executionIndex = new SimpleIntegerProperty(-1);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canAdd = catalogIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);


    private BooleanProperty programLoaded = new SimpleBooleanProperty(false);

    public MainViewModel(Cursor cursor){
        this.cursor = cursor;
        this.program = new Program();
        this.world = this.cursor.getWorld();
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

    public boolean isProgramLoaded() {
        return programLoaded.get();
    }

    public BooleanProperty programLoadedProperty() {
        return programLoaded;
    }

    public BooleanBinding canLoad() {
        return Bindings.isNotEmpty(program.getActions()).and(programLoaded.not());
    }

    public BooleanProperty canExecute() {
        return programLoaded;
    }

    public void loadProgram(){
        world.reset();
        executionIndex.set(0);
        programLoaded.set(true);
        if (!program.getActions().isEmpty()){
            actionIndex.set(0);
        }
    }



}
