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
                return new Turn(+90);
            }
            case TURN_RIGHT -> {
                return new Turn(-90);
            }
            case MOVE_FORWARD -> {
                return new MoveForward(-30);
            }
        }
        throw new IllegalArgumentException("Unknown action type: " + type);
    }
}
