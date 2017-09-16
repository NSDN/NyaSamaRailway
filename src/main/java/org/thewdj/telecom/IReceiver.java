package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public interface IReceiver<Sender> extends IInitiative {
    Sender getSender();
    void setSender(Sender sender);
    boolean senderIsPowered();
}
