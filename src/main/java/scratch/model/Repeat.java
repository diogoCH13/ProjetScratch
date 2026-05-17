package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Repeat extends OneExpressionAction {

    public Repeat(Expression expression) {
        super(expression);
    }
    @Override
    public ActionType actionType() {
        return ActionType.REPEAT;
    }

    @Override
    public Action copy() {
        return new Repeat(super.getExpression());
    }

    @Override
    public void execute(Cursor cursor, Program program) {
    }

    public String toString(){
        return "Répéter " + super.getExpression().toString() + " fois";
    }

}
