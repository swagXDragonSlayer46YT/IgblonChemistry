package igblonchemistry.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerChemicalReactor extends Container {

    private TileChemicalReactor tileChemicalReactor;

    public ContainerChemicalReactor(IInventory playerInventory, TileChemicalReactor tileChemicalReactor) {
        this.tileChemicalReactor = tileChemicalReactor;

        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    private void addPlayerSlots(IInventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + 67;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 58 + 67;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    private void addOwnSlots() {
        IItemHandler itemHandler = this.tileChemicalReactor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 26, 13));

        //IFluidHandler fluidHandler = this.tileChemicalReactor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

        //addSlotToContainer(new Slot (itemHandler, 1, 26, 38));
    }

    //I'm assuming that this function moves items to occupy the leftmost slots of the container, but this will only have 1 item slot anyways
    /*
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index < TileChemicalReactor.SIZE) {
                if (!this.mergeItemStack(itemStack1, tileChemicalReactor.SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, TileChemicalReactor.SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemStack;
    }

     */

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileChemicalReactor.canInteractWith(playerIn);
    }
}
