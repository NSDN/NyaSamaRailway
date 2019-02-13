package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamaoptics.tileblock.screen.LEDPlate;
import club.nsdn.nyasamarailway.api.cart.AbsCartBase;
import club.nsdn.nyasamarailway.api.cart.AbsLocoBase;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by drzzm32 on 2019.2.12
 */
public class NTPCore implements LEDPlate.ISpecialCommand {

    private int counter = 0;

    public final int DEFAULT_LEN = 16, DEFAULT_RANGE = 32;

    @Override
    public String runCmd(World world, BlockPos pos, String buffer, String[] args) {
        counter += 1;
        if (counter < 20) return buffer;
        counter = 0;

        int len = DEFAULT_LEN;
        double range = DEFAULT_RANGE;
        double rangeY = DEFAULT_RANGE;
        if (args.length > 1) {
            int tmp;
            try { tmp = Integer.parseInt(args[1]); }
            catch (Exception e) { tmp = DEFAULT_LEN; }
            len = tmp;
            if (args.length > 2) {
                try { tmp = Integer.parseInt(args[2]); }
                catch (Exception e) { tmp = DEFAULT_RANGE; }
                range = rangeY = tmp;
                if (args.length > 3) {
                    try { tmp = Integer.parseInt(args[3]); }
                    catch (Exception e) { tmp = DEFAULT_RANGE; }
                    rangeY = tmp;
                }
            }
        }

        String[] raw = buffer.split("\n");
        String[] lines = new String[len];
        System.arraycopy(Arrays.copyOf(raw, len), 1, lines, 0, len - 1);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) lines[i] = "";
        }
        AxisAlignedBB aabb = Block.FULL_BLOCK_AABB;
        aabb = aabb.offset(pos).expand(range, rangeY, range).expand(-range, -rangeY, -range);
        int totalCart = world.getEntitiesWithinAABB(EntityMinecart.class, aabb).size();
        int nsrCart = world.getEntitiesWithinAABB(AbsCartBase.class, aabb).size();
        int nsrLoco = world.getEntitiesWithinAABB(AbsLocoBase.class, aabb).size();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        lines[len - 1] = String.format("[%02d:%02d:%02d] [NTP] range: %d (y: %d), total: %d, cart: %d, loco: %d, etc: %s",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                (int) range, (int) rangeY, totalCart, nsrCart, nsrLoco, world.getBiome(pos).getBiomeName()
        );

        StringBuilder builder = new StringBuilder();
        for (String s : lines)
            builder.append(s).append("\n");

        return builder.toString();
    }

}
