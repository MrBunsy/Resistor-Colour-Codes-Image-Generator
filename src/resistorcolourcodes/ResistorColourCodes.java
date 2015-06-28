package resistorcolourcodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Luke
 */
public class ResistorColourCodes {

    public ResistorColourCodes() {

    }

    public static String powerOfTen(int zeroCount) {

        switch (zeroCount / 3) {
            case 0:
                return "R";
            case 1:
                return "k";
            case 2:
                return "M";
            default:
                //not quite sure when we'll need this
                return "G";
        }
    }

    public static PrettyValue getPretty(int value) {

        //TODO check value >=1
        BigDecimal b = new BigDecimal(value);

        b = b.round(new MathContext(2));

        int rounded = b.intValue();

        String stringed = String.valueOf(rounded);

        int len = stringed.length();
        // this is pointless, we round to 2sigfigs so we can just count number of zeros.  all zeroes will be at the end.
        // having just said that, this is more flexible and it turns out java doesn't have a built in "count all isntances" option...
        int zeroCount = 0;
        //count the zeros on the end
        for (int i = len - 1; i >= 0; i--) {
            if (stringed.charAt(i) == '0') {
                zeroCount++;
            } else {
                break;
            }
        }

        String postfix = powerOfTen(len - 1);

        String pretty = "";

        int zerosDealtWith = zeroCount < 3 ? 0 : (zeroCount / 3) * 3;

        int charsLeftToDealWith = len - zerosDealtWith;

        if (charsLeftToDealWith == 1) {
            pretty += stringed.charAt(0) + postfix + "0";
        } else if (charsLeftToDealWith < 4) {
            pretty += stringed.substring(0, charsLeftToDealWith) + postfix;
        } else {
            pretty += stringed.charAt(0) + postfix + stringed.charAt(1);
        }

        Color[] colours = new Color[3];

        colours[0] = valueToColour(Integer.parseInt(stringed.charAt(0) + ""));
        if (len > 1) {
            colours[1] = valueToColour(Integer.parseInt(stringed.charAt(1) + ""));
        } else {
            //edge case, I suppose
            colours[1] = null;
        }
        //digits left, or zero if less than zero
        colours[2] = valueToColour(Math.max(len - 2, 0));

        return new PrettyValue(value, pretty, colours);
    }

    public static BufferedImage getImageOfResistor(Color[] colours, Color tolerance, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = image.createGraphics();

        int resistorWidth = (int) Math.round(((double) width) * 0.8);
        int resistorHeight = (int) Math.round(((double) height) * 1);

        int strokeWidth = height / 20;
        int cornerSize = height / 6;

        graphics.setStroke(new BasicStroke(strokeWidth));

        //lines out of resistor
        graphics.setPaint(Color.black);
        graphics.draw(new Line2D.Double(0, height / 2, width, height / 2));

        //resistor body
        graphics.setPaint(new GradientPaint(0, 0, new Color(250, 170, 100), 0, height, new Color(200, 120, 50)));
        Shape resistorBody = new RoundRectangle2D.Double((width - resistorWidth) / 2, (height - resistorHeight) / 2, resistorWidth, resistorHeight, cornerSize, cornerSize);
        graphics.fill(resistorBody);

        int colourDistance = (int) Math.round(((double) width) * 0.07);
        int colourStart = (width - resistorWidth) / 2 + colourDistance;
        int colourWidth = (int) Math.round(((double) width) * 0.1);

        for (int i = 0; i < colours.length; i++) {
            graphics.setPaint(colours[i]);
            graphics.fill(new Rectangle2D.Double(colourStart + colourWidth * i + colourDistance * i, (height - resistorHeight) / 2, colourWidth, resistorHeight));
        }

        if (tolerance != null) {
            graphics.setPaint(tolerance);
            graphics.fill(new Rectangle2D.Double(width - colourStart - colourWidth, (height - resistorHeight) / 2, colourWidth, resistorHeight));
        }

        //draw outline aroudn resistor body
        graphics.setPaint(Color.black);
//        graphics.draw(resistorBody); // think I prefer it without, actually

        graphics.dispose();

        return image;
    }

