package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public interface ITransceiver<Transceiver> extends IInitiative {
    Transceiver getTransceiver();
    void setTransceiver(Transceiver transceiver);
    boolean transceiverIsPowered();
}
