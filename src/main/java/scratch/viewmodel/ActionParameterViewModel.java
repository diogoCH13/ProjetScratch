package scratch.viewmodel;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scratch.model.Action;
import scratch.model.Program;

public class ActionParameterViewModel {
    private final Action action;
    private final Program program;
    private final int index;
    private final StringProperty value = new SimpleStringProperty();
    private final BooleanProperty error = new SimpleBooleanProperty(false);

    public ActionParameterViewModel(Action action, Program program, int index) {
        this.action = action;
        this.program = program;
        this.index = index;
        this.value.set(action.getParameterValue(index));
        this.error.set(!isValidParameterValue(value.get()));

        this.value.addListener((obs, oldValue, newValue) -> updateActionParameter(newValue));
    }

    public StringProperty valueProperty() {
        return value;
    }

    public BooleanProperty errorProperty() {
        return error;
    }

    public String prefix() {
        return action.parameterPrefix(index);
    }

    public String suffix() {
        return action.parameterSuffix(index);
    }

    //à chaque fois que la valeur du paramètre change on verifie si on peut incrementer
    public BooleanBinding canIncrement() {
        return Bindings.createBooleanBinding(() -> action.isExpressionParameter(index) && program.isInteger(value.get()), value);
    }

    public void increment(int count) {
        if (action.isExpressionParameter(index) && program.isInteger(value.get())) {
            int number = Integer.parseInt(value.get().trim());
            value.set(String.valueOf(number + count));
        }
    }

    private void updateActionParameter(String newValue) {
        if (isValidParameterValue(newValue)) {
            String valueToSave = program.isInteger(newValue) ? newValue.trim() : newValue;
            action.setParameterValue(index, valueToSave, program);
            error.set(false);
        }
        else {
            error.set(true);
        }
    }

    private boolean isValidParameterValue(String value) {
        boolean valid = false;

        if (value != null && !value.isEmpty()) {
            if (action.isExpressionParameter(index)) {
                valid = expressionValueIsCorrect(value);
            }
            else if (action.isVariableNameParameter(index)) {
                valid = nameIsCorrect(value);
            }
            else {
                valid = true;
            }
        }

        return valid;
    }

    private boolean expressionValueIsCorrect(String value) {
        boolean correct;

        if (program.isInteger(value)) {
            int number = Integer.parseInt(value.trim());
            correct = action.parameterValueIsCorrect(index, number);
        }
        else {
            correct = nameIsCorrect(value);
        }

        return correct;
    }

    public boolean isLetterOrUnderscore(Character name) {
        return name == '_' || (name >= 'A' && name <= 'Z') || (name >= 'a' && name <= 'z');
    }

    public boolean nameIsCorrect(String name) {
        boolean isCorrect = name != null && !name.isEmpty() && isLetterOrUnderscore(name.charAt(0));

        for (int i = 1; i < name.length() && isCorrect; ++i) {
            if (!(isLetterOrUnderscore(name.charAt(i)) || (name.charAt(i) >= '0' && name.charAt(i) <= '9'))) {
                isCorrect = false;
            }
        }
        return isCorrect;
    }
}
