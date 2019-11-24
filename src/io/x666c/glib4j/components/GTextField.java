package io.x666c.glib4j.components;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GTextField extends JPanel {
	
	private final static Consumer<String> NOP_CALLBACK = s->{};
	
	private Consumer<String> callback = null;
	
	private final JTextField field;
	private final JLabel label;
	
	public GTextField() {
		this("Field", NOP_CALLBACK);
	}
	
	public GTextField(String label) {
		this(label, NOP_CALLBACK);
	}
	
	public GTextField(String labels, Consumer<String> editCallback) {
		callback = editCallback;
		
		setLayout(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(4, 4, 2, 3));
		
		field = new JTextField();
		label = new JLabel(labels);
		
		add(field, BorderLayout.CENTER);
		add(label, BorderLayout.WEST);
		
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				callback.accept(getText());
			}
			public void insertUpdate(DocumentEvent e) {
				callback.accept(getText());
			}
			public void changedUpdate(DocumentEvent e) {
				callback.accept(getText());
			}
		});
	}
	
	
	public String getName() {
		return label.getText();
	}
	
	public String getText() {
		return field.getText();
	}
	
	@SafeVarargs
	public final void addCallbacks(Consumer<String> editCallback, Consumer<String>... more) {
		if(callback == null)
			callback = editCallback;
		else
			callback = callback.andThen(editCallback);
		
		for (int i = 0; i < more.length; i++) {
			callback = callback.andThen(more[i]);
		}
	}
	
}
