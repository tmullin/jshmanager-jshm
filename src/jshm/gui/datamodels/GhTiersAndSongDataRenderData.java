package jshm.gui.datamodels;

import java.awt.Color;

import javax.swing.Icon;

import jshm.gh.GhSong;

import org.netbeans.swing.outline.RenderDataProvider;

public class GhTiersAndSongDataRenderData implements RenderDataProvider {

	@Override
	public Color getBackground(Object arg0) {
//		if (arg0 instanceof GhTiersAndSongDataTreeModel.Tier)
//			return Color.RED;
		return null;
	}

	@Override
	public String getDisplayName(Object arg0) {
		if (arg0 instanceof GhTiersAndSongDataTreeModel.Tier)
			return ((GhTiersAndSongDataTreeModel.Tier) arg0).name;
		
		return ((GhSong) arg0).getTitle();
	}

	@Override
	public Color getForeground(Object arg0) {
//		if (arg0 instanceof GhTiersAndSongDataTreeModel.Tier)
//			return Color.WHITE;
		return null;
	}

	@Override
	public Icon getIcon(Object arg0) {
		return null;
	}

	@Override
	public String getTooltipText(Object arg0) {
		return arg0.toString();
	}

	@Override
	public boolean isHtmlDisplayName(Object arg0) {
		return false;
	}

}
