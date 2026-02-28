package scratch.model;


public abstract class Action {

    public abstract void execute(Cursor cursor);

    public abstract ActionType actionType();

    public abstract double getValue();

    public abstract void setValue(double value);

}