package scratch.viewmodel;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import scratch.model.Cursor;
import scratch.model.Point;
import scratch.model.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class MainViewModel {

    private boolean updatingActionValue = false;

    private Timeline timeline;
    private final Cursor cursor;
    private final Program program;
    private final CursorWorld world;

    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
    private final StringProperty actionValue = new SimpleStringProperty();
    private final StringProperty actionVar = new SimpleStringProperty();
    private final BooleanProperty errorValue = new SimpleBooleanProperty(false);
    private final BooleanProperty runtimeValue = new SimpleBooleanProperty(false);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding actionsValide;

    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty executionFinished = new SimpleBooleanProperty(false);
    private final BooleanProperty autoMode = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1);

    private ObservableList<VariableRow> variableValueList = FXCollections.observableArrayList();

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

    public ObservableList<VariableRow> getVariableValueList() {
        return variableValueList;
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

    public StringProperty actionVarProperty() {
        return actionVar;
    }

    public BooleanProperty runningProperty(){
        return running;
    }

    public BooleanProperty autoModeProperty(){
        return autoMode;
    }

    public BooleanProperty errorValueProperty(){
        return errorValue;
    }

    public BooleanProperty runtimeValueProperty(){
        return runtimeValue;
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

    public void addAction() {
        int destinationIndex = actionIndex.get();
        int i = catalogIndex.get();
        if (destinationIndex >= 0){
            program.addAction(i, destinationIndex + 1);
        }
        else {
            program.addAction(i, program.getActions().size());
        }
        programLoaded.set(false);
        running.set(false);
    }

    public void deleteAction() {
        int i = actionIndex.get();
        if (isValidIndex()) {
            program.deleteAction(i);
            refreshValue();
        }
        errorValue.set(false);
        programLoaded.set(false);
        running.set(false);
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
        int destinationIndex = actionIndex.get();
        if (destinationIndex >= 0){
            program.duplicate(destinationIndex + 1, action);
        }
        else {
            program.duplicate(program.getActions().size(), action);
        }
        programLoaded.set(false);
        running.set(false);


    }

    public BooleanBinding canDuplicate() {
        return Bindings.isNotEmpty(getProgramActions())
                .and(actionIndex.greaterThanOrEqualTo(0));
    }

    public void clearAction() {
        program.removeAll();
        actionIndex.set(-1);
        refreshValue();
        errorValue.set(false);
        programLoaded.set(false);
        running.set(false);
        executionFinished.set(false);
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

    public boolean isAutoMode(){
        return autoMode.get();
    }

    public BooleanProperty programLoadedProperty() {
        return programLoaded;
    }

    public BooleanBinding canLoad() {

        return Bindings.createBooleanBinding(() -> {
           return !getProgramActions().isEmpty() &&
                   actionsValide.get() &&
                   program.declarationIsFirstAction() &&
                   program.isRepeatCounterModificationValid() &&
                   program.isEndRepeatExist() &&
                   program.actionsNotEmpty() &&
                   program.varIsExist() &&
                   program.declareIsUnique();
        }, getProgramActions(), actionValueProperty(), actionIndexProperty());
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

    //créer un booleanBindings qui ce recalul à chaque fois que actionIndex, ou la liste, ou actionValue reçois une modification
    public StringBinding actionDetailsBinding() {
        return Bindings.createStringBinding(() -> {
            //si on a sélectionné aucune action de la liste (actionList) alors on affiche
            String detail = "aucune action sélectionnées";

            //si la liste n'est pas vide et qu'on a sélectionné un élément de la liste
            if (!getProgramActions().isEmpty() && isValidIndex()) {
                //on récupère l'action courante
                Action action = getProgramActions().get(actionIndex.get());
                //on récupère le détail de l'action en fonction de son type
                detail = action.actionDetail();
            }
            //on retourne le résultat final de detail
            return detail;

            //le bindings ce recalcul à chaque fois que l'un de ces property change
        }, actionIndexProperty(), getProgramActions());
    }

    public StringBinding actionDetailValueBindings() {
        return Bindings.createStringBinding(() -> {

            String detailValue = "";

            //
            if (!getProgramActions().isEmpty() && isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                detailValue = action.actionDetailValue();
            }
            return detailValue;
        }, actionIndexProperty(), getProgramActions());
    }

    public BooleanBinding disableTextFieldByActionType(){
        return Bindings.createBooleanBinding(() -> {
            boolean textField = false;
            if (isValidIndex()){
                Action action = getProgramActions().get(actionIndex.get());
                textField = switch (action.actionType()) {
                    case PEN_UP, PEN_DOWN, END_REPEAT -> false;
                    default -> true;
                };
            }
            return textField;
        }, actionIndexProperty(), getProgramActions(), actionValueProperty());
    }

    public BooleanBinding disableVarField(){
        return Bindings.createBooleanBinding(() -> {
            boolean isValid = false;
            if (isValidIndex()){
                Action action = getProgramActions().get(actionIndex.get());
                isValid = action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE;
            }
            return isValid;
        }, getProgramActions(), actionIndexProperty());
    }

    public BooleanBinding disableIncrement(){
        return Bindings.createBooleanBinding(() -> {
            boolean isLitteral = false;
            if (isValidIndex()){
                Action currentAction = getProgramActions().get(actionIndex.get());
                switch (currentAction.actionType()){
                    case VAR_ASSIGNMENT, INCREMENT_VARIABLE:
                        if (actionVar.get() != null){
                            String value = actionVar.get();
                            if (program.isInteger(value)){
                                isLitteral = true;
                            }
                        }
                        break;
                    case MOVE_FORWARD, TURN_LEFT, TURN_RIGHT, REPEAT:
                        if (actionValue.get() != null){
                            String value = actionValue.get();
                            if (program.isInteger(value)){
                                isLitteral = true;
                            }
                        }
                }
            }
            return isLitteral;
        }, getProgramActions(), actionValueProperty(), actionIndexProperty(), actionVarProperty());
    }

    public void configureBindingsAndListeners() {

        actionValue.addListener((obs, oldVal, newVal) -> {
            if (updatingActionValue) {
                return;
            }
            if (isValidIndex() && newVal != null) {
                Action action = getProgramActions().get(actionIndex.get());
                if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE) {
                    setTargetName(action, newVal);
                }
                else {
                    setValue(action, newVal);
                }

                if (running.get()) {
                    running.set(false);
                    programLoaded.set(false);
                }
            }
        });

        actionVar.addListener((obs, oldVal, newVal) -> {
            if (updatingActionValue) {
                return;
            }
            if (isValidIndex() && newVal != null && !newVal.isEmpty()) {
                Action action = getProgramActions().get(actionIndex.get());
                setValue(action, newVal);
            }
            if (running.get()) {
                running.set(false);
                programLoaded.set(false);
            }
        });

        actionIndex.addListener((obs, oldVal, newVal) -> {
            if (isValidIndex()) {
                updatingActionValue = true;
                Action action = getProgramActions().get(actionIndex.get());
                if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE) {
                    actionValue.set(action.getVariableTargetName());
                    actionVar.set(action.getExpression().toString());
                }
                else{
                    actionValue.set(action.getExpression().toString());
                }
                updatingActionValue = false;
                errorValue.set(false);
            }
        });

        speedProperty().addListener((obs, oldVal, newVal) -> {
            if (timeline != null) {
                timeline.setRate(1 / newVal.doubleValue());
            }
        });
    }

    public BooleanProperty executionFinishedProperty() {
        return executionFinished;
    }

    public void reset(){
        world.clearSegments();
        variableValueList.clear();
        cursor.reset();
        actionIndex.set(-1);
        catalogIndex.set(0);
        executionFinished.set(false);
        running.set(false);
        runtimeValue.set(false);
        program.resetStack();
        if (timeline != null){
            timeline.stop();
        }
    }

    public void loadProgram(){
        reset();
        programLoaded.set(true);
        executionFinished.set(false);

    }

    public void execute(){
        world.clearSegments();
        program.clearDeclarationList();
        cursor.reset();
        running.set(true);
        executionFinished.set(false);
        program.startExecution();
        actionIndex.set(program.getCurrentIndex());
    }

    public void next(){
        try{
            program.executeNext(cursor);
            runtimeValue.set(false);
            updateVariablesList();
            int index = program.getCurrentIndex();
            if(index >= 0){
                actionIndex.set(index);
            }

            if (program.isExecutionFinished()){
                running.set(false);
                executionFinished.set(true);
            }
        }catch(RuntimeException exception){
            runtimeValue.set(true);
            running.set(false);
            executionFinished.set(true);
        }
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

    public DoubleProperty speedProperty() {
        return speed;
    }

    public void newFile(){
        program.clearAll();
        world.clearSegments();
        cursor.reset();
        catalogIndex.set(-1);
        actionIndex.set(-1);

        running.set(false);
        programLoaded.set(false);
        executionFinished.set(false);
    }

    public void saveFile(File file){
        program.save(file);
    }

    public void openFile(File file){
        reset();
        program.load(file);
        programLoaded.set(false);
    }

    public void setValue(Action action, String value){
        if (program.isInteger(value)){
            int val = Integer.parseInt(value);
            verificationValueRules(action, val);
        }
        else{
            verificationNameRules(action, value);
        }
    }

    public void verificationValueRules(Action action, int value){
        errorValue.set(true);
        switch (action.actionType()){
            case MOVE_FORWARD, TURN_RIGHT:
                if (action.valueIsCorrect(value)){
                    action.setExpression(new LitteralExpression(-value));
                    errorValue.set(false);
                }
                break;
            case TURN_LEFT, VAR_ASSIGNMENT, REPEAT, INCREMENT_VARIABLE:
                if (action.valueIsCorrect(value)){
                    action.setExpression(new LitteralExpression(value));
                    errorValue.set(false);
                }
                break;
        }
    }

    public void verificationNameRules(Action action, String name){
        errorValue.set(true);
        if (action.actionType() == ActionType.VAR_DECLARATION && name.isEmpty()){
            action.setExpression(new VariableExpression(name));
        }
        else{
            if (!name.isEmpty() && isLetterOrUnderscore(name.charAt(0))){
                errorValue.set(false);
                if (nameIsCorrect(name)){
                    action.setExpression(new VariableExpression(name));
                }
                else{
                    errorValue.set(true);
                }
            }
        }
    }

    public boolean isLetterOrUnderscore(Character name){
        return name == '_' || (name >= 'A' && name <= 'z');
    }

    public boolean nameIsCorrect(String name){
        boolean isCorrect = true;
        for (int i = 1; i < name.length() && isCorrect; ++i){
            if (!(isLetterOrUnderscore(name.charAt(i)) || (name.charAt(i) >= '0' && name.charAt(i) <= '9'))){
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    public void setTargetName(Action action, String name){
        errorValue.set(true);
        if (name.isEmpty()){
            action.setVariableTargetName(name);
        }
        else{
            if (isLetterOrUnderscore(name.charAt(0))){
                errorValue.set(false);
                if (nameIsCorrect(name)){
                    action.setVariableTargetName(name);
                }
                else{
                    errorValue.set(true);
                }
            }
        }
    }

    public void autoNext(){

        if (timeline != null){
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e->{
            if (!runtimeValueProperty().get()){
                next();
            }
            if (program.isExecutionFinished()){
                timeline.stop();
                running.set(false);
                executionFinished.set(true);
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);  //boucle qui s'arrête pas
        running.set(true);
        executionFinished.set(false);
        timeline.setRate(1 / speed.get());
        timeline.play();

    }

    public void stopAutoMode(){
        timeline.stop();
        running.set(false);
//        executionFinished.set(true);

    }

    public void refreshValue(){
        actionIndex.set(getProgramActions().isEmpty() ? -1 : actionIndex.get());
        if (actionIndex.get() != -1){
            Action currentAction = getProgramActions().get(actionIndex.get());
            actionValue.set(currentAction.getExpression().toString());
        }
        else{
            actionValue.set("");
        }
    }

    public int getIndentationLevel(int index){
        int level = 0;
        List<Action> actionList = program.getActions();
        for (int i = 0; i < index; ++i){
            Action action = actionList.get(i);
            if (action.actionType() == ActionType.REPEAT){
                ++level;
            }else if (action.actionType() == ActionType.END_REPEAT){
                if (level > 0){
                    --level;
                }
            }
        }
        Action currentAction = actionList.get(index);
        if (currentAction.actionType() == ActionType.END_REPEAT){
            if (level > 0){
                --level;
            }
        }
        return level;
    }

    public void updateVariablesList(){
        variableValueList.clear();
        for (Map.Entry<String, Integer>entry : program.getDeclarationList().entrySet()){
            variableValueList.add(new VariableRow(entry.getKey(), entry.getValue()));
        }
    }

    public void increment(boolean isIncrement){
        if (isValidIndex() && !getProgramActions().isEmpty()){
            Action currentAction = getProgramActions().get(actionIndex.get());
            program.incrementDuringCompilation(currentAction, isIncrement ? 1 : -1);
            if (currentAction.actionType() == ActionType.VAR_ASSIGNMENT || currentAction.actionType() == ActionType.INCREMENT_VARIABLE){
                actionVar.set(currentAction.getExpression().toString());
            }
            else{
                actionValue.set(currentAction.getExpression().toString());
            }
        }
    }
}