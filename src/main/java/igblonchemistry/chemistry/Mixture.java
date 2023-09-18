package igblonchemistry.chemistry;

import igblonchemistry.common.blocks.TileChemicalReactor;

import java.util.HashMap;
import java.util.Map;

public class Mixture {

    //Compound & amount of moles
    private HashMap<Chemical, Double> components = new HashMap<Chemical, Double>();

    private double temperature = 293;

    private boolean hasPH;
    private double pH = 0;

    private double totalVolume;
    private boolean isAqueous;

    public TileChemicalReactor chemicalReactor;

    public Mixture(TileChemicalReactor chemicalReactor, Chemical chemical, double amount) {
        components.put(chemical, amount);
        this.chemicalReactor = chemicalReactor;
    }

    //Simulate chemical reactions within the mixture, between the chemicals in the chemical list
    public void update() {

        //Delete itself if mixture is empty
        boolean isEmpty = true;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getValue() > 0) {
                isEmpty = false;
            }
        }

        if (isEmpty) {
            chemicalReactor.getContents().remove(this);
        }

        updateVariables();
        calculatePH();
        calculateTotalVolume();
        //THE MIXTURE SHOULD SIMULATE CHEMICAL REACTIONS WITHIN THE MIXTURE (IF ANY ARE POSSIBLE) BETWEEN ITS COMPONENTS EVERY TICK
    }

    public void updateVariables() {
        isAqueous = false;
        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            //remove chemical from chemical list if there is 0 of it
            if (entry.getKey().compareTo(Chemicals.Water) == 0) {
                isAqueous = true;
            }
            if (entry.getValue() <= 0) {
                components.remove(entry.getKey());
            }
        }
    }

    public Mixture addChemical(Chemical chemical, double amount) {
        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getKey().compareTo(chemical) == 0) {
                entry.setValue(entry.getValue() + amount);
                return this;
            }
        }

        this.components.put(chemical, amount);
        return this;
    }

    public Mixture moveChemical(Mixture mixtureFrom, Chemical chemicalToMove, double amount) {
        //Add an amount to this mixture, remove the same amount from another mixture
        addChemical(chemicalToMove, amount);

        for (Map.Entry<Chemical, Double> entry : mixtureFrom.getComponents().entrySet()) {
            if (entry.getKey().compareTo(chemicalToMove) == 0) {
                entry.setValue(entry.getValue() - amount);
            }
        }
        return this;
    }

    public Mixture moveChemical(Mixture mixtureFrom, double percentage) {
        //THIS FUNCTION SHOULD MOVE PERCENTAGES OF AN ENTIRE MIXTURE AT ONCE
        return this;
    }

    public HashMap<Chemical, Double> getComponents() {
        return components;
    }

    public int getColorAverage() {
        if (components.size() == 1) {
            Map.Entry<Chemical, Double> entry = components.entrySet().iterator().next();
            return entry.getKey().getColor();
        } else {
            int colorSum = 0;
            double totalMols = 0;

            for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
                colorSum += entry.getKey().getColor() * entry.getValue();
                totalMols += entry.getValue();
            }

            return (int) (colorSum / totalMols);
        }
    }

    public double[] getIndividualVolumes() {
        double[] volumes = new double[components.size()];
        int i = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
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

    public void calculateTotalVolume() {
        double[] volumes = getIndividualVolumes();
        double tVolume = 0;

        for (double a : volumes) {
            tVolume += a;
        }

        totalVolume = tVolume;
    }

    //Measured in Kelvin
    public double getTemperature() {
        return temperature;
    }

    public void calculatePH() {
        if (!isAqueous) {
            hasPH = false;
            return;
        }

        double hMolarity = 0;
        double ohMolarity = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getKey().hasPKA()) {
                //If compound dissociates completely
                if(entry.getKey().getPKA() < 0) {
                    //Add Molar concentration of ions
                    hMolarity += entry.getKey().getHIons() * (entry.getValue() / totalVolume);
                    ohMolarity += entry.getKey().getOHIons() * (entry.getValue() / totalVolume);
                } else {
                    hMolarity += entry.getKey().getPKA() * entry.getKey().getHIons() * (entry.getValue() / totalVolume);
                    ohMolarity += entry.getKey().getPKA() * entry.getKey().getOHIons() * (entry.getValue() / totalVolume);
                }
                //TODO: Partial acid dissociations for weak acids
                //SOURCE: https://chem.libretexts.org/Bookshelves/Physical_and_Theoretical_Chemistry_Textbook_Maps/Supplemental_Modules_(Physical_and_Theoretical_Chemistry)/Acids_and_Bases/Monoprotic_Versus_Polyprotic_Acids_And_Bases/Calculating_the_pH_of_the_Solution_of_a_Polyprotic_Base%2F%2FAcid
            }
        }

        //pH + pOH = 14
        if (hMolarity == 0 && ohMolarity == 0) {
            //No ions present, assume it is neutral, disable PH
            pH = 7;
            hasPH = false;
        } else {
            hasPH = true;
            if (hMolarity == ohMolarity) {
                pH = 7;
            } else if (hMolarity > ohMolarity) {
                pH = -Math.log10(hMolarity - ohMolarity);
            } else {
                pH = 14 + Math.log10(ohMolarity - hMolarity);
            }
        }

        //SOURCE: https://www.youtube.com/watch?v=fFjjh1DFYgo
    }

    public double getPH() {
        return pH;
    }

    public boolean getHasPH() {
        return hasPH;
    }

    public boolean getIsAqueous() {
        return isAqueous;
    }
}