package scratch.model;

public class IncrementVariable extends Action {

    public IncrementVariable(Expression expression, String variableTargetName) {
        super(expression);
        super.setVariableTargetName(variableTargetName);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int val = super.getExpression().valueOfVariableOrLitteral(program);
        if (val > 0){
            program.incrementDuringExecution(super.getVariableTargetName(), val);
        }
        else{
            program.decrementDuringExecution(super.getVariableTargetName(), val);
        }
    }

    @Override
    public ActionType actionType() {
        return ActionType.INCREMENT_VARIABLE;
    }

    @Override
    public String toString() {
        return "Incrémentation de la variable " + super.getVariableTargetName() + " de " + super.getExpression();
    }

}
