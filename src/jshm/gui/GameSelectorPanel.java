/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GameSelectorPanel.java
 *
 * Created on Jun 19, 2010, 5:04:05 PM
 */

package jshm.gui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.Border;
import jshm.*;
import jshm.gui.renderers.GeneralListCellRenderer;
import jshm.wt.WtGameTitle;

/**
 *
 * @author Tim
 */
public class GameSelectorPanel extends javax.swing.JPanel {
	static final Logger LOG = Logger.getLogger(GameSelectorPanel.class.getName());
	
	GUI gui;

	private GeneralListCellRenderer renderer =
		new GeneralListCellRenderer();

	private Border
		defaultBorder = null,
		errorBorder = BorderFactory.createLineBorder(Color.RED);

	private static final String
		SELECT_GAME = "Game...",
		SELECT_PLAT = "Platform...",
		SELECT_INST = "Instrument...",
		SELECT_DIFF = "Difficulty...";

	private DefaultComboBoxModel
		gameModel, platModel, instModel, diffModel;

    /** Creates new form GameSelectorPanel */
//    @SuppressWarnings("unchecked")
	public GameSelectorPanel() {
        initComponents();

		defaultBorder = gameCombo.getBorder();

		// could sort alphabetically but seems better to keep it in SH order
//		Vector items = new Vector();
		Vector<Object> items = new Vector<Object>();
		items.add(SELECT_GAME);

		items.addAll(GameTitle.getBySeries(GameSeries.GUITAR_HERO));
		items.addAll(GameTitle.getBySeries(GameSeries.WORLD_TOUR));
		items.addAll(GameTitle.getBySeries(GameSeries.ROCKBAND));

//		Collections.sort(items);
//		
//		items.add(0, SELECT_GAME);
		
		gameModel = new DefaultComboBoxModel(items);
		gameCombo.setModel(gameModel);

		populateCombos();
	}

    /**
     * Takes care of filling in the combo boxes with the allowed
     * values for a given game.
     */
    private void populateCombos() {
    	Object o = gameCombo.getSelectedItem();
    	GameTitle selectedGame = o instanceof GameTitle ? (GameTitle) o : null;
    	
    	Object lastPlat = platCombo.getSelectedItem();
    	Object lastInst = instCombo.getSelectedItem();

    	renderer.setGameTitle(selectedGame);
    	
		Vector<Object> items = new Vector<Object>();
		items.add(SELECT_PLAT);
		
		if (null != selectedGame) {
			for (Platform p : selectedGame.platforms)
				items.add(p);
		}

		platModel = new DefaultComboBoxModel(items);
		platCombo.setModel(platModel);


		items = new Vector<Object>();
		items.add(SELECT_INST);
		
		if (null != selectedGame) {
			for (Instrument.Group g : selectedGame.getSupportedInstrumentGroups())
				items.add(g);
		}

		instModel = new DefaultComboBoxModel(items);
		instCombo.setModel(instModel);

		
		populateDiffCombo();
		
		
		platCombo.setSelectedItem(lastPlat);
		instCombo.setSelectedItem(lastInst);		
		
		// may as well preset the only possible choices when there is only 1
		
		if (null != selectedGame && 1 == selectedGame.platforms.length) {
			platCombo.setSelectedItem(selectedGame.platforms[0]);
		}
		
		if (null != selectedGame && 1 == selectedGame.getSupportedInstrumentGroups().length) {
			instCombo.setSelectedItem(selectedGame.getSupportedInstrumentGroups()[0]);
		}
		
		// when switching from WT Drums in GHM to RB2 for example set to plain drums
		if (SELECT_INST == instCombo.getSelectedItem() && Instrument.Group.WTDRUMS == lastInst) {
			instCombo.setSelectedItem(Instrument.Group.DRUMS);
		}
    }
    
    private void populateDiffCombo() {
    	Object lastDiff = diffCombo.getSelectedItem();
    	
		Vector<Object> items = new Vector<Object>();
		items.add(SELECT_DIFF);
		
		if (SELECT_GAME != gameCombo.getSelectedItem()) {
			items.add(Difficulty.EASY);
			items.add(Difficulty.MEDIUM);
			items.add(Difficulty.HARD);
			items.add(Difficulty.EXPERT);
			
			if (gameCombo.getSelectedItem() instanceof WtGameTitle) {
				Object drumObj = instCombo.getSelectedItem();
				boolean isDrums =
					drumObj instanceof Instrument.Group &&
					(Instrument.Group.DRUMS == drumObj || Instrument.Group.WTDRUMS == drumObj);
				
				WtGameTitle wtgt = (WtGameTitle) gameCombo.getSelectedItem();
				
				if (isDrums && wtgt.supportsExpertPlus) {
					items.add(Difficulty.EXPERT_PLUS);
				}
			}
		}
		
		diffModel = new DefaultComboBoxModel(items);
		diffCombo.setModel(diffModel);
		
		diffCombo.setSelectedItem(lastDiff);
		
		// if it was set to x+ but the new game doesn't support it
		// or we switched instruments revert to plain expert
		if (Difficulty.EXPERT_PLUS == lastDiff && SELECT_DIFF == diffCombo.getSelectedItem()) {
			diffCombo.setSelectedItem(Difficulty.EXPERT);
		}
    }
    
