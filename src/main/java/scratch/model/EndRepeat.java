package scratch.model;

public class EndRepeat extends NoExpressionAction{

    public EndRepeat() {

    }

    @Override
    public void execute(Cursor cursor, Program program) {
    }

    @Override
    public ActionType actionType() {
        return ActionType.END_REPEAT;
    }

    @Override
    public Action copy() {
        return new EndRepeat();
    }


    public String toString(){
        return "Fin répéter";
    }


//    @Override
//    public int valueOfExpression(Program program) {
//        return 0;
//    }
}
