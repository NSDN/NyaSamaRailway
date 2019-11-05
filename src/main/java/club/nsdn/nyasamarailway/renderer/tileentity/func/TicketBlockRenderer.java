package club.nsdn.nyasamarailway.renderer.tileentity.func;

import club.nsdn.nyasamarailway.tileblock.func.CoinBlock;
import club.nsdn.nyasamarailway.tileblock.func.TicketBlockCard;
import club.nsdn.nyasamarailway.tileblock.func.TicketBlockOnce;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TicketBlockRenderer extends AbsTileEntitySpecialRenderer {

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

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        int angle = (meta & 0x3) * 90;

        switch (renderType)
        {
            case TICKET_ONCE:
                RendererHelper.renderWithResourceAndRotation(modelOnceBase, angle, textureMain);
                if (te instanceof TicketBlockOnce.TileEntityTicketBlockOnce) {
                    TicketBlockOnce.TileEntityTicketBlockOnce ticketBlock = (TicketBlockOnce.TileEntityTicketBlockOnce) te;
                    int over = ticketBlock.setOver;

                    RendererHelper.beginSpecialLighting();

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
                    if (te instanceof TicketBlockCard.TileEntityTicketBlockCard) {
                        TicketBlockCard.TileEntityTicketBlockCard ticketBlock = (TicketBlockCard.TileEntityTicketBlockCard) te;
                        int over = ticketBlock.over;
                        int s1 = over / 1000, s2 = (over % 1000) / 100, s3 = (over % 100) / 10, s4 = over % 10;

                        RendererHelper.beginSpecialLighting();

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
                RendererHelper.renderWithResourceAndRotation(modelCoinBase, angle, textureMain);
                if (te instanceof CoinBlock.TileEntityCoinBlock) {
                    CoinBlock.TileEntityCoinBlock coinBlock = (CoinBlock.TileEntityCoinBlock) te;
                    int value = coinBlock.value;
                    int s2 = (value % 100) / 10, s3 = value % 10;

                    RendererHelper.beginSpecialLighting();

                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[s2]);
                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[s3]);
                }
                break;
            default:
                break;
        }

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
