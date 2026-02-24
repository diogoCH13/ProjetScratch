package scratch.model;

public class PenDown extends Action {

    @Override
    public void execute(Cursor cursor) {
        cursor.penDown();
    }

    @Override
    public String toString() {
        return "Abaisser le stylo";
    }
}