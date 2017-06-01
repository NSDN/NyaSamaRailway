package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockLoader {

    public static LinkedHashMap<String, Block> blocks;

    private static void register(Block block, String name) {
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(((IBlockBase) block).getRegisterID()));
    }

    private static void addBlock(Block block) {
        if (block instanceof IBlockBase) {
            blocks.put(((IBlockBase) block).getRegisterID(), block);
        } else {
            blocks.put(block.getUnlocalizedName(), block);
        }
    }

    public BlockLoader(FMLPreInitializationEvent event) {
        blocks = new LinkedHashMap<>();

        addBlock(new BlockSign());
        addBlock(new BlockNSDNLogo());
        addBlock(new BlockNyaSamaRailwayLogo());

        for (String id : blocks.keySet()) register(blocks.get(id), id);

    }

}