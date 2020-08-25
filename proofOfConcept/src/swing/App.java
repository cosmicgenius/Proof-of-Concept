package swing;

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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Anthony Wang
 */

public class App extends JApplet
{

	/**
	 * Serial version ID probably. I really don't know; my IDE told me to put it
	 * here.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Dimensions of the window.
	 */
	private static int width = 600, height = 600;

	/**
	 * Creates the screen.
	 * 
	 * @param width - width of the screen in pixels.
	 * @param height - height of the screen in pixels.
	 * @param name - name of the screen (displayed at the top of the window).
	 */
	public static AppWindow construct(int width, int height, String name)
	{
		initialize();

		if (!isApplet)
		{
			isApplication = true;
		}

		// Thread safety locks on the App class.
		synchronized (App.class)
		{
			// Get the currently running instance, if there is one.
			AppWindow instance = instanceMap.get();

			// If there is no instance already running, create one and set it as
			// the current instance.
			if (instance == null)
			{
				// Create a JFrame for the window.
				JFrame frame = new JFrame(name);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	private static ArrayList<String> imagePath;

	/**
	 * Wait for the given number of seconds.
	 */
	public static void wait(double seconds)
	{
		sleep((int) (seconds * 1000));
	}

	/**
	 * Wait for the given number of seconds.
	 */
	public static void wait(int seconds)
	{
		sleep(seconds * 1000);
	}

	/**
	 * Wait for the given number of milliseconds.
	 */
	public static void sleep(int milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (Exception ignored)
		{
		}
	}

	/**
	 * Returns true if the given key is pressed.
	 */
	public static boolean pressed(String key)
	{
		if (key == null) return false;
		else if (keyMap.containsKey(key)) return get().isVirtualKeyPressed(keyMap.get(key));
		else if (key.length() > 0) return App.pressed(key.charAt(0));
		return false;
	}

	/**
	 * Returns true if the key for the given character is pressed.
	 */
	public static boolean pressed(char key)
	{
		return get().isKeyPressed(key);
	}

	/**
	 * Returns true if the mouse is clicked.
	 * 
	 * @return whether or not the mouse is clicked.
	 */
	public static boolean clicked()
	{
		return isMouseClicked;
	}

	/**
	 * Returns the x coordinate of the mouse.
	 */
	public static int mouseX()
	{
		return mouseX;
	}

	/**
	 * Returns the y coordinate of the mouse.
	 */
	public static int mouseY()
	{
		return mouseY;
	}

	/**
	 * Set the frame rate of the window to the given value.
	 * 
	 * @param rate - the number of frames to try showing every second.
	 */
	public static void setFrameRate(int rate)
	{
		frameRate = 1000 / rate;
		get().flipBuffer();
	}

	/**
	 * Push the drawn contents to a frame.
	 */
	public static void frame()
	{
		get().flipBuffer();
		App.sleep(frameRate);
	}

	// Basically moves a bunch of library draw functions.

	/**
	 * @param x - the x coordinate of the upper-left corner of the arc to be
	 * drawn.
	 * @param y - the y coordinate of the upper-left corner of the arc to be
	 * drawn.
	 * @param width - the width of the arc to be drawn.
	 * @param height - the height of the arc to be drawn.
	 * @param startAngle - the beginning angle.
	 * @param arcAngle - the angular extent of the arc,relative to the start
	 * angle.
	 */
	public static void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		get().graphics().drawArc(x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * @param x1 - the first point's x coordinate.
	 * @param y1 - the first point's y coordinate.
	 * @param x2 - the second point's x coordinate.
	 * @param y2 - the second point's y coordinate.
	 */
	public static void drawLine(int x1, int y1, int x2, int y2)
	{
		get().graphics().drawLine(x1, y1, x2, y2);
	}

	/**
	 * @param x - the x coordinate of the upper leftcorner of the oval to be drawn. 
	 * @param y - the y coordinate of the upper leftcorner of the oval to be drawn.
	 * @param width - the width of the oval to be drawn.
	 * @param height - the height of the oval to be drawn.
	 */
	public static void drawOval(int x, int y, int width, int height)
	{
		get().graphics().drawOval(x, y, width, height);
	}

	/**
	 * Draws the given image at the given x, y coordinates at the angle given.
	 */
	public static void drawImage(String img, int x, int y, int angle)
	{
		get().drawImage(img, x, y, angle);
	}

	/**
	 * Draws the given image at the given x, y coordinates.
	 */
	public static void drawImage(String img, int x, int y)
	{
		get().drawImage(img, x, y);
	}
	
	/**
	 * @param xPoints - a an array of x coordinates.
	 * @param yPoints - a an array of y coordinates.
	 * @param nPoints - a the total number of points.
	 */
	public static void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
	{
		get().graphics().drawPolygon(xPoints, yPoints, nPoints);
	}

	/**
	 * @param x - the x coordinate of the rectangle to be drawn.
	 * @param y - the y coordinate of the rectangle to be drawn.
	 * @param width - the width of the rectangle to be drawn.
	 * @param height - the height of the rectangle to be drawn.
	 */
	public static void drawRect(int x, int y, int width, int height)
	{
		get().graphics().drawRect(x, y, width, height);
	}

	/**
	 * @param x - the x coordinate of the rectangle to be drawn.
	 * @param y - the y coordinate of the rectangle to be drawn.
	 * @param width - the width of the rectangle to be drawn.
	 * @param height - the height of the rectangle to be drawn.
	 * @param arcWidth - the horizontal diameter of the arc at the four corners.
	 * @param arcHeight - the vertical diameter of the arc at the four corners.
	 */
	public static void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		get().graphics().drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	/**
	 * @param x - the x coordinate of the upper-left corner of the arc to be filled.
	 * @param y - the y coordinate of the upper-left corner of the arc to be filled.
	 * @param width - the width of the arc to be filled.
	 * @param height - the height of the arc to be filled.
	 * @param startAngle - the beginning angle.
	 * @param arcAngle - the angular extent of the arc, relative to the start angle.
	 */
	public static void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		get().graphics().fillArc(x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * @param x - the x coordinate of the upper left corner of the oval to be filled.
	 * @param y - the y coordinate of the upper left corner of the oval to be filled.
	 * @param width - the width of the oval to be filled.
	 * @param height - the height of the oval to be filled.
	 */
	public static void fillOval(int x, int y, int width, int height)
	{
		get().graphics().fillOval(x, y, width, height);
	}

	
	/**
	 * @param xPoints - a an array of x coordinates.
	 * @param yPoints - a an array of y coordinates.
	 * @param nPoints - a the total number of points.
	 */
	public static void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
	{
		get().graphics().fillPolygon(xPoints, yPoints, nPoints);
	}

	/**
	 * @param x - the x coordinate of the rectangle to be filled.
	 * @param y - the y coordinate of the rectangle to be filled.
	 * @param width - the width of the rectangle to be filled.
	 * @param height - the height of the rectangle to be filled.
	 */
	public static void fillRect(int x, int y, int width, int height)
	{
		get().graphics().fillRect(x, y, width, height);
	}

	/**
	 * @param x - the x coordinate of the rectangle to be filled.
	 * @param y - the y coordinate of the rectangle to be filled.
	 * @param width - the width of the rectangle to be filled.
	 * @param height - the height of the rectangle to be filled.
	 * @param arcWidth - the horizontal diameter of the arc at the four corners.
	 * @param arcHeight - the vertical diameter of the arc at the four corners.
	 */
	public static void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		get().graphics().fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	/**
	 * Sets the color of the currently drawing pen.
	 * @param r - red value of the color
	 * @param g - green value of the color
	 * @param b - blue value of the color
	 * @param a - alpha or opacity value of the color
	 */
	public static void setColor(int r, int g, int b, int a)
	{
		get().graphics().setColor(new Color(r, g, b, a));
	}

	/**
	 * AppWindow is the underlying representation of a window, and is created by
	 * the App class through the 'create' method.
	 */
	class AppWindow
	{

		// Returns true if the given character key is pressed.
		public boolean isKeyPressed(char key)
		{
			return key >= 0 && key < keyPressed.length ? keyPressed[key] : false;
		}

		// Returns true if the given virtual key is pressed.
		public boolean isVirtualKeyPressed(int keyCode)
		{
			return keyCode >= 0 && keyCode < virtualKeyPressed.length ? virtualKeyPressed[keyCode] : false;
		}

		// Returns the graphics2D instance, fixes potential race condition.
		public Graphics2D graphics()
		{
			while (g == null)
				sleep(1000);
			return g;
		}

		public void drawImage(String filename, int x, int y)
		{
			drawImage(filename, x, y, 0);
		}

		// Draws the given image at the given x, y coordinate.
		public void drawImage(String filename, int x, int y, double angle)
		{
			Graphics2D g = graphics();
			Image img = getImage(filename);
			if (img != null)
			{
				if (angle != 0)
				{
					AffineTransform old = new AffineTransform();
					AffineTransform trans = new AffineTransform();

					trans.rotate(Math.toRadians(angle), img.getWidth(null) / 2, img.getHeight(null) / 2);
					trans.translate(x - img.getWidth(null) / 2, y - img.getHeight(null) / 2);
					g.setTransform(trans);
					g.drawImage(img, x, y, img.getWidth(null), img.getHeight(null), null);
					trans.setToIdentity();

					g.setTransform(old);
				}
				else g.drawImage(img, x, y, null);
			}
			else
			{
				g.drawString(filename + "?", x, y);
			}
			if (paintImmediately) paintWindow();
		}

		// Returns the image associated with the given path description.
		private Image getImage(String path)
		{
			if (imageMap.containsKey(path)) return imageMap.get(path);

			if (path.indexOf("http://") == 0 || path.indexOf("https://") == 0)
			{
				return getURLImage(path);
			}
			else try
			{
				File imageFile = new File(path);
				int imageFilePath = 0;
				while (!imageFile.exists() && imageFilePath < imagePath.size())
				{
					imageFile = new File(imagePath.get(imageFilePath) + "/" + path);
					imageFilePath++;
				}

				Image image = ImageIO.read(imageFile);
				imageMap.put(path, image);
				return image;
			}
			catch (Exception ex)
			{
				return null;
			}
		}

		/**
		 * Returns the image at the given URL, caching it to the given File path
		 * if necessary.
		 * 
		 * @param path - the path to save the file
		 * @param intCache - a cache file (can be null to indicate no caching)
		 * @return the downloaded image as an Image object
		 */
		private Image getURLImage(String path)
		{
			File cache = null;
			Image image = null;

			// MD5 hash the file path to get a unique descriptor for this image.
			try
			{
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.reset();
				m.update(path.getBytes());

				cache = new File("image/" + new BigInteger(1, m.digest()).toString(16) + ".png");
			}
			catch (NoSuchAlgorithmException e1)
			{
				e1.printStackTrace();
			}

			// Try to get the image from the cache.
			try
			{
				if (cache.exists())
				{
					image = ImageIO.read(cache);
					imageMap.put(path, image);
					return image;
				}
			}
			catch (IOException e)
			{
			}

			// If no image was found, get the image from the web.
			if (image == null)
			{
				try
				{
					// Download the image
					System.out.println("Downloading " + path);
					long start = System.currentTimeMillis();
					image = ImageIO.read(new URL(path));
					System.out.println("Completed in " + (System.currentTimeMillis() - start) + "ms: " + image.getWidth(null) + " x " + image.getHeight(null));
					imageMap.put(path, image);

					// Save the image to the cache
					if (cache != null)
					{
						cache.mkdirs();
						BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
						Graphics2D g = bufferedImage.createGraphics();
						g.drawImage(image, 0, 0, null);
						g.dispose();
						ImageIO.write(bufferedImage, "png", cache);
					}

					return image;
				}
				catch (MalformedURLException e)
				{
				}
				catch (IOException e)
				{
				}
			}
			return null;
		}

		public void flipBuffer()
		{
			// Both flipBuffer and portions of paint() are synchronized
			// on the class object to ensure
			// that both cannot execute at the same time.
			paintImmediately = false; // user has called flipBuffer at least
			// once
			// getSingleton();
			synchronized (App.this)
			{
				Image temp = backImageBuffer;
				backImageBuffer = frontImageBuffer;
				frontImageBuffer = temp;

				if (g != null) g.dispose();
				paintWindow(); // paint to Video

				g = (Graphics2D) backImageBuffer.getGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, backImageBuffer.getWidth(null), backImageBuffer.getHeight(null));
				g.setColor(currentColor);
				g.setFont(currentFont);
			}
		}

		void createBuffers(int width, int height, String options)
		{
			if (g != null) g.dispose();
			if (frontImageBuffer != null) frontImageBuffer.flush();
			if (backImageBuffer != null) backImageBuffer.flush();
			options = options != null ? options.toLowerCase() : "";
			bufferSize = new Dimension(width, height);
			stretchToFit = options.contains("stretch");

			// if buffers are requested _after_ the window has been realized
			// then faster volatile images are possible
			// BUT volatile images silently fail when tested Vista IE8 and
			// JRE1.6
			boolean useVolatileImages = false;
			if (useVolatileImages)
			{
				try
				{
					// Paint silently fails when tested in IE8 Vista JRE1.6.0.14
					backImageBuffer = createVolatileImage(width, height);
					frontImageBuffer = createVolatileImage(width, height);
				}
				catch (Exception ignored)
				{

				}
			}
			if (!GraphicsEnvironment.isHeadless())
			{
				try
				{
					GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
					backImageBuffer = config.createCompatibleImage(width, height);
					frontImageBuffer = config.createCompatibleImage(width, height);
				}
				catch (Exception ignored)
				{
				}
			}

			// as a fall-back we can still use slower BufferedImage with
			// arbitrary RGB model
			if (frontImageBuffer == null)
			{
				// System.err.println("Creating BufferedImage buffers");
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

	public static synchronized AppWindow get()
	{
		AppWindow instance = instanceMap.get();
		return instance != null ? instance : construct(500, 500, "");
	}

	private static boolean isApplication;
	private static boolean isApplet;

	private AppWindow master = new AppWindow();

	private Graphics2D g;
	private Image backImageBuffer, frontImageBuffer;
	private Map<String, Image> imageMap = Collections.synchronizedMap(new HashMap<String, Image>());

	private boolean stretchToFit;

	private boolean[] keyPressed = new boolean[256];
	private boolean[] keyTyped = new boolean[256];
	private boolean[] virtualKeyPressed = new boolean[1024];
	private boolean[] virtualKeyTyped = new boolean[1024];

	private static int mouseX, mouseY, mouseClickX, mouseClickY;
	private long mouseClickTime;
	private static boolean isMouseClicked;

	protected static Dimension bufferSize = new Dimension(500, 500);

	private Color currentColor = Color.WHITE;
	private Font currentFont = Font.decode("Times-18");
	private Thread mainThread;
	private int paintAtX, paintAtY, windowWidth, windowHeight;
	protected boolean paintImmediately;
	private static int frameRate = 30;

	@Override
	public Dimension getMinimumSize()
	{
		return bufferSize;
	}

	@Override
	public final Dimension getPreferredSize()
	{
		return getMinimumSize();
	}

	@Override
	public final void init()
	{
		if (!isApplication) isApplet = true;

		instanceMap.set(master);

		setSize(bufferSize);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				addMouseListener(mouseListener);
				addMouseMotionListener(mouseMotionListener);
				addKeyListener(keyListener);
				setFocusTraversalKeysEnabled(false);
				setFocusable(true);
				setVisible(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

			}
		});
	}

	@Override
	@SuppressWarnings("deprecation")
	public final void stop()
	{
		if (mainThread == null) return;
		mainThread.interrupt();
		sleep(500);
		if (mainThread.isAlive()) mainThread.stop();
		mainThread = null;
	}

	@Override
	public final void start()
	{
		master.createBuffers(bufferSize.width, bufferSize.height, "");
		if (isApplet)
		{
			mainThread = new Thread("main")
			{
				@Override
				public void run()
				{
					try
					{
						instanceMap.set(App.this.master);
						String paramKey = "window-main-class";
						String targetClassName = getParameter(paramKey);

						if (targetClassName == null)
						{
							System.err.println("Error: no main method.");
							return;
						}
						Class<?> targetClass = Class.forName(targetClassName);
						String[] argValue = new String[0];
						Class<?>[] argTypes = {argValue.getClass()};
						Method main = targetClass.getMethod("main", argTypes);
						main.invoke(null, new Object[]{argValue});
					}
					catch (ThreadDeath ignored)
					{
					}
					catch (Exception e)
					{
						System.err.println("Exception: " + e.getMessage());
						e.printStackTrace();
					}
					finally
					{
						instanceMap.remove();
					}
				}
			};
			mainThread.start();
		}
	}

	@Override
	public final void destroy()
	{
		super.destroy();
	}

	@Override
	public void update(Graphics windowGraphics)
	{
		paint(windowGraphics);
	}

	@Override
	public void paint(Graphics windowGraphics)
	{
		if (windowGraphics == null) return;
		windowWidth = getWidth();
		windowHeight = getHeight();

		if (frontImageBuffer == null)
		{
			// no image to display
			windowGraphics.clearRect(0, 0, windowWidth, windowHeight);
			return;
		}
		synchronized (App.class)
		{
			Image image = paintImmediately ? backImageBuffer : frontImageBuffer;
			if (stretchToFit)
			{
				paintAtX = paintAtY = 0;
				windowGraphics.drawImage(image, 0, 0, windowWidth, windowHeight, this);
			}
			else
			{
				int x = windowWidth - bufferSize.width;
				int y = windowHeight - bufferSize.height;
				paintAtX = x / 2;
				paintAtY = y / 2;
				windowGraphics.setColor(Color.BLACK);
				if (y > 0)
				{
					windowGraphics.fillRect(0, 0, windowWidth + 1, paintAtY);
					windowGraphics.fillRect(0, windowHeight - paintAtY - 1, windowWidth + 1, paintAtY + 1);
				}
				if (x > 0)
				{
					windowGraphics.fillRect(0, 0, paintAtX + 1, windowHeight + 1);
					windowGraphics.fillRect(windowWidth - paintAtX - 1, 0, paintAtX + 1, windowHeight + 1);
				}
				windowGraphics.drawImage(image, paintAtX, paintAtY, this);
			}
		}
	}

	private void paintWindow()
	{
		Graphics windowGraphics = getGraphics();
		if (windowGraphics != null) paint(getGraphics());
		else repaint();
	}

	private KeyListener keyListener=new KeyAdapter(){@Override public void keyPressed(KeyEvent e){char c=e.getKeyChar();e.getModifiersEx();if(c>=0&&c<keyPressed.length)keyPressed[c]=keyTyped[c]=true;int vk=e.getKeyCode();if(vk>=0&&vk<virtualKeyPressed.length)virtualKeyPressed[vk]=virtualKeyTyped[vk]=true;}

	@Override public void keyReleased(KeyEvent e){char c=e.getKeyChar(); // may
																		 // be
																		 // CHAR_UNDEFINED
	e.getModifiersEx();if(c>=0&&c<keyPressed.length)keyPressed[c]=false;int vk=e.getKeyCode();if(vk>=0&&vk<virtualKeyPressed.length)virtualKeyPressed[vk]=false;}

	@Override public void keyTyped(KeyEvent e){}};

	private MouseListener mouseListener=new MouseAdapter(){@Override public void mouseClicked(MouseEvent me){if(windowWidth==0||windowHeight==0)return; // no
																																						 // display
																																						 // window
																																						 // yet
	mouseClickX=(stretchToFit?(int)(0.5+me.getX()*bufferSize.width/(double)windowWidth):me.getX()-paintAtX);mouseClickY=(stretchToFit?(int)(0.5+me.getY()*bufferSize.height/(double)windowHeight):me.getY()-paintAtY);mouseClickTime=me.getWhen();}

	@Override public void mousePressed(MouseEvent e){isMouseClicked=true;}

	@Override public void mouseReleased(MouseEvent e){isMouseClicked=false;}};

	private MouseMotionListener mouseMotionListener=new MouseMotionAdapter(){@Override public void mouseMoved(MouseEvent me){if(windowWidth==0||windowHeight==0)return;mouseX=(stretchToFit?(int)(0.5+me.getX()*bufferSize.width/(double)windowWidth):me.getX()-paintAtX);mouseY=(stretchToFit?(int)(0.5+me.getY()*bufferSize.height/(double)windowHeight):me.getY()-paintAtY);me.getModifiersEx();}

	@Override public void mouseDragged(MouseEvent e){mouseListener.mouseClicked(e);mouseMoved(e);}};

	public static void addImages(String path)
	{
		if (!path.endsWith("/")) path += "/";
		if (path.startsWith("~") && System.getProperty("user.home") != null)
		{
			if (path.charAt(1) != '/') path = "/" + path.substring(1);
			else path = path.substring(1);
			path = System.getProperty("user.home") + path;
		}

		if (imagePath == null) imagePath = new ArrayList<String>();
		imagePath.add(path);
	}

	/**
	 * Creates a mapping between the color string name and the given RGB value.
	 * 
	 * @param name - name of the color
	 * @param red - red component of color
	 * @param green - green component of color
	 * @param blue - blue component of color
	 */
	public static void addColor(String name, int red, int green, int blue)
	{
		if (red < 0) red = 0;
		if (green < 0) green = 0;
		if (blue < 0) blue = 0;
		if (red > 255) red = 255;
		if (green > 255) green = 255;
		if (blue > 255) blue = 255;
		colorMap.put(name, ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff));
	}

	/**
	 * Creates a mapping between the given color string name and the given hex
	 * code.
	 * 
	 * @param name - name of the color
	 * @param hex - hex code of the color
	 */
	public static void addColor(String name, String hex)
	{
		if (hex.indexOf('#') == 0 && hex.length() == 7)
		{
			colorMap.put(name, (Integer.valueOf(hex.substring(1, 3), 16) << 16 | Integer.valueOf(hex.substring(3, 5), 16) << 8 | Integer.valueOf(hex.substring(5, 7), 16)));
		}
	}

	private static void initialize()
	{
		addImages("~/");

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
		App.addColor("dark gray", 169, 169, 169);
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
		App.addColor("light gray", 211, 211, 211);
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
}
