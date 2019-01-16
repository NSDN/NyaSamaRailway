package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public interface IWirelessRx<S, T> extends ITransceiver<S>, IWireless<T> {
    // Receive from Internet
    void setState(boolean state);
}
