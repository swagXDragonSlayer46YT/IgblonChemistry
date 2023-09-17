package igblonchemistry.chemistry;

import igblonchemistry.IgblonChemistry;

import java.util.HashMap;
import java.util.Map;

public class Mixture {

    //Compound & amount of moles
    private HashMap<Compound, Double> components = new HashMap<Compound, Double>();

    private double viscosity;
    private double temperature;

    public Mixture(Compound compound, double amount) {
        components.put(compound, amount);
    }

    //Simulate chemical reactions within the mixture, between the compounds in the components list
    public void update() {
        //TODO: SIMULATE CHEMICAL REACTIONS WITHIN MIXTURE, BETWEEN COMPONENT LIST
    }

    public double getViscosity() {
        return viscosity;
    }

    public Mixture addCompound(Compound compound, double amount) {
        for (Map.Entry<Compound, Double> entry : components.entrySet()) {
            if (entry.getKey().compareTo(compound) == 0) {
                entry.setValue(entry.getValue() + amount);
                return this;
            }
        }

        this.components.put(compound, amount);
        return this;
    }

    public Mixture moveCompound() {
        //Add an amount to this mixture, remove the same amount from another mixture
        return null;
    }

    public HashMap<Compound, Double> getComponents() {
        return components;
    }

    public int getColorAverage() {
        if (components.size() == 1) {
            Map.Entry<Compound, Double> entry = components.entrySet().iterator().next();
            return entry.getKey().getColor();
        } else {
            int colorSum = 0;
            double totalMols = 0;
            for (Map.Entry<Compound, Double> entry : components.entrySet()) {
                colorSum += (int) (entry.getKey().getColor() * entry.getValue());
                totalMols += entry.getValue();
            }
            return (int) (colorSum / totalMols);
        }
    }

    public double[] getIndividualVolumes() {
        double[] volumes = new double[components.size()];
        int i = 0;

        for (Map.Entry<Compound, Double> entry : components.entrySet()) {
            volumes[i] = entry.getValue() * entry.getKey().getMolarMass() / entry.getKey().getDensity();
            i++;
        }

        return volumes;
    }

    //Measured in Liters
    public double getTotalVolume() {
        double[] volumes = getIndividualVolumes();
        double totalVolume = 0;

        for (double a : volumes) {
            totalVolume += a;
        }

        return totalVolume;
    }
}