package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.ActionType;
import scratch.model.LitteralExpression;
import scratch.model.Program;
import scratch.model.VariableExpression;

import java.util.List;

public class ActionListViewModel {

    private boolean updatingActionValue = false;

    private final Program program;
    private final BooleanProperty programLoaded;
    private final BooleanProperty running;
    private final BooleanProperty executionFinished;
    private final BooleanProperty runtimeValue;

    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
    private final StringProperty actionValue = new SimpleStringProperty();
    private final StringProperty actionVar = new SimpleStringProperty();
    private final BooleanProperty errorValue = new SimpleBooleanProperty(false);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);

    public ActionListViewModel(Program program,
                               BooleanProperty programLoaded,
                               BooleanProperty running,
                               BooleanProperty executionFinished,
                               BooleanProperty runtimeValue) {
        this.program = program;
        this.programLoaded = programLoaded;
        this.running = running;
        this.executionFinished = executionFinished;
        this.runtimeValue = runtimeValue;
        configureBindingsAndListeners();
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

    public BooleanProperty errorValueProperty() {
        return errorValue;
    }

    public BooleanProperty runtimeValueProperty() {
        return runtimeValue;
    }

    public ObservableList<Action> getProgramActions() {
        return program.getActions();
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
            return actionIndex.get() >= 0 && actionIndex.get() < getProgramActions().size() - 1;
        }, getProgramActions(), actionIndex);
    }

    public void duplicateAction(Action action) {
        int destinationIndex = actionIndex.get();
        if (destinationIndex >= 0) {
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

    public boolean isValidIndex() {
        return actionIndex.get() >= 0 && actionIndex.get() < getProgramActions().size();
    }

    public BooleanBinding canClearAll() {
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

        }, actionIndexProperty(), getProgramActions());
    }

    public StringBinding actionDetailValueBindings() {
        return Bindings.createStringBinding(() -> {
            String detailValue = "";

            if (!getProgramActions().isEmpty() && isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                detailValue = action.actionDetailValue();
            }
            return detailValue;
        }, actionIndexProperty(), getProgramActions());
    }

    public BooleanBinding disableTextFieldByActionType() {
        return Bindings.createBooleanBinding(() -> {
            boolean textField = false;
            if (isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                textField = switch (action.actionType()) {
                    case PEN_UP, PEN_DOWN, END_REPEAT -> false;
                    default -> true;
                };
            }
            return textField;
        }, actionIndexProperty(), getProgramActions(), actionValueProperty());
    }

    public BooleanBinding disableVarField() {
        return Bindings.createBooleanBinding(() -> {
            boolean isValid = false;
            if (isValidIndex()) {
                Action action = getProgramActions().get(actionIndex.get());
                isValid = action.actionType() == ActionType.VAR_ASSIGNMENT || action.actionType() == ActionType.INCREMENT_VARIABLE;
            }
            return isValid;
        }, getProgramActions(), actionIndexProperty());
    }

    public BooleanBinding disableIncrement() {
        return Bindings.createBooleanBinding(() -> {
            boolean isLitteral = false;
            if (isValidIndex()) {
                Action currentAction = getProgramActions().get(actionIndex.get());
                switch (currentAction.actionType()) {
                    case VAR_ASSIGNMENT, INCREMENT_VARIABLE:
                        if (actionVar.get() != null) {
                            String value = actionVar.get();
                            if (program.isInteger(value)) {
                                isLitteral = true;
                            }
                        }
                        break;
                    case MOVE_FORWARD, TURN_LEFT, TURN_RIGHT, REPEAT:
                        if (actionValue.get() != null) {
                            String value = actionValue.get();
                            if (program.isInteger(value)) {
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
                    actionValue.set(action.parameterValue(0));
                    actionVar.set(action.parameterValue(1));
                }
                else {
                    actionValue.set(action.parameterValue(0));
                }
                updatingActionValue = false;
                errorValue.set(false);
            }
        });
    }

    public void setValue(Action action, String value) {
        if (program.isInteger(value)) {
            int val = Integer.parseInt(value);
            verificationValueRules(action, val);
        }
        else {
            verificationNameRules(action, value);
        }
    }

    public void verificationValueRules(Action action, int value) {
        errorValue.set(true);

        int parameterIndex = 0;

        if (action.actionType() == ActionType.VAR_ASSIGNMENT
                || action.actionType() == ActionType.INCREMENT_VARIABLE) {
            parameterIndex = 1;
        }

        switch (action.actionType()) {
            case MOVE_FORWARD, TURN_RIGHT:
                if (action.valueIsCorrect(value)) {
                    action.setParameterValue(parameterIndex, String.valueOf(-value), program);
                    errorValue.set(false);
                }
                break;

            case TURN_LEFT, VAR_ASSIGNMENT, REPEAT, INCREMENT_VARIABLE:
                if (action.valueIsCorrect(value)) {
                    action.setParameterValue(parameterIndex, String.valueOf(value), program);
                    errorValue.set(false);
                }
                break;
        }
    }

    public void verificationNameRules(Action action, String name) {
        errorValue.set(true);
        if (action.actionType() == ActionType.VAR_DECLARATION && name.isEmpty()) {
            action.setParameterValue(0, name, program);
//            action.setExpression(new VariableExpression(name));
        }
        else {
            if (!name.isEmpty() && isLetterOrUnderscore(name.charAt(0))) {
                errorValue.set(false);
                if (nameIsCorrect(name)) {
                    action.setParameterValue(0, name, program);
//                    action.setExpression(new VariableExpression(name));
                }
                else {
                    errorValue.set(true);
                }
            }
        }
    }

    public boolean isLetterOrUnderscore(Character name) {
        return name == '_' || (name >= 'A' && name <= 'z');
    }

    public boolean nameIsCorrect(String name) {
        boolean isCorrect = true;
        for (int i = 1; i < name.length() && isCorrect; ++i) {
            if (!(isLetterOrUnderscore(name.charAt(i)) || (name.charAt(i) >= '0' && name.charAt(i) <= '9'))) {
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    public void setTargetName(Action action, String name) {
        errorValue.set(true);
        if (name.isEmpty()) {
            action.setParameterValue(0, name, program);
        }
        else {
            if (isLetterOrUnderscore(name.charAt(0))) {
                errorValue.set(false);
                if (nameIsCorrect(name)) {
                    action.setParameterValue(0, name, program);
                }
                else {
                    errorValue.set(true);
                }
            }
        }
    }

    public void refreshValue() {
        actionIndex.set(getProgramActions().isEmpty() ? -1 : actionIndex.get());
        if (actionIndex.get() != -1) {
            Action currentAction = getProgramActions().get(actionIndex.get());
            actionValue.set(currentAction.parameterValue(0));
        }
        else {
            actionValue.set("");
        }
    }

    public int getIndentationLevel(int index) {
        int level = 0;
        List<Action> actionList = program.getActions();
        for (int i = 0; i < index; ++i) {
            Action action = actionList.get(i);
            if (action.actionType() == ActionType.REPEAT) {
                ++level;
            }
            else if (action.actionType() == ActionType.END_REPEAT) {
                if (level > 0) {
                    --level;
                }
            }
        }
        Action currentAction = actionList.get(index);
        if (currentAction.actionType() == ActionType.END_REPEAT) {
            if (level > 0) {
                --level;
            }
        }
        return level;
    }

    public void increment(boolean isIncrement) {
        if (isValidIndex() && !getProgramActions().isEmpty()) {
            Action currentAction = getProgramActions().get(actionIndex.get());
            if (currentAction.actionType() == ActionType.VAR_ASSIGNMENT || currentAction.actionType() == ActionType.INCREMENT_VARIABLE) {
                program.incrementDuringCompilation(currentAction, 1, isIncrement ? 1 : -1);
                actionVar.set(currentAction.parameterValue(1));
            }
            else{
                program.incrementDuringCompilation(currentAction, 0, isIncrement ? 1 : -1);
                actionValue.set(currentAction.parameterValue(0));
            }
        }
    }
}
