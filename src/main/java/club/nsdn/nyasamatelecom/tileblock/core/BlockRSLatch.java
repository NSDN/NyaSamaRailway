package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.ITriStateReceiver;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.5.1.
 */
public class BlockRSLatch extends SignalBoxSender {

    public static class TileEntityRSLatch extends SignalBoxSender.TileEntitySignalBoxSender implements ITriStateReceiver {

        public static final int STATE_POS = 1;
        public static final int STATE_ZERO = 0;
        public static final int STATE_NEG = -1;

        public int state;
        private int prevState;

        public void setStatePos() {
            state = state == STATE_NEG ? STATE_ZERO : STATE_POS;
        }

        public void setStateNeg() {
            state = state == STATE_POS ? STATE_ZERO : STATE_NEG;
        }

        public boolean hasChanged() {
            return (prevIsEnabled != isEnabled) || (prevState != state);
        }

        public void updateChanged() {
            prevIsEnabled = isEnabled;
            prevState = state;
        }

        public void fromNBT(NBTTagCompound tagCompound) {
            state = tagCompound.getInteger("state");
            super.fromNBT(tagCompound);
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("state", state);
            return super.toNBT(tagCompound);
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRSLatch();
    }

    public BlockRSLatch() {
        super(NyaSamaTelecom.modid, "BlockRSLatch", "rs_latch");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public void updateSignal(World world, int x , int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRSLatch) {
            TileEntityRSLatch latch = (TileEntityRSLatch) world.getTileEntity(x, y, z);

            int meta = world.getBlockMetadata(x, y, z);
            int old = meta;

            switch (latch.state) {
                case TileEntityRSLatch.STATE_POS:
                    latch.isEnabled = true;
                    break;
                case TileEntityRSLatch.STATE_NEG:
                    latch.isEnabled = false;
                    break;
                case TileEntityRSLatch.STATE_ZERO:
                    break;
                default:
                    break;
            }

            boolean isEnabled = latch.isEnabled;

            if (latch.getTransceiver() != null) {
                isEnabled = isEnabled && latch.transceiverIsPowered();
            }

            if (isEnabled) meta |= 0x8;
            else meta &= 0x7;

            if (old != meta) {
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
            }
            if (latch.hasChanged()) {
                latch.updateChanged();
                world.markBlockForUpdate(x, y, z);
            }

            latch.state = TileEntityRSLatch.STATE_ZERO;

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

}
