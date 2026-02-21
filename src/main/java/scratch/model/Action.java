package scratch.model;

public abstract class Action {

    private final Cursor cursor;


    public Action(Cursor cursor) {
        this.cursor = cursor;
    }

    public abstract void execute();
}
