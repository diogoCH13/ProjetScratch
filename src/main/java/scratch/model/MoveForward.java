package scratch.model;

public class MoveForward extends Action {

    public MoveForward(double distance) {
        super.valueProperty().setValue(distance);
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.moveForward(super.valueProperty().get());
    }

    @Override
    public ActionType actionType() {
        return ActionType.MOVE_FORWARD;
    }

    @Override
    public double getValue() {
        return super.valueProperty().getValue();
    }

    public void setValue(double value) {
        super.valueProperty().setValue(value);
    }

    @Override
    public String toString() {
        return "Avancer de " + String.format("%.0f", Math.abs(super.valueProperty().get()));
    }
}