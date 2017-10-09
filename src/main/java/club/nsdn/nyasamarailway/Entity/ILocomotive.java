package club.nsdn.nyasamarailway.Entity;


/**
 * Created by drzzm32 on 2017.10.8.
 */
public interface ILocomotive {
    int getP();
    void setP(int value);

    int getR();
    void setR(int value);

    int getDir();
    void setDir(int value);

    double getVelocity();
    void setVelocity(double value);

    double getPrevVelocity();
    void setPrevVelocity(double value);
}
