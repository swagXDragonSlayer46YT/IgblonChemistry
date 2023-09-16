package igblonchemistry.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class Machines {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    @GameRegistry.ObjectHolder("igblonchemistry:chemical_reactor")
    public static ChemicalReactor chemicalReactor;

    public static void init() {

    }
}
