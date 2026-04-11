package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VarDeclaration extends Action{

    public VarDeclaration(Expression name){
        super(name);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_DECLARATION;
    }

//    @Override
//    public void setExpression(double value) {
//        super.valueProperty().setValue(value);
//    }
//
//    @Override
//    public double getValue() {
//        return super.valueProperty().get();
//    }

    @Override
    public void execute(Cursor cursor, Program program) {
        program.declareVariable(super.getExpression().expressionName());
    }

    public String toString(){
//        if (super.getVar() == null || super.getVar().isEmpty()){
//            return "Déclaration variable";
//        }
        return "Déclaration variable " + super.getExpression().expressionName();
    }

}