package scratch.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class OneExpressionAction extends Action {

    //permet de créer un property de l'objet expression pour voir ces changement
    private ObjectProperty<Expression> expressionProperty = new SimpleObjectProperty<>();

    public OneExpressionAction(Expression expression) {
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

    @Override
    public Observable[] observables() {
        return new Observable[]{expressionProperty};
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public String parameterValue(int index) {
        return getExpression().toString();
    }

    @Override
    public void setParameterValue(int index, String value, Program program) {
        setExpression(program.parseExpression(value));
    }
}