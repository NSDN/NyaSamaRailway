package org.thewdj.telecom;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public interface IWireless<T> {
    String id();
    String key();
    T me();
}
