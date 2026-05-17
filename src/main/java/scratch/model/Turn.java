package scratch.model;

public class Turn extends OneExpressionAction {

    private final ActionType direction;

    public Turn(ActionType direction, Expression expression){
        super(expression);
        this.direction = direction;
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int val = super.getExpression().valueOfExpression(program);

        if (!valueIsCorrect(val)) {
            throw new RuntimeException("RUNTIME ERROR !");
        }

        val = Math.abs(val);

        if (direction == ActionType.TURN_RIGHT) {
            val = -val;
        }

        cursor.rotate(val);
    }

    @Override
    public ActionType actionType() {
        return direction;
    }

    @Override
    public Action copy() {
        return new Turn(direction, super.getExpression());
    }

    @Override
    public boolean valueIsCorrect(int value){
        if (value < 0){
            value = -value;
        }
        return value > 0 && value <= 180;
    }

    public String toString(){
        String val = "Tourner à droite de " + super.getExpression().toString();
        if (direction == ActionType.TURN_LEFT){
            val = "Tourner à gauche de " + super.getExpression().toString();
        }
        return val;
    }
}
