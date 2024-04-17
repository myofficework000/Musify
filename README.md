# Music Player
A Music Player built with Jetpack compose and media3/Exoplayer

## Features
- Load/Play Songs from External/Shared Storage
- Foreground Service to play the music, when the app is in Background.
- Recommended Music Player Lifecycle Handling
- MediaSessions from Jetpack Media3 artifact.
- Styled Notification for music playback control.

### PlayerNotificationManager Integration
This Music Player integrates with PlayerNotificationManager from ExoPlayer to provide a seamless music playback experience and manage notifications for music control. Here's how it works using Media3 Android API:

1. **Notification Channel Setup**: Before displaying any notifications, a notification channel is created using the NotificationManager. This channel is essential for managing and customizing notifications for the music player.

2. **MediaStyle Notification**: The Music Player utilizes MediaStyle notifications, which provide a rich interface for controlling music playback, including play, pause, skip, and stop actions. These notifications are designed to be interactive and offer a consistent user experience across different Android versions.

3. **Updating Notification**: As the playback state changes (e.g., song changes, play/pause actions), the Music Player updates the notification to reflect the current playback status. This ensures that users can always see the current song information and control playback without needing to open the app.

4. **Foreground Service**: The music playback functionality runs as a foreground service, ensuring that the system prioritizes its execution, even when memory is low or the device is under heavy load. This allows the music player to continue playing music seamlessly in the background while users interact with other apps or the device.

## Future Updates
- PlayList
- Favourites/Liked Songs
- Detailing Mini Player to Different Screen

## Technologies
- Kotlin
- Jetpack Compose
- Koin for Dependency Injection
- Glide/Coil for Image Loading/Showing
- Exoplayer etc.,

## ScreenShots  
![image](https://github.com/myofficework000/Musify/assets/51234843/42f3194e-6a79-4e82-97dc-c5c160234d96)

![image](https://github.com/myofficework000/Musify/assets/51234843/921f661c-50ce-442c-8f08-7123d1ba9228)

![image](https://github.com/myofficework000/Musify/assets/51234843/5d7d7c00-ed61-4bdc-8b4f-7dc8d706d498)


