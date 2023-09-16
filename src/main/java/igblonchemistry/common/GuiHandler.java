package igblonchemistry.common;

import igblonchemistry.common.blocks.ContainerChemicalReactor;
import igblonchemistry.common.blocks.GuiChemicalReactor;
import igblonchemistry.common.blocks.TileChemicalReactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileChemicalReactor) {
            return new ContainerChemicalReactor(player.inventory, (TileChemicalReactor) tileEntity);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileChemicalReactor) {
            TileChemicalReactor containerTileEntity = (TileChemicalReactor) tileEntity;
            return new GuiChemicalReactor(containerTileEntity, new ContainerChemicalReactor(player.inventory, containerTileEntity));
        }
        return null;
    }
}
