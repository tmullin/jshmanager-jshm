JSHManager - Java-based ScoreHero account manager

0. Table of Contents
--------------------
1. Requirements
2. Installation
3. Updrading
4. Getting Started
5. Notes
6. Getting Help/Reporting Bugs


1. Requirements
---------------
- Java 1.6 or later (1.5 will NOT work nor be supported)
- A ScoreHero account


2. Installation
---------------
JSHManager is distributed as a zip file as well as a self-extracting
archive for Windows.

Simply extract JSHManager-x.y.z.r.zip/exe to a folder of your choice.
It is recommended to create a folder specifically for JSHManager
(perhaps called "JSHManager") because it will create a sub-folder
called "data" where settings and the database files will be stored.


3. Upgrading
------------
If you downloaded a patch jar, ensure you saved it into the directory
where JSHManager.jar is located. Then run the patch jar from that directory.
On Windows, you can generally double-click the jar file to run it. A log
of the patching operation will be located in data/logs/Patcher.txt.

NOTE: Each patch jar only updates the files that have changed from 
the previous version. If you have 0.0.1 and want to upgrade to 0.0.3, for example,
you must download and run both patches for 0.0.2 and 0.0.3 IN ORDER. 
If there is ever a question about whether you've patched correctly, you 
can download the latest full version and upgrade as described below.

If you downlaoded the full version, just unzip the distribution file into
your existing JSHManager directory and overrite any existing files
if prompted.

Your settings and data should not be affected.


4. Getting Started
------------------

4.1. Introduction
-----------------
On most Windows systems, you can simply double-click JSHManager.jar
to start it, assuming Java has been installed correctly. You can also
right-click on the jar to create a shortcut, if desired.


4.2. Download Existing Scores
-----------------------------
Now you can download all of your existing scores that are currently on
ScoreHero. To do so, from the "Guitar Hero" or "Rock Band" menu, select the 
appropriate game, instrument (if applicable), and difficulty from the 
"Scores" sub-menu. You will be prompted to launch the Score Download Wizard
for any games that do not have any scores entered. You can later launch the
wizard by clicking "Download from ScoreHero..." from the main "Scores" menu.

Even if you are not interested in or don't have any existing scores, you 
should download them, as it will also automatically download song data, 
which is required.

Once the Score Download Wizard launches, you can choose to download scores from
multiple platforms, difficulties, and instruments (if applicable) at once. 
Ctrl+click to select/deselect multiple items. If you click "Finish" now, your
latest scores will be downloaded. If you click "Next", you can choose to download
all of your scores, not only the latest (note this is only supported for Guitar
Hero games currently).

After clicking "Finish", enter your ScoreHero account information when prompted. You
can optionally save your password. If you do, subsequent logins will already have the 
username and password fields filled in and you will just have to press "Login".

Now, score data will be downloaded if needed. This is relatively quick for Guitar Hero games, 
but Rock Band games can take upwards of one to two minutes to complete. Please
be patient and keep in mind that this step only needs to happen once initially
and when there is new DLC.

After song data is downloaded, your scores will be downloaded. The time this takes 
depends on how many platforms, difficulties, and instruments you chose to download 
as well as on whether you chose to download all scores or just the latest. Once 
complete, the main window will refresh the scores for the currently selected 
game/instrument/difficulty.

Scores that you have gotten 100% on or that are FC'd will be highlighted green. 
You can download your scores again at a later point by clicking 
"Download from ScoreHero..." from the main "Scores" menu.

If you later enter a single new score for a particular song, you only need to
download the latest scores to import it into JSHManager.


4.3. Inserting New Scores
-------------------------

4.3.1. Quick Insert
-------------------
To add a new score, select the appropriate song and click "Add New Score"
from the main "Scores" menu. Alteratively, you can press the Insert key after
selecting a song. New scores that are not yet on ScoreHero are highlighted
blue. Double-click the new score row to edit the comment, score, percentage,
and streak columns. You can select the rating from a drop-down list by
clicking the rating column but it will be calculated automatically if
possible (it cannot be calculated if the cutoffs for a particular song
are unknown).


4.3.2. Insert via Editor
------------------------
You can also insert a score via the Score Editor, which allows you to
specify the song by title rather than finding it in the tree view.
Click "Add Score via Editor" from the main "Scores" menu. Alternatively,
you can press Ctrl+N. The Score Editor will appear with the song field
selected. Begin typing the name of the song and the text field will
auto-complete with the available songs. Enter the remaining information
and click the "Save" button. The newly entered score will be scrolled
into view for quick comparison to other scores for the same song.


