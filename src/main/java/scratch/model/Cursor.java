package scratch.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Cursor {
    private final CursorWorld world;
    private final Point position;
    private boolean penDown;
    private final Point startPosition;

    private DoubleProperty angle = new SimpleDoubleProperty();

    public Cursor(CursorWorld world, Point startPosition){
        this.world = world;
        this.startPosition = new Point(startPosition.getX(), startPosition.getY());
        this.position = new Point(startPosition.getX(), startPosition.getY());
        this.penDown = true;
        this.angle.set(0);
    }

    public Point getPosition() {
        return position;
    }

    public double getAngle() {
        return angle.get();
    }

    public DoubleProperty angleProperty(){
        return angle;
    }

    public void moveForward(double distance){

        double radians = Math.toRadians(angle.get()) ;

        double dx = distance * Math.sin(radians);
//        double dy = distance * Math.cos(radians);
        double dy = -distance * Math.cos(radians);

        double oldX = position.getX();
        double oldY = position.getY();

        double newX = oldX + dx;
        double newY = oldY + dy;

        if (penDown){
            Segment segment = new Segment(new Point(oldX, oldY), new Point(newX,newY));
            world.addSegment(segment);
        }

        position.setX(newX);
        position.setY(newY);
    }

    public void rectangle(double height, double width){

        double x_start =  position.getX(),
                y_start = position.getY(),
                x_end = x_start + width,
                y_end = y_start + height;

        int i = 0;
        while (i < 4){
            if (i == 0){
                Segment segment = new Segment(new Point(x_start, y_start), new Point(x_start, y_end));
                world.addSegment(segment);
            }
            else if (i == 1){
                Segment segment = new Segment(new Point(x_start, y_end), new Point(x_end, y_end));
                world.addSegment(segment);
            }
            else if (i == 2){
                Segment segment = new Segment(new Point(x_end, y_end), new Point(x_end, y_start));
                world.addSegment(segment);
            }
            else {
                Segment segment = new Segment(new Point(x_end, y_start), new Point(x_start, y_start));
                world.addSegment(segment);
            }

            ++i;
        }
    }

    public void rotate(double deltaAngle) {
        double angleValue = this.angle.get();
        angleValue = (angleValue + deltaAngle) % 360;
        this.angle.set(angleValue);
    }

    public boolean isPenDown() {
        return penDown;
    }

    public void setPen(boolean write){
        penDown = write;
    }


    public void reset(){
        position.setX(startPosition.getX());
        position.setY(startPosition.getY());
        angle.set(0);
        penDown = true;
    }

    public CursorWorld getWorld(){
        return world;
    }
}
