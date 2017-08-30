ADB Mock Location app guide:

Before using any ADB command:

1) Install ADB Mock Location.apk
1) Go to Device Settings>Developer Options>Select Mock Location App: Set "ADBMockLocation"
2) Go to Settings>Location> set Device Only


ADB Commands:
Single point location:
adb shell am startservice -n com.pacmac.adbmocklocation/com.pacmac.adbmocklocation.MockService --es loc 48.424394, -123.356764

//DEPRECATED: adb shell am start -n com.pacmac.adbmocklocation/com.pacmac.adbmocklocation.MockActivity --es loc 48.424394, -123.356764


CONTINUOUS LOCATION SPAWNING:
Using service for spawning locations in circle from given reference point and given time interval:
adb shell am startservice -n com.pacmac.adbmocklocation/com.pacmac.adbmocklocation.MockService --es loc 49.224599,17.657078 --ez circle true --ei distance 10 --ei interval 30

- distance: radius in [km]
- interval: interval between location changes [s]
- circle: true if location should be spawn in cirlce / false will spawn location as single point


SHUTDOWN
Command to stop spawning location in circle:
adb shell am stoptservice -n com.pacmac.adbmocklocation/com.pacmac.adbmocklocation.MockService