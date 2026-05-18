package scratch.model;

public class IncrementVariable extends OneVariableAndOneExpressionAction {

    public IncrementVariable(String variableTargetName, Expression expression) {
        super(variableTargetName, expression);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int val = super.getExpression().valueOfExpression(program);
        program.incrementDuringExecution(super.getVariableTargetName(), val);
    }

    @Override
    public ActionType actionType() {
        return ActionType.INCREMENT_VARIABLE;
    }

    @Override
    public Action copy() {
        return new IncrementVariable(super.getVariableTargetName(), super.getExpression());
    }

    @Override
    public String parameterPrefix(int index) {
        return index == 0 ? "Incrémentation de la variable" : "de";
    }


    @Override
    public String toString() {
        return "Incrémentation de la variable " + super.getVariableTargetName() + " de " + super.getExpression();
    }

}
