package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.tileentity.ITileAnchor;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by drzzm32 on 2019.3.10.
 */
public class BuildNode extends BlockContainer {

    public BuildNode() {
        super(Material.IRON);
        setUnlocalizedName("BuildNode");
        setRegistryName(NyaSamaRailway.MODID, "build_node");
        setLightOpacity(0);
        setHardness(2.0F);
        setResistance(blockHardness * 5.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityBuildNode();
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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
        list.add(TextFormatting.LIGHT_PURPLE + "Anchor (R) inside");
        list.add(TextFormatting.DARK_PURPLE + "from NSDN (C) 2020");
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBuildNode) {
            ((TileEntityBuildNode) tileEntity).releaseTicket();
        }
        super.breakBlock(world, pos, state);
    }

    public static class TileEntityBuildNode extends TileEntityActuator implements ITileAnchor {

        /* -------- -------- -------- -------- */

        public boolean hasTicket = false;
        public static final byte ANCHOR_RADIUS = 2;
        public static final byte MAX_CHUNKS = 25;
        public Set<ChunkPos> chunks;
        public ForgeChunkManager.Ticket ticket;

        public void setTicketFlag(boolean flag) {
            hasTicket = flag;
        }

        public boolean hasTicketFlag() {
            return hasTicket;
        }

        @Override
        public void update() {
            if (!world.isRemote)
                updateSignal(world, getPos());

            if (world.isRemote) {
                if (hasTicketFlag())
                    if (chunks == null)
                        setupChunks();
                return;
            }

            if (ticket == null)
                requestTicket();
        }

        public void releaseTicket() {
            ForgeChunkManager.releaseTicket(ticket);
            ticket = null;
            setTicketFlag(false);
        }

        private boolean requestTicket() {
            ForgeChunkManager.Ticket chunkTicket = ForgeChunkManager.requestTicket(
                    NyaSamaTelecom.getInstance(), world, ForgeChunkManager.Type.NORMAL
            );
            if (chunkTicket != null) {
                NBTTagCompound tag = chunkTicket.getModData();
                tag.setInteger("x", pos.getX());
                tag.setInteger("y", pos.getY());
                tag.setInteger("z", pos.getZ());
                chunkTicket.setChunkListDepth(MAX_CHUNKS);
                setChunkTicket(chunkTicket);
                forceChunkLoading();
                return true;
            }
            return false;
        }

        public void setChunkTicket(@Nullable ForgeChunkManager.Ticket tick) {
            if (this.ticket != tick)
                ForgeChunkManager.releaseTicket(this.ticket);
            this.ticket = tick;
            setTicketFlag(ticket != null);
        }

        public Set<ChunkPos> getChunksAround(int xChunk, int zChunk, int radius) {
            Set<ChunkPos> chunkList = new HashSet<ChunkPos>();
            for (int xx = xChunk - radius; xx <= xChunk + radius; xx++) {
                for (int zz = zChunk - radius; zz <= zChunk + radius; zz++) {
                    chunkList.add(new ChunkPos(xx, zz));
                }
            }
            return chunkList;
        }

        public void forceChunkLoading() {
            if (ticket == null)
                return;

            int xChunk = getPos().getX() >> 4, zChunk = getPos().getZ() >> 4;
            setupChunks();

            Set<ChunkPos> innerChunks = getChunksAround(xChunk, zChunk, 1);

            for (ChunkPos chunk : chunks) {
                ForgeChunkManager.forceChunk(ticket, chunk);
                ForgeChunkManager.reorderChunk(ticket, chunk);
            }
            for (ChunkPos chunk : innerChunks) {
                ForgeChunkManager.forceChunk(ticket, chunk);
                ForgeChunkManager.reorderChunk(ticket, chunk);
            }

            ChunkPos myChunk = new ChunkPos(xChunk, zChunk);
            ForgeChunkManager.forceChunk(ticket, myChunk);
            ForgeChunkManager.reorderChunk(ticket, myChunk);
        }

        public void setupChunks() {
            int xChunk = getPos().getX() >> 4, zChunk = getPos().getZ() >> 4;
            if (hasTicketFlag())
                chunks = getChunksAround(xChunk, zChunk, ANCHOR_RADIUS);
            else
                chunks = Collections.emptySet();
        }

    }

}
