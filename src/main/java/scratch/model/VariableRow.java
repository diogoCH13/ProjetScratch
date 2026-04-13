package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableRow {

    private final StringProperty nom = new SimpleStringProperty();
    private final IntegerProperty valeur = new SimpleIntegerProperty();

    public VariableRow(String nom, int valeur) {
        this.nom.set(nom);
        this.valeur.set(valeur);
    }

    public String getNom() {
        return nom.get();
    }

    public int getValeur() {
        return valeur.get();
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public IntegerProperty valeurProperty() {
        return valeur;
    }
}