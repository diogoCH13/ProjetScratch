package scratch.viewmodel;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;
import javafx.beans.Observable;
import java.io.File;

public class MainViewModel {
    private final Cursor cursor;
    private final Program program;
    private final CursorWorld world;

    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty executionIndex = new SimpleIntegerProperty(-1);
    private final StringProperty actionValue = new SimpleStringProperty();
    private final BooleanProperty errorValue = new SimpleBooleanProperty(false);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canAdd = catalogIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding actionsValide;

    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private boolean updatingActionValue = false;


    public MainViewModel(Cursor cursor) {
        this.cursor = cursor;
        this.program = new Program();
        this.world = this.cursor.getWorld();
        this.actionsValide = Bindings.createBooleanBinding(
                () -> program.isPenStateValid(),
                program.getActions()
        );
        configureBindingsAndListeners();
    }

    public Cursor getCursor(){
        return cursor;
    }

    public IntegerProperty catalogIndexProperty() {
        return catalogIndex;
    }

    public IntegerProperty actionIndexProperty() {
        return actionIndex;
    }

    public StringProperty actionValueProperty() {
        return actionValue;
    }

    public IntegerProperty executionIndexProperty(){
        return executionIndex;
    }

    public BooleanProperty runningProperty(){
        return running;
    }

    public BooleanProperty errorValueProperty(){
        return errorValue;
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

    public CursorWorld getWorld(){
        return world;
    }

    public void addSegment(Point start, Point end){
        world.addSegment(new Segment(start, end));
    }

    public void clearSegment(){
        world.clearSegments();
    }

    public void addAction() {
        int i = catalogIndex.get();
        program.addAction(i);
        programLoaded.set(false);
        running.set(false);
        executionIndex.set(-1);
    }

    public void deleteAction() {
        int i = actionIndex.get();
        if (isValidIndex()) {
            program.deleteAction(i);
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
            return actionIndex.get() >= 0 && actionIndex.get()< getProgramActions().size() - 1;
        }, getProgramActions(), actionIndex);
    }

    public void duplicateAction(Action action) {
        program.duplicate(action);
    }

//    public BooleanBinding canDuplicate() {
//        return Bindings.createBooleanBinding(() -> !getProgramActions().isEmpty(), getProgramActions());
//    }

    public BooleanBinding canDuplicate() {
        return Bindings.isNotEmpty(getProgramActions())
                .and(actionIndex.greaterThanOrEqualTo(0));
    }

    public void clearAction() {
        program.removeAll();
        actionIndex.set(-1);
        programLoaded.set(false);
        running.set(false);
        executionIndex.set(-1);
    }

    public BooleanBinding canAdd() {
        return canAdd;
    }

    public BooleanBinding canRemove() {
        return canRemove;
    }

    public boolean isProgramLoaded() {
        return programLoaded.get();
    }

    public boolean isRunning(){
        return running.get();
    }

    public BooleanProperty programLoadedProperty() {
        return programLoaded;
    }

    public BooleanBinding canLoad() {
        return Bindings.isNotEmpty(program.getActions()).and(actionsValide);
    }

    public BooleanProperty canExecute() {
        return programLoaded;
    }

    public boolean isValidIndex() {
        return actionIndex.get() >= 0 && actionIndex.get() < getProgramActions().size();
    }

    public BooleanBinding canClearAll(){
        return Bindings.createBooleanBinding(() -> !getProgramActions().isEmpty(), getProgramActions());
    }

    public StringBinding actionDetailsBinding() {
        return Bindings.createStringBinding(() -> {
            String detail = "aucune action sélectionnées";

            if (!getProgramActions().isEmpty() && isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                detail = action.actionDetail(action.actionType());
            }
            return detail;
        }, getProgramActions(), actionIndex);
    }

    public StringBinding actionDetailValueBindings() {
        return Bindings.createStringBinding(() -> {
            String detailValue = "";

            if (!getProgramActions().isEmpty() && isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                detailValue = action.actionDetailValue(action.actionType());
            }
            return detailValue;
        }, getProgramActions(), actionIndex);
    }

    public BooleanBinding disableTextFieldByActionType(){
        return Bindings.createBooleanBinding(() -> {
            boolean textField = false;
            if (isValidIndex()){
                Action action = getProgramActions().get(actionIndex.get());
                textField = switch (action.actionType()) {
                    case TURN_LEFT, TURN_RIGHT, MOVE_FORWARD -> true;
                    default -> false;
                };
            }
            return textField;
        }, actionIndex, getProgramActions());
    }

//    public void configureBindingsAndListeners() {
//        actionValue.addListener((obs, oldVal, newVal) -> {
//            if (isValidIndex() && newVal != null) {
//                Action action = getProgramActions().get(actionIndex.get());
//                action.setSelectionnedValue(newVal, errorValue, actionIndex.get(), getProgramActions());
//
//            }
//        });
//        actionIndex.addListener((obs, oldVal, newVal) -> {
//            if (isValidIndex()) {
//                String test = String.valueOf((long) Math.abs(getProgramActions().get(actionIndex.get()).getValue()));
//                actionValue.set(test);
//            }
//        });
//
//    }

    public void configureBindingsAndListeners() {

        actionValue.addListener((obs, oldVal, newVal) -> {
            if (updatingActionValue) {
                return;
            }
            if (isValidIndex() && newVal != null) {
                Action action = getProgramActions().get(actionIndex.get());
                action.setSelectionnedValue(newVal, errorValue, actionIndex.get(), getProgramActions());

                if (running.get()) {
                    running.set(false);
                    programLoaded.set(false);
                    executionIndex.set(-1);
                }
            }
        });

        actionIndex.addListener((obs, oldVal, newVal) -> {
            if (isValidIndex()) {
                updatingActionValue = true;
                String test = String.valueOf((long) Math.abs(getProgramActions().get(actionIndex.get()).getValue()));
                actionValue.set(test);
                updatingActionValue = false;
            }
        });
    }

    public void reset(){
        world.clearSegments();
        cursor.reset();
        actionIndex.set(-1);
        executionIndex.set(0);
        catalogIndex.set(0);
    }

    public void loadProgram(){
        reset();
        programLoaded.set(true);
    }

    public void execute(){
        running.set(true);
        actionIndex.set(0);
        executionIndex.set(0);

    }

    public void next(){
        int i = executionIndex.get();
        program.executeNext(i, cursor);
        i++;
        executionIndex.set(i);
        if (i < program.getActions().size()){
            actionIndex.set(i);
        }else{
            running.set(false);
        }
    }

    public BooleanBinding isExecutionFinished(){
        return Bindings.createBooleanBinding(() ->
                executionIndex.get() >= program.getActions().size(),
                executionIndex, program.getActions()
        );
    }

    public DoubleProperty cursorXProperty(){
        return cursor.getPosition().xProperty();
    }

    public DoubleProperty cursorYProperty(){
        return cursor.getPosition().yProperty();
    }

    public DoubleProperty cursorAngelProperty(){
        return cursor.angleProperty();
    }

    public void newFile(){
        clearAction();
        world.clearSegments();
        cursor.reset();
        catalogIndex.set(-1);
    }

    public void saveFile(File file){
        program.save(file);
    }

    public void openFile(File file){
        program.load(file);
        executionIndex.set(-1);
        running.set(false);
        programLoaded.set(false);
    }




}