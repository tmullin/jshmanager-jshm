package jshm.wt;

import javax.swing.ImageIcon;

import jshm.*;

public class WtScore extends Score {

	@Override
	public ImageIcon getRatingIcon() {
		return jshm.gh.GhScore.getRatingIcon(getRating());
	}

	@Override
	public void submit() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
