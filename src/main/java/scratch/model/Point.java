package scratch.model;

import javafx.beans.property.*;

public class Point {

    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();


    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty(){
        return y;
    }

    public Point(double x, double y) {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    public void setX(double x) {
        this.x.setValue(x);
    }

    public void setY(double y) {
        this.y.setValue(y);
    }

    public double getX(){
        return x.getValue();
    }

    public double getY() {
        return y.getValue();
    }
}
