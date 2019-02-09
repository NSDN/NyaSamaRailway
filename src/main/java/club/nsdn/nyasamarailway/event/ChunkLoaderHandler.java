package club.nsdn.nyasamarailway.event;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.entity.Entity;
import club.nsdn.nyasamarailway.api.cart.AbsLocoBase;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ChunkLoaderHandler implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.OrderedLoadingCallback, ForgeChunkManager.PlayerOrderedLoadingCallback {
    private static ChunkLoaderHandler instance;

    public static ChunkLoaderHandler instance() {
        if (instance == null)
            instance = new ChunkLoaderHandler();
        return instance;
    }

    @SubscribeEvent
    public void entityEnteredChunk(EntityEvent.EnteringChunk event) {
        Entity entity = event.getEntity();
        if (entity instanceof AbsLocoBase) {
            if (!entity.world.isRemote) {
                ((AbsLocoBase) entity).forceChunkLoading(event.getNewChunkX(), event.getNewChunkZ());
            } else {
                ((AbsLocoBase) entity).setupChunks(event.getNewChunkX(), event.getNewChunkZ());
            }
        }
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            if (ticket.isPlayerTicket())
                continue;
            Entity entity = ticket.getEntity();
            if (entity instanceof AbsLocoBase) {
                AbsLocoBase anchor = (AbsLocoBase) entity;
                anchor.setChunkTicket(ticket);
                anchor.forceChunkLoading(anchor.chunkCoordX, anchor.chunkCoordZ);
            }
        }
    }

    @Override
    public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount) {
        Set<ForgeChunkManager.Ticket> worldTickets = new HashSet<ForgeChunkManager.Ticket>();
        Set<ForgeChunkManager.Ticket> cartTickets = new HashSet<ForgeChunkManager.Ticket>();
        for (ForgeChunkManager.Ticket ticket : tickets) {
            Entity entity = ticket.getEntity();
            if (entity == null) {
                int y = ticket.getModData().getInteger("yCoord");
                if (y >= 0) {
                    worldTickets.add(ticket);
                }
            } else {
                if (entity instanceof AbsLocoBase) {
                    cartTickets.add(ticket);
                }
            }
        }

        List<ForgeChunkManager.Ticket> claimedTickets = new LinkedList<ForgeChunkManager.Ticket>();
        claimedTickets.addAll(cartTickets);
        claimedTickets.addAll(worldTickets);
        return claimedTickets;
    }

    @Override
    public ListMultimap<String, ForgeChunkManager.Ticket> playerTicketsLoaded(ListMultimap<String, ForgeChunkManager.Ticket> tickets, World world) {
        return LinkedListMultimap.create();
    }

}
