package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public interface ITransceiver<T> {
    T getTransceiver();
    void setTransceiver(T transceiver);
    boolean transceiverIsPowered();
}
