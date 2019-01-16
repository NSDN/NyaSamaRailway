package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public interface IWirelessTx<S, T> extends IReceiver<S>, IWireless<T> {
    // Transmit to Internet
    boolean getState();
}
