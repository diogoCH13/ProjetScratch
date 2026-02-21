package scratch.model;

public class Cursor {

    private Point position;
    private boolean penDown;
    private int angle;

    public Cursor(Point startPosition){
        this.position = startPosition;
        this.penDown = true;
        this.angle = 0;
    }

    public Point getPosition() {
        return position;
    }

    public int getAngle() {
        return angle;
    }

    public boolean isPenDown() {
        return penDown;
    }

    public void penUp() {
        penDown = false;
    }

    public void penDown() {
        penDown = true;
    }

    public void turnLeft(int angle) {
    }

    public void turnRight(int angle) {
    }



}
