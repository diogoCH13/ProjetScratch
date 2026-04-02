package scratch.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Repeat extends Action {

    private StringProperty parameter = new SimpleStringProperty("2");

    public StringProperty parameterProperty() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter.set(parameter);
    }

    @Override
    public ActionType actionType() {
        return ActionType.REPEAT;
    }

    @Override
    public void setValue(double value) {
        this.parameter.set(String.valueOf((int)value));
    }

    @Override
    public double getValue() {
        try {
            return Double.parseDouble(parameter.get());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void execute(Cursor cursor) {

    }

    public String toString(){
        return "Répéter " + parameter.get() + " fois";
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
