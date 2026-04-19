package scratch.model;

public class VarAssignment extends Action{

    public VarAssignment(Expression expression, String variableTargetName) {
        super(expression);
        super.setVariableTargetName(variableTargetName);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int value = super.getExpression().valueOfVariableOrLitteral(program);
        program.setVariableValueByName(super.getVariableTargetName(), value);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public String toString() {
        return "Assignation : " + super.getVariableTargetName() + " = " + super.getExpression().toString();
    }
}
