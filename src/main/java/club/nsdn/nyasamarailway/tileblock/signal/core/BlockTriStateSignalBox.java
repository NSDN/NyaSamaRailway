package club.nsdn.nyasamarailway.tileblock.signal.core;

import club.nsdn.nyasamarailway.NyaSamaRailway;

/**
 * Created by drzzm32 on 2017.8.9.
 */
public class BlockTriStateSignalBox extends club.nsdn.nyasamatelecom.api.device.TriStateSignalBox {

    public BlockTriStateSignalBox() {
        super(NyaSamaRailway.MODID, "TriStateSignalBox", "tri_state_signal_box");
    }

}
