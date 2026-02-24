package scratch.model;

public class MoveForward extends Action {

    private int distance;

    public MoveForward(int distance) {
        this.distance = distance;
    }

    @Override
    public void execute(Cursor cursor) {
        // logique plus tard
    }

    @Override
    public String toString() {
        return "Avancer de " + distance;
    }
}