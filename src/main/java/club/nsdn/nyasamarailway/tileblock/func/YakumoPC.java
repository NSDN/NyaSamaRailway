package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.misc.ItemTicketBase;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by drzzm32 on 2019.12.25
 */
public class YakumoPC extends TileBlock {

    public static class TileEntityYakumoPC extends TileEntityBase {

        public char hitKey = 0;
        public boolean ledLeft = false, ledRight = false;
        public String buffer = "";

        public TileEntityYakumoPC() {
            setInfo(4, 1, 0.3125, 1);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            hitKey = (char) tagCompound.getByte("hitKey");
            ledLeft = tagCompound.getBoolean("ledLeft");
            ledRight = tagCompound.getBoolean("ledRight");
            buffer = tagCompound.getString("buffer");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setByte("hitKey", (byte) hitKey);
            tagCompound.setBoolean("ledLeft", ledLeft);
            tagCompound.setBoolean("ledRight", ledRight);
            tagCompound.setString("buffer", buffer);

            return super.toNBT(tagCompound);
        }

        public EnumFacing dirFromMeta(int meta) {
            switch (meta & 0x3) {
                case 0: return EnumFacing.NORTH;
                case 1: return EnumFacing.EAST;
                case 2: return EnumFacing.SOUTH;
                case 3: return EnumFacing.WEST;
            }
            return EnumFacing.DOWN;
        }

