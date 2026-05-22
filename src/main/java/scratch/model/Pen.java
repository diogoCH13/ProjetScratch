package scratch.model;

public class Pen extends NoExpressionAction {

    private final boolean write;

    public Pen(boolean write) {
//        super(new LitteralExpression(0));
        this.write = write;
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        if (write){
            if (cursor.isPenDown()){
                throw new RuntimeException("RUNTIME ERROR");
            }
            cursor.setPen(true);
        }else {
            if (!cursor.isPenDown()){
                throw new RuntimeException("RUNTIME ERROR");
            }
            cursor.setPen(false);
        }
    }

    @Override
    public ActionType actionType() {
        return write ? ActionType.PEN_DOWN : ActionType.PEN_UP;
    }

    @Override
    public Action copy() {
        return new Pen(write);
    }

    public String parameterPrefix(int index) {
        return write ? "Abaisser le stylo" : "Lever le stylo";
    }

    public String toString(){
        return write ? "Abaisser le stylo" : "Lever le stylo";
    }
}
