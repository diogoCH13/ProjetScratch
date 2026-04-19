package scratch.model;

public class VarDeclaration extends Action{

    public VarDeclaration(Expression value){
        super(value);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        program.declareVariable(super.getExpression().toString());
    }

    public String toString(){
        String value = super.getExpression().toString().equals("0") ? "" : super.getExpression().toString();
        return "Déclaration variable " + value;
    }

}