4.3.3. CSV Importer
-------------------
You can import scores from a CSV file via the CSV Import Wizard.
JSHManager can try to determine the column contents automaticlly if
the first row is a header row. You can also explicitly define the columns,
if needed.

Note the file must be a CSV file, not a native spreadsheet, like .xls.
Most spreadsheet programs can export or "Save As" CSV format.

After selecting the scores to display, click "Import from CSV File..."
from the main "Scores" menu. Select your CSV file by clicking the "..."
button. You can specify the default difficulty and instrument (if applicable)
of the data in the file if you don't have difficulty/instrument columns.
You can click "Finish" now if you want JSHManager to infer the column contents
from the first row or click "Next" if you wish to specify the columns yourself.

If specifying the columns, uncheck the check box for inferring the columns.
The left list shows the available column names. The right list specifies the order
of the columns in your file. You can move them around as necessary with the
four buttons. The only required columns are SONG and SCORE.

After clicking "Finish", the file will be parsed and songs inserted into the
database. Any lines with errors will be skipped and a summary will be shown 
once complete.

JSHManager will attempt to match the song titles in the SONG column to a
song in the database. The case of the title does not matter. Partial matches 
will work so long as multiple songs don't match. "Heart" would match both
"Heart-Shaped Box" and "Trippin' on a Hole in a Paper Heart" for GH2. Songs
that have an exact match will take preference. For example, there is 
"Working Man" and "Working Man (Vault Edition)" in Rock Band DLC. 
"Working Ma" is invalid because it matches both songs but "Working Man" will
match the first because it is an exact match even though it is a partial match
of the second. If you want to match the second, "Working Man (Va" would work
(or you could type the whole thing, of course). Punctuation that is part of the
title must be specified. "Tripping on a Hole" and "Trippin on a Hole" will not
match "Trippin' on a Hole in a Paper Heart".


4.4. Editing Scores
-------------------
Existing scores that have not yet been uploaded can be quickly edited from
within the tree view by simply double-clicking the cell you wish to edit.
Press enter to save the changes to that cell.

You can also edit a score via the Score Editor by clicking "Toggle Editor"
from the main "Scores" menu. Alternatively, you can press Ctrl+E.

If you want to add an image or video URL to a score, you can do so by
editing the score with the Score Editor instead of the quck edit mode.


4.5. Uploading Scores
---------------------
Once you have inserted some scores, you can upload them to ScoreHero by
clicking "Upload to ScoreHero" from the main "Scores" menu. A wizzard will
appear to guide you through that process. Simply double check that your
scores are correct and click "Finish". A summary will be displayed upon
completion.

You can also quickly upload a single score by first selecting it and then
clicking "Upload Selected Score" from the main "Scores" menu. Alternatively,
with the score selected, press Ctrl+U.


4.6. Deleting Scores
--------------------
To delete a score, first select it, then click "Delete Selected Score" from
the main "Scores" menu. Alternatively, with the score selected, press the
"Del" key. If the score has been submitted to ScoreHero, you will be prompted
to confirm that you want to delete it. If the score is new, you will need to
select an uneditable cell (namely the "Date Submitted" cell) when selecting 
the score's row. Otherwise, the cell editor will appear when a key is pressed.

Note that currently JSHManager cannot delete scores that are on ScoreHero.
It can only delete the score from the local database.


4.7. Updating Song Data
-----------------------
When new DLC comes out that you want to insert scores for, you will need to
download the song data again. Select the "Guitar Hero" or "Rock Band" menu.
From the "Song Data" sub-menu, choose the game, instrument (if applicable),
and difficulty. Once the song data is listed, click the "Download..." menu
item from the "Song Data" menu. Guitar Hero data will take little time to
download. A progress dialog will be shown when updating Rock Band song data.


5. Notes
--------
If you choose to save your password after logging in to ScoreHero, it will
be saved to data/passwords.properties. The password is encoded but it
should not be considered highly secure. Since this is an open-source program,
anyone can obtain the source code to see how it is encoded. It is encoded
so that it at least is not visible as plain-text.

JSHManager logs various information in data/logs. JSHManager.txt contains
logging data for the main program. Hibernate.txt contains logging data
related to the database. When reporting problems, you may be asked to
provide these.


6. Getting Help/Reporting Bugs
------------------------------
Please post any issues in one of the JSHManager threads. If the issue is
specific to Guitar Hero or Rock Band, post in the appropriate thread.

Guitar Hero thread: http://www.scorehero.com/forum/viewtopic.php?t=74670
Rock Band Thread: http://rockband.scorehero.com/forum/viewtopic.php?t=15464

If you are getting an error message, please upload your logs via the
"Upload for Debugging..." menu item in the Help -> View Log menu. Copy and
paste the displayed URLs into your reply.
