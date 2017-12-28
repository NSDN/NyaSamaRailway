package club.nsdn.nyasamatelecom.api.tool.util;

import club.nsdn.nyasamatelecom.api.network.NGTPacket;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class GuiNGTablet extends GuiScreen {

    private final ResourceLocation ngtGuiTextures;

    private SimpleNetworkWrapper wrapper;
    private final ItemStack ngtObj;

    private boolean isNotEmpty;
    private int updateCount;
    private int ngtImageWidth = 192;
    private int ngtImageHeight = 192;
    private int ngtTotalPages = 1;
    private int currPage;
    private NBTTagList ngtPages;
    private NextPageButton buttonNextPage;
    private NextPageButton buttonPreviousPage;
    private GuiButton buttonBack;

    public GuiNGTablet(String modid, String texture, SimpleNetworkWrapper wrapper, ItemStack stack) {
        ngtGuiTextures = new ResourceLocation(modid.toLowerCase(),texture);
        this.wrapper = wrapper;
        this.ngtObj = stack;

        if (stack.hasTagCompound()) {
            NBTTagCompound var4 = stack.getTagCompound();
            this.ngtPages = var4.getTagList("pages", 8);
            if (this.ngtPages != null) {
                this.ngtPages = (NBTTagList)this.ngtPages.copy();
                this.ngtTotalPages = this.ngtPages.tagCount();
                if (this.ngtTotalPages < 1) {
                    this.ngtTotalPages = 1;
                }
            }
        }

        if (this.ngtPages == null) {
            this.ngtPages = new NBTTagList();
            this.ngtPages.appendTag(new NBTTagString(""));
            this.ngtTotalPages = 1;
        }

    }

    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(this.buttonBack = new GuiButton(0, this.width / 2 + 2, 4 + this.ngtImageHeight, 98, 20, I18n.format("gui.back", new Object[0])));

        int left = (this.width - this.ngtImageWidth) / 2;
        byte offset = 2;
        this.buttonList.add(this.buttonNextPage = new NextPageButton(1, left + 120, offset + 160, true));
        this.buttonList.add(this.buttonPreviousPage = new NextPageButton(2, left + 38, offset + 160, false));
        this.updateButtons();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons() {
        this.buttonNextPage.visible = true;
        this.buttonPreviousPage.visible =  this.currPage > 0;
        this.buttonBack.visible = true;
    }

    private void sendNgtToServer() {
        if (this.isNotEmpty) {
            if (this.ngtPages != null) {
                String endContent;
                while(this.ngtPages.tagCount() > 1) {
                    endContent = this.ngtPages.getStringTagAt(this.ngtPages.tagCount() - 1);
                    if (endContent.length() != 0) {
                        break;
                    }

                    this.ngtPages.removeTag(this.ngtPages.tagCount() - 1);
                }

                if (this.ngtObj.hasTagCompound()) {
                    NBTTagCompound tagCompound = this.ngtObj.getTagCompound();
                    tagCompound.setTag("pages", this.ngtPages);
                } else {
                    this.ngtObj.setTagInfo("pages", this.ngtPages);
                }

                wrapper.sendToServer(new NGTPacket(ngtObj));
            }

        }
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.sendNgtToServer();
            } else if (button.id == 1) {
                if (this.currPage < this.ngtTotalPages - 1) {
                    ++this.currPage;
                } else {
                    this.addNewPage();
                    if (this.currPage < this.ngtTotalPages - 1) {
                        ++this.currPage;
                    }
                }
            } else if (button.id == 2) {
                if (this.currPage > 0) {
                    --this.currPage;
                }
            }

            this.updateButtons();
        }
    }

    private void addNewPage() {
        if (this.ngtPages != null && this.ngtPages.tagCount() < 50) {
            this.ngtPages.appendTag(new NBTTagString(""));
            ++this.ngtTotalPages;
            this.isNotEmpty = true;
        }
    }

    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) {
        super.keyTyped(p_keyTyped_1_, p_keyTyped_2_);
        this.keyTypedInngt(p_keyTyped_1_, p_keyTyped_2_);
    }

    private void keyTypedInngt(char p_keyTypedInngt_1_, int p_keyTypedInngt_2_) {
        switch(p_keyTypedInngt_1_) {
            case '\u0016':
                this.func_146459_b(GuiScreen.getClipboardString());
                return;
            default:
                switch(p_keyTypedInngt_2_) {
                    case 14:
                        String var3 = this.func_146456_p();
                        if (var3.length() > 0) {
                            this.func_146457_a(var3.substring(0, var3.length() - 1));
                        }

                        return;
                    case 28:
                    case 156:
                        this.func_146459_b("\n");
                        return;
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(p_keyTypedInngt_1_)) {
                            this.func_146459_b(Character.toString(p_keyTypedInngt_1_));
                        }
                }
        }
    }

    private String func_146456_p() {
        return this.ngtPages != null && this.currPage >= 0 && this.currPage < this.ngtPages.tagCount() ? this.ngtPages.getStringTagAt(this.currPage) : "";
    }

    private void func_146457_a(String p_146457_1_) {
        if (this.ngtPages != null && this.currPage >= 0 && this.currPage < this.ngtPages.tagCount()) {
            this.ngtPages.func_150304_a(this.currPage, new NBTTagString(p_146457_1_));
            this.isNotEmpty = true;
        }

    }

    private void func_146459_b(String p_146459_1_) {
        String var2 = this.func_146456_p();
        String var3 = var2 + p_146459_1_;
        int var4 = this.fontRendererObj.splitStringWidth(var3 + "" + EnumChatFormatting.BLACK + "_", 118);
        if (var4 <= 118 && var3.length() < 256) {
            this.func_146457_a(var3);
        }

    }

    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ngtGuiTextures);
        int left = (this.width - this.ngtImageWidth) / 2;
        byte offset = 2;
        this.drawTexturedModalRect(left, offset, 0, 0, this.ngtImageWidth, this.ngtImageHeight);
        String var6;
        String var7;
        int var8;

        var6 = I18n.format("book.pageIndicator", new Object[]{this.currPage + 1, this.ngtTotalPages});
        var7 = "";
        if (this.ngtPages != null && this.currPage >= 0 && this.currPage < this.ngtPages.tagCount()) {
            var7 = this.ngtPages.getStringTagAt(this.currPage);
        }

        if (this.fontRendererObj.getBidiFlag()) {
            var7 = var7 + "_";
        } else if (this.updateCount / 6 % 2 == 0) {
            var7 = var7 + "" + EnumChatFormatting.WHITE + "_";
        } else {
            var7 = var7 + "" + EnumChatFormatting.GRAY + "_";
        }

        var8 = this.fontRendererObj.getStringWidth(var6);
        this.fontRendererObj.drawString(var6, left - var8 + this.ngtImageWidth - 44, offset + 16, 0xff6f00);
        this.fontRendererObj.drawSplitString(var7, left + 36, offset + 16 + 16, 116, 0xff6f00);

        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    //static ResourceLocation access$000() { return ngtGuiTextures; }

    @SideOnly(Side.CLIENT)
    class NextPageButton extends GuiButton {
        private final boolean field_146151_o;

        public NextPageButton(int p_i1079_1_, int p_i1079_2_, int p_i1079_3_, boolean p_i1079_4_) {
            super(p_i1079_1_, p_i1079_2_, p_i1079_3_, 23, 13, "");
            this.field_146151_o = p_i1079_4_;
        }

        public void drawButton(Minecraft p_drawButton_1_, int p_drawButton_2_, int p_drawButton_3_) {
            if (this.visible) {
                boolean var4 = p_drawButton_2_ >= this.xPosition && p_drawButton_3_ >= this.yPosition && p_drawButton_2_ < this.xPosition + this.width && p_drawButton_3_ < this.yPosition + this.height;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                p_drawButton_1_.getTextureManager().bindTexture(ngtGuiTextures);
                int var5 = 0;
                int var6 = 192;
                if (var4) {
                    var5 += 23;
                }

                if (!this.field_146151_o) {
                    var6 += 13;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, var6, 23, 13);
            }
        }
    }
}
