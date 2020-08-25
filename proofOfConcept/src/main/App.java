package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Anthony Wang, partially taken from apcs.io
 */

public class App extends JApplet {

	private static final long serialVersionUID = 1L;

	/**
	 * Dimensions of the window.
	 */
	private static int width = 600, height = 600;

	/**
	 * Creates the screen.
	 * 
	 * @param width  - width of the screen in pixels.
	 * @param height - height of the screen in pixels.
	 * @param name   - name of the screen (displayed at the top of the window).
	 */
	public static AppWindow construct(int width, int height, String name) {
		initialize();

		if (!isApplet) {
			isApplication = true;
		}

		App.width = width;
		App.height = height;

		// Thread safety locks on the App class.
		synchronized (App.class) {
			// Get the currently running instance, if there is one.
			AppWindow instance = instanceMap.get();

			// If there is no instance already running, create one and set it as
			// the current instance.
			if (instance == null) {
				// Create a JFrame for the window.
				JFrame frame = new JFrame(name);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setIconImage(Util.getImage("src/img/icon/Plasmatron.png"));
				App window = new App();
				window.bufferSize = new Dimension(width, height);
				instanceMap.set(window.master);

				// Create a container for the frame's content.
				Container pane = frame.getContentPane();
				pane.add(window);
				pane.setSize(window.getSize());
				pane.setMinimumSize(window.getSize());
				// frame.getContentPane().setIgnoreRepaint(true);

				// Initialize and start the window.
				window.init();
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
				window.start();
				return window.master;
			}
			return instance;
		}
	}

	// Mapping between String names and underlying integer values for color
	// codes
	// and mappings from human-readable keys to virtual keys.
	private static Map<String, Integer> keyMap;
	public static Map<String, Integer> colorMap;

	public static int width() {
		return width;
	}

	public static int height() {
		return height;
	}

	public static void wait(double seconds) {
		sleep((int) (seconds * 1000));
	}

