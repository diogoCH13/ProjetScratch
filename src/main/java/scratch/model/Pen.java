package scratch.model;

public class Pen extends Action {

    private final boolean write;

    public Pen(boolean write) {
        this.write = write;
    }


    @Override
    public void execute(Cursor cursor) {

    }

    public String toString(){
        return write ? "Abaisser le stylo" : "Lever le stylo";
    }
}
