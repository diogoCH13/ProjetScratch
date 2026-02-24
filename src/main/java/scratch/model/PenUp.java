package scratch.model;

public class PenUp extends Action {

    @Override
    public void execute(Cursor cursor) {
        cursor.penUp();
    }

    @Override
    public String toString() {
        return "Lever le stylo";
    }
}