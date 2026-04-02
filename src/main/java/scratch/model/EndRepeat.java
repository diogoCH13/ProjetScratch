package scratch.model;

public class EndRepeat extends Action{
    @Override
    public void execute(Cursor cursor) {

    }

    @Override
    public ActionType actionType() {
        return ActionType.END_REPEAT;
    }

    @Override
    public void setValue(double value) {

    }

    @Override
    public double getValue() {
        return 0;
    }

    public String toString(){
        return "Fin répéter";
    }
}
