package io.x666c.glib4j;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.IntConsumer;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import io.x666c.glib4j.components.GButton;
import io.x666c.glib4j.components.GButtonPane;
import io.x666c.glib4j.components.GCustomPane;
import io.x666c.glib4j.components.GLabelText;
import io.x666c.glib4j.components.GSlider;
import io.x666c.glib4j.components.GTextField;
import io.x666c.glib4j.graphics.Renderer;
import io.x666c.glib4j.input.Input;

public final class GFrame {
	static boolean instantiated = false;
	
	private final JFrame internal_frame;
	private final Canvas draw_canvas;
	
	private boolean center;
	
	private Thread renderer;
	private Thread updater;
	private volatile boolean shouldLoop = true;
	private volatile int ups;
	
	private Syncer syncer;
	
	private Color clearColor = Color.WHITE;
	
	private UpdateCallback update_callback;
	private RenderCallback draw_callback;
	
	static {
		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
//		System.setProperty("sun.java2d.opengl", "true");
	}
	
	{
		if(instantiated)
			throw new RuntimeException("There can be only one gframe instance per program!");
	}
	
	private final Map<Character, Runnable> events = new HashMap<>(255);
	KeyAdapter keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			try {
				events.get(e.getKeyChar()).run();
			} catch (Exception e1) {}
		}
	};
	
	// Simplest constructor, everythisg is default
	public GFrame(int fps, int ups, RenderCallback rc, UpdateCallback uc) {
		this(0, 0, true, fps, ups, rc, uc);
	}

	// User can set xPos, yPos and whenever the frame will be centered of not
	// is 'center' is true, 'x' and 'y' are ignored
	public GFrame(int x, int y, boolean center, int fps, int ups, RenderCallback rc, UpdateCallback uc) {
		//final boolean centered = true; // Default for 'centered'
		//final int x = 0; // Doesn't matter since 'centered' is true
		//final int y = 0; // Same
		final int width = 500; // Default for 'width'
		final int height = 500; // Default for 'height'
		final String title = "Render:"; // Default title
		
		this.center = center;
		this.ups = ups;
		
		internal_frame = new JFrame(title);
		try {
			BufferedImage img = ImageIO.read(new BufferedInputStream(GFrame.class.getResourceAsStream("/logo.png")));
			internal_frame.setIconImage(img);
		} catch (Exception e) {
			e.printStackTrace();
		}
		internal_frame.setResizable(false);
		internal_frame.setLayout(new BoxLayout(internal_frame.getContentPane(), BoxLayout.Y_AXIS));
		internal_frame.addKeyListener(keyListener);
		
		internal_frame.addWindowListener(shutdownListener);
		
		draw_canvas = new Canvas();
		draw_canvas.addKeyListener(keyListener);
		internal_frame.add(draw_canvas);
		
		
		draw_canvas.setPreferredSize(new Dimension(width, height)); // Resize the canvas instead of frame because we will be using its coordinate space
		
		internal_frame.pack();
		
		if(center)
			internal_frame.setLocationRelativeTo(null);
		else
			internal_frame.setLocation(x, y);
		
		syncer = new Syncer(fps);
		
		exitOnClose(true); // Default
		
		Input.init(draw_canvas);
		
		instantiated = true; // No return
		
		setDrawCallback(rc);
		setUpdateCallback(uc);
	}
	
	public final void start() {
		if(draw_callback == null)
			throw new IllegalStateException("RenderCallback is not set!");
		
		this.renderer = new Thread(this::frame_loop);
		this.updater  = new Thread(this::update_loop);
		
		internal_frame.setVisible(true); // Allows sleep() in update loop(?)
		
		//update_callback.update();
		
		if(!synchronize) {
			updater.start();
		}
		renderer.start();
	}
	
	private void frame_loop() {
		draw_canvas.createBufferStrategy(2);
		BufferStrategy bs = draw_canvas.getBufferStrategy();
		
		final int w = width();
		final int h = height();
		
		while(shouldLoop) {
			if(pause) {
				Thread.yield(); // Bad
				continue;
			}
			
			if(synchronize) {
				updateCall();
			}
			
			renderCall(w, h, bs);
		}
	}
	
	private final int maxConsecutiveErrorsRender = 5;
	private final int maxConsecutiveErrorsUpdate = 5;
	private int consecutiveErrorsRender = 0;
	private int consecutiveErrorsUpdate = 0;
	
	private void renderCall(final int w, final int h, final BufferStrategy bs) {
		Renderer g = new Renderer((Graphics2D) bs.getDrawGraphics(), this);
		g.color(clearColor);
		g.g2d().fillRect(0, 0, w, h);
		
		try {
			draw_callback.render(g);
		} catch (Exception e) {
			consecutiveErrorsRender++;
			if(consecutiveErrorsRender > maxConsecutiveErrorsRender)
				throw e;
			e.printStackTrace();
		}
		consecutiveErrorsRender = 0; // If no error happened, reset
		
		g.g2d().dispose();
		bs.show();
		
		syncer.sync();
	}
	
	private void update_loop() {
		while(shouldLoop) {
			if(pause) {
				Thread.yield();
				continue;
			}
			try {
				updateCall();
			} catch (Exception e) {
				consecutiveErrorsUpdate++;
				if(consecutiveErrorsUpdate > maxConsecutiveErrorsUpdate)
					throw e;
				e.printStackTrace();
			}
			consecutiveErrorsUpdate = 0; // If no error happened, reset
		}
	}
	private void updateCall() {
		if(update_callback != null && ups != 0) {
			update_callback.update();
			if(ups > 0)
				try{Thread.sleep(1000 / ups);}catch(Exception e) {}
		}
		Thread.yield();
	}
	
	
	public void setUps(int ups) {
		this.ups = ups;
	}
	
	public void addSlider(String label, int begin, int end, int start, IntConsumer action) {
		GSlider slider = new GSlider(label, begin, end, start);
		
		slider.listener((val) -> action.accept(val.getValue()));
		
		internal_frame.add(slider);
		internal_frame.pack();
//		internal_frame.setLayout(null);
	}
	
	public void setIcon(Image img) {
		internal_frame.setIconImage(img);
	}
	
	public void addButtons(GButton... btns) {
		GButtonPane pane = new GButtonPane(btns);
		
		internal_frame.add(pane);
		internal_frame.pack();
	}
	
	public void addField(GTextField field) {
		internal_frame.add(field);
		internal_frame.pack();
	}
	
	public void addLabel(GLabelText text) {
		internal_frame.add(text);
		internal_frame.pack();
	}
	
	public void addCustomPane(GCustomPane pane) {
		internal_frame.add(pane);
		internal_frame.pack();
	}
	
	private volatile boolean pause = false;
	public void pause(boolean shoud) {
		pause = shoud;
	}
	
	public boolean isPaused() {
		return pause;
	}
	
	public JFrame getInternal_frame() {
		return internal_frame;
	}
	
	public Canvas getDrawCanvas() {
		return draw_canvas;
	}
	
	
	public int width() {
		return draw_canvas.getWidth();
	}
	public int height() {
		return draw_canvas.getHeight();
	}
	
	public int windowWidth() {
		return internal_frame.getWidth();
	}
	public int windowHeight() {
		return internal_frame.getHeight();
	}
	
	public void dispose() {
		if(!internal_frame.isVisible() || !updater.isAlive())
			throw new IllegalStateException("Nothing to dispose!");
		
		shouldLoop = false;
		try {
			updater.join();
		} catch (InterruptedException e) {
			// Do nothing
		}
		internal_frame.dispose();
	}
	
	// 'Draw' is always called after 'update'
	public void setDrawCallback(RenderCallback callback) {
		this.draw_callback = callback;
	}
	
	// 'Update' is always called before 'draw'
	public void setUpdateCallback(UpdateCallback callback) {
		this.update_callback = callback;
	}
	
	public void exitOnClose(boolean val) {
		internal_frame.setDefaultCloseOperation(val ? JFrame.EXIT_ON_CLOSE : JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	public void background(Color c) {
		this.clearColor = c;
	}
	
	public void background(int c) {
		this.clearColor = new Color(c);
	}
	
	public void background(int r, int g, int b) {
		this.clearColor = new Color(r, g, b);
	}
	
	
	private volatile boolean synchronize = false;
	
	public void synchronize(boolean should) {
		if(renderer != null || updater != null) {
			System.err.println("Can't change this when running");
		} else
			this.synchronize = should;
	}
	
	private static final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank");
	
	public void hideCursor(boolean should) {
		draw_canvas.setCursor(should ? blankCursor : Cursor.getDefaultCursor());
	}
	
	public void setSize(int width, int height) {
		draw_canvas.setPreferredSize(new Dimension(width, height));
		draw_canvas.setSize(new Dimension(width, height));
		internal_frame.pack();
		
		if(center)
			internal_frame.setLocationRelativeTo(null);
	}
	
	public void shouldCenter(boolean should) {
		center = should;
	}
	
	public void setTitle(String title) {
		internal_frame.setTitle(title);
	}
	
	private final Queue<Runnable> shutdownRoutines = new LinkedList<>();
	private final WindowAdapter shutdownListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			shutdownRoutines.forEach(Runnable::run);
		}
	};
	public void addShutdownRoutine(Runnable r) {
		shutdownRoutines.add(r);
	}
	
	public void onKeyPress(char ch, Runnable action) {
		events.put(ch, action);
	}
	
	private static class Syncer {
		private long sleepTime;
		
		private long variableYieldTime;
		private long lastTime;
		
		private Syncer(int desiredFps) {
			sleepTime = 1000000000 / desiredFps;
		}
		
		public void sync() {
			final long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
			long overSleep = 0;
			
			try {
				while (true) {
					
					long t = getTime() - lastTime;
					
					if (t < sleepTime - yieldTime) {
						Thread.sleep(1);
					}
					else if (t < sleepTime) {
						Thread.yield();
					}
					else {
						overSleep = t - sleepTime;
						break;
					}
				}
			} catch (InterruptedException e) {}
			
			lastTime = getTime() - Math.min(overSleep, sleepTime);
			
			if (overSleep > variableYieldTime) {
				variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
			}
			else if (overSleep < variableYieldTime - 200*1000) {
				variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
			}
		}
		private long getTime() {
			return (System.nanoTime());
		}
	}
	
	// Simple initialization //
	// Uses reflection to add render and update callbacks
	// Caller instance is and optional argument used if the render/update methods are not static
	public static GFrame auto(Object... callerInstance) {
		Method render = null;
		Method update = null;
		Object callerClass = null;
		try {
			callerClass = callerInstance[0];
		} catch(Exception e) {}
		Class<?> clazz = getCallerClass();
		boolean renderStatic = true;
		boolean updateStatic = true;
		
		try {
			render = clazz.getDeclaredMethod("render", Renderer.class);
			render.setAccessible(true);
			renderStatic = Modifier.isStatic(render.getModifiers());
		} catch (NoSuchMethodException e) {}
		try {
			update = clazz.getDeclaredMethod("update");
			update.setAccessible(true);
			updateStatic = Modifier.isStatic(update.getModifiers());
		} catch (NoSuchMethodException e) {}
		
		final Method stupidFinalRender = render;
		final Method stupidFinalUpdate = update;
		final Object stupidFinalCallerClass = callerClass;
		
		// Render func
		
		RenderCallback rFunc = r -> {};
		if(render != null) {
			if(renderStatic) {
				rFunc = r -> {
					try {
						stupidFinalRender.invoke(null, r);
					} catch (InvocationTargetException ite) {
						ite.getCause().printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			} else if(stupidFinalCallerClass != null){
				try {
					stupidFinalCallerClass.getClass().getDeclaredMethod("render", Renderer.class);
					rFunc = r -> {
						try {
							stupidFinalRender.invoke(stupidFinalCallerClass, r);
						} catch (InvocationTargetException ite) {
							ite.getCause().printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Update func
		
		UpdateCallback uFunc = () -> {};
		if(update != null) {
			if(updateStatic) {
				uFunc = () -> {
					try {
						stupidFinalUpdate.invoke(null);
					} catch (InvocationTargetException ite) {
						ite.getCause().printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			} else if(stupidFinalCallerClass != null){
				try {
					stupidFinalCallerClass.getClass().getDeclaredMethod("update");
					uFunc = () -> {
						try {
							stupidFinalUpdate.invoke(stupidFinalCallerClass);
						} catch (InvocationTargetException ite) {
							ite.getCause().printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		GFrame f = new GFrame(60, 60, rFunc, uFunc);
		return f;
	}
	
	// Simple initialization //
	public static GFrame quick(RenderCallback r, UpdateCallback u) {
		GFrame f = new GFrame(60, 60, r, u);
		f.synchronize(true);
		f.start();
		return f;
	}
	
	private static Class<?> getCallerClass() { 
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(GFrame.class.getName())
					&& ste.getClassName().indexOf("java.lang.Thread") != 0) {
				try {
					return Class.forName(ste.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
     }
}
