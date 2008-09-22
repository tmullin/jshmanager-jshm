/*
 * ColumnsPage.java
 *
 * Created on September 21, 2008, 11:31 PM
 */

package jshm.gui.wizards.csvimport;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import jshm.Game;
import jshm.csv.CsvColumn;
import jshm.gh.GhGame;
import jshm.util.Util;

import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author  Tim
 */
public class ColumnsPage extends WizardPage {
	static final CsvColumn[] REQUIRED_COLUMNS = new CsvColumn[] {
		CsvColumn.SONG, CsvColumn.SCORE
	};
	
	private Game game;
	
    /** Creates new form ColumnsPage */
    public ColumnsPage() {
		this(GhGame.GH3_XBOX360);
	}
	
	public ColumnsPage(Game game) {
		super("columns", "Select columns");
		this.game = game;
		
        initComponents();
        setAvailableColumnListModel();
        setColumnListModel();
    }
	
	private void setAvailableColumnListModel() {
		DefaultListModel model = new DefaultListModel();
		
		model.addElement(CsvColumn.IGNORE);
		model.addElement(CsvColumn.DIFFICULTY);
		
		if (!(game instanceof GhGame))
			model.addElement(CsvColumn.INSTRUMENT);
		
		availableColumnList.setModel(model);
	}
	
	private void setColumnListModel() {
		final DefaultListModel model = new DefaultListModel();
		
		model.addListDataListener(new ListDataListener() {
			private void _() {
				putWizardData("columns", model.toArray());
			}
			
			public void contentsChanged(ListDataEvent e) { _(); }
			public void intervalAdded(ListDataEvent e) { _(); }
			public void intervalRemoved(ListDataEvent e) { _(); }
		});
		
		for (CsvColumn c : CsvColumn.values()) {
			if (CsvColumn.DIFFICULTY == c) break;
			model.addElement(c);
		}
		
		columnList.setModel(model);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inferColumnsCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        availableColumnList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        columnList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        moveRightButton = new javax.swing.JButton();
        moveLeftButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();

        inferColumnsCheckBox.setMnemonic('I');
        inferColumnsCheckBox.setSelected(true);
        inferColumnsCheckBox.setText("Infer columns names from first row");
        inferColumnsCheckBox.setName("inferColumns"); // NOI18N
        inferColumnsCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                inferColumnsCheckBoxStateChanged(evt);
            }
        });

        jLabel1.setText("JSHManager needs to know the order of the columns in your CSV file:");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Choose Column"), javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        availableColumnList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availableColumnList.setEnabled(false);
        availableColumnList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                availableColumnListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(availableColumnList);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Column Order"), javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        columnList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        columnList.setEnabled(false);
        columnList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                columnListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(columnList);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        moveRightButton.setText("->");
        moveRightButton.setEnabled(false);
        moveRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRightButtonActionPerformed(evt);
            }
        });
        jPanel1.add(moveRightButton);

        moveLeftButton.setText("<-");
        moveLeftButton.setEnabled(false);
        moveLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLeftButtonActionPerformed(evt);
            }
        });
        jPanel1.add(moveLeftButton);

        moveUpButton.setMnemonic('U');
        moveUpButton.setText("Move Up");
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });
        jPanel1.add(moveUpButton);

        moveDownButton.setMnemonic('D');
        moveDownButton.setText("Move Down");
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });
        jPanel1.add(moveDownButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(inferColumnsCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inferColumnsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void moveRightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveRightButtonActionPerformed
	Object o = availableColumnList.getSelectedValue();
	assert o != null;
	
	if (CsvColumn.IGNORE != o) {
		((DefaultListModel) availableColumnList.getModel())
			.removeElement(o);
	}
	
	((DefaultListModel) columnList.getModel())
		.addElement(o);
	columnList.setSelectedIndex(columnList.getModel().getSize() - 1);
}//GEN-LAST:event_moveRightButtonActionPerformed

private void moveLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveLeftButtonActionPerformed
	Object o = columnList.getSelectedValue();
	assert o != null;
	assert !Util.contains(o, REQUIRED_COLUMNS);
	
	((DefaultListModel) columnList.getModel())
		.removeElement(o);
	
	if (CsvColumn.IGNORE != o) {
		((DefaultListModel) availableColumnList.getModel())
			.addElement(o);
		availableColumnList.setSelectedValue(o, true);
	}
}//GEN-LAST:event_moveLeftButtonActionPerformed

private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
	Object o = columnList.getSelectedValue();
	int i = columnList.getSelectedIndex();
	assert o != null;
	assert i > 0;
	
	DefaultListModel model = (DefaultListModel) columnList.getModel();
	model.removeElement(o);
	model.add(i - 1, o);
	columnList.setSelectedIndex(i - 1);
}//GEN-LAST:event_moveUpButtonActionPerformed

private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
	Object o = columnList.getSelectedValue();
	int i = columnList.getSelectedIndex();
	DefaultListModel model = (DefaultListModel) columnList.getModel();
	
	assert o != null;
	assert i < model.size() - 1;

	model.removeElement(o);
	model.add(i + 1, o);
	columnList.setSelectedIndex(i + 1);
}//GEN-LAST:event_moveDownButtonActionPerformed

private void columnListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_columnListValueChanged
	if (null != evt && evt.getValueIsAdjusting()) return;
	
	int i = columnList.getSelectedIndex();
	Object o = columnList.getSelectedValue();
	boolean b = i > -1;
	
	boolean isSelectedRequired = Util.contains(o, REQUIRED_COLUMNS);
	
	moveLeftButton.setEnabled(b && !isSelectedRequired);
	moveUpButton.setEnabled(b && i > 0);
	moveDownButton.setEnabled(b && i < columnList.getModel().getSize() - 1);
	
}//GEN-LAST:event_columnListValueChanged

private void availableColumnListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_availableColumnListValueChanged
	if (null != evt && evt.getValueIsAdjusting()) return;
	moveRightButton.setEnabled(availableColumnList.getSelectedIndex() > -1);
}//GEN-LAST:event_availableColumnListValueChanged

private void inferColumnsCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_inferColumnsCheckBoxStateChanged
	boolean b = !inferColumnsCheckBox.isSelected();
	
	availableColumnList.setEnabled(b);
	columnList.setEnabled(b);
	
	if (b) {
		availableColumnListValueChanged(null);
		columnListValueChanged(null);
	} else {
		moveLeftButton.setEnabled(b);
		moveRightButton.setEnabled(b);
		moveUpButton.setEnabled(b);
		moveDownButton.setEnabled(b);
	}
}//GEN-LAST:event_inferColumnsCheckBoxStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList availableColumnList;
    private javax.swing.JList columnList;
    private javax.swing.JCheckBox inferColumnsCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveLeftButton;
    private javax.swing.JButton moveRightButton;
    private javax.swing.JButton moveUpButton;
    // End of variables declaration//GEN-END:variables

}
