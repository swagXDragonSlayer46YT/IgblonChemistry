package igblonchemistry.chemistry;

import java.util.ArrayList;
import java.util.HashMap;

public class ChemicalReaction {

    private HashMap<Chemical, Integer> reactants = new HashMap<Chemical, Integer>();
    private HashMap<Chemical, Integer> products = new HashMap<Chemical, Integer>();

    //measured in Joules per reaction done
    private double enthalpyChange;

    //measured in mols per tick
    private double reactionSpeed;

    private ChemicalReactionTypes chemicalReactionType;

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

    public double getReactionSpeed() {
        //TODO: RETURN DIFFERENT VALUES BASED ON EQUILIBRIUM, TEMPERATURE, ETC
        return reactionSpeed;
    }

    public ChemicalReaction setReactionSpeed(double reactionSpeed) {
        this.reactionSpeed = reactionSpeed;
        return this;
    }

    public ChemicalReactionTypes getReactionType() {
        return chemicalReactionType;
    }

    public ChemicalReaction setChemicalReactionType(ChemicalReactionTypes reactionType) {
        this.chemicalReactionType = reactionType;
        return this;
    }
}
