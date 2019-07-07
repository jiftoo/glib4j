package net.x666c.glib.util;


public final class FixedStack<TYPE> {

	private TYPE[] stack;
	private int size;
	private int top;

	public FixedStack(int size) {
		this.stack = (TYPE[]) new Object[size];
		this.top = -1;
		this.size = size;
	}

	public void push(TYPE obj) {
		if (top >= size-1) {
			throw new StackOverflowError((top+2) + " > " + (size));
		}
		stack[++top] = obj;
	}

	public TYPE pop() {
		if (top < 0)
			throw new StackUnderflowException();
		TYPE obj = stack[top--];
		stack[top + 1] = null;
		return obj;
	}

	public int size() {
		return size;
	}

	public int elements() {
		return top + 1;
	}
	
	
	private static class StackUnderflowException extends RuntimeException {}
}
