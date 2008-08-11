/*
 * GUITest.java
 *
 * Created on August 10, 2008, 2:33 AM
 */

package jshm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

/**
 *
 * @author  Tim
 */
public class GUITest extends javax.swing.JFrame {

    /** Creates new form GUITest */
    public GUITest() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        quitMenuItem = new javax.swing.JMenuItem();
        gamesMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMenu.setText("File");

        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(quitMenuItem);

        jMenuBar1.add(fileMenu);

        gamesMenu.setText("Games");
        initDynamicGameMenu(gamesMenu);
        jMenuBar1.add(gamesMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
	System.exit(0);
}//GEN-LAST:event_quitMenuItemActionPerformed

/**
 * Load the menu with all avaialable GH games.
 * @param menu
 */
private void initDynamicGameMenu(javax.swing.JMenu menu) {
	java.util.List<jshm.GameTitle> titles =
		jshm.GameTitle.getTitlesBySeries(jshm.GameSeries.GUITAR_HERO);
	
	for (jshm.GameTitle ttl : titles) {		
		System.out.println("Creating " + ttl);
		javax.swing.JMenu ttlMenu = new javax.swing.JMenu(ttl.toString());
		java.net.URL url = GUITest.class.getResource("/jshm/resources/images/icons/" + ttl.title + "_32.png");
		System.out.println("URL: " + url);
		ttlMenu.setIcon(new ImageIcon(url));
		
		java.util.List<jshm.Game> games =
			jshm.Game.getByTitle(ttl);
		
		for (final jshm.Game game : games) {
			// no need for an extra menu if there's only one platform
			javax.swing.JMenu gameMenu = null;
				
			if (games.size() > 1) {
				gameMenu = new javax.swing.JMenu(game.platform.toString());
				gameMenu.setIcon(new ImageIcon(GUITest.class.getResource("/jshm/resources/images/platforms/" + game.platform.toString() + "_32.png")));
			} else {
				gameMenu = ttlMenu;
			}
			
			System.out.println("  Creating " + game.platform);
			for (final jshm.Difficulty diff : jshm.Difficulty.values()) {
				if (diff == jshm.Difficulty.CO_OP) continue;
				System.out.println("    Creating " + diff);
				JMenuItem diffItem = new JMenuItem(diff.toString());
				diffItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dynamicGameMenuItemActionPerformed(e, (jshm.gh.GhGame) game, diff);
					}
				});
				
				gameMenu.add(diffItem);
			}
			
			ttlMenu.add(gameMenu);
		}
		
		menu.add(ttlMenu);
	}
}

private void dynamicGameMenuItemActionPerformed(java.awt.event.ActionEvent evt, jshm.gh.GhGame game, jshm.Difficulty difficulty) {
	System.out.println("Menu Evt: " + evt + "\n" + game + "," + difficulty);
}

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
					System.out.println("Couldn't set system look & feel (not fatal)");
				}
				
                new GUITest().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu gamesMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem quitMenuItem;
    // End of variables declaration//GEN-END:variables

}