	public static void wait(int seconds) {
		sleep(seconds * 1000);
	}

	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception ignored) {

		}
	}

	public static boolean pressed(String key) {
		if (key == null)
			return false;
		else if (keyMap.containsKey(key))
			return get().isVirtualKeyPressed(keyMap.get(key));
		else if (key.length() > 0)
			return App.pressed(key.charAt(0));
		return false;
	}

	public static boolean pressed(char key) {
		return get().isKeyPressed(key);
	}

	public static boolean clicked() {
		return isMouseDown;
	}

	public static int mouseX() {
		return tempMouseX;
	}

	public static int mouseY() {
		return tempMouseY;
	}

	public static int scrollAmt() {
		return scrollAmt;
	}

	/**
	 * Push the drawn contents to a frame.
	 */
	public static void frame() {
		get().flipBuffer();

		App.tempMouseX = App.mouseX;
		App.tempMouseY = App.mouseY;

		scrollAmt = 0;

	}

	// Basically moves a bunch of library draw functions.

	/**
	 * @param x          - the x coordinate of the upper-left corner of the arc
	 *                   to be drawn.
	 * @param y          - the y coordinate of the upper-left corner of the arc
	 *                   to be drawn.
	 * @param width      - the width of the arc to be drawn.
	 * @param height     - the height of the arc to be drawn.
	 * @param startAngle - the beginning angle.
	 * @param arcAngle   - the angular extent of the arc,relative to the start
	 *                   angle.
	 */
	public static void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		get().graphics().drawArc(x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * @param x1 - the first point's x coordinate.
	 * @param y1 - the first point's y coordinate.
	 * @param x2 - the second point's x coordinate.
	 * @param y2 - the second point's y coordinate.
	 */
	public static void line(int x1, int y1, int x2, int y2) {
		get().graphics().drawLine(x1, y1, x2, y2);
	}

	/**
	 * @param x      - the x coordinate of the upper leftcorner of the oval to
	 *               be drawn.
	 * @param y      - the y coordinate of the upper leftcorner of the oval to
	 *               be drawn.
	 * @param width  - the width of the oval to be drawn.
	 * @param height - the height of the oval to be drawn.
	 */
	public static void drawOval(int x, int y, int width, int height) {
		get().graphics().drawOval(x - width / 2, y - height / 2, width, height);
	}

	public static void drawCircle(int x, int y, int radius) {
		drawOval(x, y, radius * 2, radius * 2);
	}

	/**
	 * Draws the given image at the given x, y coordinates at the angle given.
	 */
	public static void drawImage(Image img, int x, int y, int angle) {
		get().drawImage(img, x, y, angle);
	}

	/**
	 * Draws the given image at the given x, y coordinates.
	 */
	public static void drawImage(Image img, int x, int y) {
		get().drawImage(img, x, y, 0);
	}


	/**
	 * @param xPoints - a an array of x coordinates.
	 * @param yPoints - a an array of y coordinates.
	 * @param nPoints - a the total number of points.
	 */
	public static void drawPolygon(int[] xPoints, int[] yPoints) {
		if (xPoints.length != yPoints.length)
			return;
		get().graphics().drawPolygon(xPoints, yPoints, xPoints.length);
	}

	public static void drawPolygon(int... coordinates) {
		if (coordinates != null && coordinates.length > 0) {
			int[] x = new int[coordinates.length / 2];
			int[] y = new int[coordinates.length / 2];
			for (int i = 0; i < coordinates.length / 2; i++) {
				x[i] = coordinates[i * 2];
				if (i * 2 + 1 < coordinates.length) {
					y[i] = coordinates[i * 2 + 1];
				}
			}
			get().graphics().drawPolygon(x, y, coordinates.length / 2);
		}
	}

	/**
	 * @param x      - the x coordinate of the rectangle to be drawn.
	 * @param y      - the y coordinate of the rectangle to be drawn.
	 * @param width  - the width of the rectangle to be drawn.
	 * @param height - the height of the rectangle to be drawn.
	 */
	public static void drawRect(int x, int y, int width, int height) {
		get().graphics().drawRect(x - width / 2, y - height / 2, width, height);
	}

	/**
	 * Rotation by an angle
	 */
	public static void drawRect(int x, int y, int width, int height, double angle) {
		angle = Math.toRadians(angle);

		drawPolygon(x + rotatedX(-width / 2, -height / 2, angle), y + rotatedY(-width / 2, -height / 2, angle),
				x + rotatedX(-width / 2, height / 2, angle), y + rotatedY(-width / 2, height / 2, angle),
				x + rotatedX(width / 2, height / 2, angle), y + rotatedY(width / 2, height / 2, angle),
				x + rotatedX(width / 2, -height / 2, angle), y + rotatedY(width / 2, -height / 2, angle));
	}

	/**
	 * @param x         - the x coordinate of the rectangle to be drawn.
	 * @param y         - the y coordinate of the rectangle to be drawn.
	 * @param width     - the width of the rectangle to be drawn.
	 * @param height    - the height of the rectangle to be drawn.
	 * @param arcWidth  - the horizontal diameter of the arc at the four
	 *                  corners.
	 * @param arcHeight - the vertical diameter of the arc at the four corners.
	 */
	public static void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		get().graphics().drawRoundRect(x - width / 2, y - height / 2, width, height, arcWidth, arcHeight);
	}

	/**
	 * @param x          - the x coordinate of the upper-left corner of the arc
	 *                   to be filled.
	 * @param y          - the y coordinate of the upper-left corner of the arc
	 *                   to be filled.
	 * @param width      - the width of the arc to be filled.
	 * @param height     - the height of the arc to be filled.
	 * @param startAngle - the beginning angle.
	 * @param arcAngle   - the angular extent of the arc, relative to the start
	 *                   angle.
	 */
	public static void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		get().graphics().fillArc(x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * @param x      - the x coordinate of the upper left corner of the oval to
	 *               be filled.
	 * @param y      - the y coordinate of the upper left corner of the oval to
	 *               be filled.
	 * @param width  - the width of the oval to be filled.
	 * @param height - the height of the oval to be filled.
	 */
	public static void fillOval(int x, int y, int width, int height) {
		get().graphics().fillOval(x - width / 2, y - height / 2, width, height);
	}

	public static void fillCircle(int x, int y, int radius) {
		fillOval(x, y, radius * 2, radius * 2);
	}

	/**
	 * @param xPoints - a an array of x coordinates.
	 * @param yPoints - a an array of y coordinates.
	 * @param nPoints - a the total number of points.
	 */
	public static void fillPolygon(int[] xPoints, int[] yPoints) {
		if (xPoints.length != yPoints.length)
			return;
		get().graphics().fillPolygon(xPoints, yPoints, xPoints.length);
	}

	public static void fillPolygon(int... coordinates) {
		if (coordinates != null && coordinates.length > 0) {
			int[] x = new int[coordinates.length / 2];
			int[] y = new int[coordinates.length / 2];
			for (int i = 0; i < coordinates.length / 2; i++) {
				x[i] = coordinates[i * 2];
				if (i * 2 + 1 < coordinates.length) {
					y[i] = coordinates[i * 2 + 1];
				}
			}
			get().graphics().fillPolygon(x, y, coordinates.length / 2);
		}
	}

	/**
	 * @param x      - the x coordinate of the center of the rectangle.
	 * @param y      - the y coordinate of the center of the rectangle.
	 * @param width  - the width of the rectangle to be filled.
	 * @param height - the height of the rectangle to be filled.
	 */
	public static void fillRect(int x, int y, int width, int height) {
		get().graphics().fillRect(x - width / 2, y - height / 2, width, height);
	}

	/**
	 * Rotation by an angle
	 */
	public static void fillRect(int x, int y, int width, int height, double angle) {
		angle = Math.toRadians(angle);

		fillPolygon(x + rotatedX(-width / 2, -height / 2, angle), y + rotatedY(-width / 2, -height / 2, angle),
				x + rotatedX(-width / 2, height / 2, angle), y + rotatedY(-width / 2, height / 2, angle),
				x + rotatedX(width / 2, height / 2, angle), y + rotatedY(width / 2, height / 2, angle),
				x + rotatedX(width / 2, -height / 2, angle), y + rotatedY(width / 2, -height / 2, angle));
	}

	/**
	 * @param x         - the x coordinate of the center of the rectangle.
	 * @param y         - the y coordinate of the center of the rectangle.
	 * @param width     - the width of the rectangle to be filled.
	 * @param height    - the height of the rectangle to be filled.
	 * @param arcWidth  - the horizontal diameter of the arc at the four
	 *                  corners.
	 * @param arcHeight - the vertical diameter of the arc at the four corners.
	 */
	public static void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		get().graphics().fillRoundRect(x - width / 2, y - height / 2, width, height, arcWidth, arcHeight);
	}

	/**
	 * Sets the color of the currently drawing pen.
	 * 
	 * @param r - red value of the color
	 * @param g - green value of the color
	 * @param b - blue value of the color
	 * @param a - alpha or opacity value of the color
	 */
	public static void color(int r, int g, int b, int a) {
		get().graphics().setColor(new Color(r, g, b, a));
	}

	/**
	 * Sets the color of the currently drawing pen.
	 * 
	 * @param r - red value of the color
	 * @param g - green value of the color
	 * @param b - blue value of the color
	 */
	public static void color(int r, int g, int b) {
		get().graphics().setColor(new Color(r, g, b, 255));
	}

	/**
	 * Sets the font to the given font.
	 */
	public static void fontSize(int size) {
		currentFont = Font.decode(font + "-" + size);
		get().graphics().setFont(currentFont);
	}

	/**
	 * Sets the color to the given String color - if the color isn't built in,
	 * this will choose black.
	 * 
	 * @param color - the name of the color.
	 */
	public static void color(String color) {
		if (color != null) {
			App.color(color, 255);
		}
	}

	/**
	 * Blurs a rectangular region of the screen.
	 * 
	 * @param x - the x coordinate of the center of the rectangle.
	 * @param y - the y coordinate of the center of the rectangle.
	 * @param w - the width of the rectangle
	 * @param h - the height of the rectangle
	 * @param b - strength of the blur, i.e. it uses a square of size 2b+1 to
	 *          blur
	 */
	public static void blur(int x, int y, int w, int h, int b) {
		get().blur(x, y, w, h, b);
	}

	/**
	 * Sets the color to the given String color - if the color isn't built in,
	 * this will choose black.
	 * 
	 * @param color - the name of the color.
	 * @param a     - alpha or opacity value of the color.
	 */
	public static void color(String color, int a) {
		if (color != null) {
			color = color.toLowerCase();

			// Convert color string to a hex value.
			if (color.indexOf('#') == 0 && color.length() == 7) {
				try {
					color(Integer.valueOf(color.substring(1, 3), 16), Integer.valueOf(color.substring(3, 5), 16),
							Integer.valueOf(color.substring(5, 7), 16), a);
				} catch (NumberFormatException e) {
					color(0, 0, 0, 255);
				}
			} else if (colorMap.containsKey(color)) {
				int c = colorMap.get(color);
				color((c >> 16) & 0xff, (c >> 8) & 0xff, c & 0xff, a);
			} else
				color(0, 0, 0, 255);
		}
	}

	public static void print(String text, int x, int y) {
		get().graphics().drawString(text, x, y);
	}

	/**
	 * @param align - 0 = left align, 1 = centered, 2 = right align
	 */
	public static void print(String text, int x, int y, int align) {
		if (align < 0 || align > 2) {
			return;
		}
		int stringLen = (int) get().graphics().getFontMetrics().getStringBounds(text, get().graphics()).getWidth();
		get().graphics().drawString(text, x - stringLen / 2 * align, y);
	}

	/**/

	/**
	 * AppWindow is the underlying representation of a window, and is created by
	 * the App class through the 'create' method.
	 */
	class AppWindow {

		// Returns true if the given character key is pressed.
		public boolean isKeyPressed(char key) {
			return key >= 0 && key < keyPressed.length ? keyPressed[key] : false;
		}

		// Returns true if the given virtual key is pressed.
		public boolean isVirtualKeyPressed(int keyCode) {
			return keyCode >= 0 && keyCode < virtualKeyPressed.length ? virtualKeyPressed[keyCode] : false;
		}

		// Returns the graphics2D instance, fixes potential race condition.
		public Graphics2D graphics() {
			while (g == null)
				sleep(1000);
			return g;
		}

		// Returns the image corresponding to the graphics2D instance.
		public Image img() {
			while (g == null)
				sleep(1000);
			return backImageBuffer;
		}

		// Draws the given image at the given x, y coordinate.
		public void drawImage(Image img, int x, int y, double angle) {
			Graphics2D g = graphics();
			if (img != null) {
				if (angle != 0) {
					g.rotate(Math.toRadians(angle), x + img.getWidth(null) / 2.0, y + img.getHeight(null) / 2.0);
					g.drawImage(img, x, y, null);
					AffineTransform identity = new AffineTransform();
					identity.setToIdentity();
					g.setTransform(identity);
				} else {
					g.drawImage(img, x, y, null);
				}
			} 
		}

		// blurs a rectangular region of the frame
		// basically copies over the desired part of the screen and draws it
		// blurred
		public void blur(int x, int y, int w, int h, int b) {
			BufferedImage img = ((BufferedImage) img()).getSubimage(x - w / 2, y - h / 2, w, h);

			float[] mask = new float[4 * b * b + 4 * b + 1];

			for (int i = 0; i < 4 * b * b + 4 * b + 1; i++) {
				mask[i] = 1f / ((float) (4 * b * b + 4 * b + 1));
			}

			Kernel kernel = new Kernel(2 * b + 1, 2 * b + 1, mask);
			BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			img = op.filter(img, null);

			g.drawImage(img, x - w / 2, y - h / 2, null);
		}

		public void flipBuffer() {
			// Both flipBuffer and portions of paint() are synchronized
			// on the class object to ensure
			// that both cannot execute at the same time.
			paintImmediately = false; // user has called flipBuffer at least
			// once
			// getSingleton();
			synchronized (App.this) {
				Image temp = backImageBuffer;
				backImageBuffer = frontImageBuffer;
				frontImageBuffer = temp;

				if (g != null)
					g.dispose();
				paintWindow(); // paint to Video

				g = (Graphics2D) backImageBuffer.getGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, backImageBuffer.getWidth(null), backImageBuffer.getHeight(null));
				g.setColor(currentColor);
				g.setFont(currentFont);
			}
		}

		void createBuffers(int width, int height, String options) {
			if (g != null)
				g.dispose();
			if (frontImageBuffer != null)
				frontImageBuffer.flush();
			if (backImageBuffer != null)
				backImageBuffer.flush();
			options = options != null ? options.toLowerCase() : "";
			bufferSize = new Dimension(width, height);
			stretchToFit = options.contains("stretch");

			// if buffers are requested _after_ the window has been realized
			// then faster volatile images are possible
			// BUT volatile images silently fail when tested Vista IE8 and
			// JRE1.6
			boolean useVolatileImages = false;
			if (useVolatileImages) {
				try {
					// Paint silently fails when tested in IE8 Vista JRE1.6.0.14
					backImageBuffer = createVolatileImage(width, height);
					frontImageBuffer = createVolatileImage(width, height);
				} catch (Exception ignored) {

				}
			}
			if (!GraphicsEnvironment.isHeadless()) {
				try {
					GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().getDefaultConfiguration();
					backImageBuffer = config.createCompatibleImage(width, height);
					frontImageBuffer = config.createCompatibleImage(width, height);
				} catch (Exception ignored) {
				}
			}

			// as a fall-back we can still use slower Image with
			// arbitrary RGB model
			if (frontImageBuffer == null) {
				// System.err.println("Creating Image buffers");
				backImageBuffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_RGB);
				frontImageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			}
			master.flipBuffer();// set up graphics, including font and color
			// state
			paintImmediately = true; // actually, user has not yet called
			// flipBuffer
		}

	};

	private static ThreadLocal<AppWindow> instanceMap = new ThreadLocal<AppWindow>();

	public static synchronized AppWindow get() {
		AppWindow instance = instanceMap.get();
		return instance != null ? instance : construct(500, 500, "");
	}

	private static boolean isApplication;
	private static boolean isApplet;

	private AppWindow master = new AppWindow();

	private Graphics2D g;
	private Image backImageBuffer, frontImageBuffer;

	private boolean stretchToFit;

	private boolean[] keyPressed = new boolean[256];
	private boolean[] keyTyped = new boolean[256];
	private boolean[] virtualKeyPressed = new boolean[1024];
	private boolean[] virtualKeyTyped = new boolean[1024];

	private static int mouseX, mouseY, mouseClickX, mouseClickY;
	private static int tempMouseX, tempMouseY;
	private long mouseClickTime;
	private static boolean isMouseDown;

	private static int scrollAmt;

	protected Dimension bufferSize = new Dimension(500, 500);

	private Color currentColor = Color.WHITE;
	private static Font currentFont = Font.decode("Times-18");
	private static String font = "Times";
	private Thread mainThread;
	private int paintAtX, paintAtY, windowWidth, windowHeight;
	protected boolean paintImmediately;

	@Override
	public Dimension getMinimumSize() {
		return bufferSize;
	}

	@Override
	public final Dimension getPreferredSize() {
		return getMinimumSize();
	}

	@Override
	public final void init() {
		if (!isApplication)
			isApplet = true;

		instanceMap.set(master);

		setSize(bufferSize);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				addMouseListener(mouseListener);
				addMouseWheelListener(mouseWheelListener);
				addMouseMotionListener(mouseMotionListener);
				addKeyListener(keyListener);
				setFocusTraversalKeysEnabled(false);
				setFocusable(true);
				setVisible(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});
	}

	@Override
	@SuppressWarnings("deprecation")
	public final void stop() {
		if (mainThread == null)
			return;
		mainThread.interrupt();
		sleep(500);
		if (mainThread.isAlive())
			mainThread.stop();
		mainThread = null;
	}

	@Override
	public final void start() {
		master.createBuffers(bufferSize.width, bufferSize.height, "");
		if (isApplet) {
			mainThread = new Thread("main") {
				@Override
				public void run() {
					try {
						instanceMap.set(App.this.master);
						String paramKey = "window-main-class";
						String targetClassName = getParameter(paramKey);

						if (targetClassName == null) {
							return;
						}
						Class<?> targetClass = Class.forName(targetClassName);
						String[] argValue = new String[0];
						Class<?>[] argTypes = { argValue.getClass() };
						Method main = targetClass.getMethod("main", argTypes);
						main.invoke(null, new Object[] { argValue });
					} catch (ThreadDeath ignored) {
					} catch (Exception e) {
						System.err.println("Exception: " + e.getMessage());
						e.printStackTrace();
					} finally {
						instanceMap.remove();
					}
				}
			};
			mainThread.start();
		}
	}

	@Override
	public final void destroy() {
		super.destroy();
	}

	@Override
	public void update(Graphics windowGraphics) {
		paint(windowGraphics);
	}

	@Override
	public void paint(Graphics windowGraphics) {
		if (windowGraphics == null)
			return;
		windowWidth = getWidth();
		windowHeight = getHeight();

		if (frontImageBuffer == null) {
			// no image to display
			windowGraphics.clearRect(0, 0, windowWidth, windowHeight);
			return;
		}
		synchronized (App.class) {
			Image image = paintImmediately ? backImageBuffer : frontImageBuffer;
			if (stretchToFit) {
				paintAtX = paintAtY = 0;
				windowGraphics.drawImage(image, 0, 0, windowWidth, windowHeight, this);
			} else {
				int x = windowWidth - bufferSize.width;
				int y = windowHeight - bufferSize.height;
				paintAtX = x / 2;
				paintAtY = y / 2;
				windowGraphics.setColor(Color.BLACK);
				if (y > 0) {
					windowGraphics.fillRect(0, 0, windowWidth + 1, paintAtY);
					windowGraphics.fillRect(0, windowHeight - paintAtY - 1, windowWidth + 1, paintAtY + 1);
				}
				if (x > 0) {
					windowGraphics.fillRect(0, 0, paintAtX + 1, windowHeight + 1);
					windowGraphics.fillRect(windowWidth - paintAtX - 1, 0, paintAtX + 1, windowHeight + 1);
				}
				windowGraphics.drawImage(image, paintAtX, paintAtY, this);
			}
		}
	}

	private void paintWindow() {
		Graphics windowGraphics = getGraphics();
		if (windowGraphics != null)
			paint(getGraphics());
		else
			repaint();
	}

	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			char c = e.getKeyChar();
			e.getModifiersEx();
			if (c >= 0 && c < keyPressed.length)
				keyPressed[c] = keyTyped[c] = true;
			int vk = e.getKeyCode();
			if (vk >= 0 && vk < virtualKeyPressed.length)
				virtualKeyPressed[vk] = virtualKeyTyped[vk] = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			char c = e.getKeyChar(); // may
										// be
										// CHAR_UNDEFINED
			e.getModifiersEx();
			if (c >= 0 && c < keyPressed.length)
				keyPressed[c] = false;
			int vk = e.getKeyCode();
			if (vk >= 0 && vk < virtualKeyPressed.length)
				virtualKeyPressed[vk] = false;
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	};

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			if (windowWidth == 0 || windowHeight == 0)
				return; // no
						// display
						// window
						// yet
			mouseClickX = (stretchToFit ? (int) (0.5 + me.getX() * bufferSize.width / (double) windowWidth)
					: me.getX() - paintAtX);
			mouseClickY = (stretchToFit ? (int) (0.5 + me.getY() * bufferSize.height / (double) windowHeight)
					: me.getY() - paintAtY);
			mouseClickTime = me.getWhen();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			isMouseDown = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isMouseDown = false;
		}
	};

	private MouseWheelListener mouseWheelListener = new MouseAdapter() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			scrollAmt = e.getWheelRotation();
		}
	};

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent me) {
			if (windowWidth == 0 || windowHeight == 0)
				return;
			mouseX = (stretchToFit ? (int) (0.5 + me.getX() * bufferSize.width / (double) windowWidth)
					: me.getX() - paintAtX);
			mouseY = (stretchToFit ? (int) (0.5 + me.getY() * bufferSize.height / (double) windowHeight)
					: me.getY() - paintAtY);
			me.getModifiersEx();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseListener.mouseClicked(e);
			mouseMoved(e);
		}
	};

	/**
	 * Creates a mapping between the color string name and the given RGB value.
	 * 
	 * @param name  - name of the color
	 * @param red   - red component of color
	 * @param green - green component of color
	 * @param blue  - blue component of color
	 */
	public static void addColor(String name, int red, int green, int blue) {
		if (red < 0)
			red = 0;
		if (green < 0)
			green = 0;
		if (blue < 0)
			blue = 0;
		if (red > 255)
			red = 255;
		if (green > 255)
			green = 255;
		if (blue > 255)
			blue = 255;
		colorMap.put(name, ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff));
	}

	/**
	 * Creates a mapping between the given color string name and the given hex
	 * code.
	 * 
	 * @param name - name of the color
	 * @param hex  - hex code of the color
	 */
	public static void addColor(String name, String hex) {
		if (hex.indexOf('#') == 0 && hex.length() == 7) {
			colorMap.put(name, (Integer.valueOf(hex.substring(1, 3), 16) << 16
					| Integer.valueOf(hex.substring(3, 5), 16) << 8 | Integer.valueOf(hex.substring(5, 7), 16)));
		}
	}

	private static void initialize() {
		Util.addImages("~/");

		keyMap = new HashMap<String, Integer>();
		keyMap.put("space", KeyEvent.VK_SPACE);
		keyMap.put("left", KeyEvent.VK_LEFT);
		keyMap.put("right", KeyEvent.VK_RIGHT);
		keyMap.put("up", KeyEvent.VK_UP);
		keyMap.put("down", KeyEvent.VK_DOWN);
		keyMap.put("escape", KeyEvent.VK_ESCAPE);
		keyMap.put("tab", KeyEvent.VK_TAB);
		keyMap.put("shift", KeyEvent.VK_SHIFT);
		keyMap.put("control", KeyEvent.VK_CONTROL);
		keyMap.put("alt", KeyEvent.VK_ALT);
		keyMap.put("delete", KeyEvent.VK_DELETE);
		keyMap.put("home", KeyEvent.VK_HOME);
		keyMap.put("backspace", KeyEvent.VK_BACK_SPACE);
		keyMap.put("enter", KeyEvent.VK_ENTER);

		colorMap = new HashMap<String, Integer>();
		App.addColor("alice blue", 240, 248, 255);
		App.addColor("antique white", 250, 235, 215);
		App.addColor("aqua", 0, 255, 255);
		App.addColor("aquamarine", 127, 255, 212);
		App.addColor("azure", 240, 255, 255);
		App.addColor("beige", 245, 245, 220);
		App.addColor("bisque", 255, 228, 196);
		App.addColor("black", 0, 0, 0);
		App.addColor("blanched almond", 255, 235, 205);
		App.addColor("blue", 0, 0, 255);
		App.addColor("blue violet", 138, 43, 226);
		App.addColor("brown", 139, 69, 19);
		App.addColor("burlywood", 222, 184, 135);
		App.addColor("cadet blue", 95, 158, 160);
		App.addColor("chartreuse", 127, 255, 0);
		App.addColor("chocolate", 210, 105, 30);
		App.addColor("coral", 255, 127, 80);
		App.addColor("cornflower blue", 100, 149, 237);
		App.addColor("cornsilk", 255, 248, 220);
		App.addColor("cyan", 0, 255, 255);
		App.addColor("dark blue", 0, 0, 139);
		App.addColor("dark cyan", 0, 139, 139);
		App.addColor("dark goldenrod", 184, 134, 11);
		App.addColor("dark gray", 75, 75, 75);
		App.addColor("dark green", 0, 100, 0);
		App.addColor("dark khaki", 189, 183, 107);
		App.addColor("dark magenta", 139, 0, 139);
		App.addColor("dark olive green", 85, 107, 47);
		App.addColor("dark orange", 255, 140, 0);
		App.addColor("dark orchid", 153, 50, 204);
		App.addColor("dark red", 139, 0, 0);
		App.addColor("dark salmon", 233, 150, 122);
		App.addColor("dark sea green", 143, 188, 143);
		App.addColor("dark slate blue", 72, 61, 139);
		App.addColor("dark slate gray", 47, 79, 79);
		App.addColor("dark turquoise", 0, 206, 209);
		App.addColor("dark violet", 148, 0, 211);
		App.addColor("deep pink", 255, 20, 147);
		App.addColor("deep sky blue", 0, 191, 255);
		App.addColor("dim gray", 105, 105, 105);
		App.addColor("dodger blue", 30, 144, 255);
		App.addColor("firebrick", 178, 34, 34);
		App.addColor("floral white", 255, 250, 240);
		App.addColor("forest green", 34, 139, 34);
		App.addColor("fuschia", 255, 0, 255);
		App.addColor("gainsboro", 220, 220, 220);
		App.addColor("ghost white", 255, 250, 250);
		App.addColor("gold", 255, 215, 0);
		App.addColor("goldenrod", 218, 165, 32);
		App.addColor("gray", 128, 128, 128);
		App.addColor("grey", 128, 128, 128);
		App.addColor("green", 0, 128, 0);
		App.addColor("green yellow", 173, 255, 47);
		App.addColor("honeydew", 240, 255, 240);
		App.addColor("hot pink", 255, 105, 180);
		App.addColor("indian red", 205, 92, 92);
		App.addColor("indigo", 111, 0, 255);
		App.addColor("ivory", 255, 255, 240);
		App.addColor("khaki", 240, 230, 140);
		App.addColor("lavender", 230, 230, 250);
		App.addColor("lavender blush", 255, 240, 245);
		App.addColor("lawn green", 124, 252, 0);
		App.addColor("lemon chiffon", 255, 250, 205);
		App.addColor("light blue", 173, 216, 230);
		App.addColor("light coral", 240, 128, 128);
		App.addColor("light cyan", 224, 255, 255);
		App.addColor("light goldenrod", 238, 221, 130);
		App.addColor("light goldenrod yellow", 250, 250, 210);
		App.addColor("light gray", 199, 199, 199);
		App.addColor("light green", 144, 238, 144);
		App.addColor("light pink", 255, 182, 193);
		App.addColor("light salmon", 255, 160, 122);
		App.addColor("light sea green", 32, 178, 170);
		App.addColor("light sky blue", 135, 206, 250);
		App.addColor("light slate blue", 132, 112, 255);
		App.addColor("light slate gray", 119, 136, 153);
		App.addColor("light steel blue", 176, 196, 222);
		App.addColor("light yellow", 255, 255, 224);
		App.addColor("lime", 0, 255, 0);
		App.addColor("lime green", 50, 205, 50);
		App.addColor("linen", 250, 240, 230);
		App.addColor("magenta", 255, 0, 255);
		App.addColor("maroon", 128, 0, 0);
		App.addColor("medium aquamarine", 102, 205, 170);
		App.addColor("medium blue", 0, 0, 205);
		App.addColor("medium gray", 162, 162, 162);
		App.addColor("medium orchid", 186, 85, 211);
		App.addColor("medium purple", 147, 112, 219);
		App.addColor("medium sea green", 60, 179, 113);
		App.addColor("medium slate blue", 123, 104, 238);
		App.addColor("medium spring green", 0, 250, 154);
		App.addColor("medium turquoise", 72, 209, 204);
		App.addColor("medium violet red", 199, 21, 133);
		App.addColor("midnight blue", 25, 25, 112);
		App.addColor("mint cream", 245, 255, 250);
		App.addColor("misty rose", 255, 228, 225);
		App.addColor("moccasin", 255, 228, 181);
		App.addColor("navajo white", 255, 222, 173);
		App.addColor("navy", 0, 0, 128);
		App.addColor("old lace", 253, 245, 230);
		App.addColor("olive", 128, 128, 0);
		App.addColor("olive drab", 107, 142, 35);
		App.addColor("orange", 255, 165, 0);
		App.addColor("orange red", 255, 69, 0);
		App.addColor("orchid", 218, 112, 214);
		App.addColor("pale goldenrod", 238, 232, 170);
		App.addColor("pale green", 152, 251, 152);
		App.addColor("pale turquoise", 175, 238, 238);
		App.addColor("pale violet red", 219, 112, 147);
		App.addColor("papaya whip", 255, 239, 213);
		App.addColor("peach puff", 255, 218, 185);
		App.addColor("peru", 205, 133, 63);
		App.addColor("pink", 255, 192, 203);
		App.addColor("plum", 221, 160, 221);
		App.addColor("powder blue", 176, 224, 230);
		App.addColor("purple", 128, 0, 128);
		App.addColor("red", 255, 0, 0);
		App.addColor("rosy brown", 188, 143, 143);
		App.addColor("royal blue", 65, 105, 225);
		App.addColor("saddle brown", 139, 69, 19);
		App.addColor("salmon", 250, 128, 114);
		App.addColor("sandy brown", 244, 164, 96);
		App.addColor("sea green", 46, 139, 87);
		App.addColor("seashell", 255, 245, 238);
		App.addColor("sienna", 160, 82, 45);
		App.addColor("silver", 192, 192, 192);
		App.addColor("sky blue", 135, 206, 235);
		App.addColor("slate blue", 106, 90, 205);
		App.addColor("slate gray", 112, 128, 144);
		App.addColor("snow", 255, 250, 250);
		App.addColor("spring green", 0, 255, 127);
		App.addColor("steel blue", 70, 130, 180);
		App.addColor("tan", 210, 180, 140);
		App.addColor("teal", 0, 128, 128);
		App.addColor("thistle", 216, 191, 216);
		App.addColor("tomato", 255, 99, 71);
		App.addColor("turquoise", 64, 224, 208);
		App.addColor("violet", 238, 130, 238);
		App.addColor("violet red", 208, 32, 144);
		App.addColor("wheat", 245, 222, 179);
		App.addColor("white", 255, 255, 255);
		App.addColor("white smoke", 245, 245, 245);
		App.addColor("yellow", 255, 255, 0);
		App.addColor("yellow green", 154, 205, 50);
	}

	// Used in rotation calculations.
	private static int rotatedX(int x, int y, double angle) {
		return (int) Math.round(x * Math.cos(angle) - y * Math.sin(angle));
	}

	// Used in rotation calculations.
	private static int rotatedY(int x, int y, double angle) {
		return (int) Math.round(x * Math.sin(angle) + y * Math.cos(angle));
	}
}
