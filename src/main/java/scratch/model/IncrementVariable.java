package scratch.model;

public class IncrementVariable extends Action {
    @Override
    public void execute(Cursor cursor) {

    }

    @Override
    public ActionType actionType() {
        return ActionType.INCREMENT_VARIABLE;
    }

    @Override
    public void setValue(double value) {

    }

    @Override
    public double getValue() {
        return 0;
    }
}
