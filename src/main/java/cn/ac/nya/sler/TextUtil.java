package cn.ac.nya.sler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class TextUtil {

    private static final boolean DEBUG = false;
    private static final int BUF_SIZE = 256;
    private static final float FACTOR = 4.0F / 3.0F;

    public enum TextAlign {
        LEFT, RIGHT, CENTER
    }

    public static class Bitmap extends BufferedImage {
        public Bitmap(int w, int h) {
            super(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        public Bitmap crop(int x, int y, int w, int h) {
            BufferedImage image = this.getSubimage(x, y, w, h);
            Bitmap bitmap = new Bitmap(w, h);
            image.copyData(bitmap.getRaster());
            return bitmap;
        }
    }

    private static class RawBmp {
        private Raster data;
        private int width, height;

        public RawBmp(Bitmap bmp) {
            width = bmp.getWidth(); height = bmp.getHeight();
            data = bmp.getData();
        }

        public boolean trans(int u, int v) {
            if (u >= width || v >= height)
                return true;
            int[] pixel = data.getPixel(u, v, new int[4]);
            return pixel[3] == 0;
        }
    }

    public static void ConfigureGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }

    private static Bitmap GetString(String str, Color color, Font font) {
        if (str.isEmpty())
            return new Bitmap(1, 1);

        Bitmap bitmap = new Bitmap(1024, 1024);
        Graphics2D g = bitmap.createGraphics();
        ConfigureGraphics(g);
        g.setFont(font);
        g.setColor(color);
        g.drawString(str, 64, 64);
        g.dispose();

        RawBmp raw = new RawBmp(bitmap);

        int left, right, up, down;
        for (left = 0; left < bitmap.getWidth(); left++) {
            int i;
            for (i = 0; i < bitmap.getHeight(); i++)
                if (!raw.trans(left, i))
                    break;
            if (i >= bitmap.getHeight())
                continue;
            if (!raw.trans(left, i))
                break;
        }
        for (right = bitmap.getWidth() - 1; right > 0; right--) {
            int i;
            for (i = 0; i < bitmap.getHeight(); i++)
                if (!raw.trans(right, i))
                    break;
            if (i >= bitmap.getHeight())
                continue;
            if (!raw.trans(right, i))
                break;
        }
        for (up = 0; up < bitmap.getHeight(); up++) {
            int i;
            for (i = 0; i < bitmap.getWidth(); i++)
                if (!raw.trans(i, up))
                    break;
            if (i >= bitmap.getWidth())
                continue;
            if (!raw.trans(i, up))
                break;
        }
        for (down = bitmap.getHeight() - 1; down > 0; down--) {
            int i;
            for (i = 0; i < bitmap.getWidth(); i++)
                if (!raw.trans(i, down))
                    break;
            if (i >= bitmap.getWidth())
                continue;
            if (!raw.trans(i, down))
                break;
        }

        int width = right - left + 1, height = down - up + 1;
        if (width <= 0 || height <= 0)
            return new Bitmap(1, 1);

        return bitmap.crop(left, up, width, height);
    }

    private static void DrawImage(Graphics2D g, Image image, int x, int y, TextAlign align) {
        switch (align) {
            case LEFT:
                break;
            case RIGHT:
                x -= image.getWidth(null);
                break;
            case CENTER:
                x -= (image.getWidth(null) / 2);
                break;
        }

        g.drawImage(image, x, y, null);
    }

    private static Rectangle DrawString(Graphics2D g, String str, Color color, Font font, int x, int y, TextAlign align) {
        Bitmap bmp = GetString(str, color, font);
        DrawImage(g, bmp, x, y, align);
        return new Rectangle(bmp.getWidth(), bmp.getHeight());
    }

    private static void DrawAccentString(Graphics2D g, String str, Color color, Font font, int x, int y, TextAlign align) {
        DrawAccentString(g, str, color, font, x, y, align, 4);
    }

    private static void DrawAccentString(Graphics2D g, String str, Color color, Font font, int x, int y, TextAlign align, int border) {
        Font fnt = font.deriveFont(Font.BOLD);
        Bitmap bmp = GetString(str, new Color(250, 250, 250, 255), fnt);
        int u = 0, v = y, w = bmp.getWidth(), h = bmp.getHeight();
        switch (align) {
            case LEFT:
                u = x;
                break;
            case RIGHT:
                u = x - w;
                break;
            case CENTER:
                u = x - (w / 2);
                break;
        }
        int xOffset = 0;
        if (w < h) {
            xOffset = (h - w) / 2;
            w = h;
        }
        g.setColor(color);
        g.fillRect(u - border, v - border, w + border * 2, h + border * 2);
        DrawImage(g, bmp, x + xOffset, y, align);
    }

    public static Bitmap GenPrimaryStationLabel(String main, String sub, Color color, Font pri, Font sec) {
        Bitmap bitmap = new Bitmap(BUF_SIZE * 2, BUF_SIZE);
        Graphics2D g = bitmap.createGraphics();
        ConfigureGraphics(g);
        if (DEBUG) {
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, BUF_SIZE * 2, BUF_SIZE);
        }
        Font fnt = pri.deriveFont(FACTOR * 48.0F);
        DrawString(g, main, color, fnt, BUF_SIZE, 64, TextAlign.CENTER);
        fnt = sec.deriveFont(FACTOR * 20.0F);
        DrawString(g, sub, color, fnt, BUF_SIZE, 128, TextAlign.CENTER);
        g.dispose();

        return bitmap;
    }

    public static Bitmap GenSecondaryStationLabel(String main, String sub, String top, Color color, Font pri, Font sec, TextAlign align) {
        Bitmap bitmap = new Bitmap(BUF_SIZE, BUF_SIZE);
        Graphics2D g = bitmap.createGraphics();
        ConfigureGraphics(g);
        int x = 0;
        switch (align) {
            case LEFT:
                x = 0;
                break;
            case RIGHT:
                x = BUF_SIZE;
                break;
            case CENTER:
                x = BUF_SIZE / 2;
                break;
        }

        if (DEBUG) {
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, BUF_SIZE, BUF_SIZE);
        }

        Font fnt = sec.deriveFont(FACTOR * 16.0F);
        DrawString(g, top, color, fnt, x, 64, align);
        fnt = pri.deriveFont(FACTOR * 24.0F);
        DrawString(g, main, color, fnt, x, 92, align);
        fnt = sec.deriveFont(FACTOR * 16.0F);
        DrawString(g, sub, color, fnt, x, 128, align);
        g.dispose();

        return bitmap;
    }

    public static Bitmap GenLineLabel(String head, String main, String sub, Color color, Color accent, Font pri, Font sec, TextAlign align) {
        final int BORDER = 4;
        Bitmap bitmap = new Bitmap(BUF_SIZE, BUF_SIZE);
        Graphics2D g = bitmap.createGraphics();
        ConfigureGraphics(g);
        int x = 0;
        switch (align) {
            case LEFT:
                x = BORDER;
                break;
            case RIGHT:
                x = BUF_SIZE;
                break;
            case CENTER:
                x = BUF_SIZE / 2;
                break;
        }

        if (DEBUG) {
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, BUF_SIZE, BUF_SIZE);
        }

        Font fnt = pri.deriveFont(FACTOR * 24.0F);
        Rectangle size = DrawString(g, main, color, fnt, x, 92, align);
        fnt = sec.deriveFont(FACTOR * 16.0F);
        DrawString(g, sub, color, fnt, x, 128, align);

        switch (align) {
            case LEFT:
                x = BORDER;
                break;
            case RIGHT:
                x = x - (int) size.getWidth();
                break;
            case CENTER:
                x = x - ((int) size.getHeight() / 2);
                break;
        }

        fnt = pri.deriveFont(FACTOR * 24.0F);
        Bitmap str = GetString(head, color, fnt);
        int yOffset = ((int) size.getHeight() - str.getHeight()) / 2;
        DrawAccentString(g, head, accent, fnt, x, 92 + yOffset, TextAlign.LEFT, BORDER);
        g.dispose();

        return bitmap;
    }

}
