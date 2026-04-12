package scratch.model;

public class ActionFactory {

    public static Action createAction(ActionType type){
        switch (type){
            case PEN_UP -> {
                return new Pen(false);
            }
            case PEN_DOWN -> {
                return new Pen(true);
            }
            case TURN_LEFT -> {
                return new Turn(ActionType.TURN_LEFT, new LitteralExpression(90));
            }
            case TURN_RIGHT -> {
                return new Turn(ActionType.TURN_RIGHT, new LitteralExpression(-90));
            }
            case MOVE_FORWARD -> {
                return new MoveForward(new LitteralExpression(-30));
            }
            case REPEAT -> {
                return new Repeat(new LitteralExpression(2));
            }
            case VAR_DECLARATION -> {
                return new VarDeclaration(new LitteralExpression(0));
            }
            case VAR_ASSIGNMENT -> {
                return new VarAssignment(new VariableExpression(""), "");
            }
            case INCREMENT_VARIABLE -> {
                return new IncrementVariable(new LitteralExpression(0));
            }
            case END_REPEAT -> {
                return new EndRepeat(new LitteralExpression(0));
            }

        }
        throw new IllegalArgumentException("Unknown action type: " + type);
    }
}
