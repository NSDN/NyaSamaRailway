package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.signal.ITrackSide;
import club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight;
import club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideReception;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.tileblock.func.*;
import club.nsdn.nyasamarailway.tileblock.rail.ConvWireMono;
import club.nsdn.nyasamarailway.tileblock.signal.deco.GateFront;
import club.nsdn.nyasamatelecom.api.device.*;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.UUID;

import static club.nsdn.nyasamatelecom.api.util.Util.say;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class Item1N4148 extends ToolBase {

    public static LinkedHashMap<UUID, TileEntityTransceiver> tmpRails;

    public Item1N4148() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item1N4148");
        setRegistryName(NyaSamaRailway.MODID, "1n4148");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);

        tmpRails = new LinkedHashMap<>();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return EnumActionResult.PASS;

        if (player.isSneaking()) {
            if (tileEntity instanceof TileEntityTransceiver) {
                TileEntityTransceiver thisRail = (TileEntityTransceiver) tileEntity;

                if (!world.isRemote) {
                    UUID uuid = player.getUniqueID();
                    if (tmpRails.containsKey(uuid)) {
                        if (tmpRails.get(uuid) == thisRail) {
                            thisRail.setTransceiver(null);
                            say(player, "info.blocking.abort");
                        } else {
                            if (thisRail.getTransceiver() == tmpRails.get(uuid)) {
                                thisRail.getTransceiver().setTransceiver(null);
                                thisRail.setTransceiver(null);
                                say(player, "info.blocking.disconnected");

                            } else {
                                thisRail.setTransceiver(tmpRails.get(uuid));
                                thisRail.getTransceiver().setTransceiver(thisRail);
                                say(player, "info.blocking.connected");
                            }
                        }
                        tmpRails.remove(uuid);
                    } else {
                        tmpRails.put(uuid, thisRail);
                        say(player, "info.blocking.begin");
                    }

                    return EnumActionResult.SUCCESS;
                }
            } else if (tileEntity instanceof TileEntityTrackSideReception) {
                TileEntityTrackSideReception reception = (TileEntityTrackSideReception) tileEntity;

                if (reception.cartType.isEmpty()) {
                    reception.cartType = "loco";
                    say(player, "info.reception.loco");
                } else {
                    if (reception.cartType.equals("loco")) {
                        reception.cartType = "";
                        say(player, "info.reception.reset");
                    } else {
                        reception.cartType = "";
                        reception.extInfo = "";
                        say(player, "info.reception.cleared");
                    }
                }

                return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            } else if (tileEntity instanceof CoinBlock.TileEntityCoinBlock) {
                CoinBlock.TileEntityCoinBlock coinBlock = (CoinBlock.TileEntityCoinBlock) tileEntity;
                if (coinBlock.value <= 0) coinBlock.value = 1;

                switch (coinBlock.value) {
                    case 1:
                        coinBlock.value = 5;
                        break;
                    case 5:
                        coinBlock.value = 10;
                        break;
                    case 10:
                        coinBlock.value = 20;
                        break;
                    case 20:
                        coinBlock.value = 50;
                        break;
                    case 50:
                        coinBlock.value = 1;
                        break;
                }
                say(player, "info.coin.value", coinBlock.value);

                return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            } else if (tileEntity instanceof TicketBlockOnce.TileEntityTicketBlockOnce) {
                TicketBlockOnce.TileEntityTicketBlockOnce ticketBlock = (TicketBlockOnce.TileEntityTicketBlockOnce) tileEntity;

                switch (ticketBlock.setOver) {
                    case 1:
                        ticketBlock.setOver = 5;
                        break;
                    case 5:
                        ticketBlock.setOver = 10;
                        break;
                    case 10:
                        ticketBlock.setOver = 20;
                        break;
                    case 20:
                        ticketBlock.setOver = 50;
                        break;
                    case 50:
                        ticketBlock.setOver = 1;
                        break;
                }
                say(player, "info.ticket.over", ticketBlock.setOver);

                return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            } else if (tileEntity instanceof GateFront.TileEntityGateFront) {
                GateFront.TileEntityGateFront gateFront = (GateFront.TileEntityGateFront) tileEntity;

                switch (gateFront.setOver) {
                    case 1:
                        gateFront.setOver = 5;
                        break;
                    case 5:
                        gateFront.setOver = 10;
                        break;
                    case 10:
                        gateFront.setOver = 20;
                        break;
                    case 20:
                        gateFront.setOver = 50;
                        break;
                    case 50:
                        gateFront.setOver = 1;
                        break;
                }
                say(player, "info.gate.over", gateFront.setOver);

                return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            } else if (tileEntity instanceof ConvWireMono.TileEntityConvWireMono) {
                ConvWireMono.TileEntityConvWireMono.switchState(world, pos);
            }
        }

        if (tileEntity instanceof ITrackSide) {
            ITrackSide trackSide = (ITrackSide) tileEntity;

            if (trackSide.hasInvert()) {
                trackSide.flipInvert();
                say(player, "info.trackside.invert");

                return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            }
        } else if (tileEntity instanceof TriStateSignalBox.TileEntityTriStateSignalBox) {
            TriStateSignalBox.TileEntityTriStateSignalBox signalBox = (TriStateSignalBox.TileEntityTriStateSignalBox) tileEntity;

            if (player.isSneaking()) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    say(player, "info.signal.box.inverter.off");
                } else {
                    signalBox.inverterEnabled = true;
                    say(player, "info.signal.box.inverter.on");
                }
            } else {
                signalBox.prevTriStateIsNeg = signalBox.triStateIsNeg;
                if (signalBox.triStateIsNeg) {
                    signalBox.triStateIsNeg = false;
                    say(player, "info.signal.box.triState.pos");
                } else {
                    signalBox.triStateIsNeg = true;
                    say(player, "info.signal.box.triState.neg");
                }
            }

            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        } else if (tileEntity instanceof SignalBox.TileEntitySignalBox) {
            SignalBox.TileEntitySignalBox signalBox = (SignalBox.TileEntitySignalBox) tileEntity;

            if (player.isSneaking()) {
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    say(player, "info.signal.box.inverter.off");
                } else {
                    signalBox.inverterEnabled = true;
                    say(player, "info.signal.box.inverter.on");
                }

                return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            }
        } else if (tileEntity instanceof TileEntitySignalLight) {
            TileEntitySignalLight signalLight = (TileEntitySignalLight) tileEntity;

            if (player.isSneaking()) {
                signalLight.prevLightType = signalLight.lightType;
                switch (signalLight.lightType) {
                    case "none":
                        signalLight.lightType = "red&off";
                        say(player, "info.signal.light.mode.red_off");
                        break;
                    case "red&off":
                        signalLight.lightType = "yellow&off";
                        say(player, "info.signal.light.mode.yellow_off");
                        break;
                    case "yellow&off":
                        signalLight.lightType = "green&off";
                        say(player, "info.signal.light.mode.green_off");
                        break;
                    case "green&off":
                        signalLight.lightType = "white&off";
                        say(player, "info.signal.light.mode.white_off");
                        break;
                    case "white&off":
                        signalLight.lightType = "blue&off";
                        say(player, "info.signal.light.mode.blue_off");
                        break;
                    case "blue&off":
                        signalLight.lightType = "purple&off";
                        say(player, "info.signal.light.mode.purple_off");
                        break;
                    case "purple&off":
                        signalLight.lightType = "red&yellow";
                        say(player, "info.signal.light.mode.red_yellow");
                        break;
                    case "red&yellow":
                        signalLight.lightType = "red&green";
                        say(player, "info.signal.light.mode.red_green");
                        break;
                    case "red&green":
                        signalLight.lightType = "yellow&green";
                        say(player, "info.signal.light.mode.yellow_green");
                        break;
                    case "yellow&green":
                        signalLight.lightType = "white&blue";
                        say(player, "info.signal.light.mode.white_blue");
                        break;
                    case "white&blue":
                        signalLight.lightType = "yellow&purple";
                        say(player, "info.signal.light.mode.yellow_purple");
                        break;
                    case "yellow&purple":
                        signalLight.lightType = "none";
                        say(player, "info.signal.light.mode.none");
                        break;
                    default:
                        break;
                }
            } else {
                if (signalLight.isBlinking) {
                    signalLight.isBlinking = false;
                    say(player, "info.signal.light.blink.off");
                } else {
                    signalLight.isBlinking = true;
                    say(player, "info.signal.light.blink.on");
                }
            }

            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

}
