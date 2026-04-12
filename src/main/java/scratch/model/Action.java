package scratch.model;


import javafx.beans.property.*;

public abstract class Action {

//    private final DoubleProperty value = new SimpleDoubleProperty();
//    private final StringProperty var = new SimpleStringProperty();
//
//    public DoubleProperty valueProperty(){
//        return value;
//    }
//
//    public StringProperty varProperty(){
//        return var;
//    }

    private ObjectProperty<Expression> expressionProperty = new SimpleObjectProperty<>();
    private StringProperty variableTarget = new SimpleStringProperty();

    public Action(Expression expression){
        expressionProperty.set(expression);
    }

    public abstract void execute(Cursor cursor, Program program);

    public abstract ActionType actionType();

    public Expression getExpression() {
        return expressionProperty.get();
    }

    public ObjectProperty<Expression> expressionProperty() {
        return expressionProperty;
    }

    public void setExpression(Expression expression) {
        this.expressionProperty.set(expression);
    }

    public String getVariableTargetName() {
        return variableTarget.get();
    }

    public StringProperty VariableTargetNameProperty() {
        return variableTarget;
    }

    public void setVariableTargetName(String variableTarget) {
        this.variableTarget.set(variableTarget);
    }

    public boolean valueIsCorrect(int value){
        return true;
    }

    public String actionDetail(){
        return switch (actionType()) {
            case MOVE_FORWARD -> "Avancer de";
            case TURN_LEFT -> "Angle vers la gauche";
            case TURN_RIGHT -> "Angle vers la droite";
            case PEN_DOWN -> "Abaisser stylo";
            case VAR_DECLARATION -> "Déclaration de la variable";
            case VAR_ASSIGNMENT -> "Assignation de la variable";
            case REPEAT -> "Répéter";
            case INCREMENT_VARIABLE -> "Incrémentation de la variable";
            default -> "Lever stylo";
        };
    }

    public String actionDetailValue(){
        return switch (actionType()) {
            case MOVE_FORWARD -> "pixels";
            case TURN_LEFT, TURN_RIGHT -> "degrés";
            case VAR_ASSIGNMENT -> "valeur:";
            case REPEAT -> "fois";
            case INCREMENT_VARIABLE -> "de";
            default -> "";
        };
    }
}