	public void setCombos(Game game, Instrument.Group group, Difficulty diff) {
		gameCombo.setSelectedItem(game.title);
		platCombo.setSelectedItem(game.platform);
		instCombo.setSelectedItem(group);
		diffCombo.setSelectedItem(diff);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gameCombo = new javax.swing.JComboBox();
        instCombo = new javax.swing.JComboBox();
        platCombo = new javax.swing.JComboBox();
        diffCombo = new javax.swing.JComboBox();
        goButton = new javax.swing.JButton();

        gameCombo.setRenderer(renderer);
        gameCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameComboActionPerformed(evt);
            }
        });
        gameCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                gameComboKeyPressed(evt);
            }
        });

        instCombo.setRenderer(renderer);
        instCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instComboActionPerformed(evt);
            }
        });
        instCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                instComboKeyPressed(evt);
            }
        });

        platCombo.setRenderer(renderer);
        platCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platComboActionPerformed(evt);
            }
        });
        platCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                platComboKeyPressed(evt);
            }
        });

        diffCombo.setRenderer(renderer);
        diffCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffComboActionPerformed(evt);
            }
        });
        diffCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                diffComboKeyPressed(evt);
            }
        });

        goButton.setText("Go");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(platCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diffCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(goButton)
                .addContainerGap(88, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(gameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(platCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(instCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(diffCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(goButton))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
		Object o;
		GameTitle gt = null;
		Platform plat = null;
		Instrument.Group grp = null;
		Difficulty diff = null;
		Game game = null;

		boolean error = false;

		o = gameCombo.getSelectedItem();

		if (o instanceof GameTitle) {
			gt = (GameTitle) o;
		}


		o = platCombo.getSelectedItem();

		if (o instanceof Platform) {
			plat = (Platform) o;
		}

		try {
			game = Game.getByTitleAndPlatform(gt, plat);
		} catch (IllegalArgumentException x) {}

		o = instCombo.getSelectedItem();

		if (o instanceof Instrument.Group) {
			grp = (Instrument.Group) o;
		}


		o = diffCombo.getSelectedItem();

		if (o instanceof Difficulty) {
			diff = (Difficulty) o;
		}


		if (null == gt) {
			error = true;
			gameCombo.setBorder(errorBorder);
		} else {
			gameCombo.setBorder(defaultBorder);
		}


		if (null == plat) {
			error = true;
			platCombo.setBorder(errorBorder);
		} else {
			platCombo.setBorder(defaultBorder);
		}


		if (null == game) {
			error = true;
			gameCombo.setBorder(errorBorder);
			platCombo.setBorder(errorBorder);
		}


		if (null == grp) {
			error = true;
			instCombo.setBorder(errorBorder);
		} else {
			instCombo.setBorder(defaultBorder);

			if (null != gt && !gt.supportsInstrumentGroup(grp)) {
				error = true;
				instCombo.setBorder(errorBorder);
			}
		}


		if (null == diff) {
			error = true;
			diffCombo.setBorder(errorBorder);
		} else {
			diffCombo.setBorder(defaultBorder);

			if (Difficulty.EXPERT_PLUS == diff) {

				if (!((Instrument.Group.DRUMS == grp || Instrument.Group.WTDRUMS == grp) &&
					  gt instanceof WtGameTitle && ((WtGameTitle) gt).supportsExpertPlus)) {
					error = true;
					instCombo.setBorder(errorBorder);
					diffCombo.setBorder(errorBorder);
				}
			}
		}


		if (!error && null != gui) {
			/// XXX passing evt here may or may not be an issue
			gui.myScoresMenuItemActionPerformed(evt, game, grp, diff);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}//GEN-LAST:event_goButtonActionPerformed

	private void gameComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameComboActionPerformed
		populateCombos();
	}//GEN-LAST:event_gameComboActionPerformed

	private void platComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platComboActionPerformed

	}//GEN-LAST:event_platComboActionPerformed

	private void instComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instComboActionPerformed
		populateDiffCombo();
	}//GEN-LAST:event_instComboActionPerformed

	private void diffComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffComboActionPerformed

	}//GEN-LAST:event_diffComboActionPerformed

	private void gameComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gameComboKeyPressed
		comboKeyPressed(evt);
	}//GEN-LAST:event_gameComboKeyPressed

	private void platComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_platComboKeyPressed
		comboKeyPressed(evt);
	}//GEN-LAST:event_platComboKeyPressed

	private void instComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_instComboKeyPressed
		comboKeyPressed(evt);
	}//GEN-LAST:event_instComboKeyPressed

	private void diffComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_diffComboKeyPressed
		comboKeyPressed(evt);
	}//GEN-LAST:event_diffComboKeyPressed

	private void comboKeyPressed(KeyEvent evt) {
		if (KeyEvent.VK_ENTER == evt.getKeyCode()) {
			goButton.doClick();
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox diffCombo;
    private javax.swing.JComboBox gameCombo;
    private javax.swing.JButton goButton;
    private javax.swing.JComboBox instCombo;
    private javax.swing.JComboBox platCombo;
    // End of variables declaration//GEN-END:variables

}
