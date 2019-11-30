package club.nsdn.nyasamarailway.item.helper;

import club.nsdn.nyasamarailway.api.signal.ITrackSide;
import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.deco.Pillar;
import club.nsdn.nyasamarailway.tileblock.signal.light.*;
import club.nsdn.nyasamarailway.tileblock.signal.trackside.*;
import club.nsdn.nyasamatelecom.api.device.*;
import club.nsdn.nyasamatelecom.api.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.11.20
 */
public class DeployHelper {

    private SignalBox signalBox;
    private TriStateSignalBox triSignalBox;
    private Pillar pillar;
    private BiSignalLight biSignalLight;
    private TrackSideSniffer trackSideSniffer;
    private TrackSideRFID trackSideRFID;
    private TrackSideSnifferHs trackSideSnifferHs;
    private TrackSideRFIDHs trackSideRFIDHs;
    private TrackSideBlocking trackSideBlocking;
    private TrackSideReception trackSideReception;

    private DeployHelper(SignalBox box, TriStateSignalBox triBox,
                         Pillar pillar, BiSignalLight light,
                         TrackSideSniffer sniffer, TrackSideRFID rfid,
                         TrackSideSnifferHs snifferHs, TrackSideRFIDHs rfidHs,
                         TrackSideBlocking blocking, TrackSideReception reception) {
        signalBox = box;
        triSignalBox = triBox;
        this.pillar = pillar;
        biSignalLight = light;
        trackSideSniffer = sniffer;
        trackSideRFID = rfid;
        trackSideSnifferHs = snifferHs;
        trackSideRFIDHs = rfidHs;
        trackSideBlocking = blocking;
        trackSideReception = reception;
    }

    public static DeployHelper INSTANCE = null;

    public static void setInstance() {
        INSTANCE = new DeployHelper(
                club.nsdn.nyasamatelecom.block.BlockLoader.signalBox,
                club.nsdn.nyasamatelecom.block.BlockLoader.triStateSignalBox,
                BlockLoader.pillar, BlockLoader.biSignalLight,
                BlockLoader.trackSideSniffer, BlockLoader.trackSideRFID,
                BlockLoader.trackSideSnifferHs, BlockLoader.trackSideRFIDHs,
                BlockLoader.trackSideBlocking, BlockLoader.trackSideReception
        );
    }

