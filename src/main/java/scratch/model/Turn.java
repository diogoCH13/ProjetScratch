package scratch.model;

public class Turn extends Action {
    private double angle;

    public Turn(double angle){
        this.angle = angle;
    }


//    @Override
//    public void execute(Cursor cursor) {
//        if (cursor.getAngle() > 0) {
//            cursor.turnLeft(angle);
//        }
//        else {
//            cursor.turnRight(angle);
//        }
//    }

    @Override
    public void execute(Cursor cursor) {
        if (angle > 0) {
            cursor.turnLeft(angle);
        }
        else {
            cursor.turnRight(angle);
        }
    }

    @Override
    public ActionType actionType() {
        return angle > 0 ? ActionType.TURN_LEFT : ActionType.TURN_RIGHT;
    }

    @Override
    public double getValue() {
        return angle;
    }

    @Override
    public void setValue(double newVal) {
        if (actionType() == ActionType.TURN_LEFT){
            if (newVal > 0 && newVal <= 180){
                angle = newVal;
            }
        }
        else{
            if (newVal <= -1 && newVal >= -180){
                angle = newVal;
            }
        }
    }


    public String toString(){
        if(angle > 0){
            return "Tourner à gauche de " + String.format("%.0f", Math.abs(angle));
        }
        return "Tourner à droite de " + String.format("%.0f", Math.abs(angle));
    }
}
