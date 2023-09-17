package igblonchemistry.chemistry;

import java.util.HashMap;

public class Mixture {

    private HashMap<Compound, Double> components = new HashMap<Compound, Double>();

    private double viscosity;
    private double temperature;

    public Mixture() {

    }

    //Simulate chemical reactions within the mixture, between the compounds in the components list
    public void update() {
        //TODO: SIMULATE CHEMICAL REACTIONS WITHIN MIXTURE, BETWEEN COMPONENT LIST
    }

    public double getViscosity() {
        return viscosity;
    }

    public Mixture addCompound(Compound compound, double amount) {
        this.components.put(compound, amount);
        return this;
    }
}