package igblonchemistry.common.blocks;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.chemistry.Chemical;
import igblonchemistry.chemistry.Chemicals;
import igblonchemistry.chemistry.GaseousMixture;
import igblonchemistry.chemistry.Mixture;
import igblonchemistry.common.ChemistryConstants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class TileChemicalReactor extends TileEntity implements ITickable {

    private double temperature = 293;

    public ArrayList<Mixture> contents = new ArrayList<Mixture>();
    private GaseousMixture containedGas;

    //measured in centimeters
    private double reactorWidth = 1000;
    private double reactorHeight = 1000;
    private double reactorLength = 1000;

    //measured in liters
    private double reactorVolume;
    private double occupiedVolume;

    private double pressureLeakageSpeed = 0.01;
    private double heatLeakageSpeed = 0.001;

    @Override
    public void onLoad() {
        reactorVolume = reactorWidth * reactorHeight * reactorLength / 1000000;

        containedGas = new GaseousMixture(this, Chemicals.Oxygen, 1000, 293);
        containedGas.addChemical(Chemicals.Nitrogen, 2000, 293);

        contents.clear();

        /*
        Mixture test = new Mixture(this, Chemicals.SulfuricAcid, 1000, 293);
        test.addChemical(Chemicals.SodiumHydroxide, 2000, 293);

        contents.add(test);

         */

        contents.add(new Mixture(this, Chemicals.SulfuricAcid, 2500, 293));
        contents.add(new Mixture(this, Chemicals.SodiumHydroxide, 2500, 293));
    }

    private double CalculateOccupiedVolume() {
        double total = 0;

        for (Mixture mixture : contents) {
            total += mixture.getTotalVolume();
        }

        return total;
    }

    @Override
    public void update() {
        //Simulate interactions between mixtures
        ArrayList<Mixture> emptyMixtures = new ArrayList<Mixture>();

        Random r = new Random();
        double simulationSpeed = 10;

        for (int h = 0; h < contents.size(); h++) {
            //Combine mixtures if they are both in the same state and the dominant chemical is the same
            if (h < contents.size() - 1) {
                if (contents.get(h).getIsMostlySolid() == contents.get(h + 1).getIsMostlySolid()) {
                    if (contents.get(h).getDominantChemical().compareTo(contents.get(h + 1).getDominantChemical()) == 0) {
                        contents.get(h).mergeMixture(contents.get(h + 1));
                        emptyMixtures.add(contents.get(h));
                        continue;
                    }
                }
            }

            //Skip empty mixtures and keep track of them to be removed later
            if (contents.get(h).isEmpty()) {
                emptyMixtures.add(contents.get(h));
                continue;
            }

            contents.get(h).cleanComponentsList();
            contents.get(h).calculateColorAverage();
            contents.get(h).updateVariables();

            //Move mixtures based on densities

            //TODO: Movement is cancelled if ONE of the mixtures is solid AND not porous (will happen if the mixture has experienced a transition from liquid to solid)
            if (h > 0) {
                if (!(contents.get(h).getIsMostlySolid() && contents.get(h - 1).getIsMostlySolid())) {
                    if (contents.get(h).getAverageDensity() > contents.get(h - 1).getAverageDensity()) {
                        contents.get(h - 1).moveMixture(contents.get(h), simulationSpeed + r.nextDouble() * simulationSpeed);
                    } else {
                        contents.get(h - 1).moveMixture(contents.get(h), (simulationSpeed + r.nextDouble() * simulationSpeed) / 10);
                    }
                }
            }

            if (h < contents.size() - 1) {
                if (!(contents.get(h).getIsMostlySolid() && contents.get(h + 1).getIsMostlySolid())) {
                    if (contents.get(h).getAverageDensity() < contents.get(h + 1).getAverageDensity()) {
                        contents.get(h + 1).moveMixture(contents.get(h), simulationSpeed + r.nextDouble() * simulationSpeed);
                    } else {
                        contents.get(h + 1).moveMixture(contents.get(h), (simulationSpeed + r.nextDouble() * simulationSpeed) / 10);
                    }
                }
            }

            contents.get(h).runPossibleReactions();

            if (h > 0 && h < contents.size() - 1) {
                contents.get(h).doSeparations(contents.get(h - 1), contents.get(h + 1), simulationSpeed);
            } else if (h > 0) {
                contents.get(h).doSeparations(contents.get(h - 1), null, simulationSpeed);
            } else if (h < contents.size() - 1) {
                contents.get(h).doSeparations(null, contents.get(h + 1), simulationSpeed);
            } else {
                contents.get(h).doSeparations(null, null, simulationSpeed);
            }
        }

        //Simulate evaporation

        /*
        if (contents.size() > 0) {
            Mixture topMixture = contents.get(contents.size() - 1);
            for (Map.Entry<Chemical, Double> entrySet : topMixture.getComponents().entrySet()) {
                //Solid components cannot evaporate
                if (topMixture.getTemperature() < entrySet.getKey().getMeltingPoint()) {
                    continue;
                }

                double boilingSpeed = Math.pow(10, (entrySet.getKey().calculateVaporPressure(topMixture.getTemperature()) - containedGas.getPressure()) / 100000 + 1);

                if (boilingSpeed > 0.1) {
                    boilingSpeed = Math.min(boilingSpeed, 10);

                    containedGas.moveChemical(topMixture, entrySet.getKey(), boilingSpeed + r.nextDouble());
                }
            }
        }

         */

        occupiedVolume = CalculateOccupiedVolume();

        containedGas.cleanComponentsList();
        containedGas.calculateColorAverage();
        containedGas.updateVariables();

        containedGas.runPossibleReactions();

        //TODO: Sealed chemical reactors will not leak
        simulatePressureLeakage();
        simulateThermalLeakage();

        /*
        if (contents.size() > 0) {
            containedGas.doGaseousSeparations(contents.get(contents.size() - 1), simulationSpeed);
        } else {
        }

         */

        for (Mixture emptyMixture : emptyMixtures) {
            contents.remove(emptyMixture);
        }
    }

    private ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            TileChemicalReactor.this.markDirty();
        }
    };

    private ItemStackHandler outputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            TileChemicalReactor.this.markDirty();
        }
    };

    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler, outputHandler);

    //Balance the gases in the chemical reactor with the gases in the atmosphere, this will include things flowing in and out
    public void simulatePressureLeakage() {
        //Balance with atmospheric gas ratios, get rid of stuff that isnt normally present in the atmosphere
        for (Map.Entry<Chemical, Double> entry2 : ChemistryConstants.EARTH_ATMOSPHERE_COMPOSITION.entrySet()) {
            if (containedGas.findChemical(entry2.getKey()) == 0) {
                containedGas.addChemical(entry2.getKey(), 1, ChemistryConstants.ROOM_TEMPERATURE);
            }
        }

        for (Map.Entry<Chemical, Double> entry : containedGas.getComponents().entrySet()) {
            double idealMols = 0;
            for (Map.Entry<Chemical, Double> entry2 : ChemistryConstants.EARTH_ATMOSPHERE_COMPOSITION.entrySet()) {
                if (entry.getKey().compareTo(entry2.getKey()) == 0) {
                    idealMols = ((reactorVolume - occupiedVolume) * ChemistryConstants.ATMOSPHERIC_PRESSURE * entry2.getValue()) / (ChemistryConstants.GAS_CONSTANT * containedGas.getTemperature());
                    break;
                }
            }

            double molDifference = idealMols - entry.getValue();

            if (molDifference > 0) {
                containedGas.addChemical(entry.getKey(), Math.max(0.01, molDifference * pressureLeakageSpeed), ChemistryConstants.ROOM_TEMPERATURE);
            }
            if (molDifference < 0) {
                containedGas.removeChemical(entry.getKey(), Math.max(0.01, -molDifference * pressureLeakageSpeed));
            }
        }
    }

    //Balance temperatures of reactor contents with outside world
    public void simulateThermalLeakage() {
        for (Mixture mixture : contents) {
            double jouleDifference = mixture.getTotalMols() * mixture.getAverageHeatCapacity() * (ChemistryConstants.ROOM_TEMPERATURE - mixture.getTemperature());
            mixture.addJoules(jouleDifference * heatLeakageSpeed);
        }
        double jouleDifference = containedGas.getTotalMols() * containedGas.getAverageHeatCapacity() * (ChemistryConstants.ROOM_TEMPERATURE - containedGas.getTemperature());
        containedGas.addJoules(jouleDifference * heatLeakageSpeed);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("itemsIn")) {
            inputHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        }
        if (compound.hasKey("itemsOut")) {
            outputHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("itemsIn", inputHandler.serializeNBT());
        compound.setTag("itemsOut", outputHandler.serializeNBT());
        return compound;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(combinedHandler);
            } else if (facing == EnumFacing.UP) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(outputHandler);
            }
        }
        return super.getCapability(capability, facing);
    }

    public ArrayList<Mixture> getContents() {

        return contents;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getOccupiedVolume() {
        return occupiedVolume;
    }

    public double getReactorVolume() {
        return reactorVolume;
    }

    public GaseousMixture getContainedGas() {
        return containedGas;
    }
}
