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

    private double leakageSpeed = 0.01;

    @Override
    public void onLoad() {
        reactorVolume = reactorWidth * reactorHeight * reactorLength / 1000000;

        containedGas = new GaseousMixture(this, Chemicals.Oxygen, 21);
        containedGas.addChemical(Chemicals.Nitrogen, 78);

        contents.clear();
        contents.add(new Mixture(this, Chemicals.Water, 10000));
        contents.add(new Mixture(this, Chemicals.SulfuricAcid, 2500));
        contents.add(new Mixture(this, Chemicals.SodiumHydroxide, 2500));
    }

    @Override
    public void update() {
        //Simulate interactions between mixtures

        for (int i = 0; i < contents.size() - 1; i++) {
            Mixture currentMix = contents.get(i);
            Mixture aboveMix = contents.get(i + 1);

            //Solubility stuff, will be removed since solids can mix with liquids regardless of solubility

            //for (Map.Entry<Chemical, Double> currentEntry : currentMix.getComponents().entrySet()) {
                for (Map.Entry<Chemical, Double> aboveEntry : aboveMix.getComponents().entrySet()) {
                    //if (aboveEntry.getKey().getSolubility(currentEntry.getKey(), temperature) > 0) {
                        currentMix.moveChemical(aboveMix, aboveEntry.getKey(), Math.min(0.2, aboveEntry.getValue()));
                    //}
                }
            //}

        }

        double fluidVolumes = 0;
        for (int h = 0; h < contents.size(); h++) {
            contents.get(h).update();
            fluidVolumes += contents.get(h).getTotalVolume();
        }
        occupiedVolume = fluidVolumes;

        containedGas.update();

        //TODO: Sealed chemical reactors will not leak
        simulateLeakage();
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
    public void simulateLeakage() {

        //Balance with atmospheric gas ratios, get rid of stuff that isnt normally present in the atmosphere
        for (Map.Entry<Chemical, Double> entry : containedGas.getComponents().entrySet()) {
            double idealMols = 0;
            for (Map.Entry<Chemical, Double> entry2 : ChemistryConstants.EARTH_ATMOSPHERE_COMPOSITION.entrySet()) {
                IgblonChemistry.logger.warn(entry.getKey());
                IgblonChemistry.logger.warn(entry.getKey().getName());
                if (entry.getKey().compareTo(entry2.getKey()) == 0) {
                    idealMols = ((reactorVolume - occupiedVolume) * ChemistryConstants.ATMOSPHERIC_PRESSURE * entry2.getValue()) / (ChemistryConstants.GAS_CONSTANT * containedGas.getTemperature());
                    break;
                }
            }

            double molDifference = idealMols - entry.getValue();

            if (molDifference > 0) {
                containedGas.addChemical(entry.getKey(), Math.max(1, molDifference * leakageSpeed));
            }
            if (molDifference < 0) {
                containedGas.removeChemical(entry.getKey(), Math.max(1, molDifference * leakageSpeed));
            }
        }
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
