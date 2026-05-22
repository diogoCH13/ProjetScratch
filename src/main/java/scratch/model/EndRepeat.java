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

    @Override
    public String parameterPrefix(int index) {
        return "Fin répéter";
    }

    public String toString(){
        return "Fin répéter";
    }

}
