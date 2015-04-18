AFJP-lab2
==========

Storage and Services

Countries visited -  Storage - 
A database to Countries visited in project AFJP.
* A list of countries. It is possible to change sorting order of the results (by year, by country) from Action
Bar menu.
* It is possible to add entries (from Action Bar menu).
* It possible to delete and update specific entry (from context menu).

Countries visited - Shared Preferences - 
Following functionality is implemented for the Visited Countries:
* Save settings for the last sorting order in the preferences. Next time when user starts the application,
the countries list should be sorted according to the sorting order saved last time when user closed app.
* Add some preferences and an activity for the user to modify them. Preferences is stored as Shared
Preferences. The content of the preferences is change of background color and font size.

Alarm Clock - 
My own alarm clock. It is possible to "change one's mind" after the alarm has been set and activated.
Also, the app displays the current time (digitally, e.g. 14:52:45) and the time display is updated every
5 seconds.

MP3 Player - 
A simple MP3 player. Player is able to play tracks from SD-card. Read files from the standard Music directory.
Basic controls (play, pause, next track, prev track) is supported. The music playback is in a Service and
continue to work when the enclosing application is closed. Ongoing notification is showing that MP3 player
is working. Restoring MP3 player activity from background when user clicks on notification.