        private int __counter = 0, __delay = 0;
        private char prevHitKey = 0;
        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityYakumoPC) {
                TileEntityYakumoPC pc = (TileEntityYakumoPC) tileEntity;

                if (__counter < 20) __counter++;
                else {
                    __counter = 0;
                    pc.ledLeft = !pc.ledLeft;
                }

                if (pc.ledRight) {
                    if (__delay < 10) __delay++;
                    else {
                        __delay = 0;
                        pc.ledRight = false;
                        pc.prevHitKey = pc.hitKey;
                        pc.hitKey = 0;
                    }
                } else __delay = 0;

                pc.loop(pc.prevHitKey);
                pc.prevHitKey = 0;

                pc.refresh();
            }
        }

        private final int STATE_MENU = 0;
        private final int STATE_ONCE = 1;
        private final int STATE_CARD = 2;
        private final int STATE_ABOUT = 3;
        private int state = STATE_ABOUT;

        private int input = 0;
        static LinkedHashMap<Character, Integer> tableOnce = new LinkedHashMap<>();
        static LinkedHashMap<Character, Integer> tableCard = new LinkedHashMap<>();
        static {
            tableOnce.put('1', 1); tableOnce.put('2', 2); tableOnce.put('3', 3);
            tableOnce.put('4', 4); tableOnce.put('5', 5); tableOnce.put('6', 6);
            tableOnce.put('7', 7); tableOnce.put('8', 8); tableOnce.put('9', 9);
            tableOnce.put('a', 10); tableOnce.put('b', 11); tableOnce.put('c', 12);
            tableOnce.put('d', 13); tableOnce.put('e', 14); tableOnce.put('f', 15);

            tableCard.put('1', 10); tableCard.put('2', 20); tableCard.put('3', 50);
            tableCard.put('4', 80); tableCard.put('5', 100); tableCard.put('6', 200);
        }

        public void loop(char key) {
            switch (state) {
                case STATE_MENU:
                    input = 0;
                    clear();
                    printT("pc.menu.line1");
                    printT("pc.menu.line2");
                    printT("pc.menu.line3");
                    printT("pc.menu.line4");
                    print(" ");
                    print(" ");
                    if (key != 0) {
                        switch (key) {
                            case '1':
                                state = STATE_ONCE;
                                break;
                            case '2':
                                state = STATE_CARD;
                                break;
                            case '0':
                                state = STATE_ABOUT;
                                break;
                        }
                    }
                    break;
                case STATE_ONCE:
                    clear();
                    printT("pc.once.line1");
                    printT("pc.once.line2");
                    printT("pc.once.line3");
                    print(" ");
                    printT("pc.once.line5");
                    if (input != 0)
                        printT("pc.once.line6", input);
                    else
                        printT("pc.once.line6");
                    if (key != 0) {
                        switch (key) {
                            case 'y':
                                if (input != 0) {
                                    ItemStack stack = new ItemStack(ItemLoader.oneCard);
                                    ItemTicketBase.setOver(stack, input);
                                    throwItem(world, pos, stack);
                                    input = 0;
                                }
                                break;
                            case 'n':
                                state = STATE_MENU;
                                break;
                            default:
                                if (tableOnce.containsKey(key))
                                    input = tableOnce.get(key);
                                break;
                        }
                    }
                    break;
                case STATE_CARD:
                    clear();
                    printT("pc.card.line1");
                    printT("pc.card.line2");
                    printT("pc.card.line3");
                    print(" ");
                    printT("pc.card.line5");
                    if (input != 0)
                        printT("pc.card.line6", input);
                    else
                        printT("pc.card.line6");
                    if (key != 0) {
                        switch (key) {
                            case 'y':
                                if (input != 0) {
                                    ItemStack stack = new ItemStack(ItemLoader.nyaCard);
                                    ItemTicketBase.setOver(stack, input);
                                    throwItem(world, pos, stack);
                                    input = 0;
                                }
                                break;
                            case 'n':
                                state = STATE_MENU;
                                break;
                            default:
                                if (tableCard.containsKey(key))
                                    input = tableCard.get(key);
                                break;
                        }
                    }
                    break;
                case STATE_ABOUT:
                    input = 0;
                    clear();
                    printT("pc.about.line1");
                    printT("pc.about.line2");
                    printT("pc.about.line3");
                    printT("pc.about.line4");
                    print(" ");
                    printT("pc.about.line6");
                    if (key != 0)
                        state = STATE_MENU;
                    break;
            }
        }

        public void clear() { buffer = ""; }

        // 18 chars (70 pixels) x 6 lines (54 pixels)
        public void print(String format, Object... args) {
            final int lineCnt = 6;
            String[] raw = buffer.split("\n");
            String[] lines = new String[lineCnt];
            System.arraycopy(Arrays.copyOf(raw, lineCnt), 1, lines, 0, lineCnt - 1);
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] == null) lines[i] = "";
            }
            lines[lineCnt - 1] = String.format(format, args);
            StringBuilder builder = new StringBuilder();
            for (String s : lines)
                builder.append(s).append("\n");
            buffer = builder.toString();
        }

        public void printT(String format, Object... args) {
            final int lineCnt = 6;
            String[] raw = buffer.split("\n");
            String[] lines = new String[lineCnt];
            System.arraycopy(Arrays.copyOf(raw, lineCnt), 1, lines, 0, lineCnt - 1);
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] == null) lines[i] = "";
            }
            lines[lineCnt - 1] = new TextComponentTranslation(format, args).getUnformattedText();
            StringBuilder builder = new StringBuilder();
            for (String s : lines)
                builder.append(s).append("\n");
            buffer = builder.toString();
        }

        public boolean busy() {
            return ledRight;
        }

        public void click(char c) {
            hitKey = c;
            ledRight = true;
        }

        public void throwItem(World world, BlockPos pos, ItemStack stack) {
            EnumFacing facing = dirFromMeta(META);
            EntityItem entityItem = new EntityItem(
                    world,
                    pos.getX() + 0.5 + facing.getFrontOffsetX(),
                    pos.getY() + 0.5 + facing.getFrontOffsetY(),
                    pos.getZ() + 0.5 + facing.getFrontOffsetZ(),
                    stack
            );
            world.spawnEntity(entityItem);
        }

    }

    public YakumoPC() {
        super("YakumoPC");
        setRegistryName(NyaSamaRailway.MODID, "yakumo_pc");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
        list.add(TextFormatting.AQUA + "Yakumo PC Inc. (R) 2020");
        list.add(TextFormatting.GRAY + "Series 8, Rev1");
        list.add(TextFormatting.DARK_AQUA + "for Railway System");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityYakumoPC();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            return dir != facing;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    private static Tuple<Float, Float> transHitPos(Tuple<Float, Float> src, int meta) {
        float x = src.getFirst(), z = src.getSecond();
        x -= 0.5F; z -= 0.5F;
        float d;
        switch (meta) {
            case 0:
                x = -x;
                z = -z;
                break;
            case 1:
                d = x;
                x = -z;
                z = +d;
                break;
            case 2:
                break;
            case 3:
                d = x;
                x = +z;
                z = -d;
                break;
        }
        x += 0.5F; z += 0.5F;
        return new Tuple<>(x, z);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityYakumoPC) {
            TileEntityYakumoPC pc = (TileEntityYakumoPC) tileEntity;
            ItemStack stack = player.getHeldItemMainhand();
            int meta = pc.META;

            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item instanceof NGTablet) {
                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;

                    if (!world.isRemote) {
                        String[][] code = NSASM.getCode(list);
                        new NSASM(code) {
                            @Override
                            public World getWorld() {
                                return world;
                            }

                            @Override
                            public double getX() {
                                return pos.getX();
                            }

                            @Override
                            public double getY() {
                                return pos.getY();
                            }

                            @Override
                            public double getZ() {
                                return pos.getZ();
                            }

                            @Override
                            public EntityPlayer getPlayer() {
                                return player;
                            }

                            @Override
                            public SimpleNetworkWrapper getWrapper() {
                                return NetworkWrapper.instance;
                            }

                            @Override
                            public void loadFunc(LinkedHashMap<String, Operator> funcList) {

                            }
                        }.run();
                    }

                    return true;
                }
            }

            final char[][] keyMap = new char[][] {
                    { 'n', '0', '1', '2', '3', '4' },
                    { 'a', 'b', '5', '6', '7', '8' },
                    { 'c', 'd', '9', 'e', 'f', 'y' },
            };
            final int cntX = keyMap[0].length, cntZ = keyMap.length;
            final float endX = 0.875F, endZ = 0.90625F;
            final float size = 0.125F;
            final float staX = endX - cntX * size;
            final float staZ = endZ - cntZ * size;
            final float lenX = endX - staX, lenZ = endZ - staZ;

            Tuple<Float, Float> res = transHitPos(new Tuple<>(hitX, hitZ), meta);
            hitX = res.getFirst();
            hitZ = res.getSecond();

            if (!pc.busy()) {
                // here equal is needed, to prevent arrayOutOfBounds.
                if (hitX <= staX || hitX >= endX)
                    return true;
                if (hitZ <= staZ || hitZ >= endZ)
                    return true;

                float u = (hitX - staX) / lenX * cntX;
                float v = (hitZ - staZ) / lenZ * cntZ;
                int x = MathHelper.floor(u);
                int z = MathHelper.floor(v);
                pc.click(keyMap[z][x]);
            }

            return true;
        }
        return false;
    }


}
