package club.nsdn.nyasamarailway.tileblock.signal.core;

import club.nsdn.nyasamarailway.NyaSamaRailway;

/**
 * Created by drzzm32 on 2017.8.10.
 */
public class BlockSignalBoxSender extends club.nsdn.nyasamatelecom.api.device.SignalBoxSender {

    public BlockSignalBoxSender() {
        super(NyaSamaRailway.MODID, "SignalBoxSender", "signal_box_sender");
    }

}
