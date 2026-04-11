package scratch.model;

import javafx.beans.property.StringProperty;

public class VarAssignment extends Action{
    @Override
    public void execute(Cursor cursor) {

    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public void setValue(double value) {
        if (value > 0){
            super.valueProperty().setValue(value);
        }
    }

    @Override
    public double getValue() {
        return super.valueProperty().getValue();
    }


//    public String toString(){
//        return "Assignation : " + super.getVar() + " = " + (int)getValue();
//    }
}
