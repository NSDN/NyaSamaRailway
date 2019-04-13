package club.nsdn.nyasamarailway.api.rail;

import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.tileblock.rail.RailEndpoint;
import club.nsdn.nyasamarailway.util.Vertex;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import cn.ac.nya.forgeobj.Face;
import cn.ac.nya.forgeobj.GroupObject;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.thewdj.spline.Spline;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.3.17.
 */
public class TileEntityRailEndpoint extends TileEntityActuator {

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        if (oldState.getBlock() == newState.getBlock()) {
            if (
                oldState.getPropertyKeys().contains(RailEndpoint.TYPE) &&
                newState.getPropertyKeys().contains(RailEndpoint.TYPE)
            ) {
                return oldState.getValue(RailEndpoint.TYPE) != newState.getValue(RailEndpoint.TYPE);
            }
        }
        return super.shouldRefresh(world, pos, oldState, newState);
    }

    @SideOnly(Side.CLIENT)
    protected LinkedList<Vertex> cookedVertices;

    @SideOnly(Side.CLIENT)
    public double getRenderStep() { return 0.5; }

    @SideOnly(Side.CLIENT)
    public LinkedList<Vertex> getVertices() {
        if (cookedVertices == null)
            cookedVertices = new LinkedList<>();
        return cookedVertices;
    }

    @SideOnly(Side.CLIENT)
    public void cookVertices(@Nonnull WavefrontObject model) {
        Vec3d vec, nex; double step = this.getRenderStep();
        for (double d = 0; d <= this.len(); d += step) {
            if (d == this.len()) {
                vec = this.get(d - step / 100.0);
                nex = this.get(d);
                nex = nex.subtract(vec);
                vec = this.get(d);
            } else {
                vec = this.get(d);
                nex = this.get(d + step);
                nex = nex.subtract(vec);
            }

            double yaw = Math.atan2(nex.z, nex.x);
            double hlen = Math.sqrt(nex.x * nex.x + nex.z * nex.z);
            double pitch = Math.atan(nex.y / hlen);

            for (GroupObject group : model.groupObjects) {
                for (Face face : group.faces) {
                    for (int i = 0; i < face.vertices.length; ++i) {
                        if (face.textureCoordinates != null && face.textureCoordinates.length > 0) {
                            Vertex vertex = new Vertex();

                            Vec3d pos = new Vec3d((double) face.vertices[i].x, (double) face.vertices[i].y, (double) face.vertices[i].z);
                            pos = CartUtil.rotatePitchFix(pos, (float) -pitch).rotateYaw((float) -yaw).add(vec);

                            vertex.pos(pos.x, pos.y, pos.z)
                                    .tex((double) face.textureCoordinates[i].u, (double) face.textureCoordinates[i].v)
                                    .nor(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z);

                            this.cookedVertices.add(vertex);
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void clearVertices() {
        cookedVertices.clear();
    }

    boolean inv = false;
    Spline hline = new Spline();
    Spline vline = new Spline();

    Vec3d origin = Vec3d.ZERO;
    LinkedList<Vec3d> points = new LinkedList<>();

    public void clear() { points.clear(); }

    static double len(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    public EnumFacing.Axis axis() {
        return inv ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
    }

    public double len() {
        if (points.isEmpty()) return 0.0;
        if (points.size() <= 2) return 0.0;
        if (!inv)
            return Math.abs(points.peekLast().x - points.peekFirst().x);
        else
            return Math.abs(points.peekLast().z - points.peekFirst().z);
    }

    public Vec3d get(double cnt) {
        double x, y, z;

        if (points.size() <= 2) return Vec3d.ZERO;

        if (!inv) {
            double sign = Math.signum(points.peekLast().x - points.peekFirst().x);
            x = points.peekFirst().x + cnt * sign;
            z = hline.get(x);
        } else {
            double sign = Math.signum(points.peekLast().z - points.peekFirst().z);
            z = points.peekFirst().z + cnt * sign;
            x = hline.get(z);
        }
        y = vline.get(len(x, z));

        return new Vec3d(x, y, z).add(origin);
    }

    public TileEntityRailEndpoint parseNext() {
        TileEntity te = getTarget();
        while (true) {
            if (te instanceof TileEntityActuator) {
                TileEntityActuator actuator = (TileEntityActuator) te;

                if (actuator instanceof TileEntityRailEndpoint)
                    return (TileEntityRailEndpoint) actuator;

                if (te == this) break;

                te = ((TileEntityActuator) te).getTarget();
            } else
                break;
        }
        return null;
    }

    void makeSplines() {
        if (points.size() <= 2) return;

        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();
        ArrayList<Double> z = new ArrayList<>();
        ArrayList<Double> h = new ArrayList<>();
        for (Vec3d vec : points) {
            x.add(vec.x); y.add(vec.y); z.add(vec.z);
            h.add(len(vec.x, vec.z));
        }

        if (!inv) {
            hline.set_points(x, z);
        } else {
            hline.set_points(z, x);
        }

        vline.set_points(h, y);
    }

    public void updateRoute() {
        points.clear();

        TileEntity te = this; double offset = 0.5;
        while (true) {
            if (te instanceof TileEntityActuator) {
                TileEntityActuator actuator = (TileEntityActuator) te;
                Vec3d vec = new Vec3d(actuator.getPos());
                vec = vec.addVector(offset, offset, offset);
                if (points.contains(vec)) break;
                points.add(vec);
                te = ((TileEntityActuator) te).getTarget();
            } else
                break;
        }

        if (points.size() <= 2) return;

        Vec3d orig = new Vec3d(this.getPos());
        for (int i = 0; i < points.size(); i++) {
            Vec3d vec = points.get(i);
            points.set(i, vec.subtract(orig));
        }
        this.origin = orig;

        Vec3d last = points.peekLast();
        if (last.x < 0 || last.y < 0 || last.z < 0) {
            this.origin = last.add(orig);
            for (int i = 0; i < points.size(); i++) {
                Vec3d vec = points.get(i);
                points.set(i, vec.subtract(last));
            }
        }

        inv = Math.abs(points.peekLast().x - points.peekFirst().x) <= Math.abs(points.peekLast().z - points.peekFirst().z);

        /*Vec3d first = points.peekFirst(), fnext = points.get(points.indexOf(first) + 1);
        Vec3d last = points.peekLast(), lnext = points.get(points.indexOf(last) - 1);
        Vec3d vecf = fnext.subtract(first), vecl = last.subtract(lnext);
        points.addFirst(first.add(vecf.scale(-offset)));
        points.addLast(last.add(vecl.scale(offset)));*/

        makeSplines();
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("railEndpoint", 0);

        //hline.toNBT(tagCompound, "hLine_");
        //vline.toNBT(tagCompound, "vLine_");

        for (int i = 0; i < points.size(); i++) {
            tagCompound.setDouble("point_" + i + "_X", points.get(i).x);
            tagCompound.setDouble("point_" + i + "_Y", points.get(i).y);
            tagCompound.setDouble("point_" + i + "_Z", points.get(i).z);
        }

        tagCompound.setDouble("originX", origin.x);
        tagCompound.setDouble("originY", origin.y);
        tagCompound.setDouble("originZ", origin.z);

        tagCompound.setBoolean("inv", inv);

        return super.toNBT(tagCompound);
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);

        tagCompound.getInteger("railEndpoint");

        //hline.fromNBT(tagCompound, "hLine_");
        //vline.fromNBT(tagCompound, "vLine_");

        double x, y, z;
        points.clear();
        for (int i = 0; tagCompound.hasKey("point_" + i + "_X"); i++) {
            x = tagCompound.getDouble("point_" + i + "_X");
            y = tagCompound.getDouble("point_" + i + "_Y");
            z = tagCompound.getDouble("point_" + i + "_Z");
            points.add(i, new Vec3d(x, y, z));
        }

        x = tagCompound.getDouble("originX");
        y = tagCompound.getDouble("originY");
        z = tagCompound.getDouble("originZ");
        origin = new Vec3d(x, y, z);

        inv = tagCompound.getBoolean("inv");

        makeSplines();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 32768.0;
    }

}
