package scratch.model;

public class Rectangle extends TwoExpression{

    public Rectangle(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public void execute(Cursor cursor, Program program) {
        int height = super.getExpression1().valueOfExpression(program);
        int width = super.getExpression2().valueOfExpression(program);

        if (!valueIsCorrect(height) || !valueIsCorrect(width)) {
            throw new RuntimeException("RUNTIME ERROR !");
        }

        cursor.rectangle(height, width);
    }

    @Override
    public ActionType actionType() {
        return ActionType.DRAW_RECTANGLE;
    }

    @Override
    public Action copy() {
        return new Rectangle(super.getExpression1(), super.getExpression2());
    }

    @Override
    public boolean valueIsCorrect(int value){
        return value >= 20;
    }

    @Override
    public boolean canBeLastAction() {
        return false;
    }

    @Override
    public String parameterPrefix(int index) {
        String prefix = "";

        if (index == 0){
            prefix = "Affiche un rectangle de hauteur";
        }
        else if (index == 1){
            prefix = "et de largeur";
        }

        return prefix;
    }

    @Override
    public String toString() {
        return "Rectangle : " + super.getExpression1().toString() + ", " + super.getExpression2().toString();
    }
}
