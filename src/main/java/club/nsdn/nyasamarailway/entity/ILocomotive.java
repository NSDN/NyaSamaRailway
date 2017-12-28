package club.nsdn.nyasamarailway.entity;


/**
 * Created by drzzm32 on 2017.10.8.
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
