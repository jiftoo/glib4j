package io.x666c.glib4j.components;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GLabelText extends JPanel {
	
	private JLabel label;
	
	public GLabelText() {
		this(" ");
	}
	
	public GLabelText(String text) {
		label = new JLabel(text);
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		add(label);
	}
	
	public JLabel getLabel() {
		return label;
	}
	
	public String getText() {
		return label.getText();
	}
	
	public void setText(String str) {
		label.setText(str);
	}
	
}