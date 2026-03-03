package scratch.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Cursor {
    private final CursorWorld world;
    private final Point position;
    private boolean penDown;
//    private double angle;
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
        double dy = distance * Math.cos(radians);

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

//    public void turnLeft(double angle) {
//        if (angle < 1 || angle > 180){
//            throw new RuntimeException("angle invalide");
//        }
//        this.angle = (this.angle + angle) % 360;
//    }
//
//    public void turnRight(double angle) {
//        if (angle > -1 || angle < -180){
//            throw new RuntimeException("angle invalide");
//        }
//        this.angle = (this.angle + angle) % 360;
//    }

    public void rotate(double deltaAngle) {
        //        this.angle.get() = (this.angle + deltaAngle) % 360;
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
