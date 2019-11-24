package io.x666c.glib4j;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import io.x666c.glib4j.graphics.DisplayRenderer;
import io.x666c.glib4j.graphics.Renderer;
import io.x666c.glib4j.input.Input;

public final class GDisplay extends Canvas {
	
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
		if (GFrame.instantiated)
			throw new RuntimeException("There can be only one gframe/gdisplay instance per program!");
	}

	private final Map<Character, Runnable> events = new HashMap<>(255);
	KeyAdapter keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			try {
				events.get(e.getKeyChar()).run();
			} catch (Exception e1) {
			}
		}
	};

	// Simplest constructor, everythisg is default
	public GDisplay(int fps, int ups, RenderCallback rc, UpdateCallback uc) {
		this(0, 0, fps, ups, rc, uc);
	}

	// User can set xPos, yPos and whenever the frame will be centered of not
	// is 'center' is true, 'x' and 'y' are ignored
	public GDisplay(int x, int y, int fps, int ups, RenderCallback rc, UpdateCallback uc) {
		// final boolean centered = true; // Default for 'centered'
		// final int x = 0; // Doesn't matter since 'centered' is true
		// final int y = 0; // Same
		final int width = 500; // Default for 'width'
		final int height = 500; // Default for 'height'

		this.ups = ups;

		addKeyListener(keyListener);

		setPreferredSize(new Dimension(width, height)); // Resize the canvas instead of frame because we
																	// will be using its coordinate space

		syncer = new Syncer(fps);

		Input.init(this);

		GFrame.instantiated = true; // No return

		setDrawCallback(rc);
		setUpdateCallback(uc);
	}

	public final void start() {
		if (draw_callback == null)
			throw new IllegalStateException("UpdateCallback and/or RenderCallback is not set!");

		this.renderer = new Thread(this::frame_loop);
		this.updater = new Thread(this::update_loop);

		// update_callback.update();

		if (!synchronize) {
			updater.start();
		}
		renderer.start();
	}

	private void frame_loop() {
		createBufferStrategy(2);
		BufferStrategy bs = getBufferStrategy();

		final int w = width();
		final int h = height();

		while (shouldLoop) {
			if (pause) {
				Thread.yield(); // Bad
				continue;
			}

			if (synchronize) {
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
		Renderer g = new DisplayRenderer((Graphics2D) bs.getDrawGraphics(), this);
		g.color(clearColor);
		g.g2d().fillRect(0, 0, w, h);

		try {
			draw_callback.render(g);
		} catch (Exception e) {
			consecutiveErrorsRender++;
			if (consecutiveErrorsRender > maxConsecutiveErrorsRender)
				throw e;
			e.printStackTrace();
		}
		consecutiveErrorsRender = 0; // If no error happened, reset

		g.g2d().dispose();
		bs.show();

		syncer.sync();
	}

	private void update_loop() {
		while (shouldLoop) {
			if (pause) {
				Thread.yield();
				continue;
			}
			try {
				updateCall();
			} catch (Exception e) {
				consecutiveErrorsUpdate++;
				if (consecutiveErrorsUpdate > maxConsecutiveErrorsUpdate)
					throw e;
				e.printStackTrace();
			}
			consecutiveErrorsUpdate = 0; // If no error happened, reset
		}
	}

	private void updateCall() {
		if (update_callback != null && ups != 0) {
			update_callback.update();
			if (ups > 0)
				try {
					Thread.sleep(1000 / ups);
				} catch (Exception e) {
				}
		}
		Thread.yield();
	}

	public void setUps(int ups) {
		this.ups = ups;
	}

	private volatile boolean pause = false;

	public void pause(boolean shoud) {
		pause = shoud;
	}

	public boolean isPaused() {
		return pause;
	}

	public int width() {
		return getWidth();
	}

	public int height() {
		return getHeight();
	}

	public void dispose() {
		if (!isVisible() || !updater.isAlive())
			throw new IllegalStateException("Nothing to dispose!");

		shouldLoop = false;
		try {
			updater.join();
		} catch (InterruptedException e) {
			// Do nothing
		}
		dispose();
	}

	// 'Draw' is always called after 'update'
	public void setDrawCallback(RenderCallback callback) {
		this.draw_callback = callback;
	}

	// 'Update' is always called before 'draw'
	public void setUpdateCallback(UpdateCallback callback) {
		this.update_callback = callback;
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
		if (renderer != null || updater != null) {
			System.err.println("Can't change this when running");
		} else
			this.synchronize = should;
	}

	private static final Cursor blankCursor = Toolkit.getDefaultToolkit()
			.createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank");

	public void hideCursor(boolean should) {
		setCursor(should ? blankCursor : Cursor.getDefaultCursor());
	}

	public void setDisplaySize(int width, int height) {
		super.setPreferredSize(new Dimension(width, height));
		super.setSize(width, height);
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
			final long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000 * 1000));
			long overSleep = 0;

			try {
				while (true) {

					long t = getTime() - lastTime;

					if (t < sleepTime - yieldTime) {
						Thread.sleep(1);
					} else if (t < sleepTime) {
						Thread.yield();
					} else {
						overSleep = t - sleepTime;
						break;
					}
				}
			} catch (InterruptedException e) {
			}

			lastTime = getTime() - Math.min(overSleep, sleepTime);

			if (overSleep > variableYieldTime) {
				variableYieldTime = Math.min(variableYieldTime + 200 * 1000, sleepTime);
			} else if (overSleep < variableYieldTime - 200 * 1000) {
				variableYieldTime = Math.max(variableYieldTime - 2 * 1000, 0);
			}
		}

		private long getTime() {
			return (System.nanoTime());
		}
	}

	// Simple initialization //
	// Uses reflection to add render and update callbacks
	// Caller instance is and optional argument used if the render/update methods
	// are not static
	public static GFrame auto(Object... callerInstance) {
		Method render = null;
		Method update = null;
		Object callerClass = null;
		try {
			callerClass = callerInstance[0];
		} catch (Exception e) {
		}
		Class<?> clazz = getCallerClass();
		boolean renderStatic = true;
		boolean updateStatic = true;

		try {
			render = clazz.getDeclaredMethod("render", Renderer.class);
			render.setAccessible(true);
			renderStatic = Modifier.isStatic(render.getModifiers());
		} catch (NoSuchMethodException e) {
		}
		try {
			update = clazz.getDeclaredMethod("update");
			update.setAccessible(true);
			updateStatic = Modifier.isStatic(update.getModifiers());
		} catch (NoSuchMethodException e) {
		}

		final Method stupidFinalRender = render;
		final Method stupidFinalUpdate = update;
		final Object stupidFinalCallerClass = callerClass;

		// Render func

		RenderCallback rFunc = r -> {
		};
		if (render != null) {
			if (renderStatic) {
				rFunc = r -> {
					try {
						stupidFinalRender.invoke(null, r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			} else if (stupidFinalCallerClass != null) {
				try {
					stupidFinalCallerClass.getClass().getDeclaredMethod("render", Renderer.class);
					rFunc = r -> {
						try {
							stupidFinalRender.invoke(stupidFinalCallerClass, r);
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

		UpdateCallback uFunc = () -> {
		};
		if (update != null) {
			if (updateStatic) {
				uFunc = () -> {
					try {
						stupidFinalUpdate.invoke(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			} else if (stupidFinalCallerClass != null) {
				try {
					stupidFinalCallerClass.getClass().getDeclaredMethod("update");
					uFunc = () -> {
						try {
							stupidFinalUpdate.invoke(stupidFinalCallerClass);
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
