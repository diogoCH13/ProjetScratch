package scratch.model;


import javafx.beans.Observable;
import javafx.beans.property.*;

public abstract class Action {
    public abstract void execute(Cursor cursor, Program program);

    public abstract ActionType actionType();

    public abstract Action copy();

    public abstract Observable[] observables();

    public boolean valueIsCorrect(int value){
        return true;
    }

    public int parameterCount() {
        return 0;
    }

    public String parameterValue(int index) {
        return "";
    }

    public void setParameterValue(int index, String value, Program program) { }

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

    //    //permet de créer un property de l'objet expression pour voir ces changement
//    private ObjectProperty<Expression> expressionProperty = new SimpleObjectProperty<>();

//    //uniquement pour increment et assignation
//    private StringProperty variableTarget = new SimpleStringProperty();

//    public Action(Expression expression){
//        expressionProperty.set(expression);
//    }

//    public Expression getExpression() {
//        return expressionProperty.get();
//    }
//
//    public ObjectProperty<Expression> expressionProperty() {
//        return expressionProperty;
//    }
//
//    public void setExpression(Expression expression) {
//        this.expressionProperty.set(expression);
//    }

//    public String getVariableTargetName() {
//        return variableTarget.get();
//    }
//
//    public StringProperty VariableTargetNameProperty() {
//        return variableTarget;
//    }
//
//    public void setVariableTargetName(String variableTarget) {
//        this.variableTarget.set(variableTarget);
//    }
}