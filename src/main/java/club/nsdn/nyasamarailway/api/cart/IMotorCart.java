package club.nsdn.nyasamarailway.api.cart;


/**
 * Created by drzzm32 on 2019.2.10
 */
public interface IMotorCart {
    void setMotorState(boolean state);
    void setMotorPower(int power);
    void setMotorBrake(int brake);
    void setMotorDir(int dir);
    void setMotorVel(double vel);

    boolean getMotorState();
    int getMotorPower();
    int getMotorBrake();
    int getMotorDir();
    double getMotorVel();
}
