package club.nsdn.nyasamarailway.api.cart;

/**
 * Created by drzzm32 on 2018.3.21.
 */
public interface IExtendedInfoCart {
    void setExtendedInfo(String key, String data);
    String getExtendedInfo(String key);
    void setExtendedInfo(String info);
    String getExtendedInfo();
}
