package scratch.model;

public class TurnRight extends Action {

    private int angle;

    public TurnRight(int angle) {
        this.angle = angle;
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.turnRight(angle);
    }

    @Override
    public String toString() {
        return "Tourner à droite de " + angle;
    }
}