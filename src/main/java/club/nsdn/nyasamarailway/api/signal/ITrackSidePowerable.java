package club.nsdn.nyasamarailway.api.signal;

/**
 * Created by drzzm32 on 2019.2.3.
 */
public interface ITrackSidePowerable {
    boolean hasPowered();
    void setPowered(boolean state);
}
