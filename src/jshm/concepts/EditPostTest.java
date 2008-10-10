package jshm.concepts;

import jshm.GameSeries;
import jshm.gui.LoginDialog;
import jshm.logging.Log;
import jshm.sh.Forum;

public class EditPostTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		
		LoginDialog.showDialog();
		
		Forum.editPost(GameSeries.GUITAR_HERO, 1052139,
		"sorry, my wording was poor. i did [i]barely[/i] 5* raining blood (227,357, stats page says cuttoff is 227,195 - 227,286) but since then i haven't been able to 5* it again. it was naturally the last song i needed to 5* for a while. that time i miraculously passed mosh 1 without sp and did decently on the verses and fc'd the 3 note chords in mosh 2 for the points. " +
		"\n\n" +
		"the 5* rb guide thread in the rb sticky really helped" + "\n" +
		"http://www.scorehero.com/forum/viewtopic.php?t=35238" + "\n\n" +

		"on a side note, after i entered all my gh2/3 scores in, i found out i 8*ed my name is jonas and closer" +
		"\n\n" +
		""
		);
	}
}
