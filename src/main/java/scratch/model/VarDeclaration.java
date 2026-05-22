package scratch.model;

public class VarDeclaration extends OneVariableAction{

    public VarDeclaration(String value){
        super(value);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public Action copy() {
        return new VarDeclaration(super.getVariableTargetName());
    }

    @Override
    public String parameterPrefix(int index) {
        return "Déclaration de la variable";
    }

    @Override
    public boolean isVariableNameParameter(int index) {
        return true;
    }

    public void setParameterValue(int index, String value, Program program) {
        if (!program.isInteger(value)){
            super.setParameterValue(index, value, program);
        }
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        program.declareVariable(super.getVariableTargetName());
    }

    @Override
    public boolean parameterDeclareVariable(int index) {
        return index == 0;
    }

    @Override
    public boolean parameterUseVariable(int index) {
        return false;
    }

    public String toString(){
        return "Déclaration variable " + super.getVariableTargetName();
    }

}