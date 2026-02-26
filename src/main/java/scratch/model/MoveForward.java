package scratch.model;

public class MoveForward extends Action {

    private double distance;

    public MoveForward(double distance) {
        this.distance = distance;
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.moveForward(distance);
    }

    @Override
    public String toString() {
        return "Avancer de " + String.format("%.0f", distance);
    }
}