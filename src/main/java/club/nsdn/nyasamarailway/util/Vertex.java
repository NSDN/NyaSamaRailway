package club.nsdn.nyasamarailway.util;

import net.minecraft.client.renderer.BufferBuilder;

/**
 * Created by drzzm32 on 2019.3.19.
 */
public class Vertex {

    private double x, y, z;
    private double u, v;
    private float a, b, c;

    public Vertex() {
        x = y = z = 0;
        u = v = 0;
        a = b = c = 0;
    }

    public Vertex pos(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
        return this;
    }

    public Vertex tex(double u, double v) {
        this.u = u; this.v = v;
        return this;
    }

    public Vertex nor(float a, float b, float c) {
        this.a = a; this.b = b; this.c = c;
        return this;
    }

    public void push(BufferBuilder buffer) {
        buffer.pos(x, y, z).tex(u, v).normal(a, b, c).endVertex();
    }

}
