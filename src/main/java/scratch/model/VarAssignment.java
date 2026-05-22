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

    public String parameterSuffix(int index) {
        return "";
    }

    public boolean isExpressionParameter(int index) {
        return index == 0 ? false : true;
    }

    public boolean isVariableNameParameter(int index) {
        return !isExpressionParameter(index);
    }

    public void setParameterValue(int index, String value, Program program) {
        if (index == 0){
            if (!program.isInteger(value)){
                super.setParameterValue(index, value, program);
            }
        }
        else{
            super.setParameterValue(index, value, program);
        }
    }

    @Override
    public boolean parameterUseVariable(int index) {
        return index == 0 || isExpressionParameter(index);
    }

    @Override
    public String toString() {
        return "Assignation : " + super.getVariableTargetName() + " = " + super.getExpression().toString();
    }
}
