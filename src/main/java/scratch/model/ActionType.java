package scratch.model;

import javafx.scene.paint.Color;

public enum ActionType {
    VAR_DECLARATION("Déclaration variable"),
    VAR_ASSIGNMENT("Assignation"),
    INCREMENT_VARIABLE("Inc/Dec variable"),
    MOVE_FORWARD("Avancer de"),
    TURN_LEFT("Tourner à gauche de"),
    TURN_RIGHT("Tourner à droite de"),
    REPEAT("Répéter"),
    END_REPEAT("Fin répéter"),
    PEN_UP("Lever stylo"),
    PEN_DOWN("Abaisser le stylo");

    public Color getColor(){
         switch(this){
            case PEN_UP, PEN_DOWN -> {
                return Color.GREEN;
            }
            case TURN_LEFT, TURN_RIGHT -> {
                return Color.RED;
            }
            case MOVE_FORWARD -> {
                return Color.BLUE;
            }

            default -> {
                 return Color.DARKTURQUOISE;
            }
        }
    }


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