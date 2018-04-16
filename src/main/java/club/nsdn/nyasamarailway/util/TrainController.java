package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.entity.loco.NSPCT8J;
import club.nsdn.nyasamarailway.network.TrainPacket;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2016.5.9.
 */
public class TrainController {

    public final static int MaxP = 20;

    @SideOnly(Side.CLIENT)
    public static class KeyInput {
        public final static KeyBinding keyPowerUp = new KeyBinding("ntp.control.power.up", Keyboard.KEY_RIGHT, "ntp.control.title");
        public final static KeyBinding keyPowerDown = new KeyBinding("ntp.control.power.down", Keyboard.KEY_LEFT, "ntp.control.title");
        public final static KeyBinding keyDirUp = new KeyBinding("ntp.control.dir.up", Keyboard.KEY_UP, "ntp.control.title");
        public final static KeyBinding keyDirDown = new KeyBinding("ntp.control.dir.down", Keyboard.KEY_DOWN, "ntp.control.title");
        public final static KeyBinding keyBrakeUp = new KeyBinding("ntp.control.brake.up", Keyboard.KEY_PERIOD, "ntp.control.title");
        public final static KeyBinding keyBrakeDown = new KeyBinding("ntp.control.brake.down", Keyboard.KEY_COMMA, "ntp.control.title");

        public static int DirUP = 0;
        public static int DirDOWN = 0;
        public static int PowerDown = 0;
        public static int PowerUp = 0;
        public static int BrakeDown = 0;
        public static int BrakeUp = 0;

        public static void registerKeyBindings() {
            ClientRegistry.registerKeyBinding(keyPowerUp);
            ClientRegistry.registerKeyBinding(keyPowerDown);
            ClientRegistry.registerKeyBinding(keyDirUp);
            ClientRegistry.registerKeyBinding(keyDirDown);
            ClientRegistry.registerKeyBinding(keyBrakeUp);
            ClientRegistry.registerKeyBinding(keyBrakeDown);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void doControl(TrainPacket train, EntityPlayer player) {
        if (KeyInput.keyDirUp.isPressed() && KeyInput.DirUP == 0) {
            KeyInput.DirUP = 1;
        } else if (KeyInput.keyDirDown.isPressed() && KeyInput.DirDOWN == 0) {
            KeyInput.DirDOWN = 1;
        }
        if (KeyInput.keyPowerDown.isPressed() && KeyInput.PowerDown == 0) {
            KeyInput.PowerDown = 1;
        } else if (KeyInput.keyPowerUp.isPressed() && KeyInput.PowerUp == 0) {
            KeyInput.PowerUp = 1;
        }
        if (KeyInput.keyBrakeDown.isPressed() && KeyInput.BrakeDown == 0) {
            KeyInput.BrakeDown = 1;
        } else if (KeyInput.keyBrakeUp.isPressed() && KeyInput.BrakeUp == 0) {
            KeyInput.BrakeUp = 1;
        }

        if (KeyInput.DirUP == 1) {
            if (train.P > 0) {
                if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.notZeroPower"));
            } else {
                if (train.Dir == -1) train.Dir = 0;
                else if (train.Dir == 0) train.Dir = 1;
                if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.dir", train.Dir));
            }
            KeyInput.DirUP = 2;
        } else if (KeyInput.DirDOWN == 1) {
            if (train.P > 0) {
                if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.notZeroPower"));
            } else {
                if (train.Dir == 1) train.Dir = 0;
                else if (train.Dir == 0) train.Dir = -1;
                if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.dir", train.Dir));
            }
            KeyInput.DirDOWN = 2;
        }

        if (KeyInput.PowerDown == 1) {
            if (train.P > 0) train.P -= 1;
            if (train.P < 0) train.P = 0;
            if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.power", train.P));
            KeyInput.PowerDown = 2;
        } else if (KeyInput.PowerUp == 1) {
            if (train.P < MaxP) train.P += 1;
            if (train.P > MaxP) train.P = MaxP;
            if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.power", train.P));
            KeyInput.PowerUp = 2;
        }

        if (KeyInput.BrakeDown == 1) {
            if (train.R < 10) train.R += 1;
            if (train.R > 10) train.R = 10;
            if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.brake", 10 - train.R));
            KeyInput.BrakeDown = 2;
        } else if (KeyInput.BrakeUp == 1) {
            if (train.R > 1) train.R -= 1;
            if (train.R < 1) train.R = 1;
            if (!(player.ridingEntity instanceof NSPCT8J)) player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.brake", 10 - train.R));
            KeyInput.BrakeUp = 2;
        }

        if (!KeyInput.keyDirUp.isPressed() && KeyInput.DirUP == 2) {
            KeyInput.DirUP = 0;
        } else if (!KeyInput.keyDirDown.isPressed() && KeyInput.DirDOWN == 2) {
            KeyInput.DirDOWN = 0;
        }
        if (!KeyInput.keyPowerDown.isPressed() && KeyInput.PowerDown == 2) {
            KeyInput.PowerDown = 0;
        } else if (!KeyInput.keyPowerUp.isPressed() && KeyInput.PowerUp == 2) {
            KeyInput.PowerUp = 0;
        }
        if (!KeyInput.keyBrakeDown.isPressed() && KeyInput.BrakeDown == 2) {
            KeyInput.BrakeDown = 0;
        } else if (!KeyInput.keyBrakeUp.isPressed() && KeyInput.BrakeUp == 2) {
            KeyInput.BrakeUp = 0;
        }

    }

    private static void calcYaw(TrainPacket packet, Entity cart) {
        packet.prevYaw = packet.Yaw;
        packet.Yaw = 180.0 - cart.rotationYaw;
    }

    public static double calcYaw(Entity cart) {
        return 180.0 - cart.rotationYaw;
    }

    public static void doMotion(TrainPacket packet, Entity cart) {
        calcYaw(packet, cart);

        if (packet.P > 0 && packet.Velocity < 0.005) {
            packet.Velocity = 0.005;
        }

        if (packet.R > 1) {
            if (packet.highSpeed) {
                packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUp(Math.abs(packet.Velocity), 0.1, 1.0, packet.P / 10.0, 0.01);
            } else {
                packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUp(Math.abs(packet.Velocity), 0.1, 1.0, packet.P / 20.0, 0.02);
            }

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDown(Math.abs(packet.Velocity), 0.1, 1.0, 0.1, 1.0, packet.R / 10.0, 0.02);
            if (packet.Velocity < 0.005) packet.Velocity = 0;
        }

        if (packet.Dir != 0) {
            cart.motionX = Math.cos(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
            cart.motionZ = -Math.sin(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
        } else {
            packet.Velocity = Math.abs(cart.motionX / Math.cos(packet.Yaw * Math.PI / 180.0));
            if (Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0)) > packet.Velocity)
                packet.Velocity = Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0));
        }

    }

