package igblonchemistry.chemistry;

import java.util.ArrayList;
import java.util.HashMap;

public class ChemicalReaction {

    private HashMap<Chemical, Integer> reactants = new HashMap<Chemical, Integer>();
    private HashMap<Chemical, Integer> products = new HashMap<Chemical, Integer>();

    //measured in Joules per reaction done
    private double enthalpyChange;

    public ChemicalReaction() {
        ChemicalReactions.chemicalReactions.add(this);
    }

    public ChemicalReaction addReactant(Chemical reactant, int mols){
        reactants.put(reactant, mols);
        return this;
    }

    public ArrayList<Chemical> getReactantArray() {
        return new ArrayList<>(reactants.keySet());
    }

    public HashMap<Chemical, Integer> getReactants() {
        return reactants;
    }

    public HashMap<Chemical, Integer> getProducts() {
        return products;
    }

    public ChemicalReaction addProduct(Chemical reactant, int mols){
        products.put(reactant, mols);
        return this;
    }

    public ChemicalReaction setEnthalpyChange(double joules){
        enthalpyChange = joules;
        return this;
    }

    public double getEnthalpyChange() {
        return enthalpyChange;
    }
}
