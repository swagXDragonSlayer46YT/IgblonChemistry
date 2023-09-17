package igblonchemistry.chemistry;

import java.util.HashMap;

public class Mixture {

    //Compound & amount of moles
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

    public Mixture moveCompound() {
        //Add an amount to this mixture, remove the same amount from another mixture
        return null;
    }

    public int getColorAverage() {
        int colorSum = 0;
        double totalMols = 0;
        for (Compound key : components.keySet()) {
            colorSum += (int) (key.getColor() * components.get(key));
            totalMols += components.get(key);
        }

        return (int) (colorSum / totalMols);
    }
}