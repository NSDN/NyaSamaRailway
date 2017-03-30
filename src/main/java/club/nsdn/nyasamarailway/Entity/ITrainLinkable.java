package club.nsdn.nyasamarailway.Entity;

import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.7.6.
 */

public interface ITrainLinkable {
    int getPrevTrainID();
    int getNextTrainID();
    boolean LinkTrain(int ID);
    void deLinkTrain(int ID);
    void calcLink(World world);
}
