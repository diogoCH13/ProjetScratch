package scratch.model;

public class Turn extends Action {

//    private double angle;
//    private DoubleProperty angle = new SimpleDoubleProperty();
//
//    private DoubleProperty angleProperty(){
//        return angle;
//    }

//    public Turn(double angle){
//        super.valueProperty().setValue(angle);
//    }

    private final ActionType direction;

    public Turn(ActionType direction, Expression expression){
        super(expression);
        this.direction = direction;
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int val = super.getExpression().valueOfVariableOrLitteral(program);
//        if (direction == ActionType.TURN_RIGHT){
//            val = -val;
//        }
        if (!valueIsCorrect(val)){
            throw new RuntimeException("RUNTIME ERROR !");
        }
        cursor.rotate(val);
    }

    @Override
    public ActionType actionType() {
        return direction;
    }

    @Override
    public boolean valueIsCorrect(int value){
        if (value < 0){
            value = -value;
        }
        return value > 0 && value <= 180;
    }

//    @Override
//    public double getValue() {
//        return super.valueProperty().get();
//    }
//
//    @Override
//    public void setExpression(double newVal) {
//        boolean valid = actionType() == ActionType.TURN_LEFT ? (newVal > 0 && newVal <= 180) : (newVal >= -180 && newVal <= -1);
//        if (valid){
//            super.valueProperty().setValue(newVal);
//        }
//    }

    public String toString(){
        String val = "Tourner à droite de " + super.getExpression();
        if (direction == ActionType.TURN_LEFT){
            val = "Tourner à gauche de " + super.getExpression();
        }
        return val;
//        if(super.valueProperty().get() > 0){
//            return "Tourner à gauche de " + String.format("%.0f", Math.abs(super.valueProperty().get()));
//        }
//        return "Tourner à droite de " + String.format("%.0f", Math.abs(super.valueProperty().get()));
    }
}
