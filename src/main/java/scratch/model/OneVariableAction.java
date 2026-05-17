package scratch.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class OneVariableAction extends Action{

    private StringProperty variableTargetProperty = new SimpleStringProperty();

    public OneVariableAction(String variableTarget){
        setVariableTargetName(variableTarget);
    }

    public String getVariableTargetName() {
        return variableTargetProperty.get();
    }

    public StringProperty VariableTargetNameProperty() {
        return variableTargetProperty;
    }

    public void setVariableTargetName(String variableTarget) {
        this.variableTargetProperty.set(variableTarget);
    }

    @Override
    public Observable[] observables() {
        return new Observable[]{variableTargetProperty};
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public String parameterValue(int index) {
        return getVariableTargetName();
    }

    @Override
    public void setParameterValue(int index, String value, Program program) {
        setVariableTargetName(value);
    }
}
