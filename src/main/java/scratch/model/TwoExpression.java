package scratch.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class TwoExpression extends Action {

    //permet de créer un property de l'objet expression pour voir ces changement
    private ObjectProperty<Expression> expressionProperty1 = new SimpleObjectProperty<>();

    private ObjectProperty<Expression> expressionProperty2 = new SimpleObjectProperty<>();

    public TwoExpression(Expression expression1, Expression expression2) {
        setExpression1(expression1);
        setExpression2(expression2);
    }

    public Expression getExpression1() {
        return expressionProperty1.get();
    }

    public Expression getExpression2() {
        return expressionProperty2.get();
    }

    public ObjectProperty<Expression> expressionProperty1() {
        return expressionProperty1;
    }

    public ObjectProperty<Expression> expressionProperty2() {
        return expressionProperty2;
    }

    public void setExpression1(Expression expression) {
        this.expressionProperty1.set(expression);
    }

    public void setExpression2(Expression expression) {
        this.expressionProperty2.set(expression);
    }

    @Override
    public Observable[] observables() {
        return new Observable[]{expressionProperty1, expressionProperty2};
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public String getParameterValue(int index) {
        if (index == 0) {
            return getExpression1().toString();
        }
        else {
            return getExpression2().toString();
        }
    }

    @Override
    public void setParameterValue(int index, String value, Program program) {
        if(index == 0){
            setExpression1(program.parseExpression(value));
        }

        if(index == 1){
            setExpression2(program.parseExpression(value));
        }
    }

    @Override
    public boolean isExpressionParameter(int index) {
        return index == 0 || index == 1;
    }
}
