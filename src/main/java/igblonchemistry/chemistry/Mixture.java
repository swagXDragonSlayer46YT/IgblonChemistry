package igblonchemistry.chemistry;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.client.renderer.RenderingUtils;
import igblonchemistry.common.blocks.TileChemicalReactor;
import scala.Console;

import java.util.*;

public class Mixture {
    //Compound & amount of moles
    protected HashMap<Chemical, Double> components = new HashMap<Chemical, Double>();

    //In Kelvin
    private double temperature;

    //In Joules
    private double energyContained;

    private double pH = 0;

    private double porosity = 0.1;

    private double averageHeatCapacity;
    private double averageDensity;

    protected double totalVolume;
    private double totalMass;
    private double totalMols;

    private int color;

    public TileChemicalReactor chemicalReactor;

    private Chemical dominantChemical;
    private boolean isMostlySolid;
    private boolean isPorous = true;

    public Mixture(TileChemicalReactor chemicalReactor, Chemical chemical, double amount, double temperature) {
        this.chemicalReactor = chemicalReactor;

        amount = Math.max(amount, 1);

        components.put(chemical, amount);

        averageHeatCapacity = chemical.getHeatCapacity();
        energyContained = averageHeatCapacity * temperature * amount;

        updateVariables();
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
            for (Chemical chemical : components.keySet()) {
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
        double _totalHeatCapacity = 0;
        double _totalMols = 0;
        double _totalVolume = 0;
        double _totalMass = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            _totalHeatCapacity += entry.getKey().getHeatCapacity() * entry.getValue();
            _totalMols += entry.getValue();
            _totalVolume += entry.getValue() * entry.getKey().getMolarMass() / entry.getKey().getDensity();
            _totalMass += entry.getValue() * entry.getKey().getMolarMass();
        }

        //Prevent divide by 0 errors
        if (_totalMols > 0.0001 && _totalVolume > 0.0001) {
            totalVolume = _totalVolume;
            totalMols = _totalMols;
            averageHeatCapacity = _totalHeatCapacity / _totalMols;
            averageDensity = _totalMass / _totalVolume;
            totalMass = _totalMass;
            temperature = (energyContained / averageHeatCapacity) / _totalMols;

            if (components.size() > 1) {
                dominantChemical = Collections.max(components.entrySet(), Map.Entry.comparingByValue()).getKey();
            } else {
                dominantChemical = components.entrySet().stream().findFirst().get().getKey();
            }

            if (temperature > dominantChemical.getMeltingPoint()) {
                isMostlySolid = false;
            } else {
                isMostlySolid = true;
            }
        }
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

        updateVariables();
    }

    public boolean removeChemical(Chemical chemical, double amount) {
        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            if (entry.getKey().compareTo(chemical) == 0) {
                double constraintedAmount = Math.min(amount, entry.getValue());

                entry.setValue(entry.getValue() - constraintedAmount);
                energyContained -= constraintedAmount * temperature * chemical.getHeatCapacity();
                return true;
            }
        }

        updateVariables();

