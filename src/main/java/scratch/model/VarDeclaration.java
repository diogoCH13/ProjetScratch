package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VarDeclaration extends Action{

    private StringProperty name = new SimpleStringProperty();

    public String getName() {
        return name.get();
    }

    public void setName(String name){
        this.name.set(name);
    }

    @Override
    public ActionType actionType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public void setValue(double value) {
    }

    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public void execute(Cursor cursor) {

    }

    public String toString(){
        if (name.get() == null || name.get().isEmpty()){
            return "Déclaration variable";
        }
        return "Déclaration variable " + name.get();
    }

}