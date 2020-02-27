package cn.ac.nya.sler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static cn.ac.nya.sler.TextUtil.*;

public class SLER {

    public static Bitmap defArrow(Color color) {
        Bitmap defArrow = new Bitmap(100, 100);
        Graphics2D g = defArrow.createGraphics();
        ConfigureGraphics(g);
        Polygon polygon = new Polygon();
        polygon.addPoint(10, 20);
        polygon.addPoint(90, 50);
        polygon.addPoint(10, 80);
        polygon.addPoint(30, 50);
        g.setColor(color);
        g.fillPolygon(polygon);
        g.dispose();
        return defArrow;
    }

    private static void drawLine(Graphics2D g, float x1, float y1, float x2, float y2) {
        g.drawLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));
    }

    public static String[] arg(
            String nowMain, String nowSub,
            String prevTop, String prevMain, String prevSub,
            String nextTop, String nextMain, String nextSub,
            String prevMainS, String prevSubS,
            String nextMainS, String nextSubS) {
        return new String[] {
                nowMain, nowSub, prevTop, prevMain, prevSub,
                 nextTop, nextMain, nextSub,
                 prevMainS, prevSubS,
                 nextMainS, nextSubS
        };
    }

    public static String[] arg(
            String nowMain, String nowSub,
            String prevTop, String prevMain, String prevSub,
            String nextTop, String nextMain, String nextSub) {
        return arg(
                nowMain, nowSub, prevTop, prevMain, prevSub,
                nextTop, nextMain, nextSub,
                "", "", "", ""
        );
    }

    public static String[] arg(
            String head,
            String line, String lineSub,
            String target, String targetSub) {
        return new String[] {
                head, line, lineSub,
                target, targetSub
        };
    }

    public static BufferedImage gen(
            String[] args,
            Color colorPri, Color colorSec, Color colorAccent,
            Font fontPri, Font fontSec,
            boolean isRight, boolean wide) {

        Bitmap bitmap = new Bitmap(wide ? 1024 : 768, 256);

        int width = bitmap.getWidth(), heigh = bitmap.getHeight();

        if (args.length > 5) {

            args = Arrays.copyOf(args, 12);
            String nowMain = args[0];
            String nowSub = args[1];

            String prevTop = args[2];
            String prevMain = args[3];
            String prevSub = args[4];

            String nextTop = args[5];
            String nextMain = args[6];
            String nextSub = args[7];

            String prevMainS = args[8];
            String prevSubS = args[9];

            String nextMainS = args[10];
            String nextSubS = args[11];

            boolean hasPrev = !prevMain.isEmpty();
            boolean hasNext = !nextMain.isEmpty();

            boolean hasPrevS = !prevMainS.isEmpty();
            boolean hasNextS = !nextMainS.isEmpty();

            Bitmap main = GenPrimaryStationLabel(
                    nowMain, nowSub, colorPri, fontPri, fontSec);

            Bitmap prev = GenSecondaryStationLabel(
                    prevMain, prevSub, prevTop, colorPri, fontPri, fontSec,
                    isRight ? TextAlign.LEFT : TextAlign.RIGHT);

            Bitmap next = GenSecondaryStationLabel(
                    nextMain, nextSub, nextTop, colorPri, fontPri, fontSec,
                    isRight ? TextAlign.RIGHT : TextAlign.LEFT);

            int h = 72; float thick = 8;

            if (wide) {
                Bitmap prevS = GenSecondaryStationLabel(
                        prevMainS, prevSubS, "", colorPri, fontPri, fontSec,
                        isRight ? TextAlign.LEFT : TextAlign.RIGHT);

                Bitmap nextS = GenSecondaryStationLabel(
                        nextMainS, nextSubS, "", colorPri, fontPri, fontSec,
                        isRight ? TextAlign.RIGHT : TextAlign.LEFT);

                Graphics2D g = bitmap.createGraphics();
                ConfigureGraphics(g);

                BasicStroke stroke = new BasicStroke(thick);
                if (hasPrevS) {
                    g.drawImage(prevS, isRight ? 0 : width - 256, h - 112, null);
                    g.setColor(colorSec);
                    g.setStroke(stroke);
                    drawLine(g, isRight ? 0 : width / 16.0F * 13 - thick * 0.3F - width / 16.0F, h + 48, isRight ? width / 16.0F * 3 + thick * 0.3F + width / 16.0F : width, h + 48);
                    drawLine(g, isRight ? width / 16.0F * 3 + width / 16.0F : width / 16.0F * 13 - width / 16.0F, h + 48, isRight ? width / 4.0F + width / 16.0F : width / 4.0F * 3 - width / 16.0F, h + 160);
                }
                if (hasNextS) {
                    g.drawImage(nextS, isRight ? width - 256 : 0, h - 112, null);
                    g.setColor(colorAccent);
                    g.setStroke(stroke);
                    drawLine(g, isRight ? width / 16.0F * 13 - thick * 0.3F - width / 16.0F : 0, h + 48, isRight ? width : width / 16.0F * 3 + thick * 0.3F + width / 16.0F, h + 48);
                    drawLine(g, isRight ? width / 16.0F * 13 - width / 16.0F : width / 16.0F * 3 + width / 16.0F, h + 48, isRight ? width / 4.0F * 3 - width / 16.0F : width / 4.0F + width / 16.0F, h + 160);
                }
                g.dispose();
            }

            Graphics2D g = bitmap.createGraphics();
            ConfigureGraphics(g);

            if (hasPrev)
                g.drawImage(prev, isRight ? 0 : width - 256, h, null);
            g.drawImage(main, (width - 512) / 2, h, null);
            if (hasNext)
                g.drawImage(next, isRight ? width - 256 : 0, h, null);

            BasicStroke stroke = new BasicStroke(thick);
            if (hasPrev ^ hasNext) {
                g.setColor(hasNext ? colorAccent : colorSec);
                g.setStroke(stroke);
                drawLine(g, 0, h + 160, width, h + 160);
            } else {
                g.setStroke(stroke);
                g.setColor(isRight ? colorSec : colorAccent);
                drawLine(g, 0, h + 160, width / 2.0F, h + 160);
                g.setColor(isRight ? colorAccent : colorSec);
                drawLine(g, width / 2.0F, h + 160, width, h + 160);
            }
            g.dispose();

        } else {

            args = Arrays.copyOf(args, 5);
            String head = args[0];
            String line = args[1];
            String lineSub = args[2];
            String target = args[3];
            String targetSub = args[4];
            Bitmap targetArrow = defArrow(colorPri);

            Bitmap label = GenLineLabel(
                    head, line, lineSub, colorPri, colorAccent, fontPri, fontSec,
                    isRight ? TextAlign.LEFT : TextAlign.RIGHT);

            Bitmap tar = GenSecondaryStationLabel(
                    target, targetSub, "", colorPri, fontPri, fontSec,
                    isRight ? TextAlign.RIGHT : TextAlign.LEFT);

            int h = 72; float thick = 8;

            if (!isRight) {
                Bitmap tmp = new Bitmap(targetArrow.getWidth(), targetArrow.getHeight());
                Graphics2D g = tmp.createGraphics();
                ConfigureGraphics(g);
                g.rotate(Math.PI, targetArrow.getWidth() / 2.0, targetArrow.getHeight() / 2.0);
                g.drawImage(targetArrow, 0, 0, null);
                targetArrow = tmp;
                g.dispose();
            }

            Graphics2D g = bitmap.createGraphics();
            ConfigureGraphics(g);

            g.drawImage(label, isRight ? 8 : width - 256 - 8, h, null);
            g.drawImage(tar, isRight ? width - 256 - 92 : 92, h, null);

            BasicStroke stroke = new BasicStroke(thick);
            g.setColor(colorAccent);
            g.setStroke(stroke);
            drawLine(g, 0, h + 160, width, h + 160);
            int x = isRight ? width - 84 : 0, y = h + 80;
            g.drawImage(targetArrow,
                    x, y, x + 80, y + 80,
                    0, 0, targetArrow.getWidth(), targetArrow.getHeight(),
                    null);
            g.dispose();

        }

        return bitmap;
    }

}
