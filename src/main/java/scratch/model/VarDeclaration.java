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
    public void execute(Cursor cursor, Program program) {
        program.declareVariable(super.getVariableTargetName());
    }

    public String toString(){
        return "Déclaration variable " + super.getVariableTargetName();
    }

}