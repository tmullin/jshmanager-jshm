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
package jshm.concepts;

import jshm.GameSeries;
import jshm.gui.LoginDialog;
import jshm.logging.Log;
import jshm.sh.Forum;
import jshm.sh.Forum.PostMode;

public class EditPostTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		
		LoginDialog.showDialog();
		
		Forum.post(GameSeries.GUITAR_HERO, PostMode.EDIT, 1052139,
		"sorry, my wording was poor. i did [i]barely[/i] 5* raining blood (227,357, stats page says cuttoff is 227,195 - 227,286) but since then i haven't been able to 5* it again. it was naturally the last song i needed to 5* for a while. that time i miraculously passed mosh 1 without sp and did decently on the verses and fc'd the 3 note chords in mosh 2 for the points. " +
		"\n\n" +
		"the 5* rb guide thread in the rb sticky really helped" + "\n" +
		"http://www.scorehero.com/forum/viewtopic.php?t=35238" + "\n\n" +

		"on a side note, after i entered all my gh2/3 scores in, i found out i 8*ed my name is jonas and closer" +
//		"\n\n" +
		""
		);
	}
}
