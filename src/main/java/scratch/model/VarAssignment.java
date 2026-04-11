package scratch.model;

public class VarAssignment extends Action{

    private String variableTarget;

    public VarAssignment(Expression expression, String variableTarget) {
        super(expression);
        this.variableTarget = variableTarget;
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int value = super.getExpression().expressionValue(program);
        program.setVariableValueByName(variableTarget, value);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public String getVariableTargetName() {
        return variableTarget;
    }

    //    @Override
//    public void setExpression(double value) {
//        if (value > 0){
//            super.valueProperty().setValue(value);
//        }
//    }
//
//    @Override
//    public double getValue() {
//        return super.valueProperty().getValue();
//    }
    @Override
    public String toString() {
        return "Assignation : " + variableTarget + " = " + super.getExpression().expressionName();
    }
}
