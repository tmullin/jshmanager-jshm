package jshm.gui.components;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * This class extends JDialog to fire a {@link WindowEvent#WINDOW_CLOSING}
 * event when the user presses the escape key.
 * @author Tim Mullin
 *
 */
public class EscapeableDialog extends JDialog {
	public EscapeableDialog() {
		this(null);
	}
	
	public EscapeableDialog(Frame parent) {
		this(parent, true);
	}
	
	public EscapeableDialog(Frame parent, boolean modal) {
		super(parent, modal);
	}
	
	protected JRootPane createRootPane() {
		JRootPane rootPane = super.createRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		Action actionListener = new AbstractAction() { 
			public void actionPerformed(ActionEvent actionEvent) { 
				EscapeableDialog.this.dispatchEvent(new WindowEvent( 
					EscapeableDialog.this, WindowEvent.WINDOW_CLOSING 
				)); 
			}
		};
		
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}
}
