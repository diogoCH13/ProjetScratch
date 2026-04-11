package scratch.model;

public class Pen extends Action {

    private final boolean write;

    public Pen(boolean write) {
        super(new LitteralExpression(0));
        this.write = write;
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        cursor.setPen(write);
    }

    @Override
    public ActionType actionType() {
        return write ? ActionType.PEN_DOWN : ActionType.PEN_UP;
    }

//    @Override
//    public double getValue() {
//        return super.valueProperty().getValue();
//    }

    public String toString(){
        return write ? "Abaisser le stylo" : "Lever le stylo";
    }
}
