package scratch.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class OneVariableAndOneExpressionAction extends Action{

    //nom de la variable à modifier
    private StringProperty variableTarget = new SimpleStringProperty();
    //permet de créer un property de l'objet expression pour voir ces changement
    private ObjectProperty<Expression> expressionProperty = new SimpleObjectProperty<>();

    public OneVariableAndOneExpressionAction(String variableTarget, Expression expression) {
        setVariableTargetName(variableTarget);
        setExpression(expression);
    }

    public Expression getExpression() {
        return expressionProperty.get();
    }

    public ObjectProperty<Expression> expressionProperty() {
        return expressionProperty;
    }

    public void setExpression(Expression expression) {
        this.expressionProperty.set(expression);
    }

    public String getVariableTargetName() {
        return variableTarget.get();
    }

    public StringProperty VariableTargetNameProperty() {
        return variableTarget;
    }

    public void setVariableTargetName(String variableTarget) {
        this.variableTarget.set(variableTarget);
    }

    @Override
    public Observable[] observables() {
        return new Observable[]{variableTarget, expressionProperty};
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public String parameterValue(int index) {
        return index == 0 ? getVariableTargetName() : getExpression().toString();
    }

    @Override
    public void setParameterValue(int index, String value, Program program) {
        if (index == 0) {
            setVariableTargetName(value);
        }
        else {
            setExpression(program.parseExpression(value));
        }
    }

    @Override
    public boolean isVariableNameParameter(int index) {
        return index == 0;
    }

    @Override
    public boolean isExpressionParameter(int index) {
        return index == 1;
    }
}
