package net.x666c.glib;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.IntConsumer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import net.x666c.glib.components.GButton;
import net.x666c.glib.components.GButtonPane;
import net.x666c.glib.components.GSlider;
import net.x666c.glib.graphics.Renderer;
import net.x666c.glib.input.Input;

public final class GFrame {
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
		internal_frame.setResizable(false);
		internal_frame.setLayout(new BoxLayout(internal_frame.getContentPane(), BoxLayout.Y_AXIS));
		internal_frame.addKeyListener(keyListener);
		
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
		
		setDrawCallback(rc);
		setUpdateCallback(uc);
	}
	
	public final void start() {
		if(draw_callback == null)
			throw new IllegalStateException("UpdateCallback and/or RenderCallback is not set!");
		
		this.renderer = new Thread(this::frame_loop);
		this.updater  = new Thread(this::update_loop);
		
		update_callback.update();
		
		if(!synchronize) {
			updater.start();
		}
		renderer.start();
	}
	
	private void frame_loop() {
		internal_frame.setVisible(true);
		
		draw_canvas.createBufferStrategy(2);
		BufferStrategy bs = draw_canvas.getBufferStrategy();
		
		final int w = width();
		final int h = height();
		
		while(shouldLoop) {
			if(pause) {
				Thread.yield();
				continue;
			}
			
			if(synchronize) {
				updateCall();
			}
			
			renderCall(w, h, bs);
		}
	}
	private void renderCall(final int w, final int h, final BufferStrategy bs) {
		Renderer g = new Renderer((Graphics2D) bs.getDrawGraphics(), this);
		g.color(clearColor);
		g.g2d().fillRect(0, 0, w, h);
		
		draw_callback.render(g);
		
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
			
			updateCall();
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
	
	public void addButtons(GButton... btns) {
		GButtonPane pane = new GButtonPane(btns);
		
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
}
