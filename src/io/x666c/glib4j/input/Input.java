package io.x666c.glib4j.input;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import io.x666c.glib4j.math.vector.Point;
import io.x666c.glib4j.math.vector.Vector;

public class Input {
	
	private static Component component;
	private static final FocusAdapter focusAdapter = new FocusAdapter() {
		public void focusLost(java.awt.event.FocusEvent e) {
			mouse.reset();
			keyboard.reset();
		};
	};
	
	public static MouseInput 	  mouse;
	public static KeyboardInput keyboard;
	
	public static void init(Component component) {
		Input.component = component;
		component.addFocusListener(focusAdapter);
		
		mouse = new MouseInput();
		keyboard = new KeyboardInput();
	}
	
	public static final class MouseInput {
		
		private static final boolean[] arr	   = new boolean[6];
		private static final boolean[] arrOnce = new boolean[6];
		
		private MouseInput() {
			component.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {
					int btn = e.getButton();
					if(btn < arr.length) {
						arr[btn] = false;
						arrOnce[btn] = false;
					}
				}
				public void mousePressed(MouseEvent e) {
					int btn = e.getButton();
					if(btn < arr.length) {
						arr[btn] = true;
						arrOnce[btn] = true;
					}
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
		
		// Now returns null instead of (0;0)
		public Point xy(int offsetX, int offsetY) {
			java.awt.Point p = component.getMousePosition();
			if(p != null)
				return new Point(p.x - offsetX, p.y - offsetY);
			return null;
		}
		
		// Now returns null instead of {0;0}
		public Vector vec_xy(int offsetX, int offsetY) {
			java.awt.Point p = component.getMousePosition();
			if(p != null)
				return Vector.create(p.x - offsetX, p.y - offsetY);
			return null;
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
		public boolean mouse4() {
			return arr[4];
		}
		public boolean mouse5() {
			return arr[5];
		}
		
		public boolean lmbOnce() {
			boolean b = arrOnce[MouseEvent.BUTTON1];
			arrOnce[MouseEvent.BUTTON1] = false;
			return b;
		}
		public boolean mmbOnce() {
			boolean b = arrOnce[MouseEvent.BUTTON2];
			arrOnce[MouseEvent.BUTTON2] = false;
			return b;
		}

		public boolean rmbOnce() {
			boolean b = arrOnce[MouseEvent.BUTTON3];
			arrOnce[MouseEvent.BUTTON3] = false;
			return b;
		}
		public boolean mouse4Once() {
			boolean b = arrOnce[4];
			arrOnce[4] = false;
			return b;
		}
		public boolean mouse5Once() {
			boolean b = arrOnce[5];
			arrOnce[5] = false;
			return b;
		}
		
		private void reset() {
			final boolean[] empty = new boolean[arr.length];
			System.arraycopy(empty, 0, arr, 0, empty.length);
		}
	}
	
	public static final class KeyboardInput {
		
		private static final boolean[] keys = new boolean[Short.MAX_VALUE + 1];
		private static final boolean[] keysOnce = new boolean[Short.MAX_VALUE + 1];
		private static char lastPressedKey = '\0';
		private static char lastPressedKeyOnce = '\0';
		
		private KeyboardInput() {
			component.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {}
				
				public void keyReleased(KeyEvent e) {
					keys[e.getKeyCode()] = false;
					keysOnce[e.getKeyCode()] = false;
					lastPressedKey = '\0';
					lastPressedKeyOnce = '\0';
				}
				public void keyPressed(KeyEvent e) {
					keys[e.getKeyCode()] = true;
					keysOnce[e.getKeyCode()] = true;
					lastPressedKey = e.getKeyChar();
					lastPressedKeyOnce = e.getKeyChar();
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
		
		
		public boolean keyOnce(char ch) {
			final boolean ret = keys[KeyEvent.getExtendedKeyCodeForChar(ch)];
			keys[KeyEvent.getExtendedKeyCodeForChar(ch)] = false;
			return ret;
		}
		
		public boolean keyOnce(int keycode) {
			if(keycode > keys.length)
				return false;
			
			final boolean ret = keys[keycode];
			keys[keycode] = false;
			
			return ret;
		}
		
		public char last() {
			return lastPressedKey;
		}
		
		public char lastOnce() {
			final char ret = lastPressedKeyOnce;
			lastPressedKeyOnce = '\0';
			return ret;
		}
		
		
		
		private void reset() {
			final boolean[] empty = new boolean[keys.length];
			System.arraycopy(empty, 0, keys, 0, empty.length);
		}
	}
	
}
