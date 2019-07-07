package net.x666c.glib.input;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.x666c.glib.math.vector.Point;
import net.x666c.glib.math.vector.Vector;

public class Input {
	
	private static Component component;
	
	public static MouseInput 	  mouse;
	public static KeyboardInput keyboard;
	
	public static void init(Component component) {
		Input.component = component;
		
		mouse = new MouseInput();
		keyboard = new KeyboardInput();
	}
	
	public static final class MouseInput {
		
		private static final boolean[] arr = new boolean[4];
		
		private MouseInput() {
			component.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {
					arr[e.getButton()] = false;
				}
				public void mousePressed(MouseEvent e) {
					arr[e.getButton()] = true;
				}
				public void mouseExited(MouseEvent e) {
					
				}
				public void mouseEntered(MouseEvent e) {
					
				}
				public void mouseClicked(MouseEvent e) {
					
				}
			});
		}
		
		public int x() {
			java.awt.Point p = component.getMousePosition();
			if(p != null)
				return p.x;
			return -1;
		}
		
		public int y() {
			java.awt.Point p = component.getMousePosition();
			if(p != null)
				return p.y;
			return -1;
		}
		
		public Point xy() {
			java.awt.Point p = component.getMousePosition();
			if(p != null)
				return new Point(p.x, p.y);
			return Point.zero();
		}
		
		public Point xy(int offsetX, int offsetY) {
			java.awt.Point p = component.getMousePosition();
			if(p != null)
				return new Point(p.x - offsetX, p.y - offsetY);
			return Point.zero();
		}
		
		public Vector vec_xy(int offsetX, int offsetY) {
			java.awt.Point p = component.getMousePosition();
			return Vector.create(p.x - offsetX, p.y - offsetY);
		}
		
		public boolean lmb() {
			return arr[MouseEvent.BUTTON1];
		}
		
		public boolean mmb() {
			return arr[MouseEvent.BUTTON2];
		}

		public boolean rmb() {
			return arr[MouseEvent.BUTTON3];
		}
	}
	
	public static final class KeyboardInput {
		
		private static final boolean[] keys = new boolean[512];
		
		private KeyboardInput() {
			component.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
					
				}
				public void keyReleased(KeyEvent e) {
					keys[e.getKeyCode()] = false;
				}
				public void keyPressed(KeyEvent e) {
					keys[e.getKeyCode()] = true;
				}
			});
		}
		
		public boolean key(char ch) {
			return keys[KeyEvent.getExtendedKeyCodeForChar(ch)];
		}
		
		public boolean key(int keycode) {
			if(keycode > keys.length)
				return false;
			return keys[keycode];
		}
		
	}
	
}
