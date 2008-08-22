JSHManager - Java-based ScoreHero account manager

Requirements:
-------------
- Java 1.6 or later (1.5 will NOT work nor be supported)
- A ScoreHero account


Installation:
-------------
Simply unzip JSHManager-W.X.Y.Z.zip to a folder of your choice.
It is recommended to create a folder specifically for JSHManager
(perhaps called "JSHManager") because it will create a sub-folder
called "data" where settings and the database files will be stored.


Upgrading:
----------
To upgrade from a previous version, unzip the distribution file into
your existing JSHManager directory and overrite any existing files
if prompted. Your settings and data should not be affected.


Getting Started:
----------------
On most Windows systems, you can simply double-click JSHManager.jar
to start it, assuming Java has been installed correctly. You can also
right-click on the jar to create a shortcut, if desired.

Once JSHManager is running, you first must download the song data for
the particular game and difficulty that you are interested in.
From the "Song Data" menu, select the game, platform, and difficulty
you want. The first time you access the song data for a given game,
you will be prompted to download it from ScoreHero. Click "Yes" to do so.
You can come back to this screen to view the number of notes and cutoffs
for each song. You can update the song data at a later point (if new DLC
comes out, for example) by first selecting the game as before and then
clicking "Load from ScoreHero" in the "Song Data" menu.

Now you can download all of your existing scores that are currently on
ScoreHero. To do so, select the appropriate game from the "My Scores" menu.
Again, you will be prompted to download. Click "Yes". You should now
be able to look at all of your scores within JSHManager. Enter your 
ScoreHero account information when prompted. You can optionally save
your password. Subsequent logins will already have the username and
password fields filled in. Scores that you have gotten 100% on or that
are FC'd will be highlighted green. You can download your scores again at
a later point (if you add a new score from ScoreHero.com directly, for
example) by clicking "Load from ScoreHero" in the "My Scores" menu.

To add a new score, select the appropriate song and click "Add new score..."
in the "My Scores" menu. Alteratively you can press the Insert key after
selecting a song. New scores that are not yet on ScoreHero are highlighted
blue. Double-click the new score row to edit the comment, score, percentage,
and streak columns. You can select the rating from a drop-down list by
clicking the rating column but it will be calculated automatically if
possible (it cannot be calculated if the cutoffs for a particular song
are unknown).

Once you have inserted some scores, you can upload them to ScoreHero by
clicking "Upload to ScoreHero..." in the "My Scores" menu. A wizzard will
appear to guide you through that process. Simply double check that your
scores are correct and click "Finish".

To manage scores for a different game or difficulty you must repeat the
process of downloading the song and score data for that game/difficulty
combination. Note that you only have to download the song data once
(unless you want to update it for new DLC or when new cutoffs are determined).
You only need to download the score data once unless you manually add
new scores at a later point from ScoreHero.com and would like them to
appear in JSHManager. Downloading data subsequent times should not interfere
with JSHManager. It should detect existing items so as to not produce
duplicate entries.


Notes:
------
If you choose to save your password after logging in to ScoreHero, it will
be saved to data/passwords.properties. While the password is encrypted, it
should not be considered highly secure. Since this is an open-source program,
anyone can obtain the source code to see how it is encrypted. It is encrypted
so that it at least is not visible as plain-text.

JSHManager logs various information in data/logs. JSHManager.txt contains
logging data for the main program. Hibernate.txt contains logging data
related to the database. When reporting problems, you may be asked to
provide these.
