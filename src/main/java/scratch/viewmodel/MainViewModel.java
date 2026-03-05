package scratch.viewmodel;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;

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

    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);

    public MainViewModel(Cursor cursor) {
        this.cursor = cursor;
        this.program = new Program();
        this.world = this.cursor.getWorld();

        configureBindingsAndListeners();
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

    public void addAction() {
        int i = catalogIndex.get();
        program.addAction(i);
        programLoaded.set(false);
        running.set(false);
        executionIndex.set(-1);
    }

    public void deleteAction() {
        int i = actionIndex.get();
        if (i >= 0) {
            long value = (long) getProgramActions().get(i).getValue();
            actionValue.set(String.valueOf(value));
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
            int idx = actionIndex.get();
            return idx >= 0 && idx < getProgramActions().size() - 1;
        }, getProgramActions(), actionIndex);
    }

    public void duplicateAction(Action action) {
        program.duplicate(action);
    }

    public BooleanBinding canDuplicate() {
        return Bindings.createBooleanBinding(() -> !getProgramActions().isEmpty(), getProgramActions());
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
//        return Bindings.isNotEmpty(program.getActions()).and(programLoaded.not());
        return Bindings.isNotEmpty(program.getActions());
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
                long value = (long) getProgramActions().get(actionIndex.get()).getValue();

                detail = switch (action.actionType()) {
                    case MOVE_FORWARD -> "Avancer de";
                    case TURN_LEFT -> "Angle vers la gauche";
                    case TURN_RIGHT -> "Angle vers la droite";
                    case PEN_DOWN -> "Abaisser stylo";
                    default -> "Lever stylo";
                };
            }
            return detail;
        }, getProgramActions(), actionIndex);
    }

    public StringBinding actionDetailValueBindings() {
        return Bindings.createStringBinding(() -> {
            String detailValue = "";

            if (!getProgramActions().isEmpty() && isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());

                detailValue = switch (action.actionType()) {
                    case MOVE_FORWARD -> "pixels";
                    case TURN_LEFT, TURN_RIGHT -> "degrés";
                    default -> "";
                };
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

    public void configureBindingsAndListeners() {
        actionValue.addListener((obs, oldVal, newVal) -> {
            if (isValidIndex() && newVal != null) {
                Action action = getProgramActions().get(actionIndex.get());
                try{
                    int value = Integer.valueOf(newVal);

                    switch (action.actionType()) {
                        case MOVE_FORWARD:
                            if (value > 0 && value <= 100) {
                                getProgramActions().get(actionIndex.get()).setValue(-value);
                                errorValue.set(false);
                            }
                            else{
                                errorValue.set(true);
                            }
                            break;
                        case TURN_LEFT:
                            if (value > 0 && value <= 180) {
                                getProgramActions().get(actionIndex.get()).setValue(value);
                                errorValue.set(false);
                            }
                            else{
                                errorValue.set(true);
                            }
                            break;
                        case TURN_RIGHT:
                            if (value > 0 && value <= 180) {
                                getProgramActions().get(actionIndex.get()).setValue(-value);
                                errorValue.set(false);
                            }
                            else{
                                errorValue.set(true);
                            }
                            break;
                        default:
                            errorValue.set(false);
                    }
                }
                catch(NumberFormatException exc){
                    errorValue.set(true);
                }
            }
        });
        actionIndex.addListener((obs, oldVal, newVal) -> {
            if (isValidIndex()) {
                String test = String.valueOf((long) Math.abs(getProgramActions().get(actionIndex.get()).getValue()));
                actionValue.set(test);
            }
        });

    }

    public void reset(){
        world.clearSegments();
        cursor.reset();
        executionIndex.set(0);
//        if (!program.getActions().isEmpty()){
//            actionIndex.set(0);
//        }
    }

    public void loadProgram(){
        reset();
        programLoaded.set(true);
    }

    public void execute(){
        running.set(true);
        actionIndex.set(0); //selection 1er élément
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
//        System.out.println(
//                "X : " + cursor.getPosition().getX() +
//                        " Y : " + cursor.getPosition().getY()
//        );
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
}