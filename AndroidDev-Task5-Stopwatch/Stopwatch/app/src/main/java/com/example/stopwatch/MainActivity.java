package com.example.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Stopwatch with Start / Stop / Reset / Lap.
 *
 * Accuracy: elapsed time is never counted by adding up Handler ticks (those drift and
 * stop while the screen is off). Instead we store the SystemClock.elapsedRealtime()
 * timestamp of the last start and the time accumulated before it, so the elapsed time
 * is always computed from a monotonic clock. The Handler only refreshes the display.
 *
 * Lifecycle: the Handler is stopped in onPause() and restarted in onResume(). Because
 * the elapsed time is derived from the clock, the stopwatch is still correct when the
 * user returns, and its state also survives rotation via onSaveInstanceState().
 */
public class MainActivity extends AppCompatActivity {

    private static final long UPDATE_INTERVAL_MS = 10;

    private static final String KEY_RUNNING = "running";
    private static final String KEY_START = "start_time";
    private static final String KEY_ACCUM = "accumulated";
    private static final String KEY_LAP_TOTALS = "lap_totals";

    // ---- UI ----
    private TextView timeText;
    private TextView millisText;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    private Button lapButton;
    private LapAdapter lapAdapter;
    private final List<Lap> laps = new ArrayList<>();

    // ---- Stopwatch state ----
    private boolean running = false;
    private long startTime = 0;      // elapsedRealtime when the timer was last started
    private long accumulatedMs = 0;  // time banked from earlier run segments

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateDisplay();
            handler.postDelayed(this, UPDATE_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeText = findViewById(R.id.timeText);
        millisText = findViewById(R.id.millisText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);
        ListView lapList = findViewById(R.id.lapList);

        lapAdapter = new LapAdapter(this, laps);
        lapList.setAdapter(lapAdapter);
        lapList.setEmptyView(findViewById(R.id.emptyLapsText));

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        startButton.setOnClickListener(v -> startTimer());
        stopButton.setOnClickListener(v -> stopTimer());
        resetButton.setOnClickListener(v -> resetTimer());
        lapButton.setOnClickListener(v -> recordLap());

        updateDisplay();
        updateButtons();
    }

    // ------------------------------------------------------------------
    // Controls
    // ------------------------------------------------------------------

    private void startTimer() {
        if (running) return;
        startTime = SystemClock.elapsedRealtime();
        running = true;
        handler.removeCallbacks(ticker);
        handler.post(ticker);
        updateButtons();
    }

    private void stopTimer() {
        if (!running) return;
        accumulatedMs += SystemClock.elapsedRealtime() - startTime;
        running = false;
        handler.removeCallbacks(ticker);
        updateDisplay(); // freeze on the exact stop time
        updateButtons();
    }

    private void resetTimer() {
        running = false;
        handler.removeCallbacks(ticker);
        accumulatedMs = 0;
        startTime = 0;
        laps.clear();
        lapAdapter.notifyDataSetChanged();
        updateDisplay();
        updateButtons();
    }

    private void recordLap() {
        if (!running) return;
        long total = getElapsedMs();
        long previousTotal = laps.isEmpty() ? 0 : laps.get(0).totalMs; // newest lap is first
        laps.add(0, new Lap(laps.size() + 1, total - previousTotal, total));
        lapAdapter.notifyDataSetChanged();
    }

    // ------------------------------------------------------------------
    // Time + display helpers
    // ------------------------------------------------------------------

    private long getElapsedMs() {
        return running
                ? accumulatedMs + (SystemClock.elapsedRealtime() - startTime)
                : accumulatedMs;
    }

    private void updateDisplay() {
        long ms = getElapsedMs();
        timeText.setText(formatHms(ms));
        millisText.setText(String.format(Locale.US, ".%02d", (ms % 1000) / 10));
    }

    /** HH:MM:SS */
    static String formatHms(long ms) {
        long totalSeconds = ms / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /** HH:MM:SS.cc (hundredths) - used in the lap list. */
    static String formatFull(long ms) {
        return formatHms(ms) + String.format(Locale.US, ".%02d", (ms % 1000) / 10);
    }

    /**
     * Start: enabled when stopped/paused (reads "Resume" if there is banked time).
     * Stop:  enabled only while running.
     * Lap:   enabled only while running.
     * Reset: enabled unless the stopwatch is pristine (0 and not running).
     */
    private void updateButtons() {
        boolean hasTime = accumulatedMs > 0 || !laps.isEmpty();

        startButton.setEnabled(!running);
        startButton.setText(!running && hasTime ? R.string.resume : R.string.start);
        stopButton.setEnabled(running);
        lapButton.setEnabled(running);
        resetButton.setEnabled(running || hasTime);
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay(); // catch up immediately with the time that passed while away
        if (running) {
            handler.removeCallbacks(ticker);
            handler.post(ticker);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // No UI updates while not visible. The elapsed time is still tracked correctly
        // because it is derived from SystemClock, not from these callbacks.
        handler.removeCallbacks(ticker);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RUNNING, running);
        outState.putLong(KEY_START, startTime);
        outState.putLong(KEY_ACCUM, accumulatedMs);

        long[] totals = new long[laps.size()];
        for (int i = 0; i < laps.size(); i++) totals[i] = laps.get(i).totalMs;
        outState.putLongArray(KEY_LAP_TOTALS, totals); // newest first
    }

    private void restoreState(Bundle state) {
        running = state.getBoolean(KEY_RUNNING, false);
        startTime = state.getLong(KEY_START, 0);
        accumulatedMs = state.getLong(KEY_ACCUM, 0);

        long[] totals = state.getLongArray(KEY_LAP_TOTALS);
        if (totals != null) {
            for (int i = 0; i < totals.length; i++) {
                long previous = (i + 1 < totals.length) ? totals[i + 1] : 0;
                laps.add(new Lap(totals.length - i, totals[i] - previous, totals[i]));
            }
            lapAdapter.notifyDataSetChanged();
        }
    }
}