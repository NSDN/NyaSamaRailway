package club.nsdn.nyasamarailway.api.cart;

/**
 * Created by drzzm32 on 2019.2.10
 */
public interface IExtendedInfoCart {
    void setExtendedInfo(String key, String data);
    String getExtendedInfo(String key);
    void setExtendedInfo(String info);
    String getExtendedInfo();
}
