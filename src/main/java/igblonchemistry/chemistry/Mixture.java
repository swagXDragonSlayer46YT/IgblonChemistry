package igblonchemistry.chemistry;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.client.renderer.RenderingUtils;
import igblonchemistry.common.blocks.TileChemicalReactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Mixture {

    //Compound & amount of moles
    protected HashMap<Chemical, Double> components = new HashMap<Chemical, Double>();

    protected ArrayList<Chemical> containedChemicals = new ArrayList<Chemical>();

    //In Kelvin
    private double temperature;

    //In Joules
    private double energyContained;

    private boolean hasPH;
    private double pH = 0;
    private double averageHeatCapacity;

    private double totalVolume;
    private double totalMols;
    private boolean isAqueous;

    private int color;

    public TileChemicalReactor chemicalReactor;

    public Mixture(TileChemicalReactor chemicalReactor, Chemical chemical, double amount, double temperature) {
        components.put(chemical, amount);
        averageHeatCapacity = chemical.getHeatCapacity();
        energyContained = averageHeatCapacity * temperature * amount;
        this.temperature = temperature;
        this.chemicalReactor = chemicalReactor;
    }

    //Simulate chemical reactions within the mixture, between the chemicals in the chemical list
    public void update() {
        cleanComponentsList();

        calculateColorAverage();

        containedChemicals = new ArrayList<>(components.keySet());

        updateVariables();

        calculatePH();

        calculateTotalVolume();

        runPossibleReactions();
    }

    //Delete itself if mixture is empty
    public boolean isEmpty() {
        if (components.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    //remove chemical from chemical list if there is 0 of it
    public void cleanComponentsList() {
        ArrayList<Chemical> chemicalsToRemove = new ArrayList<Chemical>();
        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getValue() <= 0) {
                chemicalsToRemove.add(entry.getKey());
            }
        }

        for (Chemical chemical : chemicalsToRemove) {
            components.remove(chemical);
        }
    }
    //Runs through all chemical reactions, sees which ones are valid, and runs the valid ones
    //Multiple chemical reactions may occur at once
    public void runPossibleReactions() {

        for (ChemicalReaction chemicalReaction : ChemicalReactions.chemicalReactions) {
            boolean canRun = checkIfReactionPossible(chemicalReaction);

            if (canRun) {
                double reactionAmount = 0;

                if (chemicalReaction.getReactionType().equals(ChemicalReactionTypes.INSTANT)) {
                    //Set reaction amount per tick to 1% of the limiting factor of the reaction
                    for (Map.Entry<Chemical, Integer> entry : chemicalReaction.getReactants().entrySet()) {
                        if (components.get(entry.getKey()) / entry.getValue() < reactionAmount || reactionAmount == 0) {
                            reactionAmount = components.get(entry.getKey()) > 0.01 ? (components.get(entry.getKey()) / entry.getValue()) / 100 : components.get(entry.getKey());
                        }
                    }
                }

                for (Map.Entry<Chemical, Integer> entry : chemicalReaction.getReactants().entrySet()) {
                    removeChemical(entry.getKey(), entry.getValue() * reactionAmount);
                }
                for (Map.Entry<Chemical, Integer> entry : chemicalReaction.getProducts().entrySet()) {
                    addChemical(entry.getKey(), entry.getValue() * reactionAmount, temperature);
                }
                addJoules(-chemicalReaction.getEnthalpyChange() * reactionAmount);
            }
        }
    }

    public boolean checkIfReactionPossible(ChemicalReaction chemicalReaction) {
        ArrayList<Chemical> reactantsNeeded = chemicalReaction.getReactantArray();

        //Check if this mixture contains each chemical needed in the reaction, return false if any single required chemical is not found (reaction impossible)
        for (Chemical reactant : reactantsNeeded) {
            boolean reactantFound = false;
            for (Chemical chemical : containedChemicals) {
                if (chemical.compareTo(reactant) == 0) {
                    reactantFound = true;
                    break;
                }
            }

            if (!reactantFound) {
                return false;
            }
        }

        return true;
    }

    //Update all variables in one function
    public void updateVariables() {
        isAqueous = false;
        double totalHeatCapacity = 0;
        double totalMoles = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getKey().compareTo(Chemicals.Water) == 0) {
                isAqueous = true;
            }

            totalHeatCapacity += entry.getKey().getHeatCapacity() * entry.getValue();
            totalMoles += entry.getValue();
        }

        totalMols = totalMoles;
        averageHeatCapacity = totalHeatCapacity / totalMoles;
        temperature = (energyContained / averageHeatCapacity) / totalMols;
    }

    public double findChemical(Chemical chemical) {
        if (components.get(chemical) != null) {
            return components.get(chemical);
        }
        return 0;
    }

    public void addChemical(Chemical chemical, double amount, double temperature) {
        energyContained += amount * temperature * chemical.getHeatCapacity();

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getKey().compareTo(chemical) == 0) {
                entry.setValue(entry.getValue() + amount);
                return;
            }
        }

        this.components.put(chemical, amount);
    }

    public boolean removeChemical(Chemical chemical, double amount) {
        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getKey().compareTo(chemical) == 0) {
                entry.setValue(Math.max(entry.getValue() - amount, 0));
                energyContained -= amount * temperature * chemical.getHeatCapacity();
                return true;
            }
        }

        return false;
    }

    //moves a specific chemical from a mixture
    public boolean moveChemical(Mixture mixtureFrom, Chemical chemicalToMove, double amount) {
        if (mixtureFrom.removeChemical(chemicalToMove, amount)) {
            addChemical(chemicalToMove, amount, mixtureFrom.getTemperature());
            return true;
        }

        return false;
    }

    //moves all chemicals from a mixture, proportionally
    public Mixture moveMixture(Mixture mixtureFrom, double percentage) {
        //IgblonChemistry.logger.warn(percentage);
        for (Map.Entry<Chemical, Double> entry : mixtureFrom.getComponents().entrySet()) {
            if (entry.getValue() * percentage > 0.01) {
                moveChemical(mixtureFrom, entry.getKey(), entry.getValue() * percentage);
            } else {
                moveChemical(mixtureFrom, entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public HashMap<Chemical, Double> getComponents() {
        return components;
    }

    public void calculateColorAverage() {
        if (components.size() == 1) {
            Map.Entry<Chemical, Double> entry = components.entrySet().iterator().next();
            color = entry.getKey().getColor();
        } else {
            int rSum = 0;
            int gSum = 0;
            int bSum = 0;
            int totalMols = 0;

            for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
                rSum += RenderingUtils.red(entry.getKey().getColor()) * entry.getValue();
                gSum += RenderingUtils.green(entry.getKey().getColor()) * entry.getValue();
                bSum += RenderingUtils.blue(entry.getKey().getColor()) * entry.getValue();
                totalMols += entry.getValue();
            }

            if (totalMols <= 0) {
                totalMols = 1;
            }

            color = RenderingUtils.RGBtoHex(Math.min(rSum / totalMols, 255), Math.min(gSum / totalMols, 255), Math.min(bSum / totalMols, 255));
        }
    }

    public int getColor() {
        return color;
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
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public double getEnergyContained() {
        return energyContained;
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

    public double getTotalMols() {
        return totalMols;
    }

    //Add an amount of joules to this mixture, changing its temperature based on its heat capacity
    public void addJoules(double joules) {
        energyContained += joules;
    }

    public double getJoules() {
        return energyContained;
    }

    public double getAverageHeatCapacity() {
        return averageHeatCapacity;
    }

    public void setTotalMols(double totalMols) {
        this.totalMols = totalMols;
    }

    public void setAverageHeatCapacity(double averageHeatCapacity) {
        this.averageHeatCapacity = averageHeatCapacity;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}