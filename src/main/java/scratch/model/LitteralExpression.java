package scratch.model;

public class LitteralExpression implements Expression{

    private final int value;

    public LitteralExpression(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    @Override
    public int valueOfVariableOrLitteral(Program program) {
        return value;
    }

    @Override
    public String toString() {
        return value < 0 ? String.valueOf(-value) : String.valueOf(value);
    }
}
