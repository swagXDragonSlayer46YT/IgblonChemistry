package igblonchemistry.common.blocks;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.chemistry.Chemical;
import igblonchemistry.chemistry.Chemicals;
import igblonchemistry.chemistry.GaseousMixture;
import igblonchemistry.chemistry.Mixture;
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

    @Override
    public void onLoad() {
        contents.clear();
        //contents.add(new Mixture(this, Chemicals.Water, 10000));
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

        for (int h = 0; h < contents.size(); h++) {
            contents.get(h).update();
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
}
