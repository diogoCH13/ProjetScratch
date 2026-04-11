package scratch.model;

import javafx.beans.InvalidationListener;

public class VariableExpression implements Expression{

    private String name;

    public VariableExpression(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public int expressionValue(Program program) {
        return program.getVariableValueByName(name);
    }

    @Override
    public String expressionName() {
        return name;
    }
}
