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
package jshm.gui.wizards.scoredownload;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.netbeans.spi.wizard.WizardPage;

public class DownloadModePage extends WizardPage {
	JRadioButton latestButton;
	JRadioButton allButton;
	
	public DownloadModePage() {
		super("downloadMode", "Select mode");
		this.setLongDescription("Select how to download your scores");
		
		ButtonGroup group = new ButtonGroup();
		latestButton = new JRadioButton("<html>Download latest scores.<br>Only one HTTP request is required but only the latest scores from the manage scores page are retrieved.");
		latestButton.setSelected(true);
		latestButton.setName("latest");
		group.add(latestButton);
		allButton = new JRadioButton("<html>Download all scores.<br>One HTTP request is required per song but all scores will be retrieved.<br>This can take more than a minute to complete.");
		allButton.setName("all");
		group.add(allButton);
		
		setLayout(new GridLayout(0, 1));
		add(latestButton);
		add(allButton);
	}
	
	@Override
	protected String validateContents(Component component, Object event) {
		if (!(latestButton.isSelected() || allButton.isSelected())) {
			return "Please select a download mode";
		}
		
		return null;
	}

}
