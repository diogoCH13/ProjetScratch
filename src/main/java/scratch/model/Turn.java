package scratch.model;

public class Turn extends Action {
    private final double angle;

    public Turn(double angle){
        this.angle = angle;
    }


    @Override
    public void execute(Cursor cursor) {
        if (cursor.getAngle() > 0) {
            cursor.turnLeft(angle);
        }
        else {
            cursor.turnRight(angle);
        }
    }

    public String toString(){
        if(angle > 0){
            return "Tourner à gauche de " + Math.abs(angle);
        }
        return "Tourner à droite de " + Math.abs(angle);
    }
}
