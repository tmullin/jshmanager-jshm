package jshm.gui.components;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * This is a text field that displays a default text value
 * that is grayed out until a text value explicitly set.
 * Similar to some websites that have search boxes that say "Search"
 * until you type something in.
 * 
 * @author Tim Mullin
 *
 */
public class LabeledTextField extends JTextField {
	private Color fadedColor = Color.GRAY;
	private Color lastForeground;
	private String defaultText = "";
	private boolean usingDefaultText = true;
	
	public LabeledTextField() {
		this(null);
	}
	
	public LabeledTextField(String text) {
		this(text, null);
	}
	
	public LabeledTextField(String defaultText, Color fadedColor) {
		super(defaultText);
		this.defaultText = defaultText;
		
		if (null != fadedColor)
			this.fadedColor = fadedColor;
		
		addFocusListener(new MyFocusListener());
	}
	
	public void setDefaultText(String newText) {
		usingDefaultText = true;
		this.defaultText = newText;
		setForeground(fadedColor);
		super.setText(defaultText);
	}
	
	public String getDefaultText() {
		return defaultText;
	}
	
	public void resetDefaultText() {
		setDefaultText(defaultText);
	}
	
	public boolean isUsingDefaultText() {
		return usingDefaultText;
	}
	
	public Color getFadedColor() {
		return fadedColor;
	}

	public void setFadedColor(Color fadedColor) {
		this.fadedColor = fadedColor;
	}

	public void setText(String text) {		
		usingDefaultText = false;
		setForeground(lastForeground);
		super.setText(text);
	}
	
	private class MyFocusListener implements FocusListener {
		@Override public void focusGained(FocusEvent e) {
			if (usingDefaultText && isEditable()) {
				setText("");
			}
		}

		@Override public void focusLost(FocusEvent e) {
			if ("".equals(getText())) {
				resetDefaultText();
			}
		}
	}
}
