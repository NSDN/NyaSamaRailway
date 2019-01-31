package club.nsdn.nyasamaoptics.block;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.api.LightBeam;
import club.nsdn.nyasamaoptics.tileblock.holo.HoloJetRev;
import club.nsdn.nyasamaoptics.tileblock.holo.PillarHead;
import club.nsdn.nyasamaoptics.tileblock.light.*;
import club.nsdn.nyasamaoptics.tileblock.screen.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class BlockLoader {

    private static BlockLoader instance;
    public static BlockLoader instance() {
        if (instance == null) instance = new BlockLoader();
        return instance;
    }

    public static LinkedList<Block> blocks;
    public static LinkedHashMap<Block, Item> itemBlocks;
    public static Block logo;
    public static LightBeam light;
    public static LightBeam lineLight;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        NyaSamaOptics.logger.info("registering Blocks");
        event.getRegistry().registerAll(blocks.toArray(new Block[0]));
    }

    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        NyaSamaOptics.logger.info("registering ItemBlocks");
        for (Block b : blocks) {
            String regName = b.getUnlocalizedName().toLowerCase();
            if (b.getRegistryName() != null)
                regName = b.getRegistryName().toString().split(":")[1];
            itemBlocks.put(b, new ItemBlock(b).setRegistryName(NyaSamaOptics.MODID, regName));
        }
        event.getRegistry().registerAll(itemBlocks.values().toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemBlockModels(ModelRegistryEvent event) {
        NyaSamaOptics.logger.info("registering ItemBlock Models (Block's Icon)");
        for (Item i : itemBlocks.values()) {
            String regName = i.getUnlocalizedName().toLowerCase();
            if (i.getRegistryName() != null)
                regName = i.getRegistryName().toString();
            ModelLoader.setCustomModelResourceLocation(i, 0,
                    new ModelResourceLocation(regName, "inventory")
            );
        }
    }

    public BlockLoader() {
        blocks = new LinkedList<>();
        itemBlocks = new LinkedHashMap<>();

        blocks.add(new BlockSign());
        blocks.add(new BlockNSDNLogo());
        logo = new BlockLogo();
        blocks.add(logo);

        blocks.add(new HoloJetRev());

        blocks.add(new LEDPlate());
        blocks.add(new PlatformPlateFull());
        blocks.add(new PlatformPlateHalf());
        blocks.add(new StationLamp());

        light = new LightBeam(RGBLight.class, LightBeam.TYPE_DOT);
        blocks.add(light);
        lineLight = new LightBeam(RGBLight.class, LightBeam.TYPE_LINE);
        blocks.add(lineLight);

        blocks.add(new PillarHead());
        blocks.add(new RGBLight("PillarBody", "pillar_body", 1.0F, 1.0F, 0.5F));

        blocks.add(new RGBLight("BlockAdsorptionLampLarge", "adsorption_lamp_large", 1.0F, 0.125F, 1.0F));
        blocks.add(new RGBLight("BlockAdsorptionLampMono", "adsorption_lamp_mono", 0.25F, 0.125F, 0.25F));
        blocks.add(new RGBLight("BlockAdsorptionLampMulti", "adsorption_lamp_multi", 1.0F, 0.125F, 0.25F));
        blocks.add(new RGBLight("BlockFluorescentLight", "fluorescent_light", 1.0F, 0.1875F,0.25F));
        blocks.add(new RGBLight("BlockFluorescentLightFlock", "fluorescent_light_flock", 1.0F, 0.25F, 0.625F));
        blocks.add(new RGBLight("BlockMosaicLightMono", "mosaic_light_mono", 1.0F, 0.5F, 1.0F, true));
        blocks.add(new RGBLight("BlockMosaicLightMonoSmall", "mosaic_light_mono_small", 0.625F, 0.25F, 0.625F, true));
        blocks.add(new RGBLight("BlockMosaicLightMulti", "mosaic_light_multi", 1.0F, 0.5F, 1.0F));
        blocks.add(new RGBLight("BlockMosaicLightMultiSmall", "mosaic_light_multi_small", 1.0F, 0.25F, 0.625F));
        blocks.add(new RGBLight("BlockPlatformLightFull", "platform_light_full", 1.0F, 1.0F, 0.625F));
        blocks.add(new RGBLight("BlockPlatformLightHalf", "platform_light_half", 1.0F, 0.5F, 0.5F));
        blocks.add(new RGBLight("BlockCuballLamp", "cuball_lamp", 1.0F, 1.0F, 1.0F));

    }

}