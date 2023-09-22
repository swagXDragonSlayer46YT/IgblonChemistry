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

public class TileChemicalReactor extends TileEntity implements ITickable {

    private double temperature = 293;

    private ArrayList<Mixture> contents = new ArrayList<Mixture>();
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
        contents.add(new Mixture(this, Chemicals.SulfuricAcid, 2500, 293));
        contents.add(new Mixture(this, Chemicals.SodiumHydroxide, 2500, 293));
        contents.add(new Mixture(this, Chemicals.Salt, 2500, 293));
        contents.add(new Mixture(this, Chemicals.SodiumHydroxide, 2500, 293));
        contents.add(new Mixture(this, Chemicals.SulfuricAcid, 2500, 293));
        contents.add(new Mixture(this, Chemicals.Water, 2500, 293));

         */
    }

    @Override
    public void update() {
        //Simulate interactions between mixtures
        ArrayList<Mixture> emptyMixtures = new ArrayList<Mixture>();

        double fluidVolumes = 0;
        for (int h = 0; h < contents.size(); h++) {
            contents.get(h).update();
            fluidVolumes += contents.get(h).getTotalVolume();
            if (contents.get(h).isEmpty()) {
                emptyMixtures.add(contents.get(h));
            }
        }

        for (int i = 0; i < contents.size() - 1; i++) {
            Mixture currentMix = contents.get(i);
            Mixture aboveMix = contents.get(i + 1);

            //Consume a higher % of the above mixture as it gets smaller
            //TODO: FOLLOWING INTERACTIONS:
            /*
                LIQUID-LIQUID: IF LIQUIDS ARE IMMISCIBLE, SORT BY DENSITY, OTHERWISE MIX
                LIQUID-SOLID: IF SOLID IS INSOLUBLE SORT BY DENSITY, IF SOLID IS SOLUBLE, DISSOLVE UNTIL LIMIT AND THEN SORT BY DENSITY
                LIQUID-GAS: TOP FLUID WILL ABSORB SOLUBLE GASES FROM THE CONTAINED GASES
                SOLID-SOLID: NO INTERACTION UNLESS REACTOR IS MIXING
             */
            currentMix.moveMixture(aboveMix, Math.min(1.0, 1.0 / (aboveMix.getTotalVolume() + 1.0)));

            //TODO: DETERMINE WHETHER THE BOTTOM OR TOP MIXTURE IS HOTTER, AND MOVE JOULES IN THAT DIRECTION
        }

        occupiedVolume = fluidVolumes;

        containedGas.update();

        //TODO: Sealed chemical reactors will not leak
        simulatePressureLeakage();
        simulateThermalLeakage();

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
