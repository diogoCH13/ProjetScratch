package scratch.model;

import javafx.beans.InvalidationListener;

public class VariableExpression implements Expression{

    private String name;

    public VariableExpression(String name){
        this.name = name;
    }

    @Override
    public int valueOfVariableOrLitteral(Program program) {
        return program.getVariableValueByName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
