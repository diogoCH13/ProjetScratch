package scratch.model;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Action {

    public DoubleProperty value = new SimpleDoubleProperty();
    public StringProperty var = new SimpleStringProperty();

    public DoubleProperty valueProperty(){
        return value;
    }

    public StringProperty varProperty(){
        return var;
    }

    public abstract void execute(Cursor cursor);

    public abstract ActionType actionType();

    public abstract void setValue(double value);

    public abstract double getValue();

    public void setVar(String var){
        this.var.set(var);
    }

    public String getVar(){
        return var.get();
    }

    public String actionDetail(ActionType actionType){
        return switch (actionType) {
            case MOVE_FORWARD -> "Avancer de";
            case TURN_LEFT -> "Angle vers la gauche";
            case TURN_RIGHT -> "Angle vers la droite";
            case PEN_DOWN -> "Abaisser stylo";
            case VAR_DECLARATION -> "Déclaration de la variable";
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

    public void setSelectionnedValue(String newVal, BooleanProperty errorValue, int index, ObservableList<Action> list){

        if (!newVal.isEmpty()){
            if (Character.isDigit(newVal.charAt(0))){
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
                        case VAR_DECLARATION:
                            errorValue.set(true);
                            break;

                        default:
                            errorValue.set(false);
                    }
                }
                catch(NumberFormatException exc){
                    errorValue.set(true);
                }
            }
            else if (Character.isLetter(newVal.charAt(0)) || newVal.charAt(0) == '_'){

                errorValue.set(false);
                for (int i = 0; i < newVal.length() && !errorValue.getValue(); ++i){
                    if (!(Character.isLetterOrDigit(newVal.charAt(i)) || newVal.charAt(i) == '_')){
                        errorValue.set(true);
                    }

                }
                if (errorValue.getValue() == false){
                    list.get(index).setVar(newVal);
                }
            }
            else{
                errorValue.set(true);
            }
        }
        else{
            errorValue.set(true);
        }
    }

//    public boolean isComposite() {
//        return false;
//    }
//
//    public ObservableList<Action> getChildren() {
//        return FXCollections.emptyObservableList();
//    }
}