/*
 * TextFileViewerDialog.java
 *
 * Created on August 29, 2008, 1:16 AM
 */

package jshm.gui;

import java.io.File;
import java.io.IOException;

import jshm.Config;

/**
 *
 * @author  Tim
 */
public class TextFileViewerDialog extends javax.swing.JDialog {

	public TextFileViewerDialog() {
		this(null, true);
	}
	
    /** Creates new form TextFileViewerDialog */
    public TextFileViewerDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
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