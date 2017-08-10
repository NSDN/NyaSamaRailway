package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.Items.Item74HC04;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.7.4.
 */
public class TileEntitySignalLight extends TileEntityBase {

    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_R = 1;
    private static final int LIGHT_Y = 2;
    private static final int LIGHT_G = 3;

    public static class SignalLight extends TileEntityRailReceiver {

        public String lightType= "red&green";
        public boolean isBlinking = false;
        public boolean isPowered = false;

        public int delay;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            lightType = tagCompound.getString("lightType");
            isBlinking = tagCompound.getBoolean("isBlinking");
            isPowered = tagCompound.getBoolean("isPowered");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setString("lightType", lightType);
            tagCompound.setBoolean("isBlinking", isBlinking);
            tagCompound.setBoolean("isPowered", isPowered);
            return super.toNBT(tagCompound);
        }

    }

    public TileEntitySignalLight() {
        super("SignalLight");
        setIconLocation("signal_light");
        setLightOpacity(0);
        setLightLevel(0.75F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new SignalLight();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        SignalLight signalLight = null;
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof SignalLight) {
            signalLight = (SignalLight) world.getTileEntity(x, y, z);

            if (player.isSneaking() && !world.isRemote) {
                if (signalLight.isBlinking) {
                    signalLight.isBlinking = false;
                    player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.blink.off"));
                } else {
                    signalLight.isBlinking = true;
                    player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.blink.on"));
                }
                updateLight(world, x, y, z);
                return true;
            }
            if (!player.isSneaking() && !world.isRemote) {
                if (player.getCurrentEquippedItem() == null) return false;
                if (player.getCurrentEquippedItem().getItem() instanceof Item74HC04) {
                    if (signalLight.lightType.equals("none")) {
                        signalLight.lightType = "red&off";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.red_off"));
                    } else if (signalLight.lightType.equals("red&off")) {
                        signalLight.lightType = "yellow&off";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.yellow_off"));
                    } else if (signalLight.lightType.equals("yellow&off")) {
                        signalLight.lightType = "green&off";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.green_off"));
                    } else if (signalLight.lightType.equals("green&off")) {
                        signalLight.lightType = "red&yellow";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.red_yellow"));
                    } else if (signalLight.lightType.equals("red&yellow")) {
                        signalLight.lightType = "red&green";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.red_green"));
                    } else if (signalLight.lightType.equals("red&green")) {
                        signalLight.lightType = "yellow&green";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.yellow_green"));
                    } else if (signalLight.lightType.equals("yellow&green")) {
                        signalLight.lightType = "white&blue";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.white_blue"));
                    } else if (signalLight.lightType.equals("white&blue")) {
                        signalLight.lightType = "none";
                        player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.none"));
                    }
                    updateLight(world, x, y, z);
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int tickRate(World world) {
        return 10;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            updateLight(world, x, y, z);
        }
    }

    public void updateLight(World world, int x ,int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof SignalLight) {
            SignalLight signalLight = (SignalLight) world.getTileEntity(x, y, z);
            boolean isEnable;
            if (signalLight.getSenderRail() == null) {
                isEnable = nearbyBlockIsPowered(world, x, y, z) || signalLight.isPowered;
            } else {
                isEnable = signalLight.senderRailIsPowered() ^ thisBlockIsPowered(world, x, y, z);
            }
            int meta = world.getBlockMetadata(x, y, z);
            int old = meta;

            meta = meta & 0x3;
            if (signalLight.isBlinking) {
                if (signalLight.delay > 10) {
                    if (signalLight.delay < 20) {
                        signalLight.delay += 1;
                    } else {
                        signalLight.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, signalLight.lightType);
                } else {
                    signalLight.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, signalLight.lightType);
            }

            if (old != meta) {
                if (((meta >> 2) & 0x3) == 0) {
                    setLightLevel(0.0F);
                } else {
                    setLightLevel(0.75F);
                }
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public int setLightState(boolean isEnable, int meta, String type) {
        String lightType = type.toLowerCase();
        if (lightType.equals("red&off"))
            return isEnable ? meta | (LIGHT_R << 2) : meta;
        else if (lightType.equals("yellow&off"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta;
        else if (lightType.equals("green&off"))
            return isEnable ? meta | (LIGHT_G << 2) : meta;
        else if (lightType.equals("red&yellow"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_Y << 2);
        else if (lightType.equals("red&green"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("yellow&green"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("white&blue"))
            return isEnable ? meta | (2 << 2) : meta | (3 << 2);
        else
            return meta;
    }

    public boolean nearbyBlockIsPowered(World world, int x, int y, int z) {
        return (
            world.isBlockIndirectlyGettingPowered(x, y, z) ||
            world.isBlockIndirectlyGettingPowered(x + 1, y, z) ||
            world.isBlockIndirectlyGettingPowered(x - 1, y, z) ||
            world.isBlockIndirectlyGettingPowered(x, y, z + 1) ||
            world.isBlockIndirectlyGettingPowered(x, y, z - 1) ||
            world.isBlockIndirectlyGettingPowered(x, y - 1, z)
        );
    }

    public boolean thisBlockIsPowered(World world, int x, int y, int z) {
        return (
            world.isBlockIndirectlyGettingPowered(x, y, z) ||
            world.isBlockIndirectlyGettingPowered(x, y - 1, z)
        );
    }

}
