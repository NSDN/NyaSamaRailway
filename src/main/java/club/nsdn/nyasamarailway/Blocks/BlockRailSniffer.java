package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailTransceiver;
import club.nsdn.nyasamarailway.Util.NSASM;
import club.nsdn.nyasamarailway.Util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by drzzm32 on 2017.10.2.
 */
public class BlockRailSniffer extends BlockRailDetectorBase implements IRailNoDelay {

    public static class RailSniffer extends TileEntityRailTransceiver {

        public static final boolean NSASM_IDLE = false;
        public static final boolean NSASM_DONE = true;
        public boolean nsasmState = NSASM_IDLE;
        public String nsasmCode = "";

        public boolean enable = false;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            nsasmState = tagCompound.getBoolean("nsasmState");
            nsasmCode = tagCompound.getString("nsasmCode");
            enable = tagCompound.getBoolean("enable");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("nsasmState", nsasmState);
            tagCompound.setString("nsasmCode", nsasmCode);
            tagCompound.setBoolean("enable", enable);
            return super.toNBT(tagCompound);
        }
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new RailSniffer();
    }

    public BlockRailSniffer() {
        super("BlockRailSniffer");
        setTextureName("rail_sniffer");
    }

    public void setOutputSignal(World world, int x, int y, int z, boolean state) {
        Block block = world.getBlock(x, y, z);
        if (block != this) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (state) {
            if ((meta & 8) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, block);
                world.notifyBlocksOfNeighborChange(x, y - 1, z, block);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        } else {
            if ((meta & 8) != 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, block);
                world.notifyBlocksOfNeighborChange(x, y - 1, z, block);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        }
        world.func_147453_f(x, y, z, block);
    }

    public void setOutputSignal(TileEntityRailTransceiver rail, boolean state) {
        setOutputSignal(rail.getWorldObj(), rail.xCoord, rail.yCoord, rail.zCoord, state);
    }

    public boolean railHasPowered(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z) & 8) != 0;
    }

    public boolean railHasCart(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox(
                        (double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize)
                )
        );
        return !bBox.isEmpty();
    }

    public EntityMinecart getCart(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox(
                        (double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize)
                )
        );
        if (bBox.isEmpty()) return null;
        if (!(bBox.get(0) instanceof EntityMinecart)) return null;
        return (EntityMinecart) bBox.get(0);
    }

    @Override
    public void setRailOutput(World world, int x, int y, int z, int meta) {
        RailSniffer sniffer = null;
        if (world.getTileEntity(x, y, z) instanceof RailSniffer)
            sniffer = (RailSniffer) world.getTileEntity(x, y, z);

        if (sniffer != null) {

            if (railHasCart(world, x, y, z) && sniffer.nsasmState == RailSniffer.NSASM_IDLE) {
                sniffer.nsasmState = RailSniffer.NSASM_DONE;

                EntityMinecart cart = getCart(world, x, y, z);
                EntityPlayer player;
                if (!(cart.riddenByEntity instanceof EntityPlayer))
                    player = null;
                else player = (EntityPlayer) cart.riddenByEntity;
                RailSniffer rail = sniffer;


                new NSASM(sniffer.nsasmCode) {
                    @Override
                    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                        funcList.replace("prt", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;

                            if (player == null) return Result.OK;
                            if (dst.type == RegType.STR) {
                                player.addChatComponentMessage(new ChatComponentText(((String) dst.data).substring(dst.strPtr)));
                            } else player.addChatComponentMessage(new ChatComponentText(dst.data.toString()));
                            return Result.OK;
                        }));

                        funcList.put("enb", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst != null) return Result.ERR;

                            rail.enable = true;
                            return Result.OK;
                        }));
                        funcList.put("rnd", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.readOnly) return Result.ERR;

                            dst.type = RegType.INT;
                            dst.data = Math.round(Math.random() * 255);
                            return Result.OK;
                        }));
                        funcList.put("equ", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;
                            if (src.data.toString().isEmpty()) return Result.ERR;
                            if (dst.data.toString().isEmpty()) return Result.ERR;

                            if (dst.data.toString().equals(src.data.toString())) {
                                funcList.get("push").run(regGroup[0], null);
                                funcList.get("push").run(regGroup[1], null);
                                regGroup[0].type = RegType.INT;
                                regGroup[0].data = 0;
                                regGroup[1].type = RegType.INT;
                                regGroup[1].data = 0;
                                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                                funcList.get("pop").run(regGroup[1], null);
                                funcList.get("pop").run(regGroup[0], null);
                            } else {
                                funcList.get("push").run(regGroup[0], null);
                                funcList.get("push").run(regGroup[1], null);
                                regGroup[0].type = RegType.INT;
                                regGroup[0].data = 1;
                                regGroup[1].type = RegType.INT;
                                regGroup[1].data = 0;
                                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                                funcList.get("pop").run(regGroup[1], null);
                                funcList.get("pop").run(regGroup[0], null);
                            }
                            return Result.OK;
                        }));
                        funcList.put("ctn", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;
                            if (src.data.toString().isEmpty()) return Result.ERR;
                            if (dst.data.toString().isEmpty()) return Result.ERR;

                            if (dst.data.toString().contains(src.data.toString())) {
                                funcList.get("push").run(regGroup[0], null);
                                funcList.get("push").run(regGroup[1], null);
                                regGroup[0].type = RegType.INT;
                                regGroup[0].data = 0;
                                regGroup[1].type = RegType.INT;
                                regGroup[1].data = 0;
                                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                                funcList.get("pop").run(regGroup[1], null);
                                funcList.get("pop").run(regGroup[0], null);
                            } else {
                                funcList.get("push").run(regGroup[0], null);
                                funcList.get("push").run(regGroup[1], null);
                                regGroup[0].type = RegType.INT;
                                regGroup[0].data = 1;
                                regGroup[1].type = RegType.INT;
                                regGroup[1].data = 0;
                                funcList.get("cmp").run(regGroup[0], regGroup[1]);
                                funcList.get("pop").run(regGroup[1], null);
                                funcList.get("pop").run(regGroup[0], null);
                            }
                            return Result.OK;
                        }));
                        funcList.put("sum", ((dst, src) -> {
                            if (src == null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (src.type != RegType.STR) return Result.ERR;
                            if (src.data.toString().isEmpty()) return Result.ERR;
                            if (dst.readOnly) return Result.ERR;

                            dst.type = RegType.INT;
                            dst.data = 0;
                            for (char c : src.data.toString().toCharArray())
                                dst.data = (int) dst.data + (int) c;
                            return Result.OK;
                        }));
                        funcList.put("cid", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.readOnly) return Result.ERR;

                            dst.type = RegType.STR;
                            dst.data = cart.getCommandSenderName();
                            return Result.OK;
                        }));
                        funcList.put("pid", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.readOnly) return Result.ERR;

                            dst.type = RegType.STR;
                            if (player == null) dst.data = "null";
                            else dst.data = player.getDisplayName();
                            return Result.OK;
                        }));
                    }
                }.run();
            }

            if (!railHasCart(world, x, y, z) && sniffer.nsasmState == RailSniffer.NSASM_DONE) {
                sniffer.nsasmState = RailSniffer.NSASM_IDLE;
                sniffer.enable = false;
            }

            if (railHasCart(world, x, y, z) && !railHasPowered(world, x, y, z) && !sniffer.enable) {
                setOutputSignal(sniffer, true);
                if (sniffer.getTransceiver() != null) setOutputSignal(sniffer.getTransceiver(), true);
            }
            if (!railHasCart(world, x, y, z) && railHasPowered(world, x, y, z)) {
                setOutputSignal(sniffer, false);
                if (sniffer.getTransceiver() != null) setOutputSignal(sniffer.getTransceiver(), false);
            }

            if (railHasCart(world, x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, this, 100); // 5s
            }

        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof RailSniffer) {
            RailSniffer sniffer = (RailSniffer) world.getTileEntity(x, y, z);
            if (!world.isRemote) {
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {

                    NBTTagList list = Util.getTagListFromBook(stack);
                    if (list == null) return true;
                    String code = NSASM.getCodeString(list);

                    sniffer.nsasmState = RailSniffer.NSASM_IDLE;
                    sniffer.nsasmCode = code;

                    player.addChatComponentMessage(new ChatComponentTranslation("info.sniffer.set"));
                }
            }
            return true;
        }

        return false;
    }

}
