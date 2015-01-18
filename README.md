mancala
=======

Mancala game Android App
Developed with AndroidStudio 1.0.2
To import just do Import Project
To run the unitTest create an AndroidTest configuration for the GameLibraryTestSuite class.

Some note:
- most of the test where done on a Samsung Nexus S with Android 4.4.4 and a screen resolution of 800×480 never run the app in other real device, expected some problem with high resolution display.
- followed the Android Guidelines for the Layout, that's why for the back button was used the system one, that's available in all devices and that was intentionally left on the screen (in the new device it's not an HW button but it can be hidden by the app theme)
- animation is available only in the HvH game, still having some trouble with threading ad queue in the HvC game, because of the high speed of the events.