package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.api.cart.IHighSpeedCart;
import club.nsdn.nyasamarailway.api.cart.IInspectionCart;
import club.nsdn.nyasamarailway.api.cart.IMobileBlocking;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2019.2.10
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
        public final static KeyBinding keyModeSwitch = new KeyBinding("ntp.control.mode.switch", Keyboard.KEY_M, "ntp.control.title");
        public final static KeyBinding keyEmeBrake = new KeyBinding("ntp.control.brake.emergency", Keyboard.KEY_RMENU, "ntp.control.title");
        public final static KeyBinding keyMobBlock = new KeyBinding("ntp.control.mode.mblk", Keyboard.KEY_RCONTROL, "ntp.control.title");

        public static int DirUP = 0;
        public static int DirDOWN = 0;
        public static int PowerDown = 0;
        public static int PowerUp = 0;
        public static int BrakeDown = 0;
        public static int BrakeUp = 0;
        public static int ModeSwitch = 0;
        public static int EmeBrake = 0;
        public static int MobBlock = 0;

        public static void registerKeyBindings() {
            ClientRegistry.registerKeyBinding(keyPowerUp);
            ClientRegistry.registerKeyBinding(keyPowerDown);
            ClientRegistry.registerKeyBinding(keyDirUp);
            ClientRegistry.registerKeyBinding(keyDirDown);
            ClientRegistry.registerKeyBinding(keyBrakeUp);
            ClientRegistry.registerKeyBinding(keyBrakeDown);
            ClientRegistry.registerKeyBinding(keyModeSwitch);
            ClientRegistry.registerKeyBinding(keyEmeBrake);
            ClientRegistry.registerKeyBinding(keyMobBlock);
        }
    }

    public static void say(EntityPlayer player, String format, Object... args) {
        if (player != null)
            player.sendMessage(new TextComponentTranslation(format, args));
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
        if (KeyInput.keyModeSwitch.isPressed() && KeyInput.ModeSwitch == 0) {
            KeyInput.ModeSwitch = 1;
        }
        if (KeyInput.keyEmeBrake.isPressed() && KeyInput.EmeBrake == 0) {
            KeyInput.EmeBrake = 1;
        }
        if (KeyInput.keyMobBlock.isPressed() && KeyInput.MobBlock == 0) {
            KeyInput.MobBlock = 1;
        }

        if (KeyInput.DirUP == 1) {
            if (train.P > 0) {
                if (!(player.getRidingEntity() instanceof IInspectionCart))
                    say(player, "info.ntp.notZeroPower");
            } else {
                if (train.Dir == -1) train.Dir = 0;
                else if (train.Dir == 0) train.Dir = 1;
                if (!(player.getRidingEntity() instanceof IInspectionCart))
                    say(player, "info.ntp.dir", train.Dir);
            }
            KeyInput.DirUP = 2;
        } else if (KeyInput.DirDOWN == 1) {
            if (train.P > 0) {
                if (!(player.getRidingEntity() instanceof IInspectionCart))
                    say(player, "info.ntp.notZeroPower");
            } else {
                if (train.Dir == 1) train.Dir = 0;
                else if (train.Dir == 0) train.Dir = -1;
                if (!(player.getRidingEntity() instanceof IInspectionCart))
                    say(player, "info.ntp.dir", train.Dir);
            }
            KeyInput.DirDOWN = 2;
        }

        if (KeyInput.PowerDown == 1) {
            if (train.P > 0) train.P -= 1;
            if (train.P < 0) train.P = 0;
            if (!(player.getRidingEntity() instanceof IInspectionCart))
                say(player, "info.ntp.power", train.P);
            KeyInput.PowerDown = 2;
        } else if (KeyInput.PowerUp == 1) {
            if (train.P < MaxP) train.P += 1;
            if (train.P > MaxP) train.P = MaxP;
            if (!(player.getRidingEntity() instanceof IInspectionCart))
                say(player, "info.ntp.power", train.P);
            KeyInput.PowerUp = 2;
        }

        if (KeyInput.BrakeDown == 1) {
            if (train.R < 10) train.R += 1;
            if (train.R > 10) train.R = 10;
            if (!(player.getRidingEntity() instanceof IInspectionCart))
                say(player, "info.ntp.brake", 10 - train.R);
            KeyInput.BrakeDown = 2;
        } else if (KeyInput.BrakeUp == 1) {
            if (train.R > 1) train.R -= 1;
            if (train.R < 1) train.R = 1;
            if (!(player.getRidingEntity() instanceof IInspectionCart))
                say(player, "info.ntp.brake", 10 - train.R);
            KeyInput.BrakeUp = 2;
        }

        if (KeyInput.ModeSwitch == 1) {
            if (player.getRidingEntity() instanceof IHighSpeedCart) {
                train.Mode = !train.Mode;
                if (!(player.getRidingEntity() instanceof IInspectionCart))
                    say(player, "info.ntp.mode", String.valueOf(train.Mode).toUpperCase());
            }
            KeyInput.ModeSwitch = 2;
        }
        if (KeyInput.EmeBrake == 1) {
            train.P = 0;
            train.R = 1;
            if (!(player.getRidingEntity() instanceof IInspectionCart))
                say(player, "info.ntp.eme", String.valueOf(train.Mode).toUpperCase());
            KeyInput.EmeBrake = 2;
        }
        if (KeyInput.MobBlock == 1) {
            if (player.getRidingEntity() instanceof IMobileBlocking) {
                train.MBlk = !train.MBlk;
                if (!(player.getRidingEntity() instanceof IInspectionCart))
                    say(player, "info.ntp.mblk", String.valueOf(train.MBlk).toUpperCase());
            }
            KeyInput.MobBlock = 2;
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
        if (!KeyInput.keyModeSwitch.isPressed() && KeyInput.ModeSwitch == 2) {
            KeyInput.ModeSwitch = 0;
        }
        if (!KeyInput.keyEmeBrake.isPressed() && KeyInput.EmeBrake == 2) {
            KeyInput.EmeBrake = 0;
        }
        if (!KeyInput.keyMobBlock.isPressed() && KeyInput.MobBlock == 2) {
            KeyInput.MobBlock = 0;
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
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUp(Math.abs(packet.Velocity), 0.1, 1.0, packet.P / 20.0, 0.02);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity > packet.Velocity * 10 ? packet.nextVelocity / 10 : packet.nextVelocity;
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

    public static final double DT = 0.001;
    public static final double MinV = 0.2;

    public static void doMotionWithAir(TrainPacket packet, Entity cart) {
        calcYaw(packet, cart);

        if (packet.P > 0 && packet.Velocity < 0.005) {
            packet.Velocity = 0.005;
        }

        if (packet.R > 1) {
            double MaxP = 4.0;
            double OutP = MaxP / Math.pow(20.0, Math.E / 2.0) * Math.pow((double) packet.P, Math.E / 2.0);
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, DT);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity > packet.Velocity * 10 ? packet.nextVelocity / 10 : packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            double B = Math.abs(packet.Velocity) < MinV ? 2.0 : 1.0;
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, B, 1.0, packet.R / 10.0, DT);
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

    public static void doMotionWithAirEx(TrainPacket packet, Entity cart) {
        calcYaw(packet, cart);

        if (packet.P > 0 && packet.Velocity < 0.005) {
            packet.Velocity = 0.005;
        }

        if (packet.R > 1) {
            double MaxP = 10.0;
            double OutP = MaxP / Math.pow(20.0, Math.E / 2.0) * Math.pow((double) packet.P, Math.E / 2.0);
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, DT);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity > packet.Velocity * 10 ? packet.nextVelocity / 10 : packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            double B = Math.abs(packet.Velocity) < MinV ? 2.0 : 1.0;
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, B, 1.0, packet.R / 10.0, DT);
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
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, DT);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity > packet.Velocity * 10 ? packet.nextVelocity / 10 : packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            double B = Math.abs(packet.Velocity) < MinV ? 2.0 : 1.0;
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, B, 1.0, packet.R / 10.0, DT);
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
            packet.nextVelocity = Dynamics.LocoMotions.calcVelocityUpWithAir(Math.abs(packet.Velocity), 0.1, 1.0, OutP, DT);

            if (packet.Velocity < packet.nextVelocity) {
                packet.Velocity = packet.nextVelocity > packet.Velocity * 10 ? packet.nextVelocity / 10 : packet.nextVelocity;
            }
        }

        if (packet.R < 10) {
            double B = Math.abs(packet.Velocity) < MinV ? 2.0 : 1.0;
            packet.Velocity = Dynamics.LocoMotions.calcVelocityDownWithAir(Math.abs(packet.Velocity), 0.1, 1.0, B, 1.0, packet.R / 10.0, DT);
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

    public static void doMotionWithEuler(TrainPacket packet, Entity cart, double maxV) {
        calcYaw(packet, cart);

        double minV = 0.02;
        if (packet.P > 0 && packet.Velocity < minV)
            packet.Velocity = minV;

        double p = packet.P / 20.0, r = 1.0 - (packet.R - 1.0) / 9.0;
        packet.nextVelocity = Dynamics.LocoMotions.calcVelocityWithEuler(Math.abs(packet.Velocity), p, r, maxV, 0.05);

        if ((packet.R > 1 && packet.Velocity < packet.nextVelocity) || packet.R < 10)
            packet.Velocity = packet.nextVelocity;

        if (packet.Velocity < minV) packet.Velocity = 0;
        if (packet.Velocity > maxV) packet.Velocity = maxV;

        if (packet.Dir != 0) {
            cart.motionX = Math.cos(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
            cart.motionZ = -Math.sin(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
        } else {
            packet.Velocity = Math.abs(cart.motionX / Math.cos(packet.Yaw * Math.PI / 180.0));
            if (Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0)) > packet.Velocity)
                packet.Velocity = Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0));
        }
    }

    public static void doMotionWithSlip(TrainPacket packet, Entity cart, double maxV) {
        calcYaw(packet, cart);

        double minV = 0.02;
        if (packet.P > 0 && packet.Velocity < minV)
            packet.Velocity = minV;

        double p = packet.P / 20.0, r = 1.0 - (packet.R - 1.0) / 9.0;
        packet.nextVelocity = Dynamics.LocoMotions.calcVelocityWithSlip(Math.abs(packet.Velocity), p, r, maxV, 0.05);

        if ((packet.R > 1 && packet.Velocity < packet.nextVelocity) || packet.R < 10)
            packet.Velocity = packet.nextVelocity;

        if (packet.Velocity < minV) packet.Velocity = 0;
        if (packet.Velocity > maxV) packet.Velocity = maxV;

        if (packet.Dir != 0) {
            cart.motionX = Math.cos(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
            cart.motionZ = -Math.sin(packet.Yaw * Math.PI / 180.0) * packet.Dir * packet.Velocity;
        } else {
            packet.Velocity = Math.abs(cart.motionX / Math.cos(packet.Yaw * Math.PI / 180.0));
            if (Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0)) > packet.Velocity)
                packet.Velocity = Math.abs(cart.motionZ / Math.sin(packet.Yaw * Math.PI / 180.0));
        }
    }

}
