package igblonchemistry.chemistry;

import igblonchemistry.common.ChemistryConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class Compound implements Comparable<Compound> {
    //SOLUBILITIES TAKEN FROM: https://en.wikipedia.org/wiki/Solubility_table

    private String name;
    private double boilingPoint;
    private double meltingPoint;
    private int color;

    //measured in grams/liter
    private double density;

    //measured in grams
    private double molarMass;

    private double pH;

    //measured in Joules/mol
    private double heatOfVaporization = -1;

    private HashMap<Compound, SolubilityInfo> solubilityInfos = new HashMap<Compound, SolubilityInfo>();

    public Compound(String name) {
        this.name = name;
    }

    public Compound addSolubilityInfo(Compound compound, SolubilityInfo solubilityInfo) {
        solubilityInfos.put(compound, solubilityInfo);
        return this;
    }

    public double getSolubility(Compound solvent, double temperature) {
        SolubilityInfo solubilityInfo = solubilityInfos.get(solvent);

        if (solubilityInfo != null) {
            return solubilityInfo.calculateSolubility(temperature);
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Compound compound) {
        return getName().compareTo(compound.getName());
    }

    public String getName() {
        return this.name;
    }

    //Taken at 1 atm (~101,000 Pascals)
    public Compound setMeltingPoint(double meltingPoint) {
        this.meltingPoint = meltingPoint;
        return this;
    }

    //Pressure measured in Pascals
    //Temperature measured in Kelvins
    public double getMeltingPoint() {
        return this.meltingPoint;
    }

    //Taken at 1 atm (~101,000 Pascals)
    public Compound setBoilingPoint(double boilingPoint) {
        this.boilingPoint = boilingPoint;
        return this;
    }

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

    public int getColor() {
        return color;
    }

    public Compound setColor(int color) {
        this.color = color;
        return this;
    }

    public Compound setHeatOfVaporization(double heatOfVaporization) {
        this.heatOfVaporization = heatOfVaporization;
        return this;
    }

    public double getHeatOfVaporization() {
        return heatOfVaporization;
    }

    public Compound setMolarMass(double molarMass) {
        this.molarMass = molarMass;
        return this;
    }

    public double getMolarMass() {
        return this.molarMass;
    }

    public Compound setDensity(double density) {
        this.density = density;
        return this;
    }

    public double getDensity() {
        return this.density;
    }

    public Compound setPH(double pH) {
        this.pH = pH;
        return this;
    }

    public double getPH() {
        return this.pH;
    }
}