package scratch.model;

public class IncrementVariable extends Action {

    public IncrementVariable(Expression expression) {
        super(expression);
    }

    @Override
    public void execute(Cursor cursor, Program program) {

    }

    @Override
    public ActionType actionType() {
        return ActionType.INCREMENT_VARIABLE;
    }

//    @Override
//    public void setExpression(double value) {
//
//    }
//
//    @Override
//    public double getValue() {
//        return 0;
//    }
}
