package io.x666c.glib4j.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;

public class GSlider extends JPanel {
	
	private final JSlider slider;
	private final JLabel label;
	
	public GSlider(String text, int start, int end, int val) {
		setLayout(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(4, 4, 2, 3));
		
		add(label = new JLabel(text), BorderLayout.WEST);
		add(slider = new JSlider(start, end, val), BorderLayout.CENTER);
		
		slider.setFocusable(false);
		label.setFocusable(false);
		
		label.setForeground(Color.BLACK);
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		
		slider.setUI(new CustomSliderUI(slider));
	}
	
	public void listener(Consumer<JSlider> event) {
		slider.addChangeListener(ev -> event.accept((JSlider) ev.getSource()));
	}
	
	
	public class CustomSliderUI extends BasicSliderUI {
		private static final int THUMB_RADIUS = 14;
	    private BasicStroke stroke = new BasicStroke(4f);

	    public CustomSliderUI(JSlider b) {super(b);}

	    public void paint(Graphics g, JComponent c) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			super.paint(g, c);
	    }

	    protected Dimension getThumbSize() {
	        return new Dimension(THUMB_RADIUS + 1, THUMB_RADIUS + 1);
	    }

	    public void paintTrack(Graphics g) {
	        Graphics2D g2d = (Graphics2D) g;
	        Stroke old = g2d.getStroke();
			g2d.setStroke(stroke);
			g2d.setPaint(new Color(0xAAAAAA));
			g2d.fillOval(trackRect.x - (int)stroke.getLineWidth(), trackRect.y + trackRect.height / 2 - 1, (int)stroke.getLineWidth(), (int)stroke.getLineWidth());
			g2d.drawLine(trackRect.x, trackRect.y + trackRect.height / 2, trackRect.x + trackRect.width,
					trackRect.y + trackRect.height / 2);
			g2d.fillOval(trackRect.x + trackRect.width + (int)stroke.getLineWidth() / 2,
					trackRect.y + trackRect.height / 2 - 1, (int)stroke.getLineWidth(), (int)stroke.getLineWidth());
			g2d.setStroke(old);
	    }

	    public void paintThumb(Graphics gg) {
	        Graphics2D g = (Graphics2D) gg;
	        int x = thumbRect.x + thumbRect.width / 2 - THUMB_RADIUS / 2;
	        int y = thumbRect.y + thumbRect.height / 2 - THUMB_RADIUS / 2;
	        
	        g.setColor(new Color(0xBBBBBB));
	        g.fillOval(x, y, THUMB_RADIUS, THUMB_RADIUS);
	        g.setColor(new Color(0x959595));
	        g.setStroke(new BasicStroke(1.6f));
	        g.drawOval(x, y, THUMB_RADIUS, THUMB_RADIUS);
	    }
	}
}