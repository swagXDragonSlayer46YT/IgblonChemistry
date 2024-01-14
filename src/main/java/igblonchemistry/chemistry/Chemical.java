package igblonchemistry.chemistry;

import igblonchemistry.common.ChemistryConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Chemical implements Comparable<Chemical> {
    //SOLUBILITIES TAKEN FROM: https://en.wikipedia.org/wiki/Solubility_table

    private String name;

    private int color = 0xffffff;

    //the temperature required for this chemical's vapor pressure to reach 100,000 Pa
    //measured in Kelvin
    private double boilingPoint;

    //measured in Kelvin
    private double meltingPoint;

    //measured in grams/liter
    private double density;

    //measured in grams
    private double molarMass;

    //measure of acidity
    private double pKa;

    //number of H and OH ions it can donate
    private int hIons;
    private int ohIons;

    //measured in Joules/moles * kelvin
    //default set to water
    private double heatCapacity = 75.38;

    //pKa will only apply to certain compounds
    private boolean hasPKA = false;

    //solubilities measured in grams of this chemical/liters of [x] solvent
    private HashMap<Chemical, SolubilityInfo> solubilityInfos = new HashMap<Chemical, SolubilityInfo>();

    private HashMap<Chemical, Integer> chemicalFormula = new HashMap<Chemical, Integer>();

    private ArrayList<Chemical> miscibleWith = new ArrayList<Chemical>();

    public Chemical(String name) {
        this.name = name;
    }

    public Chemical(String name, Object... chemicalFormula) {
        this.name = name;

        for (int i = 0; i < chemicalFormula.length; i += 2) {
            this.chemicalFormula.put((Chemical) chemicalFormula[i], (Integer) chemicalFormula[i + 1]);
        }
    }

    public HashMap<Chemical, SolubilityInfo> getSolubilityInfos() {
        return solubilityInfos;
    }

    public Chemical registerMiscibility(Chemical... chemicals) {
        miscibleWith.addAll(Arrays.asList(chemicals));
        return this;
    }

    public ArrayList<Chemical> getMiscibilities() {
        return miscibleWith;
    }

    public Chemical setHeatCapacity(double heatCapacity) {
        this.heatCapacity = heatCapacity;
        return this;
    }

    public double getHeatCapacity() {
        return heatCapacity;
    }

    public Chemical addSolubilityInfo(Chemical chemical, SolubilityInfo solubilityInfo) {
        solubilityInfos.put(chemical, solubilityInfo);
        return this;
    }

    public double getSolubility(Chemical solvent, double temperature) {
        SolubilityInfo solubilityInfo = solubilityInfos.get(solvent);

        if (solubilityInfo != null) {
            return solubilityInfo.calculateSolubility(temperature);
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Chemical chemical) {
        return getName().compareTo(chemical.getName());
    }

    public String getName() {
        return this.name;
    }

    //Taken at 1 atm (~101,000 Pascals)
    public Chemical setMeltingPoint(double meltingPoint) {
        this.meltingPoint = meltingPoint;
        return this;
    }

    //Pressure measured in Pascals
    //Temperature measured in Kelvins
    public double getMeltingPoint() {
        return this.meltingPoint;
    }

    //Taken at 1 atm (~101,000 Pascals)
    public Chemical setBoilingPoint(double boilingPoint) {
        this.boilingPoint = boilingPoint;
        return this;
    }

    //Temperature measured in Kelvins

    public double getBoilingPoint() {return this.boilingPoint; }

    /*
    //Pressure measured in Pascals
    //Temperature measured in Kelvins
    public double getBoilingPoint(double pressure) {
        if (heatOfVaporization < 0) {
            //If heat of vaporization is unknown
            return boilingPoint;
        } else {
            return 1 / ((1 / boilingPoint) - (ChemistryConstants.GAS_CONSTANT * Math.log(pressure / 100000)) / heatOfVaporization);
        }
    }

     */

    public int getColor() {
        return color;
    }

    public Chemical setColor(int color) {
        this.color = color;
        return this;
    }

    public Chemical setMolarMass(double molarMass) {
        this.molarMass = molarMass;
        return this;
    }

    public double getMolarMass() {
        return this.molarMass;
    }

    public Chemical setDensity(double density) {
        this.density = density;
        return this;
    }

    public double getDensity() {
        return this.density;
    }

    //Equation obtained by trial and error on Desmos and water's vapor pressure graph
    public double getBPVaporPressure() {
        return Math.pow(10, (boilingPoint - 123) / 50);
    }

    public double calculateVaporPressure(double temperature) {
        return Math.pow(10, (temperature - 123) / 50);
    }

    public Chemical setAcidData(double pKa, int hIons, int ohIons) {
        this.pKa = pKa;
        this.hasPKA = true;
        this.hIons = hIons;
        this.ohIons = ohIons;
        return this;
    }

    public double getPKA() {
        return this.pKa;
    }

    public double getHIons() {
        return this.hIons;
    }

    public double getOHIons() {
        return this.ohIons;
    }

    public boolean hasPKA() {
        return this.hasPKA;
    }

    public double getVolume(double mols) {
        return mols * molarMass / density;
    }
}