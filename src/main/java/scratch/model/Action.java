package scratch.model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;

public abstract class Action {

    public DoubleProperty value = new SimpleDoubleProperty();

    public DoubleProperty valueProperty(){
        return value;
    }

    public abstract void execute(Cursor cursor);

    public abstract ActionType actionType();

    public abstract void setValue(double value);

    public abstract double getValue();

    public String actionDetail(ActionType actionType){
        return switch (actionType) {
            case MOVE_FORWARD -> "Avancer de";
            case TURN_LEFT -> "Angle vers la gauche";
            case TURN_RIGHT -> "Angle vers la droite";
            case PEN_DOWN -> "Abaisser stylo";
            default -> "Lever stylo";
        };
    }

    public String actionDetailValue(ActionType actionType){
        return switch (actionType()) {
            case MOVE_FORWARD -> "pixels";
            case TURN_LEFT, TURN_RIGHT -> "degrés";
            default -> "";
        };
    }

    public void setSelectedValue(String newVal, BooleanProperty errorValue, int index, ObservableList<Action> list){
        try{
            int value = Integer.valueOf(newVal);

            switch (actionType()) {
                case MOVE_FORWARD:
                    if (value > 0 && value <= 100) {
                        list.get(index).setValue(-value);
                        errorValue.set(false);
                    }
                    else{
                        errorValue.set(true);
                    }
                    break;
                case TURN_LEFT:
                    if (value > 0 && value <= 180) {
                        list.get(index).setValue(value);
                        errorValue.set(false);
                    }
                    else{
                        errorValue.set(true);
                    }
                    break;
                case TURN_RIGHT:
                    if (value > 0 && value <= 180) {
                        list.get(index).setValue(-value);
                        errorValue.set(false);
                    }
                    else{
                        errorValue.set(true);
                    }
                    break;

                default:
                    errorValue.set(false);
            }
        }
        catch(NumberFormatException exc){
            errorValue.set(true);
        }
    }
}