    public static void doMotionWithAir(TrainPacket packet, Entity cart) {
        calcYaw(packet, cart);

        if (packet.P > 0 && packet.Velocity < 0.005) {
            packet.Velocity = 0.005;
        }

        if (packet.R > 1) {
            double MaxP = 4.0;
            double OutP = MaxP / Math.pow(20.0, Math.E / 2.0) * Math.pow((double) packet.P, Math.E / 2.0);
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, 0.001);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, 1.0, 1.0, packet.R / 10.0, 0.001);
            if (packet.Velocity < 0.005) packet.Velocity = 0;
        }

        if (packet.Dir != 0) {
            cart.motionX = Math.cos(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
            cart.motionZ = -Math.sin(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
        } else {
            packet.Velocity = Math.abs(cart.motionX / Math.cos(packet.Yaw * Math.PI / 180.0));
            if (Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0)) > packet.Velocity)
                packet.Velocity = Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0));
        }

    }

    public static void doMotionWithAirHigh(TrainPacket packet, Entity cart) {
        calcYaw(packet, cart);

        if (packet.P > 0 && packet.Velocity < 0.005) {
            packet.Velocity = 0.005;
        }

        if (packet.R > 1) {
            double MaxP = 40.0;
            double OutP = MaxP / Math.pow(20.0, 2.0) * Math.pow((double) packet.P, 2.0);
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, 0.001);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, 1.0, 1.0, packet.R / 10.0, 0.001);
            if (packet.Velocity < 0.005) packet.Velocity = 0;
        }

        if (packet.Dir != 0) {
            cart.motionX = Math.cos(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
            cart.motionZ = -Math.sin(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
        } else {
            packet.Velocity = Math.abs(cart.motionX / Math.cos(packet.Yaw * Math.PI / 180.0));
            if (Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0)) > packet.Velocity)
                packet.Velocity = Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0));
        }

        if (packet.Velocity > 6.0) packet.Velocity = 6.0;

    }

    public static void doMotionWithAirHighEx(TrainPacket packet, Entity cart) {
        calcYaw(packet, cart);

        if (packet.P > 0 && packet.Velocity < 0.005) {
            packet.Velocity = 0.005;
        }

        if (packet.R > 1) {
            double MaxP = 80.0;
            double OutP = MaxP / Math.pow(20.0, 2.0) * Math.pow((double) packet.P, 2.0);
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, 0.001);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, 1.0, 1.0, packet.R / 10.0, 0.001);
            if (packet.Velocity < 0.005) packet.Velocity = 0;
        }

        if (packet.Dir != 0) {
            cart.motionX = Math.cos(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
            cart.motionZ = -Math.sin(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
        } else {
            packet.Velocity = Math.abs(cart.motionX / Math.cos(packet.Yaw * Math.PI / 180.0));
            if (Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0)) > packet.Velocity)
                packet.Velocity = Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0));
        }

        if (packet.Velocity > 8.0) packet.Velocity = 8.0;

    }

}
