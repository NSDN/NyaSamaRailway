package club.nsdn.nyasamatelecom.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class Utility {

    @SideOnly(Side.CLIENT)
    public static void setTitle() {
        String prevTitle = Display.getTitle();
        if (!prevTitle.contains("NSDN-MC") && !prevTitle.contains("Biucraft")) {
            Display.setTitle(prevTitle + " | using mods by NSDN-MC");
        }
    }

}
