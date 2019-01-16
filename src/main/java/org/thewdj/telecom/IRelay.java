package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public interface IRelay<Target, Sender> extends IReceiver<Sender> {
    Target getTarget();
    void setTarget(Target target);
    void controlTarget(boolean state);
}
