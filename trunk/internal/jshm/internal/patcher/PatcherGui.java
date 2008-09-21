/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
*/
/*
 * PatcherGui.java
 *
 * Created on August 26, 2008, 12:47 AM
 */

package jshm.internal.patcher;

import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;

/**
 *
 * @author  Tim
 */
public class PatcherGui extends javax.swing.JFrame {

    /** Creates new form PatcherGui */
    public PatcherGui() {
        initComponents();
    }

    /**
     * Adds msg to msgList. This method is thread-safe.
     * @param msg
     */
	public void addMessage(final String msg) {
		if (EventQueue.isDispatchThread()) {
			((DefaultListModel) msgList.getModel()).addElement(msg);
			msgList.ensureIndexIsVisible(msgList.getModel().getSize() - 1);
		} else {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					addMessage(msg);
				}
			});
		}
	}
	
	/**
	 * Sets the status label's text and makes the progress
	 * bar indeterminate. This method is thread-safe.
	 * @param text
	 */
	public void setProgress(final String text) {
		setProgress(text, -1, -1);
	}
	
	/**
	 * Sets the status label's text as well as the progress bar's
	 * current value and maximum value (the minimum value is always 0).
	 * If cur is < 0 or total is < 1 the progress bar is set to be
	 * indeterminate. This method is thread-safe.
	 * @param text
	 * @param cur
	 * @param total
	 */
	public void setProgress(final String text, final int cur, final int total) {
		if (EventQueue.isDispatchThread()) {
			statusLabel.setText(text);

			if (cur >= 0 && total > 0) {
				progBar.setMinimum(0);
				progBar.setValue(cur);
				progBar.setMaximum(total);
				progBar.setIndeterminate(false);
			} else {
				progBar.setIndeterminate(true);
			}
		} else {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					setProgress(text, cur, total);
				}
			});
		}
	}
	
	/**
	 * Sets whether the close button is enabled. This method
	 * is thread-safe.
	 * @param b
	 */
	public void setClosedEnabled(final boolean b) {
		if (EventQueue.isDispatchThread()) {
			closeButton.setEnabled(b);
		} else {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					setClosedEnabled(b);
				}
			});
		}
	}
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        msgList = new javax.swing.JList();
        progBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("JSHManager Patcher");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);

        msgList.setModel(new DefaultListModel());
        msgList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(msgList);

        progBar.setIndeterminate(true);

        statusLabel.setText("Patcher status...");

        closeButton.setText("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(closeButton)
                    .addComponent(progBar, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                .addContainerGap(22, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
	if (closeButton.isEnabled()) {
		this.dispose();
	}
}//GEN-LAST:event_formWindowClosing

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
	formWindowClosing(null);
}//GEN-LAST:event_closeButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        		try {
        			// Set the Look & Feel to match the current system
        			UIManager.setLookAndFeel(
        				UIManager.getSystemLookAndFeelClassName());
        		} catch (Exception e) {
//        			LOG.log(Level.WARNING, "Couldn't set system look & feel (not fatal)", e);
        		}
        		
        		PatcherGui gui = new PatcherGui();
                gui.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent evt) {
                        System.exit(0);
                    }
                });
        		gui.setClosedEnabled(true);
        		gui.setLocationRelativeTo(null);
        		gui.setVisible(true);
        		gui.addMessage("Msg 1");
        		gui.addMessage("Msg 2");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList msgList;
    private javax.swing.JProgressBar progBar;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

}
