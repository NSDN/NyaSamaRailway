package club.nsdn.nyasamarailway.Renderers.TileEntity;

import club.nsdn.nyasamarailway.Blocks.BlockEdge;
import club.nsdn.nyasamarailway.Renderers.RendererHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.WavefrontObject;

/**
 * Created by drzzm on 2016.11.28.
 */
public class EdgeRenderer implements ISimpleBlockRenderingHandler {
    private int blockMetadata;
    private int attachedBlockMetadata;
    private int xOffset;
    private int yOffset;
    private int zOffset;
    private float rotation;
    private Block attachedBlock;
    private IIcon topIcon;
    private IIcon sidesIcon;
    private static final WavefrontObject topModel = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/platform_edge_top.obj"));
    private static final WavefrontObject sidesModel = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/platform_edge_sides.obj"));
    private static final WavefrontObject tallSidesModel = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/platform_edge_sides_tall.obj"));

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        this.blockMetadata = world.getBlockMetadata(x, y, z);
        if ((this.blockMetadata & 0x3) == 2) {
            this.xOffset = -1;
        } else if ((this.blockMetadata & 0x3) == 0) {
            this.xOffset = 1;
        } else {
            this.xOffset = 0;
        }
        if ((this.blockMetadata & 0x3) == 3) {
            this.zOffset = -1;
        } else if ((this.blockMetadata & 0x3) == 1) {
            this.zOffset = 1;
        } else {
            this.zOffset = 0;
        }
        if (Minecraft.getMinecraft().theWorld.getBlock(x, y - 1, z).equals(block)) {
            this.yOffset = -1;
        } else {
            this.yOffset = 0;
        }
        this.attachedBlock = world.getBlock(x + this.xOffset, y + this.yOffset, z + this.zOffset);
        this.attachedBlockMetadata = world.getBlockMetadata(x + this.xOffset, y + this.yOffset, z + this.zOffset);
        if (this.attachedBlock.equals(Blocks.air) || this.attachedBlock.getIcon(2, this.attachedBlockMetadata) == null) {
            this.attachedBlock = block;
        }
        this.sidesIcon = this.attachedBlock.getIcon(2, this.attachedBlockMetadata);
        this.topIcon = this.sidesIcon;
        Tessellator.instance.addTranslation(x + 0.5F, y - (this.blockMetadata & 0x4) / 8.0F, z + 0.5F);
        Tessellator.instance.setColorOpaque(255, 255, 255);
        Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        RendererHelper.renderWithIconAndRotation(topModel, (this.blockMetadata & 0x3) * 90.0F, this.topIcon, Tessellator.instance);
        if ((this.blockMetadata & 0xC) == 0) {
            RendererHelper.renderWithIconAndRotation(tallSidesModel, (this.blockMetadata & 0x3) * 90.0F, this.sidesIcon, Tessellator.instance);
        } else {
            RendererHelper.renderWithIconAndRotation(sidesModel, (this.blockMetadata & 0x3) * 90.0F, this.sidesIcon, Tessellator.instance);
        }
        Tessellator.instance.addTranslation(-x - 0.5F, -y + (this.blockMetadata & 0x4) / 8.0F, -z - 0.5F);
        return true;
    }

    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    public boolean shouldRender3DInInventory(int modelId)
    {
        return false;
    }

    public int getRenderId()
    {
        return BlockEdge.renderType;
    }
}
