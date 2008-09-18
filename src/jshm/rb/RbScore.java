package jshm.rb;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import jshm.Score;

@Entity
public class RbScore extends Score {
	@Transient
	@Override
	public ImageIcon getRatingIcon() {
		return getRatingIcon(getRating());
	}

	private static final ImageIcon[] RATING_ICONS = new ImageIcon[6];
	
	public static ImageIcon getRatingIcon(int rating) {
		if (rating != 0 && rating < 1)
			rating = 1;
		else if (rating > 6)
			rating = 6;
		
		if (null == RATING_ICONS[rating]) {
			RATING_ICONS[rating] = new javax.swing.ImageIcon(
				RbScore.class.getResource(
					"/jshm/resources/images/ratings/rb/" + rating + ".gif"));
		}
		
		return RATING_ICONS[rating];
	}
}
