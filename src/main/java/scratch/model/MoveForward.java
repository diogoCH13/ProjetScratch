package scratch.model;

public class MoveForward extends Action {

    public MoveForward(Expression distance) {
        super(distance);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        cursor.moveForward(super.getExpression().expressionValue(program));
    }

    @Override
    public ActionType actionType() {
        return ActionType.MOVE_FORWARD;
    }

    @Override
    public boolean valueIsCorrect(int value){
        return value >= 1 && value <= 180;
    }

//    @Override
//    public double getValue() {
//        return super.valueProperty().getValue();
//    }
//
//    public void setExpression(double value) {
//        super.valueProperty().setValue(value);
//    }

    @Override
    public String toString() {
        return "Avancer de " + super.getExpression().expressionName() ;
    }

}