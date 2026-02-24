package scratch.model;

public class TurnLeft extends Action {

    private int angle;

    public TurnLeft(int angle) {
        this.angle = angle;
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.turnLeft(angle);
    }

    @Override
    public String toString() {
        return "Tourner à gauche de " + angle;
    }
}