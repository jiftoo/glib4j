package io.x666c.glib4j.components;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GCustomPane extends JPanel {
	
	private static final ArrayList<GCustomPane> ALL_PANES = new ArrayList<>();
	private static final Timer PANE_UPDATER = new Timer(20, ev -> {
		for (GCustomPane p : ALL_PANES) {
			p.repaint();
		}
	});
	static {
		PANE_UPDATER.start();
	}
	
	private final BiConsumer<Graphics2D, GCustomPane> render;
	private final Canvas drawCanvas;
	
	private final int height;
	
	public GCustomPane(int height, BiConsumer<Graphics2D, GCustomPane> drawFunc) {
		render = drawFunc;
		drawCanvas = new Canvas();
		this.height = height;
		
		setBorder(null);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, height));
		
		add(drawCanvas, BorderLayout.CENTER);
		
		ALL_PANES.add(this);
		
		drawCanvas.addMouseListener(paneMouseListener);
	}
	
	private final ArrayList<ClickableArea> clickableAreas = new ArrayList<>();
	
	public void addClickableArea(String id, int x, int y, int w, int h, MouseListener actions) {
		clickableAreas.add(new ClickableArea(id, new Rectangle(x, y, w, h), actions));
	}
	
	public ClickableArea getClickableArea(String id) {
		for (ClickableArea ca : clickableAreas) {
			if(ca.id.equals(id))
				return ca;
		}
		return null;
	}
	
	public class ClickableArea {
		private final Rectangle area;
		private final MouseListener actions;
		private final String id;
		
		private volatile boolean pressed = false;
		private volatile boolean hover = false;
		
		private ClickableArea(String idString, Rectangle r, MouseListener a) {
			area = r;
			actions = a;
			id = idString;
		}
		
		public boolean hover() {
			return hover;
		}
		public boolean pressed() {
			return pressed;
		}
	}
	
	private MouseListener paneMouseListener = new MouseListener() {
		public void mouseReleased(MouseEvent e) {
			for (ClickableArea ca : clickableAreas) {
				if(ca.area.contains(e.getPoint())) {
					ca.actions.mouseReleased(e);
					ca.pressed = false;
				}
			}
		}
		public void mousePressed(MouseEvent e) {
			for (ClickableArea ca : clickableAreas) {
				if(ca.area.contains(e.getPoint())) {
					ca.actions.mousePressed(e);
					ca.pressed = true;
				}
			}
		}
		public void mouseExited(MouseEvent e) {
			for (ClickableArea ca : clickableAreas) {
				if(ca.area.contains(e.getPoint())) {
					ca.actions.mouseExited(e);
					ca.hover = false;
					ca.pressed = false;
				}
			}
		}
		public void mouseEntered(MouseEvent e) {
			for (ClickableArea ca : clickableAreas) {
				if(ca.area.contains(e.getPoint())) {
					ca.actions.mouseEntered(e);
					ca.hover = true;
				}
			}
		}
		public void mouseClicked(MouseEvent e) {
			for (ClickableArea ca : clickableAreas) {
				if(ca.area.contains(e.getPoint())) {
					ca.actions.mouseClicked(e);
					ca.pressed = false;
				}
			}
		}
	};
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		BufferStrategy bs = drawCanvas.getBufferStrategy();
		if(bs == null) {
			drawCanvas.createBufferStrategy(2);
			bs = drawCanvas.getBufferStrategy();
		}
		
		final Graphics2D g2d = (Graphics2D)bs.getDrawGraphics();
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, (int)getSize().getWidth(), height);
		g2d.setColor(Color.WHITE);
		render.accept(g2d, this);
		
		g2d.dispose();
		bs.show();
	}
	
}