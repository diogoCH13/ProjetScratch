package scratch.model;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public abstract class Action {

    public DoubleProperty value = new SimpleDoubleProperty();

    public DoubleProperty valueProperty(){
        return value;
    }

    public abstract void execute(Cursor cursor);

    public abstract ActionType actionType();

    public abstract void setValue(double value);

    public abstract double getValue();
}