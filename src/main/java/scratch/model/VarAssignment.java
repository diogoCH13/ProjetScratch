package scratch.model;

public class VarAssignment extends OneVariableAndOneExpressionAction {

    public VarAssignment(String variableTargetName, Expression expression) {
        super(variableTargetName, expression);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int value = super.getExpression().valueOfExpression(program);
        program.setVariableValueByName(super.getVariableTargetName(), value);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public Action copy() {
        return new VarAssignment(getVariableTargetName(), getExpression());
    }

    public String parameterPrefix(int index) {
        return index == 0 ? "Assignation de la variable" : "valeur:";
    }

    @Override
    public String toString() {
        return "Assignation : " + super.getVariableTargetName() + " = " + super.getExpression().toString();
    }
}
