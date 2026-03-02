package scratch.model;

public class Turn extends Action {

    public Turn(double angle){
        super.value.setValue(angle);
    }

    @Override
    public void execute(Cursor cursor) {
        if (super.value.get() > 0) {
            cursor.turnLeft(super.value.get());
        }
        else {
            cursor.turnRight(super.value.get());
        }
    }

    @Override
    public ActionType actionType() {
        return super.value.get() > 0 ? ActionType.TURN_LEFT : ActionType.TURN_RIGHT;
    }

    @Override
    public double getValue() {
        return super.value.getValue();
    }

    public void setValue(double value) {
        super.value.setValue(value);
    }


    public String toString(){
        if(super.value.get() > 0){
            return "Tourner à gauche de " + String.format("%.0f", Math.abs(super.value.get()));
        }
        return "Tourner à droite de " + String.format("%.0f", Math.abs(super.value.get()));
    }
}
