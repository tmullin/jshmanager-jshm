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
 * StatusBar.java
 *
 * Created on August 15, 2008, 4:01 AM
 */

package jshm.gui.components;

import java.util.logging.Logger;

import javax.swing.JProgressBar;

/**
 * A status is a component, generally along the bottom of a window,
 * that tells the user various information.
 * <p>
 * This status bar has two strings associated with it. A main string and
 * a temporary string. The main string is always shown unless the temporary
 * string has been set. Once whatever operation completes that set the
 * temporary string, it can revert the text to display the main string
 * again.
 * </p><p>
 * There is also a progress bar that can be utilized to show an
 * operations's progress. Booleans can be passed to the set*Text() methods
 * to conveniently show/hide the progress in an indeterminate mode.
 * Retrieve the progress bar via {@link #getProgressBar()} if you want to
 * manually set actual progress values.
 * </p>
 * @author Tim Mullin
 */
public class StatusBar extends javax.swing.JPanel {
	static final Logger LOG = Logger.getLogger(StatusBar.class.getName());
	
	String text = "";
	String tempText = ""; // for when the status bar is temporarily changed
	
    /** Creates new form StatusBar */
    public StatusBar() {
        initComponents();
		setProgressVisible(false);
		setText("");
    }
    
    /**
     * Sets the progress bar's text to <code>str</code>. The 
     * temporary text is cleared.
     * @param str
     * @see #setTempText(String)
     */
    public void setText(String str) {
    	setText(str, false);
    }
    
    /**
     * Sets the progress bar's text to <code>str</code>. The 
     * temporary text is cleared.
     * @param str
     * @param showProgress
     * @see #setTempText(String, boolean)
     */
	public void setText(String str, boolean showProgress) {
		setText(str, showProgress, false);
	}
	
	private void setText(String str, boolean showProgress, boolean isTemp) {
		if (!isTemp) this.text = str;
		this.tempText = isTemp ? str : "";
		String display = isTemp ? this.tempText : this.text;
		
		if (display.isEmpty()) {
			textLabel.setText("  ");
		} else {
			textLabel.setText(display);
		}
		
		if (showProgress) {
			getProgressBar().setIndeterminate(true);
			setProgressVisible(true);
		} else {
			setProgressVisible(false);
		}
	}
	
    /**
     * Sets the progress bar's temporary text to <code>str</code>. The 
     * main text is not changed.
     * @param str
     * @see #setText(String)
     * @see #revertText()
     */
	public void setTempText(String str) {
		setTempText(str, false);
	}
	
    /**
     * Sets the progress bar's temporary text to <code>str</code>. The 
     * main text is not changed.
     * @param str
     * @param showProgress
     * @see #setText(String, boolean)
     * @see #revertText()
     */
	public void setTempText(String str, boolean showProgress) {
		setText(str, showProgress, true);
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getTempText() {
		return this.tempText;
	}
	
	public void revertText() {
		setText(text);
	}
	
	public JProgressBar getProgressBar() {
		return jProgressBar1;
	}
	
	public void setProgressVisible(boolean value) {
		LOG.finer("Setting progress visible to " + value);
		progSeparator.setVisible(value);
		jProgressBar1.setVisible(value);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topSeparator = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        textLabel = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        progSeparator = new javax.swing.JSeparator();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(topSeparator, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        textLabel.setText("Status text here...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        jPanel1.add(textLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jProgressBar1, gridBagConstraints);

        progSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(progSeparator, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSeparator progSeparator;
    private javax.swing.JLabel textLabel;
    private javax.swing.JSeparator topSeparator;
    // End of variables declaration//GEN-END:variables

}
