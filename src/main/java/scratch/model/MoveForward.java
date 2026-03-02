package scratch.model;

public class MoveForward extends Action {

    public MoveForward(double distance) {
        super.value.setValue(distance);
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.moveForward(super.value.get());
    }

    @Override
    public ActionType actionType() {
        return ActionType.MOVE_FORWARD;
    }

    @Override
    public double getValue() {
        return super.value.getValue();
    }

    public void setValue(double value) {
        super.value.setValue(value);
    }

    @Override
    public String toString() {
        return "Avancer de " + String.format("%.0f", super.value.get());
    }
}