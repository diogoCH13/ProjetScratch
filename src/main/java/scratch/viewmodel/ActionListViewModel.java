package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.ActionType;
import scratch.model.Program;

import java.util.List;

public class ActionListViewModel {

    private final Program program;
    private final BooleanProperty programLoaded;
    private final BooleanProperty running;
    private final BooleanProperty executionFinished;
    private final BooleanProperty runtimeValue;

    private final IntegerProperty actionIndex = new SimpleIntegerProperty(-1);
    private final ObservableList<ActionParameterViewModel> parameters = FXCollections.observableArrayList();
    private final BooleanProperty errorValue = new SimpleBooleanProperty(false);

    private final BooleanBinding canUp = actionIndex.greaterThan(0);
    private final BooleanBinding canRemove = actionIndex.greaterThanOrEqualTo(0);

    public ActionListViewModel(Program program, BooleanProperty programLoaded, BooleanProperty running,
                               BooleanProperty executionFinished, BooleanProperty runtimeValue) {
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
    public ObservableList<ActionParameterViewModel> getParameters() {
        return parameters;
    }

    public BooleanProperty errorValueProperty() {
        return errorValue;
    }

    public BooleanProperty runtimeValueProperty() {
        return runtimeValue;
    }

    public ObservableList<Action> getProgramActions() {
        return program.getActionsList();
    }

    public void deleteAction() {
        int i = actionIndex.get();
        if (isValidIndex()) {
            program.deleteAction(i);
            refreshParameters();
        }
        errorValue.set(false);
        programLoaded.set(false);
        running.set(false);
    }

    public void upAction() {
        int index = actionIndex.get();

        if (index > 0) {
            program.up(index);
            actionIndex.set(index - 1);
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
            program.duplicate(program.getActionsList().size(), action);
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
        refreshParameters();
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


    private void refreshParameters() {
        parameters.clear();

        if (isValidIndex()) {
            Action action = getProgramActions().get(actionIndex.get());

            for (int i = 0; i < action.parameterCount(); i++) {
                ActionParameterViewModel parameter = new ActionParameterViewModel(action, program, i);

                parameter.errorProperty().addListener((obs, oldValue, newValue) -> {
                    refreshErrorValue();
                });

                parameter.valueProperty().addListener((obs, oldValue, newValue) -> {
                    programLoaded.set(false);
                    running.set(false);
                });

                parameters.add(parameter);
            }
        }

        refreshErrorValue();
    }

    private void refreshErrorValue() {
        boolean hasError = false;

        for (ActionParameterViewModel parameter : parameters) {
            if (parameter.errorProperty().get()) {
                hasError = true;
            }
        }
        errorValue.set(hasError);
    }

    public void configureBindingsAndListeners() {
        actionIndex.addListener((obs, oldVal, newVal) -> {
            refreshParameters();
        });
    }

    public int getIndentationLevel(int index) {
        int level = 0;
        List<Action> actionList = program.getActionsList();
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

    public StringBinding actionDetailsBinding() {
        return Bindings.createStringBinding(() -> {
            String detail = "";

            if (getProgramActions().isEmpty() || !isValidIndex()) {
                detail = "aucune action sélectionnées";
            }
            else {
                Action action = getProgramActions().get(actionIndex.get());
                if (action.parameterCount() == 0) {
                    detail = action.parameterPrefix(0);
                }
            }
            return detail;

        }, actionIndexProperty(), getProgramActions());
    }
}
