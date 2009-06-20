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
 * TextFileViewerDialog.java
 *
 * Created on August 29, 2008, 1:16 AM
 */

package jshm.gui;

import java.io.File;
import java.io.IOException;

import jshm.Config;
import jshm.gui.components.EscapeableDialog;

/**
 *
 * @author  Tim
 */
// TODO rename to TextViewerDialog
public class TextFileViewerDialog extends EscapeableDialog {

	public TextFileViewerDialog() {
		this(null, true);
	}
	
    /** Creates new form TextFileViewerDialog */
    public TextFileViewerDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        EditPopupMenu.add(jTextPane1);
        
        try {
        	setSize(Config.getInt("window.textfileviewer.width"), Config.getInt("window.textfileviewer.height"));
        } catch (NullPointerException e) {
        	GUI.LOG.finest("No saved size for TextFileViewer");
        }
    }

	public void setVisible(final File file) throws IOException {
		jTextPane1.setPage(file.toURI().toURL());
		setLocationRelativeTo(getOwner());
		setTitle("Viewing " + file.getName());
		setVisible(true);
	}
	
	public void setVisible(final String content) {
		setVisible(null, content);
	}
	
	public void setVisible(final String title, final String content) {
		jTextPane1.setContentType("text/plain");
		jTextPane1.setText(content);
		
		if (null != title && !title.isEmpty()) {
			setTitle(title);
		} else {
			setTitle("");
		}
		
		setLocationRelativeTo(getOwner());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextPane1.setEditable(false);
        jTextPane1.setPreferredSize(new java.awt.Dimension(700, 500));
        jScrollPane1.setViewportView(jTextPane1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(closeButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
	setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TextFileViewerDialog dialog = new TextFileViewerDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

}
