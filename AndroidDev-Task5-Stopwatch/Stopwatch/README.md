# Author
Huda Saeed Othman Al-amin

# Stopwatch App (Android)

**Oasis Infobyte Internship - Android App Development - Task 5**

A stopwatch app for Android with Start, Stop/Resume, Reset and Lap controls. It shows the elapsed time in `HH:MM:SS` with hundredths of a second, and keeps counting correctly when the user leaves the app and comes back.

## Screenshots

AndroidDev-Task5-Stopwatch\Stopwatch\Screenshots

## Features

- Large time display in `HH:MM:SS` with hundredths of a second shown underneath
- **Start** begins from 0, or continues from a paused state (the button changes to "Resume")
- **Stop** freezes the timer at the current elapsed time
- **Reset** stops the timer, sets the display back to `00:00:00` and clears the laps
- Buttons change state visually: Start is greyed out while running, and Stop, Lap and Reset become active
- Timer keeps running correctly when the user navigates away and returns (`onPause` / `onResume`)
- State is also kept when the screen is rotated (`onSaveInstanceState`)
- **Bonus:** Lap button records the current time in a scrollable list (newest first), showing each lap's split time and total time

## Tech Stack

- Android Studio
- Java
- XML layouts (LinearLayout, FrameLayout, ListView)

## How It Works

- A `Handler` and `Runnable` refresh the display every 10 ms on the UI thread.
- The elapsed time is **not** counted by adding up ticks. It is calculated from `SystemClock.elapsedRealtime()` using the time of the last start and the time banked before the last stop. This keeps it accurate even when the app is in the background.
- `onPause()` stops the display refresh and `onResume()` restarts it if the stopwatch is running.
- Laps are stored as `Lap` objects and shown with a custom `ArrayAdapter` (`LapAdapter`).
- A colour selector in `res/color` turns disabled buttons grey automatically.

## Project Structure

```
app/src/main/
├── java/com/example/stopwatch/
│   ├── MainActivity.java   # timer logic, buttons, lifecycle
│   ├── Lap.java            # model: lap number, split time, total time
│   └── LapAdapter.java     # draws the rows of the lap list
└── res/
    ├── layout/
    │   ├── activity_main.xml   # main screen
    │   └── item_lap.xml        # one lap row
    ├── color/                  # button colour selectors (grey when disabled)
    └── values/                 # strings.xml, colors.xml
```

## How to Run

1. Clone the repository and open the `AndroidDev-Task5-Stopwatch` folder in Android Studio.
2. Wait for Gradle to finish syncing.
3. Run the app on an emulator or a physical device (enable USB debugging on the phone).

## Testing Checklist

- [x] Start, Stop, Resume and Reset work
- [x] Buttons grey out and re-enable correctly
- [x] Lap list records split and total times
- [x] Timer stays correct after pressing Home and returning
- [x] Layout fits the screen on a Redmi Note 13 (4G)


