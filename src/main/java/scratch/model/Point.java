package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Point {

    private IntegerProperty x = new SimpleIntegerProperty(250);
    private IntegerProperty y = new SimpleIntegerProperty(250);

    public void setX(int x) {
        this.x.setValue(x);
    }

    public void setY(int y) {
        this.y.setValue(y);
    }

    public int getX(){
        return x.getValue();
    }

    public int getY() {
        return y.getValue();
    }


}
