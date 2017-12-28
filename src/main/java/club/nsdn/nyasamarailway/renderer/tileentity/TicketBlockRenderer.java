package club.nsdn.nyasamarailway.renderer.tileentity;

import club.nsdn.nyasamarailway.renderer.RendererHelper;
import club.nsdn.nyasamarailway.tileblock.functional.TileEntityCoinBlock;
import club.nsdn.nyasamarailway.tileblock.functional.TileEntityTicketBlockCard;
import club.nsdn.nyasamarailway.tileblock.functional.TileEntityTicketBlockOnce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TicketBlockRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelOnceBase;
    private final WavefrontObject modelCardBase;

    private final WavefrontObject modelCoinBase;

    private static final int SCREEN_INSERT = 0;
    private static final int SCREEN_ONCE = 1;
    private static final int SCREEN_CARD = 2;
    private static final int SCREEN_OVER = 3;
    private static final int SCREEN_1 = 4;
    private static final int SCREEN_2 = 5;
    private static final int SCREEN_3 = 6;
    private static final int SCREEN_4 = 7;
    private final WavefrontObject modelScreen[];

    private final ResourceLocation textureMain;

    private static final int TEXT_E = 10;
    private static final int TEXT_R = 11;
    private static final int TEXT_SUB = 12;
    private static final int TEXT_N = 13;
    private static final int TEXT_U = 14;
    private static final int TEXT_L = 15;
    private final ResourceLocation textureText[];

    public static final int TICKET_ONCE = 0;
    public static final int TICKET_CARD = 1;
    public static final int COIN = 2;
    private final int renderType;

    public TicketBlockRenderer(int renderType) {
        modelOnceBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_once_base.obj")
        );
        modelCardBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_card_base.obj")
        );

        modelCoinBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/coin_block.obj")
        );

        modelScreen = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_insert.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_once.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_card.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_over.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_over1.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_over2.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_over3.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/ticket/ticket_block_screen_over4.obj")
                )
        };

        textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/ticket_block_main.png");

        textureText = new ResourceLocation[16];
        for (int i = 0; i < 10; i++)
            textureText[i] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_" + i + ".png");
        textureText[TEXT_E] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_e.png");
        textureText[TEXT_R] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_r.png");
        textureText[TEXT_SUB] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_sub.png");
        textureText[TEXT_N] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_n.png");
        textureText[TEXT_U] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_u.png");
        textureText[TEXT_L] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_l.png");

        this.renderType = renderType;
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        RenderHelper.disableStandardItemLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
        int angle = (meta & 0x3) * 90;

        switch (renderType)
        {
            case TICKET_ONCE:
                RendererHelper.renderWithResourceAndRotation(modelOnceBase, angle, textureMain);
                if (te instanceof TileEntityTicketBlockOnce.TicketBlock) {
                    TileEntityTicketBlockOnce.TicketBlock ticketBlock = (TileEntityTicketBlockOnce.TicketBlock) te;
                    int over = ticketBlock.setOver;

                    if ((meta & 0x4) == 0) {
                        if (over > 1) {
                            int s2 = (over % 100) / 10, s3 = over % 10;
                            RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_OVER], angle, textureMain);
                            RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[s2]);
                            RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[s3]);
                        } else {
                            RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_INSERT], angle, textureMain);
                        }
                    } else {
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_ONCE], angle, textureMain);
                    }
                }
                break;
            case TICKET_CARD:
                RendererHelper.renderWithResourceAndRotation(modelCardBase, angle, textureMain);
                if ((meta & 0x8) != 0) {
                    if (te instanceof TileEntityTicketBlockCard.TicketBlock) {
                        TileEntityTicketBlockCard.TicketBlock ticketBlock = (TileEntityTicketBlockCard.TicketBlock) te;
                        int over = ticketBlock.over;
                        int s1 = over / 1000, s2 = (over % 1000) / 100, s3 = (over % 100) / 10, s4 = over % 10;

                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_OVER], angle, textureMain);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_1], angle, textureText[s1]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[s2]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[s3]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_4], angle, textureText[s4]);
                    }
                } else if ((meta & 0x4) != 0)
                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_CARD], angle, textureMain);
                else
                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_INSERT], angle, textureMain);
                break;
            case COIN:
                if (te instanceof TileEntityCoinBlock.CoinBlock) {
                    TileEntityCoinBlock.CoinBlock coinBlock = (TileEntityCoinBlock.CoinBlock) te;
                    int value = coinBlock.value;
                    int s2 = (value % 100) / 10, s3 = value % 10;

                    RendererHelper.renderWithResourceAndRotation(modelCoinBase, angle, textureMain);
                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[s2]);
                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[s3]);
                }
                break;
            default:
                break;
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
