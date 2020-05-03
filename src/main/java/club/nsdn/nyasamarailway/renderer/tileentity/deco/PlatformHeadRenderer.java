package club.nsdn.nyasamarailway.renderer.tileentity.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.deco.PlatformHead;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.NyaSamaLib;
import cn.ac.nya.forgeobj.WavefrontObject;
import cn.ac.nya.sler.SLER;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by drzzm32 on 2020.2.20
 */
public class PlatformHeadRenderer extends AbsTileEntitySpecialRenderer {

    protected static InputStream getImageStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            }
        } catch (IOException e) {
            NyaSamaRailway.logger.error(e.getMessage());
        }
        return null;
    }

    protected static class Texture extends AbstractTexture {

        protected final InputStream stream;
        protected BufferedImage image = null;

        public Texture(InputStream stream) {
            this.stream = stream;
        }

        public Texture(BufferedImage image) {
            this.stream = null;
            this.image = image;
        }

        public void preLoadTexture() {
            if (stream == null) return;
            try {
                image = TextureUtil.readBufferedImage(stream);
            } catch (Exception e) {
                NyaSamaRailway.logger.error(e.getMessage());
            }
        }

        public void loadTexture(@Nonnull IResourceManager manager) {
            this.deleteGlTexture();
            if (image == null) return;
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image,
                    false, false);
        }
    }

    protected static class SafePool<K, V> extends LinkedHashMap<K, V> {

        private final ReentrantLock lock = new ReentrantLock();

        public SafePool() { super(); }

        @Override
        public boolean containsKey(Object key) {
            if (lock.tryLock()) {
                try {
                    return super.containsKey(key);
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        @Override
        public V put(K key, V value) {
            try {
                lock.lock();
                return super.put(key, value);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public V get(Object key) {
            try {
                lock.lock();
                return super.get(key);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public V remove(Object key) {
            try {
                lock.lock();
                return super.remove(key);
            } finally {
                lock.unlock();
            }
        }
    }

    protected static class SafeList<T> extends ArrayList<T> {

        private final ReentrantLock lock = new ReentrantLock();

        public SafeList() { super(); }

        @Override
        public boolean contains(Object o) {
            if (lock.tryLock()) {
                try {
                    return super.contains(o);
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        @Override
        public T remove(int index) {
            try {
                lock.lock();
                return super.remove(index);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean add(T t) {
            try {
                lock.lock();
                return super.add(t);
            } finally {
                lock.unlock();
            }
        }
    }

    public static ScheduledThreadPoolExecutor executor = NyaSamaLib.EXECUTOR;
    public static SafePool<BlockPos, Texture> textures = new SafePool<>();
    public static SafeList<BlockPos> stack = new SafeList<>();

    public static void removeTexture(PlatformHead.TileEntityPlatformHead head) {
        BlockPos pos = head.getPos();
        if (textures.containsKey(pos))
            textures.get(pos).deleteGlTexture();
        textures.remove(pos);
    }

    public static Texture getTexture(PlatformHead.TileEntityPlatformHead head) {
        BlockPos pos = head.getPos();
        if (textures.containsKey(pos)) {
            Texture texture = textures.get(pos);
            texture.loadTexture(Minecraft.getMinecraft().getResourceManager());
            return texture;
        } else if (!stack.contains(pos)) {
            stack.add(pos);
            String url = head.url;
            executor.schedule(() -> {
                Texture texture = new Texture(getImageStream(url));
                texture.preLoadTexture();
                textures.put(pos, texture);
                stack.remove(pos);
            }, 10, TimeUnit.MILLISECONDS);
        }
        return null;
    }

    private static final ResourceLocation RES_YAN = new ResourceLocation("nyasamarailway", "fonts/yan.otf");
    private static final ResourceLocation RES_SONG = new ResourceLocation("nyasamarailway", "fonts/song.otf");
    private static final ResourceLocation RES_HEI = new ResourceLocation("nyasamarailway", "fonts/hei.otf");
    private static final Font yan, song, hei;
    private static Font copyFont(Font font) { return font.deriveFont(16.0F); }
    private static Font loadFont(ResourceLocation loc, Font def) {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        Font font;
        try {
            IResource res = manager.getResource(loc);
            font = Font.createFont(Font.TRUETYPE_FONT, res.getInputStream());
        } catch (Exception e) {
            NyaSamaRailway.logger.info("Font not found: " + loc);
            font = copyFont(def);
        }
        return font;
    }

    private static Font getFont(String name) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (Font f : ge.getAllFonts())
            if (f.getName().equalsIgnoreCase(name))
                return f.deriveFont(Font.BOLD);
        for (Font f : ge.getAllFonts())
            if (f.getName().equalsIgnoreCase("Arial"))
                return f.deriveFont(Font.BOLD);
        return null;
    }

    static {
        yan = loadFont(RES_YAN, getFont("\u6977\u4F53"));
        song = loadFont(RES_SONG, getFont("\u5B8B\u4F53"));
        hei = loadFont(RES_HEI, getFont("\u9ED1\u4F53"));
    }

    public static Texture genTexture(PlatformHead.TileEntityPlatformHead head) {
        BlockPos pos = head.getPos();
        if (textures.containsKey(pos)) {
            Texture texture = textures.get(pos);
            texture.loadTexture(Minecraft.getMinecraft().getResourceManager());
            return texture;
        } else if (!stack.contains(pos)) {
            stack.add(pos);

            String[] args;
            if (head.isDir)
                args = SLER.arg(head.dirHead, head.dirLine, head.dirLineSub, head.dirTarget, head.dirTargetSub);
            else
                args = SLER.arg(
                        head.nowMain, head.nowSub,
                        head.prevTop, head.prevMain, head.prevSub,
                        head.nextTop, head.nextMain, head.nextSub,
                        head.prevMainS, head.prevSubS,
                        head.nextMainS, head.nextSubS
                );

            String[] arg = args;
            Color pri = new Color(head.colorPri), sec = new Color(head.colorSec), acc = new Color(head.colorAcc);
            Font fontPri = head.isDir ? copyFont(song) : copyFont(yan), fontSec = copyFont(hei);
            boolean right = head.right, wide = head.wide;

            executor.schedule(() -> {
                BufferedImage image = SLER.gen(arg, pri, sec, acc, fontPri, fontSec, right, wide);
                Texture texture = new Texture(image);
                texture.preLoadTexture();

                textures.put(pos, texture);
                stack.remove(pos);
            }, 10, TimeUnit.MILLISECONDS);
        }
        return null;
    }

    private final WavefrontObject model = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/platform_head_frame.obj")
    );
    private final WavefrontObject modelWide = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/platform_head_frame_wide.obj")
    );
    private final ResourceLocation defTexture = new ResourceLocation(
            "nyasamarailway", "textures/blocks/platform_head_frame.png");

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        if (te instanceof PlatformHead.TileEntityPlatformHead) {
            PlatformHead.TileEntityPlatformHead head = (PlatformHead.TileEntityPlatformHead) te;
            if (!head.enabled) {
                head.texture = null;
                removeTexture(head);
                return;
            }

            if (head.texture == null) {
                switch (head.use) {
                    case PlatformHead.TileEntityPlatformHead.USE_MOD:
                        if (head.url.toLowerCase().equals("null"))
                            head.texture = defTexture;
                        else
                            head.texture = new ResourceLocation(head.url);
                        break;
                    case PlatformHead.TileEntityPlatformHead.USE_WEB:
                        head.texture = getTexture(head);
                        break;
                    case PlatformHead.TileEntityPlatformHead.USE_GEN:
                        head.texture = genTexture(head);
                        break;
                }
            }

            int meta = te.getBlockMetadata();
            if ((meta % 2) == 0) meta = 2 - meta;
            boolean isHalf = false;
            if (te.getBlockType() instanceof PlatformHead)
                isHalf = ((PlatformHead) te.getBlockType()).isHalf;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

            int angle = (meta & 0x3) * 90;

            TextureManager manager = Minecraft.getMinecraft().getTextureManager();
            Object texture = head.texture;
            if (texture instanceof ResourceLocation)
                manager.bindTexture((ResourceLocation) texture);
            else if (texture instanceof Texture)
                GlStateManager.bindTexture(((Texture) texture).getGlTextureId());
            else
                manager.bindTexture(defTexture);

            GL11.glPushMatrix();
            GL11.glTranslatef(0, isHalf ? 0.5F : 0.0F, 0);
            GL11.glPushMatrix();
            GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
            GL11.glPushMatrix();
            GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
            (head.wide ? modelWide : model).renderAll();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }
    }

}
