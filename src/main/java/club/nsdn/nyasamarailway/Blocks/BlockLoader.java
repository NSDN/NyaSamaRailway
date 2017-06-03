package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockLoader {

    public static LinkedHashMap<String, Block> blocks;

    private static void register(Block block) {
        GameRegistry.register(block);
        Item item = new ItemBlock(block).setRegistryName(block.getRegistryName());
        GameRegistry.register(item);
        GameData.getBlockItemMap().put(block, item);
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

        for (String id : blocks.keySet()) register(blocks.get(id));
    }

    @SideOnly(Side.CLIENT)
    public static void preLoadModels() {
        for (String id : blocks.keySet()) {
            Item item = Item.getItemFromBlock(blocks.get(id));
            if (item == null) item = new ItemBlock(blocks.get(id));
            ModelBakery.registerItemVariants(item, new ResourceLocation(NyaSamaRailway.modid, id));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void loadModels() {
        for (String id : blocks.keySet()) {
            Item item = Item.getItemFromBlock(blocks.get(id));
            if (item == null) item = new ItemBlock(blocks.get(id));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                    item, 0,
                    new ModelResourceLocation(NyaSamaRailway.modid + ":" + id, null)
            );
        }
    }

}