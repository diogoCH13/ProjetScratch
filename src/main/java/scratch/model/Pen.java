package scratch.model;

public class Pen extends Action {

    private final boolean write;

    public Pen(boolean write) {
        this.write = write;
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.setPen(write);
    }

    @Override
    public ActionType actionType() {
        return write ? ActionType.PEN_DOWN : ActionType.PEN_UP;
    }

    @Override
    public double getValue() {
        return 0 ;
    }

    @Override
    public void setValue(double value) {

    }

    public String toString(){
        return write ? "Abaisser le stylo" : "Lever le stylo";
    }
}
