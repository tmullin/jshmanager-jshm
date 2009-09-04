package jshm.gui.notificationbars;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Tim
 */
public abstract class NotificationBarPanel extends JPanel {
	public NotificationBarPanel() {
		addContainerListener(new ContainerListener() {
			@Override
			public void componentAdded(ContainerEvent e) {
				if (e.getChild() instanceof JComponent) {
					((JComponent) e.getChild()).setOpaque(false);
				}
			}

			@Override
			public void componentRemoved(ContainerEvent e) {}
		});
	}
}
