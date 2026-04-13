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

    private Timeline timeline;
    private final Cursor cursor;
    private final Program program;
    private final CursorWorld world;

    private final IntegerProperty catalogIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
//    private final IntegerProperty executionIndex = new SimpleIntegerProperty(-1);
    private final StringProperty actionValue = new SimpleStringProperty();
    private final StringProperty actionVar = new SimpleStringProperty();
    private final BooleanProperty errorValue = new SimpleBooleanProperty(false);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canAdd = catalogIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);
    private final BooleanBinding actionsValide;

    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty executionFinished = new SimpleBooleanProperty(false);
    private boolean updatingActionValue = false;
    private final BooleanProperty autoMode = new SimpleBooleanProperty(false);

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

//    public IntegerProperty executionIndexProperty(){
//        return executionIndex;
//    }

    public BooleanProperty runningProperty(){
        return running;
    }

    public BooleanProperty autoModeProperty(){
        return autoMode;
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
//        executionIndex.set(-1);
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
        program.duplicate(action);
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
//        executionIndex.set(-1);
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

    public boolean isAutoMode(){
        return autoMode.get();
    }

    public BooleanProperty programLoadedProperty() {
        return programLoaded;
    }

    public BooleanBinding canLoad() {
        return Bindings.isNotEmpty(program.getActions()).and(actionsValide).and(varDeclarationIsBefore()).
                and(Bindings.createBooleanBinding(()-> program.isRepeatCounterModificationValid(),
                        program.getActions()
                        ,actionValueProperty()
                )).and(Bindings.createBooleanBinding(()->
                        program.isEndRepeatExist(),
                        program.getActions()
                        )).and((Bindings.createBooleanBinding(() ->
                        program.actionsNotEmpty(),
                        program.getActions(), actionValue))).and(Bindings.createBooleanBinding(()->
                            program.varIsExist(),
                            program.getActions()
                        ));
        //.and(allActionIsValid());
        //.and(canLoadProgram).and(allActionIsValid());
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
                detail = action.actionDetail();
            }
            return detail;
        }, actionIndexProperty(), getProgramActions(), actionValueProperty());
    }

    public StringBinding actionDetailValueBindings() {
        return Bindings.createStringBinding(() -> {
            String detailValue = "";

            if (!getProgramActions().isEmpty() && isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                detailValue = action.actionDetailValue();
            }
            return detailValue;
        }, actionIndexProperty(), getProgramActions(), actionValueProperty());
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

    public BooleanBinding verificationVarInList(){
        return Bindings.createBooleanBinding(() -> {
            boolean textField = true;
            if (isValidIndex()){
                int pos = actionIndex.get();
                textField = verificationVarBeforeExecution();
            }
            return textField;
        }, actionValueProperty(), getProgramActions(), actionIndexProperty());
    }

    public BooleanBinding varDeclarationIsBefore(){
        return Bindings.createBooleanBinding(() -> {
            boolean isBefore = true;

            for(Action action : getProgramActions()){
                if (action.actionType() == ActionType.VAR_DECLARATION && isBefore){
                    int index = getProgramActions().indexOf(action);
                    for (int i = 0; i < index && isBefore; ++i){
                        ActionType type = getProgramActions().get(i).actionType();
                        if (type != ActionType.VAR_DECLARATION){
                            isBefore = false;
                        }
                    }
                }
            }
            return isBefore;
        }, getProgramActions(), actionValueProperty());
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

//    public BooleanBinding checkIfActionIsInit(){
//        return Bindings.createBooleanBinding(() -> {
//            boolean isInit = true;
//            if (isValidIndex() && !getProgramActions().isEmpty()){
//                for (Action action : getProgramActions()){
//                    String name = action.getExpression().toString();
//                    if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.VAR_DECLARATION || action.actionType() == ActionType.INCREMENT_VARIABLE){
//                        if (name.equals("0") || name.isEmpty()){
//                            isInit = false;
//                        }
//                    }
//                }
//            }
//            return isInit;
//        }, getProgramActions(), actionValueProperty());
//    }

//    public BooleanBinding declarationIsValid(){
//        return Bindings.createBooleanBinding(() -> {
//           boolean isValid = false;
//           if (isValidIndex() && !getProgramActions().isEmpty()){
//               Action action = getProgramActions().get(actionIndex.get());
//               if (action.actionType() == ActionType.VAR_DECLARATION){
//                   String name = action.getExpression().toString();
//                   System.out.println(action.getIsValid());
//                   action.setIsValid(!name.equals("0") && !name.isEmpty());
//                   System.out.println(action.getIsValid());
//                   isValid = action.getIsValid();
//               }
//               else{
//                   isValid = true;
//               }
//
//           }
//           return isValid;
//        }, actionValueProperty(), actionIndexProperty());
//    }

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
//                    executionIndex.set(-1);
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
//                    executionIndex.set(-1);
            }
        });

        actionIndex.addListener((obs, oldVal, newVal) -> {
            if (isValidIndex()) {
                updatingActionValue = true;
                Action action = getProgramActions().get(actionIndex.get());
//                String test = action.actionType() == ActionType.VAR_DECLARATION ? action.getVar() : String.valueOf((long) Math.abs(action.getValue()));
                if (action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE) {
                    actionValue.set(action.getVariableTargetName());
                    actionVar.set(action.getExpression().toString());
                }
                else{
                    actionValue.set(action.getExpression().toString());
                }
                updatingActionValue = false;
//                System.out.println(errorValue.getValue());
                errorValue.set(false);
            }
        });
    }

    public BooleanProperty executionFinishedProperty() {
        return executionFinished;
    }

    public void reset(){
        world.clearSegments();
        cursor.reset();
        actionIndex.set(-1);
//        executionIndex.set(0);
        catalogIndex.set(0);
        executionFinished.set(false);
        running.set(false);
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
        cursor.reset();
        running.set(true);
        executionFinished.set(false);
        program.startExecution();
        actionIndex.set(program.getCurrentIndex());

//        actionIndex.set(0);
//        executionIndex.set(0);

    }

    public void next(){
//        int i = executionIndex.get();
//        program.executeNext(cursor);
//        i++;
//        executionIndex.set(i);
//        if (i < program.getActions().size()){
//            actionIndex.set(i);
//        }else{
//            running.set(false);
//        }
        program.executeNext(cursor);
        updateVariablesList();
        int index = program.getCurrentIndex();
        if(index >= 0){
            actionIndex.set(index);
        }

        if (program.isExecutionFinished()){
            running.set(false);
            executionFinished.set(true);
        }
    }

