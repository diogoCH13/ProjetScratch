package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Repeat extends Action {

//    private StringProperty parameter = new SimpleStringProperty("2");

    public Repeat(Expression expression) {
        super(expression);
    }

//    public StringProperty parameterProperty() {
//        return parameter;
//    }

//    public void setParameter(String parameter) {
//        this.parameter.set(parameter);
//    }

    @Override
    public ActionType actionType() {
        return ActionType.REPEAT;
    }

//    @Override
//    public void setExpression(double value) {
//        this.parameter.set(String.valueOf((int)value));
//    }
//
//    @Override
//    public double getValue() {
//        try {
//            return Double.parseDouble(parameter.get());
//        }
//        catch (NumberFormatException e) {
//            return 0;
//        }
//    }

    @Override
    public void execute(Cursor cursor, Program program) {

    }

    public String toString(){
        return "Répéter " + super.getExpression() + " fois";
    }

//    @Override
//    public boolean isComposite() {
//        return true;
//    }
//
//     @Override
//    public ObservableList<Action> getChildren() {
//        return actions;
//    }

}
