package club.nsdn.nyasamarailway.Util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

/**
 * Created by drzzm on 2017.6.2.
 */
public class Utility {

    @SideOnly(Side.CLIENT)
    public static void setTitle() {
        String prevTitle = Display.getTitle();
        if (!prevTitle.contains("NSDN-MC")) {
            Display.setTitle(prevTitle + " | using mods by NSDN-MC");
        }
    }

}
