package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.item.tool.ItemNGTablet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.3.10.
 */
public class BuildEndpoint extends BlockContainer {

    public BuildEndpoint() {
        super(Material.IRON);
        setUnlocalizedName("BuildEndpoint");
        setRegistryName(NyaSamaRailway.MODID, "build_endpoint");
        setLightOpacity(0);
        setHardness(2.0F);
        setResistance(blockHardness * 5.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityBuildEndpoint();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public int tickRate(World world) {
        return 1;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBuildEndpoint) {
                TileEntityBuildEndpoint endpoint = (TileEntityBuildEndpoint) te;

                if (endpoint.theTask == null) return;

                if (endpoint.hasNext()) {
                    Vec3d vec = endpoint.next();
                    BlockPos next = vec2Pos(vec);
                    SoundType soundType = Block.getBlockById(endpoint.theTask.block).getSoundType();
                    world.playSound(
                            null, pos,
                            soundType.getPlaceSound(), SoundCategory.BLOCKS,
                            (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F
                    );
                    world.playSound(
                            null, vec.x, vec.y, vec.z,
                            soundType.getPlaceSound(), SoundCategory.BLOCKS,
                            (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F
                    );
                    if (world.getBlockState(next).getBlock() != this)
                        endpoint.theTask.place(world, next, endpoint::recordUndo, endpoint::hadPlaced);

                    if (endpoint.theTask.tick <= 0) {
                        for (int i = endpoint.theTask.tick; i < 1; i++) {
                            if (!endpoint.hasNext())
                                break;
                            vec = endpoint.next(); next = vec2Pos(vec);
                            if (world.getBlockState(next).getBlock() != this)
                                endpoint.theTask.place(world, next, endpoint::recordUndo, endpoint::hadPlaced);
                        }
                        world.scheduleUpdate(pos, this, 1);
                    } else {
                        world.scheduleUpdate(pos, this, endpoint.theTask.tick);
                    }
                } else {
                    endpoint.theTask = null;
                    endpoint.reset();
                    world.playSound(
                            null, pos,
                            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                            0.5F, 0.8F
                    );
                }
            }
        }
    }

    BlockPos vec2Pos(Vec3d vec) {
        return new BlockPos(
                MathHelper.floor(vec.x), MathHelper.floor(vec.y), MathHelper.floor(vec.z)
        );
    }

    void say(EntityPlayer player, String format, Object... args) {
        if (!player.world.isRemote)
            player.sendMessage(new TextComponentString(String.format(format, args)));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof Item1N4148) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBuildEndpoint) {
                TileEntityBuildEndpoint endpoint = (TileEntityBuildEndpoint) te;

                endpoint.updateRoute();

                Vec3d vec; endpoint.reset();
                while (endpoint.hasNext()) {
                    vec = endpoint.next();
                    if (Double.isNaN(vec.x + vec.y + vec.z) || Double.isInfinite(vec.x + vec.y + vec.z)) {
                        say(player, "[NSR] NAN ERROR | INF ERROR");
                        endpoint.points.clear();
                        return true;
                    }
                }

                if (!world.isRemote) {
                    endpoint.theTask = null;
                    say(player, "[NSR] Total: %d", endpoint.points.size());
                    endpoint.refresh();
                }

                return true;
            }
        } else if (stack.getItem() instanceof Item74HC04) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBuildEndpoint) {
                TileEntityBuildEndpoint endpoint = (TileEntityBuildEndpoint) te;

                if (!world.isRemote && player.isSneaking()) {
                    if (!endpoint.oldBlocks.isEmpty()) {
                        endpoint.undo(world);
                        say(player, "[NSR] Undo success!");
                    }
                    return true;
                }
            }
        } else if (stack.getItem() instanceof ItemNGTablet) {
            NBTTagList list = Util.getTagListFromNGT(stack);
            if (list == null) return false;

            if (!world.isRemote) {
                TileEntityBuildEndpoint.Task task = new TileEntityBuildEndpoint.Task();

                String[][] code = NSASM.getCode(list);
                NSASM.Register reg = new NSASM(code) {
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
                        funcList.put("v", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            task.setTick((int) dst.data);

                            return Result.OK;
                        }));

                        funcList.put("t", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.STR) return Result.ERR;

                            switch (dst.data.toString()) {
                                case "tube": task.setType(TileEntityBuildEndpoint.TYPE_TUBE); break;
                                case "rect": task.setType(TileEntityBuildEndpoint.TYPE_RECT); break;
                                case "mono": task.setType(TileEntityBuildEndpoint.TYPE_MONO); break;
                                case "bird": task.setType(TileEntityBuildEndpoint.TYPE_BRID); break;
                                case "tun": task.setType(TileEntityBuildEndpoint.TYPE_TUN); break;
                            }

                            return Result.OK;
                        }));

                        funcList.put("b", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            task.setBlock((int) dst.data);

                            return Result.OK;
                        }));

                        funcList.put("r", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            task.setRadius((int) dst.data);

                            return Result.OK;
                        }));

                        funcList.put("h", ((dst, src) -> {
                            if (src != null) return Result.ERR;
                            if (dst == null) return Result.ERR;
                            if (dst.type != RegType.INT) return Result.ERR;

                            task.setHeight((int) dst.data);

                            return Result.OK;
                        }));
                    }
                }.run();

                if (reg != null) {
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof TileEntityBuildEndpoint) {
                        TileEntityBuildEndpoint endpoint = (TileEntityBuildEndpoint) te;

                        endpoint.reset();
                        endpoint.clearUndo();
                        endpoint.theTask = task;
                        endpoint.theTask.make();
                        say(player, "[NSR] Building started.");
                        world.scheduleUpdate(pos, this, 1);
                    }
                }
            }

            return true;
        }

        return false;
    }

}
