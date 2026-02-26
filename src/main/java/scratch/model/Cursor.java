package scratch.model;

public class Cursor {
    private final CursorWorld world;
    private final Point position;
    private boolean penDown;
    private double angle;


    public Cursor(CursorWorld world, Point startPosition){
        this.world = world;
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

    public void moveForward(double distance){

        double radians = Math.toRadians(angle) + Math.PI / 2;

        double dx = distance * Math.cos(radians);
        double dy = distance * Math.sin(radians);

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

    public void turnLeft(double angle) {
        if (angle < 1 || angle > 180){
            throw new RuntimeException("angle invalide");
        }
        this.angle = (this.angle + angle) % 360;
    }

    public void turnRight(double angle) {
        if (angle > -1 || angle < -180){
            throw new RuntimeException("angle invalide");
        }
        this.angle = (this.angle + angle) % 360;
    }

    public boolean isPenDown() {
        return penDown;
    }

    public void setPen(boolean write){
        penDown = write;
    }
}
