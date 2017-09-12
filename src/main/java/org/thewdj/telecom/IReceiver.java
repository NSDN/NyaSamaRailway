package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public interface IReceiver<T> {
    T getSender();
    void setSender(T sender);
    boolean senderIsPowered();
}
