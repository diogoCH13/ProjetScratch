package scratch.model;


import javafx.beans.Observable;

public abstract class Action {
    public abstract void execute(Cursor cursor, Program program);

    public abstract ActionType actionType();

    public abstract Action copy();

    public abstract Observable[] observables();

    public boolean valueIsCorrect(int value){
        return true;
    }

    public int parameterCount() {
        return 0;
    }

    public String getParameterValue(int index) {
        return "";
    }

    public void setParameterValue(int index, String value, Program program) { }

    public String parameterPrefix(int index) {
        return "";
    }

    public String parameterSuffix(int index) {
        return "";
    }

    public boolean isExpressionParameter(int index) {
        return false;
    }

    public boolean isVariableNameParameter(int index) {
        return false;
    }

    public boolean parameterValueIsCorrect(int index, int value) {
        return valueIsCorrect(value);
    }

    public boolean parameterDeclareVariable(int index) {
        return false;
    }

    public boolean parameterUseVariable(int index) {
        return isExpressionParameter(index);
    }

    public boolean canBeLastAction() {
        return true;
    }
}
