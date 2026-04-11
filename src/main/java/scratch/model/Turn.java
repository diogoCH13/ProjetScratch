package scratch.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Turn extends Action {

//    private double angle;
//    private DoubleProperty angle = new SimpleDoubleProperty();
//
//    private DoubleProperty angleProperty(){
//        return angle;
//    }

    public Turn(double angle){
        super.valueProperty().setValue(angle);
    }

    @Override
    public void execute(Cursor cursor) {
        cursor.rotate(super.valueProperty().get());
    }

    @Override
    public ActionType actionType() {
        return super.valueProperty().get() > 0 ? ActionType.TURN_LEFT : ActionType.TURN_RIGHT;
    }

    @Override
    public double getValue() {
        return super.valueProperty().get();
    }

    @Override
    public void setValue(double newVal) {
        boolean valid = actionType() == ActionType.TURN_LEFT ? (newVal > 0 && newVal <= 180) : (newVal >= -180 && newVal <= -1);
        if (valid){
            super.valueProperty().setValue(newVal);
        }
    }


    public String toString(){
        if(super.valueProperty().get() > 0){
            return "Tourner à gauche de " + String.format("%.0f", Math.abs(super.valueProperty().get()));
        }
        return "Tourner à droite de " + String.format("%.0f", Math.abs(super.valueProperty().get()));
    }
}
