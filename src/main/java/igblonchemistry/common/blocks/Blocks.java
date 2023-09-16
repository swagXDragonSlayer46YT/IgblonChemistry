package igblonchemistry.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class Blocks {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    @GameRegistry.ObjectHolder("igblonchemistry:chemical_reactor")
    public static ChemicalReactor chemicalReactor;

    public static void init() {

    }

    //TODO: MAKE COMMON CLASS FOR EVERY CHEMICAL REACTOR, HAVE EACH MACHINE EXTEND OFF OF IT

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        chemicalReactor.initModel();
    }
}
