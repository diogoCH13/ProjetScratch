package scratch.model;

public enum ActionType {

    MOVE_FORWARD("Avancer de"),
    TURN_LEFT("Tourner à gauche de"),
    TURN_RIGHT("Tourner à droite de"),
    PEN_UP("Lever stylo"),
    PEN_DOWN("Abaisser le stylo");

    private final String label;

    ActionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}