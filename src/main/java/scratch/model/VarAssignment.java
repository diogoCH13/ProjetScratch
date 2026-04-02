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

    }

    @Override
    public double getValue() {
        return 0;
    }


    public String toString(){
        return "Assignation : ";
    }
}
