package scratch.model;

public class Turn extends Action {
    private final double angle;

    public Turn(double angle){
        this.angle = angle;
    }


    @Override
    public void execute(Cursor cursor) {

    }

    public String toString(){
        if(angle > 0){
            return "Tourner à droite de " + Math.abs(angle);
        }
        return "Tourner à gauche de " + Math.abs(angle);
    }
}
