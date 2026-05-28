package com.example.timemaster;
import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LinearLayout stopwatchLayout, timerLayout;

    private TextView txtStopwatch, txtTimer;
    private EditText editMinutes, editSeconds;

    private Handler handler = new Handler(Looper.getMainLooper());

    // STOPER
    private boolean stopwatchRunning = false;
    private long stopwatchBaseTime = 0;
    private long stopwatchElapsedBeforePause = 0;

    // MINUTNIK
    private boolean timerRunning = false;
    private long timerDurationMillis = 0;
    private long timerRemainingMillis = 0;
    private long timerEndTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopwatchLayout = findViewById(R.id.stopwatchLayout);
        timerLayout = findViewById(R.id.timerLayout);

        txtStopwatch = findViewById(R.id.txtStopwatch);
        txtTimer = findViewById(R.id.txtTimer);

        editMinutes = findViewById(R.id.editMinutes);
        editSeconds = findViewById(R.id.editSeconds);

        Button btnShowStopwatch = findViewById(R.id.btnShowStopwatch);
        Button btnShowTimer = findViewById(R.id.btnShowTimer);

        Button btnStartStopwatch = findViewById(R.id.btnStartStopwatch);
        Button btnPauseStopwatch = findViewById(R.id.btnPauseStopwatch);
        Button btnResetStopwatch = findViewById(R.id.btnResetStopwatch);

        Button btnSetTimer = findViewById(R.id.btnSetTimer);
        Button btnStartTimer = findViewById(R.id.btnStartTimer);
        Button btnPauseTimer = findViewById(R.id.btnPauseTimer);
        Button btnResetTimer = findViewById(R.id.btnResetTimer);

        btnShowStopwatch.setOnClickListener(v -> {
            stopwatchLayout.setVisibility(View.VISIBLE);
            timerLayout.setVisibility(View.GONE);
        });

        btnShowTimer.setOnClickListener(v -> {
            stopwatchLayout.setVisibility(View.GONE);
            timerLayout.setVisibility(View.VISIBLE);
        });

        btnStartStopwatch.setOnClickListener(v -> startStopwatch());
        btnPauseStopwatch.setOnClickListener(v -> pauseStopwatch());
        btnResetStopwatch.setOnClickListener(v -> resetStopwatch());

        btnSetTimer.setOnClickListener(v -> setTimer());
        btnStartTimer.setOnClickListener(v -> startTimer());
        btnPauseTimer.setOnClickListener(v -> pauseTimer());
        btnResetTimer.setOnClickListener(v -> resetTimer());
    }

    // ===================== STOPER =====================

    private final Runnable stopwatchRunnable = new Runnable() {
        @Override
        public void run() {
            if (stopwatchRunning) {
                long currentElapsed =
                        stopwatchElapsedBeforePause +
                                (SystemClock.elapsedRealtime() - stopwatchBaseTime);

                txtStopwatch.setText(formatStopwatchTime(currentElapsed));

                handler.postDelayed(this, 10); // 0,01 sekundy
            }
        }
    };

    private void startStopwatch() {
        if (!stopwatchRunning) {
            stopwatchBaseTime = SystemClock.elapsedRealtime();
            stopwatchRunning = true;
            handler.post(stopwatchRunnable);
        }
    }

    private void pauseStopwatch() {
        if (stopwatchRunning) {
            stopwatchElapsedBeforePause +=
                    SystemClock.elapsedRealtime() - stopwatchBaseTime;

            stopwatchRunning = false;
            handler.removeCallbacks(stopwatchRunnable);
        }
    }

    private void resetStopwatch() {
        stopwatchRunning = false;
        stopwatchElapsedBeforePause = 0;
        handler.removeCallbacks(stopwatchRunnable);
        txtStopwatch.setText("00:00:00.00");
    }

    private String formatStopwatchTime(long millis) {
        long totalCentiseconds = millis / 10;

        long centiseconds = totalCentiseconds % 100;
        long totalSeconds = totalCentiseconds / 100;

        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;

        long minutes = totalMinutes % 60;
        long hours = totalMinutes / 60;

        return String.format("%02d:%02d:%02d.%02d",
                hours, minutes, seconds, centiseconds);
    }

    // ===================== MINUTNIK =====================

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning) {
                timerRemainingMillis = timerEndTime - SystemClock.elapsedRealtime();

                if (timerRemainingMillis <= 0) {
                    timerRunning = false;
                    timerRemainingMillis = 0;
                    txtTimer.setText("00:00:00");

                    Toast.makeText(
                            MainActivity.this,
                            "Czas minął!",
                            Toast.LENGTH_SHORT
                    ).show();

                    playSound();
                } else {
                    txtTimer.setText(formatTimerTime(timerRemainingMillis));
                    handler.postDelayed(this, 200);
                }
            }
        }
    };

    private void setTimer() {
        timerDurationMillis = readTimerTime();

        if (timerDurationMillis <= 0) {
            Toast.makeText(this, "Podaj poprawny czas!", Toast.LENGTH_SHORT).show();
            return;
        }

        timerRunning = false;
        timerRemainingMillis = timerDurationMillis;
        handler.removeCallbacks(timerRunnable);

        txtTimer.setText(formatTimerTime(timerRemainingMillis));

        Toast.makeText(this, "Czas ustawiony", Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        if (timerRunning) {
            return;
        }

        if (timerRemainingMillis <= 0) {
            timerRemainingMillis = readTimerTime();
            timerDurationMillis = timerRemainingMillis;
        }

        if (timerRemainingMillis <= 0) {
            Toast.makeText(this, "Podaj czas minutnika!", Toast.LENGTH_SHORT).show();
            return;
        }

        timerEndTime = SystemClock.elapsedRealtime() + timerRemainingMillis;
        timerRunning = true;

        handler.post(timerRunnable);
    }

    private void pauseTimer() {
        if (timerRunning) {
            timerRemainingMillis = timerEndTime - SystemClock.elapsedRealtime();

            if (timerRemainingMillis < 0) {
                timerRemainingMillis = 0;
            }

            timerRunning = false;
            handler.removeCallbacks(timerRunnable);

            txtTimer.setText(formatTimerTime(timerRemainingMillis));
        }
    }

    private void resetTimer() {
        timerRunning = false;
        handler.removeCallbacks(timerRunnable);

        timerDurationMillis = readTimerTime();
        timerRemainingMillis = timerDurationMillis;

        if (timerRemainingMillis > 0) {
            txtTimer.setText(formatTimerTime(timerRemainingMillis));
        } else {
            txtTimer.setText("00:00:00");
        }
    }

    private long readTimerTime() {
        long minutes = 0;
        long seconds = 0;

        String minText = editMinutes.getText().toString();
        String secText = editSeconds.getText().toString();

        if (!minText.isEmpty()) {
            minutes = Long.parseLong(minText);
        }

        if (!secText.isEmpty()) {
            seconds = Long.parseLong(secText);
        }

        return (minutes * 60 + seconds) * 1000;
    }

    private String formatTimerTime(long millis) {
        long totalSeconds = (millis + 999) / 1000;

        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;

        long minutes = totalMinutes % 60;
        long hours = totalMinutes / 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void playSound() {
        ToneGenerator toneGen =
                new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        toneGen.startTone(
                ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE,
                300
        );

        handler.postDelayed(toneGen::release, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(stopwatchRunnable);
        handler.removeCallbacks(timerRunnable);
    }
}