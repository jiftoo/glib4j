package net.x666c.glib.components;

import java.awt.Color;

import javax.swing.JButton;

public class GButton {
	
	private JButton btn;
	
	public GButton(String label, Color color, Runnable action) {
		btn = new JButton(label);
		btn.addActionListener(action != null ? a -> action.run() : (a)->{return;});
		btn.setBackground(color);
		btn.setFocusable(false);
	}
	
	public GButton(String label, Runnable action) {
		this(label, Color.GRAY, action);
	}
	
	public GButton(Runnable action) {
		this("Button", action);
	}
	
	public JButton getBtn() {
		return btn;
	}
}