        return false;
    }

    //moves a specific chemical from a mixture
    public boolean moveChemical(Mixture mixtureFrom, Chemical chemicalToMove, double amount) {
        if (mixtureFrom.removeChemical(chemicalToMove, amount)) {
            addChemical(chemicalToMove, amount, mixtureFrom.getTemperature());
            mixtureFrom.updateVariables();
            return true;
        }

        updateVariables();

        return false;
    }

    public void doSeparations(Mixture mixtureBelow, Mixture mixtureAbove, double separationSpeed) {

        Random r = new Random();

        //This mixture is solid, so everything is trapped in it
        if (!isPorous) {
            return;
        }

        //This mixture only has 1 component, so it doesnt have to separate anything out
        if (components.size() < 2) {
            return;
        }

        double totalFluidVolume = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            //the dominant chemical does not move from its own mixture
            if (entry.getKey().compareTo(dominantChemical) == 0) {
                continue;
            }

            //calculate the max amount a certain chemical can stay in this mixture
            double maxAmount = 0;

            if (isMostlySolid) {
                if (temperature > entry.getKey().getBoilingPoint()) {
                    //This chemical is gaseous in this porous mixture, so it rises up
                    if (mixtureAbove != null) {
                        mixtureAbove.moveChemical(this, entry.getKey(), Math.min(entry.getValue(), separationSpeed));
                    } else {
                        chemicalReactor.getContainedGas().moveChemical(this, entry.getKey(), Math.min(entry.getValue(), separationSpeed));
                    }

                    continue;

                } else if (temperature > entry.getKey().getMeltingPoint()) {
                    //This chemical is fluid in this porous mixture, so the max amount is whatever room is left
                    maxAmount = (totalVolume * porosity - totalFluidVolume) * entry.getKey().getDensity() / entry.getKey().getMolarMass();

                    totalFluidVolume += getVolumeOfIndividualChemical(entry.getKey());

                } else {
                    //This chemical is solid in this porous mixture, so it can't move
                    maxAmount = Double.MAX_VALUE;
                }
            } else {
                //Find if this chemical is miscible or soluble in this fluid mixture
                boolean isMiscible = false;

                for (Chemical miscibleChemical : entry.getKey().getMiscibilities()) {
                    if (dominantChemical != null) {
                        if (miscibleChemical.compareTo(dominantChemical) == 0) {
                            isMiscible = true;
                            break;
                        }
                    }
                }

                if (isMiscible) {
                    maxAmount = Double.MAX_VALUE;
                } else {
                    HashMap<Chemical, SolubilityInfo> solubilityInfos = entry.getKey().getSolubilityInfos();

                    if (solubilityInfos.isEmpty()) {
                        maxAmount = 0;
                    } else {
                        double solubilityTotal = 0;

                        for (Map.Entry<Chemical, SolubilityInfo> entry2 : solubilityInfos.entrySet()) {
                            for (Map.Entry<Chemical, Double> entry3 : components.entrySet()) {
                                if (entry3.getKey().compareTo(entry2.getKey()) == 0 && entry.getKey().compareTo(entry2.getKey()) != 0) {
                                    solubilityTotal += entry2.getValue().calculateSolubility(temperature) * getVolumeOfIndividualChemical(entry3.getKey()) / entry.getKey().getMolarMass();
                                }
                            }
                        }

                        maxAmount = solubilityTotal;
                    }
                }
            }

            //get rid of the chemical if necessary
            if (entry.getValue() > maxAmount) {
                double amountToMove = Math.min(Math.min(separationSpeed, entry.getValue()), entry.getValue() - maxAmount + r.nextDouble());

                if (entry.getKey().getDensity() > averageDensity) {
                    if (mixtureBelow != null) {
                        mixtureBelow.moveChemical(this, entry.getKey(), amountToMove);
                    } else {
                        chemicalReactor.contents.add(0, (new Mixture(chemicalReactor, entry.getKey(), amountToMove, temperature)));
                    }
                } else {
                    if (mixtureAbove != null) {
                        mixtureAbove.moveChemical(this, entry.getKey(), amountToMove);
                    } else {
                        chemicalReactor.contents.add(chemicalReactor.contents.size(), (new Mixture(chemicalReactor, entry.getKey(), amountToMove, temperature)));
                    }
                }
            }
        }

        updateVariables();
    }

    //moves all chemicals from a mixture, proportionally
    public Mixture moveMixture(Mixture mixtureFrom, double amount) {
        for (Map.Entry<Chemical, Double> entry : mixtureFrom.getComponents().entrySet()) {
            double proportion = mixtureFrom.getVolumeOfIndividualChemical(entry.getKey()) / mixtureFrom.getTotalVolume();
            mixtureFrom.updateVariables();

            moveChemical(mixtureFrom, entry.getKey(), amount * proportion);
        }

        updateVariables();

        return this;
    }

    public void mergeMixture(Mixture mixtureTo) {
        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            mixtureTo.moveChemical(this, entry.getKey(), entry.getValue());
        }

        mixtureTo.updateVariables();

        components.clear();
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

    public double getVolumeOfIndividualChemical(Chemical chemical) {
        return components.get(chemical) * chemical.getMolarMass() / chemical.getDensity();
    }

    //Measured in Liters
    public double getTotalVolume() {
        return totalVolume;
    }

    public double getEnergyContained() {
        return energyContained;
    }

    public Chemical getDominantChemical() {
        return dominantChemical;
    }

    public boolean getIsMostlySolid() {
        return isMostlySolid;
    }

    //Measured in Kelvin
    public double getTemperature() {
        return temperature;
    }

    /*
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

     */



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

    public double getAverageDensity() {
        return averageDensity;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}