    private void place(World world, BlockPos pos, Block block, EntityPlayer player) {
        IBlockState state = block.getDefaultState();
        world.setTileEntity(pos, block.createTileEntity(world, state));
        world.setBlockState(pos, state);
        block.onBlockPlacedBy(world, pos, state, player, null);
        world.notifyBlockUpdate(pos, state, state, 2);
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public TileEntityActuator placeSignalBox(World world, BlockPos pos, EntityPlayer player, boolean invert) {
        place(world, pos, signalBox, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof SignalBox.TileEntitySignalBox) {
            SignalBox.TileEntitySignalBox box = (SignalBox.TileEntitySignalBox) te;
            box.inverterEnabled = invert;
            box.refresh();
            return box;
        }
        return null;
    }

    public TileEntityActuator placeTriSignalBox(World world, BlockPos pos, EntityPlayer player, boolean neg, boolean invert) {
        place(world, pos, triSignalBox, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TriStateSignalBox.TileEntityTriStateSignalBox) {
            TriStateSignalBox.TileEntityTriStateSignalBox box = (TriStateSignalBox.TileEntityTriStateSignalBox) te;
            box.triStateIsNeg = neg;
            box.inverterEnabled = invert;
            box.refresh();
            return box;
        }
        return null;
    }

    public TileEntityActuator placeTriSignalBox(World world, BlockPos pos, EntityPlayer player, boolean neg) {
        return placeTriSignalBox(world, pos, player, neg, false);
    }

    // by default, dir is left
    private void modifyDir(TileEntityBase te) {
        te.META = ((te.META & 0x3) + 2) & 0x3;
        ITrackSide.getDirByMeta(te);
    }

    public TileEntityReceiver placeSignalLight(World world, BlockPos pos, EntityPlayer player, boolean right) {
        place(world, pos, pillar, player);
        place(world, pos.up(), biSignalLight, player);
        TileEntity te = world.getTileEntity(pos.up());
        if (te instanceof BiSignalLight.TileEntityBiSignalLight) {
            BiSignalLight.TileEntityBiSignalLight light = (BiSignalLight.TileEntityBiSignalLight) te;
            light.lightType = "blue&off";
            if (right)
                modifyDir(light);
            light.refresh();
            return light;
        }
        return null;
    }

    public TileEntityReceiver placeSignalLight(World world, BlockPos pos, EntityPlayer player) {
        return placeSignalLight(world, pos, player, false);
    }

    public Tuple<TileEntityTransceiver, TileEntityTransceiver> placeBlocking(World world, BlockPos posA, BlockPos posB, EntityPlayer player, boolean right) {
        place(world, posA, trackSideBlocking, player);
        place(world, posB, trackSideBlocking, player);
        TileEntity teA = world.getTileEntity(posA);
        TileEntity teB = world.getTileEntity(posB);
        if (teA instanceof TrackSideBlocking.TileEntityTrackSideBlocking &&
            teB instanceof TrackSideBlocking.TileEntityTrackSideBlocking) {
            TrackSideBlocking.TileEntityTrackSideBlocking bA = (TrackSideBlocking.TileEntityTrackSideBlocking) teA;
            TrackSideBlocking.TileEntityTrackSideBlocking bB = (TrackSideBlocking.TileEntityTrackSideBlocking) teB;
            if (right) {
                modifyDir(bA); modifyDir(bB);
            }
            bA.setTransceiver(bB);
            bB.setTransceiver(bA);
            bA.refresh();
            bB.refresh();
            return new Tuple<>(bA, bB);
        }
        return null;
    }

    public TileEntityTransceiver placeSniffer(World world, BlockPos pos, EntityPlayer player, boolean right) {
        place(world, pos, trackSideSniffer, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TrackSideSniffer.TileEntityTrackSideSniffer) {
            TrackSideSniffer.TileEntityTrackSideSniffer sniffer = (TrackSideSniffer.TileEntityTrackSideSniffer) te;
            sniffer.keep = 20;
            if (right)
                modifyDir(sniffer);
            sniffer.refresh();
            return sniffer;
        }
        return null;
    }

    public TileEntityTransceiver placeSnifferHs(World world, BlockPos pos, EntityPlayer player, boolean invert, boolean right) {
        place(world, pos, trackSideSnifferHs, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TrackSideSnifferHs.TileEntityTrackSideSnifferHs) {
            TrackSideSnifferHs.TileEntityTrackSideSnifferHs sniffer = (TrackSideSnifferHs.TileEntityTrackSideSnifferHs) te;
            sniffer.invert = invert;
            sniffer.keep = 20;
            if (right)
                modifyDir(sniffer);
            sniffer.refresh();
            return sniffer;
        }
        return null;
    }

    public TileEntityActuator placeReception(World world, BlockPos pos, EntityPlayer player, boolean invert, boolean right) {
        place(world, pos, trackSideReception, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TrackSideReception.TileEntityTrackSideReception) {
            TrackSideReception.TileEntityTrackSideReception reception = (TrackSideReception.TileEntityTrackSideReception) te;
            reception.invert = invert;
            if (right)
                modifyDir(reception);
            reception.refresh();
            return reception;
        }
        return null;
    }

    public TileEntityActuator placeReception(World world, BlockPos pos, EntityPlayer player, boolean right) {
        return placeReception(world, pos, player, right, right);
    }

    public TileEntityReceiver placeRFID(
            World world, BlockPos pos, EntityPlayer player,
            int power, int brake, double limit,
            boolean state, boolean block,
            boolean right
    ) {
        place(world, pos, trackSideRFID, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TrackSideRFID.TileEntityTrackSideRFID) {
            TrackSideRFID.TileEntityTrackSideRFID rfid = (TrackSideRFID.TileEntityTrackSideRFID) te;

            rfid.P = power > 20 ? 20 : (power < 0 ? 0 : power);

            brake = 10 - brake;
            rfid.R = brake > 10 ? 10 : (brake < 1 ? 1 : brake);

            rfid.vel = limit;
            rfid.state = state;
            rfid.mblk = block;

            if (right)
                modifyDir(rfid);

            rfid.refresh();
            return rfid;
        }
        return null;
    }

    public TileEntityReceiver placeRFIDHs(
            World world, BlockPos pos, EntityPlayer player,
            int power, int brake, double limit, boolean high,
            boolean state, boolean block,
            boolean invert, boolean right
    ) {
        place(world, pos, trackSideRFIDHs, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TrackSideRFIDHs.TileEntityTrackSideRFIDHs) {
            TrackSideRFIDHs.TileEntityTrackSideRFIDHs rfid = (TrackSideRFIDHs.TileEntityTrackSideRFIDHs) te;
            rfid.invert = invert;

            rfid.P = power > 20 ? 20 : (power < 0 ? 0 : power);

            brake = 10 - brake;
            rfid.R = brake > 10 ? 10 : (brake < 1 ? 1 : brake);

            rfid.vel = limit;
            rfid.high = high;
            rfid.state = state;
            rfid.mblk = block;

            if (right)
                modifyDir(rfid);

            rfid.refresh();
            return rfid;
        }
        return null;
    }

    public void connect(TileEntityTransceiver source, TileEntityReceiver target) {
        if (source == null || target == null)
            return;

        target.setSender(source);
        if (source instanceof TileEntityMultiSender)
            ((TileEntityMultiSender) source).incTarget();
        source.refresh();
        target.refresh();
    }

    public void connect(TileEntityActuator source, TileEntity target) {
        if (source == null || target == null)
            return;

        source.setTarget(target);
        source.refresh();
    }

}
