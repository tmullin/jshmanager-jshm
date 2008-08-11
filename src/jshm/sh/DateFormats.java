package jshm.sh;

import java.text.SimpleDateFormat;

public class DateFormats {
	public static final SimpleDateFormat
		// Aug. 8, 2008, 3:47AM
		GH_MANAGE_SCORES =
			new SimpleDateFormat("MMM. d, yyyy, h:mma"),
		
		// Jul. 31, 2008 @ 9:17 PM
		GH_VIEW_SCORES =
			new SimpleDateFormat("MMM. d, yyyy @ h:mm a")
	
	;
}