//    public BooleanBinding isExecutionFinished(){
//        return Bindings.createBooleanBinding(() ->
//                executionIndex.get() >= program.getActions().size(),
//                executionIndex, program.getActions()
//        );
//    }

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
            if (isLetterOrUnderscore(name.charAt(0))){
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

    public boolean verificationVarBeforeExecution(){
        boolean varExist = actionValue.get() != null;

        if (varExist){
            if (!program.isInteger(actionValue.get().trim())){
                varExist = false;
                Action currentAction = getProgramActions().get(actionIndex.get());
                int pos = actionIndex.get();
                for (int i = 0; i <= pos && !varExist; ++i){
                    Action actionList = getProgramActions().get(i);
                    if (actionList.actionType() == ActionType.VAR_DECLARATION){
                        String declarationName = actionList.getExpression().toString();
                        String currentName = currentAction.getExpression().toString();
                        varExist = currentName.equals(declarationName);
                    }
                }
            }
        }
        return varExist;
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

//    public boolean checkIfNameExist(String name){
//        boolean nameExist = false;
//        if (isValidIndex() && !getProgramActions().isEmpty()){
//            for (Action action : getProgramActions()){
//                if (action.actionType() == ActionType.VAR_DECLARATION){
//                    String nameInList = action.getExpression().toString();
//                    if (name.equals(nameInList)){
//                        nameExist = true;
//                        action.setIsValid(true);
//                    }
//                }
//            }
//        }
//        return nameExist;
//    }

    public void autoNext(){

        if (timeline != null){
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e->{
            next();
            if (program.isExecutionFinished()){
                timeline.stop();
                running.set(false);
                executionFinished.set(true);
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);  //boucle qui s'arrête pas
        running.set(true);
        executionFinished.set(false);
        timeline.play();

    }

    public void stopAutoMode(){
        timeline.stop();
        running.set(false);
//        executionFinished.set(true);

    }


    public void setTime(double val){
        if (timeline != null){
            timeline.setRate(1 / val);
        }
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

    public void increment(){
        if (isValidIndex() && !getProgramActions().isEmpty()){
            Action currentAction = getProgramActions().get(actionIndex.get());
            program.incrementDuringCompilation(currentAction);
            if (currentAction.actionType() == ActionType.VAR_ASSIGNMENT || currentAction.actionType() == ActionType.INCREMENT_VARIABLE){
                actionVar.set(currentAction.getExpression().toString());
            }
            else{
                actionValue.set(currentAction.getExpression().toString());
            }
        }
    }

    public void decrement(){
        if (isValidIndex() && !getProgramActions().isEmpty()){
            Action currentAction = getProgramActions().get(actionIndex.get());
            program.decrementDuringCompilation(currentAction);
            if (currentAction.actionType() == ActionType.VAR_ASSIGNMENT || currentAction.actionType() == ActionType.INCREMENT_VARIABLE){
                actionVar.set(currentAction.getExpression().toString());
            }
            else{
                actionValue.set(currentAction.getExpression().toString());
            }
        }
    }
}