    public static boolean writeImageToFile(BufferedImage image, String filename, int scaleDown) {
        try {
            BufferedImage scaledImage = getScaledInstance(image, image.getWidth() / scaleDown, image.getHeight() / scaleDown, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);

            ImageIO.write(scaledImage, "png", new File(filename));
        } catch (IOException ex) {
            Logger.getLogger(ResistorColourCodes.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    /**
     * significant figures and multipliers for resistors
     *
     * @param fig
     * @return
     */
    public static Color valueToColour(int fig) {
        switch (fig) {
            case 0:
                return Color.BLACK;
            case 1:
                //brown
//                return new Color(139, 69, 19);
                return new Color(0x96, 0x4B, 0x00);
            case 2:
                return Color.RED;
            case 3:
//                return Color.ORANGE;
                return new Color(255, 170, 0);
            case 4:
                return Color.YELLOW;
            case 5:
//                return Color.GREEN;
                return new Color(0, 180, 0);
            case 6:
                return Color.BLUE;
            case 7:
                //violet
//                return new Color(143,0,255);
                return new Color(0xEE, 0x82, 0xEE);
            case 8:
                return Color.GRAY;
            case 9:
                return Color.WHITE;
            case -1:
                //gold
                return GOLD;
            case -2:
                //silver
                return new Color(0xC0, 0xC0, 0xC0);
            default:
                //'none'
                return new Color(0, 0, 0, 0);

        }
    }

//    public static final int[] e12 = {10, 12, 15, 18, 22, 27, 33, 39, 47, 56, 68, 82,
//        100, 120, 150, 180, 220, 270, 330, 390, 470, 560, 680, 820,
//        1000, 1200, 1500, 1800, 2200, 2700, 3300, 3900, 4700, 5600, 6800, 8200,
//        10000, 12000, 15000, 18000, 22000, 27000, 33000, 39000, 47000, 56000, 68000, 82000,
//        100000, 120000, 150000, 180000, 220000, 270000, 330000, 390000, 470000, 560000, 680000, 820000,
//        1000000};
    public static final int[] e12 = {10, 12, 15, 18, 22, 27, 33, 39};

    public static final Color GOLD = new Color(0xcf, 0xb5, 0x3b);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        ResistorColourCodes.getPretty(103456);
        PrettyValue p = ResistorColourCodes.getPretty(1000);

        int width = 1000;
        //golden ratio
//        int height =(int)Math.round(((double)width)/1.61803398875);
        int height = 250;

        int AA = 4;

        BufferedImage img = getImageOfResistor(p.colours, GOLD, width, height);
        writeImageToFile(img, "example.png", 2);

        PrettyValue[] prettyValues = new PrettyValue[e12.length];
        BufferedImage[] images = new BufferedImage[e12.length];

        for (int i = 0; i < e12.length; i++) {
//            System.out.println("e12: " + e12[i] + " =>" + ResistorColourCodes.getPretty(e12[i]).prettyText);
            PrettyValue r = ResistorColourCodes.getPretty(e12[i]);
            BufferedImage png = getImageOfResistor(r.colours, GOLD, width * AA, height * AA);
            writeImageToFile(png, "images/" + r.prettyText + ".png", AA);
            prettyValues[i] = r;
            images[i] = png;
        }

        //produce actual drawer labels
        for (int i = 0; i < e12.length; i += 2) {
            if (i < e12.length - 2) {
                //two together
                BufferedImage png = combineTwo(prettyValues[i], prettyValues[i + 1], images[i], images[i + 1], 510 * 2 * AA, 110 * 2 * AA);
                writeImageToFile(png, "drawer_images/" + prettyValues[i].prettyText + "_" + prettyValues[i + 1].prettyText + ".png", AA);
            } else {
                //only one left
            }
        }

    }

    public static BufferedImage combineTwo(PrettyValue one, PrettyValue two, BufferedImage imageOne, BufferedImage imageTwo, int width, int height) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        try {
        Graphics2D graphics = image.createGraphics();

        double scaledDown = (double) (width / 2) / (double) imageOne.getWidth();

        int resistorHeight = (int) Math.round(imageOne.getHeight() * scaledDown);

        int fontHeight = (int) Math.round((height - resistorHeight)*1.5);

        //new Font("Serif", Font.BOLD, fontHeight)
        graphics.setFont(getFont(Font.SANS_SERIF, Font.PLAIN, fontHeight, graphics));
        graphics.setColor(Color.black);
        FontMetrics f = graphics.getFontMetrics();
//
        int fontWidth = f.stringWidth(one.prettyText);
        int actualFontHeight = f.getHeight();
//        BufferedImage testOne = ImageIO.read(new File("images/" + one.prettyText + ".png"));

        graphics.drawImage(imageOne, 0, 0, width / 2, resistorHeight, null);
        graphics.drawString(one.prettyText, (width / 2 - fontWidth) / 2, Math.round(height*0.97));

        graphics.drawImage(imageTwo, width / 2, 0, width / 2, resistorHeight, null);
        graphics.drawString(two.prettyText, width / 2 + (width / 2 - fontWidth) / 2, Math.round(height*0.97) );

        graphics.dispose();
//        } catch (IOException ex) {
//            Logger.getLogger(ResistorColourCodes.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return image;

    }

    /**
     * http://stackoverflow.com/questions/5829703/java-getting-a-font-with-a-specific-height-in-pixels
     *
     * @param name
     * @param style
     * @param height
     * @return
     */
    public static Font getFont(String name, int style, int height, Graphics2D g) {
        int size = height;
        Boolean up = null;
        while (true) {
            Font font = new Font(name, style, size);
            int testHeight = g.getFontMetrics(font).getHeight();
            if (testHeight < height && up != Boolean.FALSE) {
                size++;
                up = Boolean.TRUE;
            } else if (testHeight > height && up != Boolean.TRUE) {
                size--;
                up = Boolean.FALSE;
            } else {
                return font;
            }
        }
    }

    //http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
    /**
     * Convenience method that returns a scaled instance of the provided
     * {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance, in pixels
     * @param targetHeight the desired height of the scaled instance, in pixels
     * @param hint one of the rendering hints that corresponds to
     * {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     * {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     * {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step scaling
     * technique that provides higher quality than the usual one-step technique
     * (only useful in downscaling cases, where {@code targetWidth} or
     * {@code targetHeight} is smaller than the original dimensions, and
     * generally only when the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}

class PrettyValue {

    public PrettyValue(int realValue, String prettyText, Color[] colours) {
        this.realValue = realValue;
        this.prettyText = prettyText;
        this.colours = colours;
    }

    public int realValue;
    public String prettyText;
    public Color[] colours;
}
