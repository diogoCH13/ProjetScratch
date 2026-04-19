package scratch.model;

public class MoveForward extends Action {

    public MoveForward(Expression distance) {
        super(distance);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int val = super.getExpression().valueOfVariableOrLitteral(program);
        val = val < 0 ? -val : val;
        if (!valueIsCorrect(val)){
            throw new RuntimeException("RUNTIME ERROR !");
        }
        cursor.moveForward(-val);
    }

    @Override
    public ActionType actionType() {
        return ActionType.MOVE_FORWARD;
    }

    @Override
    public boolean valueIsCorrect(int value){
        return value >= 1 && value <= 100;
    }

    @Override
    public String toString() {
        return "Avancer de " + super.getExpression().toString();
    }

}