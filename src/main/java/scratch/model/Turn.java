package scratch.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Turn extends Action {
//    private double angle;
    private DoubleProperty angle = new SimpleDoubleProperty();

    private DoubleProperty angleProperty(){
        return angle;
    }


    public Turn(double angle){
        this.angle.set(angle);
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

//    @Override
//    public void execute(Cursor cursor) {
//        if (angle > 0) {
//            cursor.turnLeft(angle);
//        }
//        else {
//            cursor.turnRight(angle);
//        }
//    }

    @Override
    public void execute(Cursor cursor) {
        cursor.rotate(angle.get());
    }

    @Override
    public ActionType actionType() {
        return angle.get() < 0 ? ActionType.TURN_LEFT : ActionType.TURN_RIGHT;
    }

    @Override
    public double getValue() {
        return angle.get();
    }

    @Override
    public void setValue(double newVal) {
        if (actionType() == ActionType.TURN_LEFT){
            if (newVal > 0 && newVal <= 180){
//                angle = newVal;
                angle.set(newVal);
            }
        }
        else{
            if (newVal <= -1 && newVal >= -180){
//                angle = newVal;
                angle.set(newVal);
            }
        }
    }


    public String toString(){
        if(angle.get() > 0){
            return "Tourner à gauche de " + String.format("%.0f", Math.abs(angle.get()));
        }
        return "Tourner à droite de " + String.format("%.0f", Math.abs(angle.get()));
    }
}
