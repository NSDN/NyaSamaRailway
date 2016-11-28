package club.nsdn.nyasamarailway.ExtMod;

/**
 * Created by drzzm on 2016.11.27.
 */
public class ClassVerifier {

    public static boolean verifyClass(Class<?> c, String tag, Class<?> end) {
        if (c == end) return false;
        if (c.getSuperclass().getName().contains(tag))
            return true;
        return verifyClass(c.getSuperclass(), tag, end);
    }



}
