package scratch.model;

public class Cursor {

    private final Point position;
    private boolean penDown;
    private final double angle;



    public Cursor(Point startPosition){
        this.position = startPosition;
        this.penDown = true;
        this.angle = 0;
    }

    public Point getPosition() {
        return position;
    }

    public double getAngle() {
        return angle;
    }

    public void move(double distance, double angle){
        double radians = Math.toRadians(angle);

    }

    public boolean isPenDown() {
        return penDown;
    }

    public void pen(){
    }

    public void penUp() {
        penDown = false;
    }

    public void penDown() {
        penDown = true;
    }

    public void turnLeft(double angle) {
    }

    public void turnRight(double angle) {
    }



}
