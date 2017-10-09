package club.nsdn.nyasamarailway.Entity;


/**
 * Created by drzzm32 on 2017.10.1.
 */
public interface IMotorCart {
    void setMotorState(boolean state);
    void setMotorPower(int power);
    void setMotorBrake(int brake);
    void setMotorDir(int dir);
    void setMotorVel(float vel);

    boolean getMotorState();
    int getMotorPower();
    int getMotorBrake();
    int getMotorDir();
    float getMotorVel();
}
