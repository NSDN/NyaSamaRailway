package club.nsdn.nyasamarailway.api.cart;


/**
 * Created by drzzm32 on 2019.2.10
 */
public interface ILocomotive {
    int getEnginePower();
    void setEnginePower(int value);

    int getEngineBrake();
    void setEngineBrake(int value);

    int getEngineDir();
    void setEngineDir(int value);

    double getEngineVel();
    void setEngineVel(double value);

    double getEnginePrevVel();
    void setEnginePrevVel(double value);
}
