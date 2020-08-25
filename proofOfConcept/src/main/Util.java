package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Util {
    /**
     * The constant tau = 2pi
     */
    public static final double TAU = 2 * Math.PI;

    /**
     * Whether the mouse/key was pressed down in the previous frame
     */
    private static Map<String, Boolean> prevPressed = new HashMap<String, Boolean>();
    /**
     * Whether the mouse/key is currently pressed (actually whether it was
     * pressed in the range (prev frame, cur frame])
     */
    private static Map<String, Boolean> currPressed = new HashMap<String, Boolean>();
    private static int prevScrollAmt = 0, currScrollAmt = 0;

    /**
     * Map of already accessed image names.
     */
    private static Map<String, BufferedImage> imageMap = Collections
            .synchronizedMap(new HashMap<String, BufferedImage>());
    private static ArrayList<String> imagePath;

    public static void tick() {
        String[] specialKeys = new String[] { "space", "left", "right", "up", "down", "escape", "tab", "shift",
                "control", "alt", "delete", "home", "backspace", "hyphen", "enter" };
        
        for (int i = 0; i < specialKeys.length; i++) {
            currPressed.put(specialKeys[i], currPressed.get(specialKeys[i]) || App.pressed(specialKeys[i]));
        }

        for (int i = 0; i < 128; i++) {
            currPressed.put("" + (char) (' ' + i),
                    currPressed.get("" + (char) (' ' + i)) || App.pressed((char) (' ' + i)));
        }

        currPressed.put("mouse", currPressed.get("mouse") || App.clicked());

        currScrollAmt += App.scrollAmt();
    }

    public static void reset() {
        String[] specialKeys = new String[] { "space", "left", "right", "up", "down", "escape", "tab", "shift",
                "control", "alt", "delete", "home", "backspace", "hyphen", "enter" };

        prevPressed = new HashMap<String, Boolean>(currPressed);
        prevScrollAmt = currScrollAmt;
        currScrollAmt = 0;

        for (int i = 0; i < specialKeys.length; i++) {
            currPressed.put(specialKeys[i], App.pressed(specialKeys[i]));
        }

        for (int i = 0; i < 128; i++) {
            currPressed.put("" + (char) (' ' + i), App.pressed((char) (' ' + i)));
        }

        currPressed.put("mouse", App.clicked());

        currScrollAmt += App.scrollAmt();
    }

    /**
     * Checks if 2 rectangles are touching
     * 
     * @param x1
     *            - The x of the first rectangle
     * @param y1
     *            - The y of the first rectangle
     * @param width1
     *            - The width of the first rectangle
     * @param height1
     *            - The height of the first rectangle
     * @param x2
     *            - The x of the second rectangle
     * @param y2
     *            - The y of the second rectangle
     * @param width2
     *            - The width of the second rectangle
     * @param height2
     *            - The height of the second rectangle
     * @param hedge
     *            - Whether the horizontal edges are counted
     * @param vedge
     *            - Whether the vertical edges are counted
     * @return Whether the rectangles are touching
     */
    public static boolean checkRectangles(int x1, int y1, int width1, int height1, int x2, int y2, int width2,
            int height2, boolean hedge, boolean vedge) {
        int distx = Math.abs(x1 - x2);
        int disty = Math.abs(y1 - y2);
        if (hedge) {
            distx--;
        }
        if (vedge) {
            disty--;
        }
        int distw = width1 / 2 + width2 / 2;
        int disth = height1 / 2 + height2 / 2;
        if (distx < distw && disty < disth) {
            return true;
        }
        return false;
    }

    /**
     * Checks if 2 circles are touching
     * 
     * @param x1
     *            - The x of the first circle
     * @param y1
     *            - The y of the first circle
     * @param radius1
     *            - The radius of the first circle
     * @param x2
     *            - The x of the second circle
     * @param y2
     *            - The y of the second circle
     * @param radius2
     *            - The radius of the second circle
     * @return Whether the circles are touching
     */
    public static boolean checkCircles(double x1, double y1, double radius1, double x2, double y2, double radius2) {
    	double distx = x1 - x2;
    	double disty = y1 - y2;
        double dist = radius1 + radius2;
        if (distx * distx + disty * disty <= dist * dist) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the mouse has just clicked in this exact tick
     */
    public static boolean click() {
        return currPressed.get("mouse") && !prevPressed.get("mouse");
    }

    /**
     * Checks if the key has just pressed in this exact tick (string).
     * 
     * @param s
     *            - the string naming the key pressed.
     */
    public static boolean key(String s) {
        return currPressed.get(s) && !prevPressed.get(s);
    }

    /**
     * Checks if the key has just pressed in this exact tick (char).
     * 
     * @param c
     *            - the character pressed.
     */
    public static boolean key(char c) {
        return currPressed.get("" + c) && !prevPressed.get("" + c);
    }

    /**
     * Creates a rectangle with round corners
     * 
     * @param x
     *            - The x of the rectangle
     * @param y
     *            - The y of the rectangle
     * @param width
     *            - The width of the rectangle
     * @param height
     *            - The height of the rectangle
     * @param curveRadius
     *            - The radius of the curve in the corners of the rectangle
     */
    public static void roundRectangle(int x, int y, int width, int height, int curveRadius) {
        App.fillRoundRect(x, y, width, height, curveRadius * 2, curveRadius * 2);
    }

    /**
     * Creates a rectangle with shadows on the bottom and right
     * 
     * @param x
     *            - The x of the rectangle
     * @param y
     *            - The y of the rectangle
     * @param width
     *            - The width of the rectangle
     * @param height
     *            - The height of the rectangle
     * @param shadowWidth
     *            - The width of the right shadow
     * @param shadowHeight
     *            - The height of the bottom shadow
     * @param tr
     *            - The r value of the color of the regular rectangle
     * @param tg
     *            - The g value of the color of the regular rectangle
     * @param tb
     *            - The b value of the color of the regular rectangle
     * @param mr
     *            - The r value of the color of the middle shadow
     * @param mg
     *            - The g value of the color of the middle shadow
     * @param mb
     *            - The b value of the color of the middle shadow
     * @param dr
     *            - The r value of the color of the dark shadow
     * @param dg
     *            - The g value of the color of the dark shadow
     * @param db
     *            - The b value of the color of the dark shadow
     */
    public static void shadowRectangle(int x, int y, int width, int height, int shadowWidth, int shadowHeight, int tr,
            int tg, int tb, int mr, int mg, int mb, int dr, int dg, int db) {
        App.color(mr, mg, mb);
        App.fillRect(x, y, width, height);
        App.color(tr, tg, tb);
        App.fillRect(x - shadowWidth / 2, y - shadowHeight / 2, width - shadowWidth, height - shadowHeight);
        App.color(dr, dg, db);
        App.fillRect(x + width / 2 - shadowWidth / 2, y + height / 2 - shadowHeight / 2, shadowWidth, shadowHeight);
    }

    /**
     * Creates 2 concentric ovals at using x, y, width, height, thickness r, g,
     * and b values
     * 
     * @param x
     *            - The x of the concentric ovals
     * @param y
     *            - The y of the concentric ovals
     * @param width
     *            - The width of the outer oval
     * @param height
     *            - The height of the outer oval
     * @param thickness
     *            - The thickness of the band between the ovals
     * @param r1
     *            - The r value of the color of the outer oval
     * @param g1
     *            - The g value of the color of the outer oval
     * @param b1
     *            - The b value of the color of the outer oval
     * @param r2
     *            - The r value of the color of the inner oval
     * @param g2
     *            - The g value of the color of the inner oval
     * @param b2
     *            - The b value of the color of the inner oval
     */
    public static void concentricOvals(int x, int y, int width, int height, int thickness, int r1, int b1, int g1,
            int r2, int b2, int g2) {
        App.color(r1, g1, b1);
        App.fillOval(x, y, width, height);
        App.color(r2, g2, b2);
        App.fillOval(x, y, width - 2 * thickness, height - 2 * thickness);
    }

    /**
     * Draws and checks for clicks a button
     * 
     * @param x
     *            - The x of the button
     * @param y
     *            - The y of the button
     * @param width
     *            - The width of the button
     * @param height
     *            - The height of the button
     * @param curveRadius
     *            - The radius of the curve in the corners of the button
     * @param c1
     *            - The color of the button normally
     * @param c2
     *            - The color of the button when hovered over
     * @param tx
     *            - The x-offset of the text from the middle of the button
     * @param ty
     *            - The y-offset of the text from the middle of the button
     * @param textSize
     *            - The size of the text on the button
     * @param text
     *            - The text on the button
     * @param c3
     *            - The color of the text normally
     * @param c2
     *            - The color of the text when hovered over
     * @return Whether the button is clicked
     */
    public static boolean button(int x, int y, int width, int height, int curveRadius, String c1, String c2, int tx,
            int ty, int textSize, String text, String c3, String c4) {
        App.fontSize(textSize);
        if (checkRectangles(x, y, width, height, App.mouseX(), App.mouseY(), 0, 0, true, true)) {
            App.color(c2);
            roundRectangle(x, y, width, height, curveRadius);
            App.color(c4);
            App.print(text, x + tx, y + ty);
            if (Util.click()) {
                return true;
            }
        } else {
            App.color(c1);
            roundRectangle(x, y, width, height, curveRadius);
            App.color(c3);
            App.print(text, x + tx, y + ty);
        }
        return false;
    }

    /**
     * Draws the outline of a box
     * 
     * @param x
     *            - the x of the box
     * @param y
     *            - the y of the box
     * @param width
     *            - the width of the box
     * @param height
     *            - the height of the box
     * @param sideWidth
     *            - the sideWidth of the outline
     */
    public static void drawBox(int x, int y, int width, int height, int sideWidth) {
        App.fillRect(x - width / 2 + sideWidth / 2, y, sideWidth, height);
        App.fillRect(x + width / 2 - sideWidth / 2 - 1, y, sideWidth, height);
        App.fillRect(x, y - height / 2 + sideWidth / 2, width, sideWidth);
        App.fillRect(x, y + height / 2 - sideWidth / 2 - 1, width, sideWidth);
    }

    /**
     * Draws a line with a set width
     * 
     * @param x
     *            - the x of the first endpoint
     * @param y
     *            - the y of the first endpoint
     * @param endx
     *            - the x of the second endpoint
     * @param endy
     *            - the y of the second endpoint
     * @param width
     *            - the width of the line
     */
    public static void line(int x, int y, int endx, int endy, int width) {
        App.fillRect((x + endx) / 2, (y + endy) / 2, width,
                (int) (Math.sqrt((endx - x) * (endx - x) + (endy - y) * (endy - y))),
                90 + Math.toDegrees(Util.angle(endx - x, endy - y)));
    }

    /**
     * Randomly shuffles an int array
     * 
     * @param a
     *            - The array in question
     */
    public static int[] shuffle(int[] a) {
        int[] b = new int[a.length];
        boolean[] used = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            used[i] = false;
        }
        for (int i = 0; i < a.length; i++) {
            while (true) {
                int j = (int) (Math.random() * (a.length));
                if (!used[j]) {
                    b[i] = a[j];
                    used[j] = true;
                    break;
                }
            }
        }
        return b;
    }

    /**
     * Turns a size 3 integer array into a color
     * 
     * @param a
     *            - The array in question
     */
    public static void color(int[] a) {
        App.color(a[0], a[1], a[2]);
    }

    /**
     * Finds the angle in radians in respect to the x-axis given dx and dy
     * 
     * @param dx
     *            - the difference in x
     * @param dy
     *            - the difference in y
     */
    public static double radangle(int dx, int dy) {
        return Math.PI - Util.angle(dx, dy);
    }

    /**
     * Finds the sign of n
     * 
     * @param d
     *            - the variable whose sign is we seek
     */
    public static int sign(double d) {
        if (d == 0) {
            return 0;
        }
        return (int) (Math.abs(d) / d);
    }

    /**
     * Finds double the area (which is always an integer) of a polygon with the
     * given list of integer x, y coordinates as vertices.
     * 
     * @param x
     *            - list of x coordinates
     * @param y
     *            - list of y coordinates
     */
    public static int darea(int[] x, int[] y) {
        int area = 0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            area += x[i] * (y[(i + 1) % n] - y[(i + n - 1) % n]);
        }
        return Math.abs(area);
    }

    /**
     * Finds double the area (which is always an integer) of a polygon with the
     * given list of integer x, y coordinates as vertices.
     */
    public static int darea(int... coordinates) {
        if (coordinates != null && coordinates.length > 0) {
            int[] x = new int[coordinates.length / 2];
            int[] y = new int[coordinates.length / 2];
            for (int i = 0; i < coordinates.length / 2; i++) {
                x[i] = coordinates[i * 2];
                if (i * 2 + 1 < coordinates.length) {
                    y[i] = coordinates[i * 2 + 1];
                }
            }
            return darea(x, y);
        }
        return 0;
    }

    /**
     * Finds the area of a polygon with the given list of integer x, y
     * coordinates as vertices.
     * 
     * @param x
     *            - list of x coordinates
     * @param y
     *            - list of y coordinates
     */
    public static double area(double[] x, double[] y) {
        double area = 0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            area += x[i] * (y[(i + 1) % n] - y[(i + n - 1) % n]);
        }
        return Math.abs(area) / 2;
    }

    /**
     * Finds the area of a polygon with the given list of integer x, y
     * coordinates as vertices.
     */
    public static double area(double... coordinates) {
        if (coordinates != null && coordinates.length > 0) {
            double[] x = new double[coordinates.length / 2];
            double[] y = new double[coordinates.length / 2];
            for (int i = 0; i < coordinates.length / 2; i++) {
                x[i] = coordinates[i * 2];
                if (i * 2 + 1 < coordinates.length) {
                    y[i] = coordinates[i * 2 + 1];
                }
            }
            return area(x, y);
        }
        return 0.0;
    }

    /**
     * Returns the distance from a point to a line
     * 
     * @param x1
     *            - The ax of the first point on the line
     * @param y1
     *            - The ay of the first point on the line
     * @param x2
     *            - The bx of the second point on the line
     * @param y2
     *            - The by of the second point on the line
     * @param x3
     *            - The px of the point not on the line
     * @param y3
     *            - The py of the point not on the line
     * @return Whether the circles are touching
     */
    public static double pointToLine(double x1, double y1, double x2, double y2, double x3, double y3) {
        double area = area(x1, y1, x2, y2, x3, y3);
        double dist = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        dist = Math.sqrt(dist);

        return area / dist * 2;
    }

    /**
     * Returns the slope of the line between the points (x1, y1) and (x2, y2)
     */
    public static double slope(int x1, int y1, int x2, int y2) {
        return (y2 - y1) / (x2 - x1);
    }

    /**
     * Rotates the point (x, y) by r radians about the origin
     */
    public static int[] rotate(int x, int y, double r) {
        int image[] = { (int) Math.round(x * Math.cos(r) - y * Math.sin(r)),
                (int) Math.round(y * Math.cos(r) + x * Math.sin(r)) };
        return image;
    }

    /**
     * Returns the angle made between the x axis and (x, y) in radians
     * specifically it returns theta such that e^(i theta) = x/(x^2+y^2) + i
     * y/(x^2+y^2) and -tau/4 <= theta < tau/4
     */
    public static double angle(double x, double y) {
        double d = Util.dist(x / Math.sqrt(x * x + y * y), y / Math.sqrt(x * x + y * y), 1, 0);

        double angle = Math.acos(1 - d * d / 2);

        if (y < 0) {
            return -angle;
        }
        return angle;
    }

    /**
     * Finds the distance between the points (x1, y1) and (x2, y2)
     * 
     * @param x1
     *            - x coordinate of the first point
     * @param y1
     *            - y coordinate of the first point
     * @param x2
     *            - x coordinate of the second point
     * @param y2
     *            - y coordinate of the second point
     * @return
     */
    public static double dist(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Finds the square of the distance between the points (x1, y1) and (x2, y2)
     * 
     * @param x1
     *            - x coordinate of the first point
     * @param y1
     *            - y coordinate of the first point
     * @param x2
     *            - x coordinate of the second point
     * @param y2
     *            - y coordinate of the second point
     */
    public static int sdist(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    /**
     * Finds the distance between the points (x1, y1) and (x2, y2)
     * 
     * @param x1
     *            - x coordinate of the first point
     * @param y1
     *            - y coordinate of the first point
     * @param x2
     *            - x coordinate of the second point
     * @param y2
     *            - y coordinate of the second point
     */
    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Maps a double to an integer using a randomizing method.
     */
    public static int intMap(double d) {
        if ((d == Math.floor(d)) && !Double.isInfinite(d)) {
            return (int) d;
        }
        if (d > 1.0 || d < 0.0) {
            return intMap(d - Math.floor(d)) + (int) Math.floor(d);
        }

        return Math.random() < d ? 1 : 0;
    }

    /**
     * Finds a ^ b (since I'm not sure if Math.pow is reliable)
     * 
     * @param a
     *            - base of exponent
     * @param b
     *            - exponent
     */
    public static int pow(int a, int b) {
        int r = 1;

        while (b > 0) {
            r *= (b % 2) * a;
            a = a * a;
        }

        return r;
    }

    /**
     * Finds the foot of the altitude from (x1, y1) to the line made by (x2,
     * y2), (x3, y3)
     */
    public static double[] foot(double x1, double y1, double x2, double y2, double x3, double y3) {
        double b = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        double a = (x1 - x3) * (x1 - x3) + (y1 - y3) * (y1 - y3);
        double d = pointToLine(x2, y2, x3, y3, x1, y1) * pointToLine(x2, y2, x3, y3, x1, y1);
        double c = (x2 - x3) * (x2 - x3) + (y2 - y3) * (y2 - y3);

        double[] foot = new double[] { x1, y1 };

        if (d < 0.0001) {
            return foot;
        }

        double w1 = Math.sqrt(a - d), w2 = Math.sqrt(b - d);

        if (a + c < b) {
            w1 = -w1;
        } else if (b + c < a) {
            w2 = -w2;
        }

        foot[0] = (w1 * x2 + w2 * x3) / (w1 + w2);
        foot[1] = (w1 * y2 + w2 * y3) / (w1 + w2);
        return foot;
    }

    /**
     * Returns the amount scrolled by the scroll wheel. 0 = none, positive = up,
     * negative = down.
     */
    public static int scroll() {
        if (prevScrollAmt != App.scrollAmt()) {
            return App.scrollAmt();
        }
        return 0;
    }

    /**
     * Returns the image associated with the given path description.
     */
    public static BufferedImage getImage(String path) {
        if (imageMap.containsKey(path))
            return imageMap.get(path);

        if (path.indexOf("http://") == 0 || path.indexOf("https://") == 0) {
            return getURLImage(path);
        } else
            try {
                File imageFile = new File(path);
                int imageFilePath = 0;
                while (!imageFile.exists() && imageFilePath < imagePath.size()) {
                    imageFile = new File(imagePath.get(imageFilePath) + "/" + path);
                    imageFilePath++;
                }

                BufferedImage image = ImageIO.read(imageFile);
                imageMap.put(path, image);
                return image;
            } catch (Exception ex) {
                return null;
            }
    }

    /**
     * Returns the image at the given URL, caching it to the given File path if
     * necessary.
     * 
     * @param path
     *            - the path to save the file
     * @param intCache
     *            - a cache file (can be null to indicate no caching)
     * @return the downloaded image as an BufferedImage object
     */
    public static BufferedImage getURLImage(String path) {
        File cache = null;
        BufferedImage image = null;

        // MD5 hash the file path to get a unique descriptor for this image.
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(path.getBytes());

            cache = new File("image/" + new BigInteger(1, m.digest()).toString(16) + ".png");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        // Try to get the image from the cache.
        try {
            if (cache.exists()) {
                image = ImageIO.read(cache);
                imageMap.put(path, image);
                return image;
            }
        } catch (IOException e) {
        }

        // If no image was found, get the image from the web.
        if (image == null) {
            try {
                // Download the image
                System.out.println("Downloading " + path);
                long start = System.currentTimeMillis();
                image = ImageIO.read(new URL(path));
                System.out.println("Completed in " + (System.currentTimeMillis() - start) + "ms: "
                        + image.getWidth(null) + " x " + image.getHeight(null));
                imageMap.put(path, image);

                // Save the image to the cache
                if (cache != null) {
                    cache.mkdirs();
                    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = bufferedImage.createGraphics();
                    g.drawImage(image, 0, 0, null);
                    g.dispose();
                    ImageIO.write(bufferedImage, "png", cache);
                }

                return image;
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void addImages(String path) {
        if (!path.endsWith("/"))
            path += "/";
        if (path.startsWith("~") && System.getProperty("user.home") != null) {
            if (path.charAt(1) != '/')
                path = "/" + path.substring(1);
            else
                path = path.substring(1);
            path = System.getProperty("user.home") + path;
        }

        if (imagePath == null)
            imagePath = new ArrayList<String>();
        imagePath.add(path);
    }

}