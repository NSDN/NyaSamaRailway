package club.nsdn.nyasamatelecom.api.tileentity;

import org.thewdj.telecom.IPassive;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TileEntityPassiveReceiver extends TileEntityReceiver implements IPassive {

    // signal box should control this receiver
    // receiver should use META & 0x8 as enabled/powered flag
    // but this device is different from actuator
    public TileEntityPassiveReceiver() { super(); }

}
