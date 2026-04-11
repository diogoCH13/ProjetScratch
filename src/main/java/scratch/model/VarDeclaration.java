package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VarDeclaration extends Action{

    public VarDeclaration(String var){
        super.setVar(var);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public void setValue(double value) {
        super.valueProperty().setValue(value);
    }

    @Override
    public double getValue() {
        return super.valueProperty().get();
    }

    @Override
    public void execute(Cursor cursor) {

    }

    public String toString(){
        if (super.getVar() == null || super.getVar().isEmpty()){
            return "Déclaration variable";
        }
        return "Déclaration variable " + super.getVar();
    }

}