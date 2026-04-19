package scratch.model;

public class EndRepeat extends Action{

    public EndRepeat(Expression expression) {
        super(expression);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
    }

    @Override
    public ActionType actionType() {
        return ActionType.END_REPEAT;
    }


    public String toString(){
        return "Fin répéter";
    }
}
