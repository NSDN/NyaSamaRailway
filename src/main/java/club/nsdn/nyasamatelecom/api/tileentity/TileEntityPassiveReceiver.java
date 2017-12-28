package club.nsdn.nyasamatelecom.api.tileentity;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TileEntityPassiveReceiver extends TileEntityReceiver {

    // signal box should control this receiver
    // receiver should use meta & 0x8 as enabled/powered flag
    public TileEntityPassiveReceiver() { super(); }

}
