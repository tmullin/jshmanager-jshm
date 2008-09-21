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
 * ProgressDialog.java
 *
 * Created on September 19, 2008, 4:10 PM
 */

package jshm.gui;

import java.awt.Container;

import javax.swing.SwingUtilities;

import org.netbeans.spi.wizard.ResultProgressHandle;

/**
 * This just has a label and a progress bar for long running
 * operations. It implements {@link ResultProgressHandle} even
 * though this has nothing to do with a wizard since the data
 * updaters use it and I didn't want to create my own wrapper.
 * @author  Tim
 */
public class ProgressDialog extends javax.swing.JDialog implements ResultProgressHandle {
	public ProgressDialog() {
		this(null);
	}
	
	public ProgressDialog(java.awt.Frame parent) {
		this(parent, false);
	}
	
    /** Creates new form ProgressDialog */
    public ProgressDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setResizable(false);

        statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getStyle() | java.awt.Font.BOLD));
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusLabel.setText("Please wait...");
        statusLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        statusLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        progressBar.setIndeterminate(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                    .addComponent(statusLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	GuiUtil.init();
                ProgressDialog dialog = new ProgressDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
    
    
    // implement org.netbeans.spi.wizard.ResultProgressHandle
    
	@Override
	public void addProgressComponents(Container panel) {
		// ignore
	}

	@Override
	public void failed(String message, boolean canNavigateBack) {
		// ignore
	}

	public void finished() {
		finished(null);
	}
	
	@Override
	public void finished(Object result) {
		this.dispose();
	}

	@Override
	public boolean isRunning() {
		// not really used
		return true;
	}

	@Override
	public void setBusy(String description) {
		setStatus(description);
		setProgress(true, -1, -1);
	}

	@Override
	public void setProgress(int currentStep, int totalSteps) {
		setProgress(false, currentStep, totalSteps);
	}
	
	public void setProgress(final boolean indeterminate, final int currentStep, final int totalSteps) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setProgress(indeterminate, currentStep, totalSteps);
				}
			});
			
			return;
		}
		
		progressBar.setIndeterminate(indeterminate);
		
		if (!indeterminate) {
			progressBar.setMinimum(0);
			progressBar.setMaximum(totalSteps);
			progressBar.setValue(currentStep);
		}
	}

	@Override
	public void setProgress(String description, int currentStep, int totalSteps) {
		setStatus(description);
		setProgress(currentStep, totalSteps);
	}

	public void setStatus(final String description) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setStatus(description);
				}
			});
			
			return;
		}
		
		statusLabel.setText(description);
	}
}
