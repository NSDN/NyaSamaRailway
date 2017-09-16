package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public interface IRelay<T, R> extends IReceiver<R> {
    T getTarget();
    void setTarget(T target);
}
