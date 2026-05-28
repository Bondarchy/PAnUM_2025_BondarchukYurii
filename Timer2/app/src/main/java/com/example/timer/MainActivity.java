package com.example.timer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editPaceMin, editPaceSec, editDistanceFromPace;
    private EditText editSpeed, editDistanceFromSpeed;
    private EditText editRequiredDistance, editRequiredHours, editRequiredMinutes, editRequiredSeconds;

    private TextView textResult;

    private final double MARATHON = 42.195;
    private final double HALF_MARATHON = 21.0975;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPaceMin = findViewById(R.id.editPaceMin);
        editPaceSec = findViewById(R.id.editPaceSec);
        editDistanceFromPace = findViewById(R.id.editDistanceFromPace);

        editSpeed = findViewById(R.id.editSpeed);
        editDistanceFromSpeed = findViewById(R.id.editDistanceFromSpeed);

        editRequiredDistance = findViewById(R.id.editRequiredDistance);
        editRequiredHours = findViewById(R.id.editRequiredHours);
        editRequiredMinutes = findViewById(R.id.editRequiredMinutes);
        editRequiredSeconds = findViewById(R.id.editRequiredSeconds);

        Button buttonFromPace = findViewById(R.id.buttonFromPace);
        Button buttonFromSpeed = findViewById(R.id.buttonFromSpeed);
        Button buttonRequired = findViewById(R.id.buttonRequired);

        textResult = findViewById(R.id.textResult);

        buttonFromPace.setOnClickListener(v -> calculateFromPace());
        buttonFromSpeed.setOnClickListener(v -> calculateFromSpeed());
        buttonRequired.setOnClickListener(v -> calculateRequiredPace());
    }

    private void calculateFromPace() {
        int minutes = getInt(editPaceMin);
        int seconds = getInt(editPaceSec);
        double distance = getDouble(editDistanceFromPace);

        if (minutes < 0 || seconds < 0 || distance <= 0 || seconds >= 60) {
            showError();
            return;
        }

        double paceMinutes = minutes + seconds / 60.0;
        double speed = 60.0 / paceMinutes;

        String result =
                "Obliczenia na podstawie tempa:\n\n" +
                        "Tempo: " + formatPace(paceMinutes) + "\n" +
                        "Prędkość: " + formatNumber(speed) + " km/h\n\n" +
                        "Czas maratonu: " + formatTime(paceMinutes, MARATHON) + "\n" +
                        "Czas półmaratonu: " + formatTime(paceMinutes, HALF_MARATHON) + "\n" +
                        "Czas dla dystansu " + formatNumber(distance) + " km: " + formatTime(paceMinutes, distance);

        textResult.setText(result);
    }

    private void calculateFromSpeed() {
        double speed = getDouble(editSpeed);
        double distance = getDouble(editDistanceFromSpeed);

        if (speed <= 0 || distance <= 0) {
            showError();
            return;
        }

        double paceMinutes = 60.0 / speed;

        String result =
                "Obliczenia na podstawie prędkości:\n\n" +
                        "Prędkość: " + formatNumber(speed) + " km/h\n" +
                        "Tempo: " + formatPace(paceMinutes) + "\n\n" +
                        "Czas maratonu: " + formatTime(paceMinutes, MARATHON) + "\n" +
                        "Czas półmaratonu: " + formatTime(paceMinutes, HALF_MARATHON) + "\n" +
                        "Czas dla dystansu " + formatNumber(distance) + " km: " + formatTime(paceMinutes, distance);

        textResult.setText(result);
    }

    private void calculateRequiredPace() {
        double distance = getDouble(editRequiredDistance);
        int hours = getInt(editRequiredHours);
        int minutes = getInt(editRequiredMinutes);
        int seconds = getInt(editRequiredSeconds);

        int totalSeconds = hours * 3600 + minutes * 60 + seconds;

        if (distance <= 0 || totalSeconds <= 0 || hours < 0 || minutes < 0 || seconds < 0 || minutes >= 60 || seconds >= 60) {
            showError();
            return;
        }

        double totalHours = totalSeconds / 3600.0;
        double requiredSpeed = distance / totalHours;

        double totalMinutes = totalSeconds / 60.0;
        double requiredPace = totalMinutes / distance;

        String result =
                "Wymagane tempo i prędkość:\n\n" +
                        "Dystans: " + formatNumber(distance) + " km\n" +
                        "Czas: " + hours + " godz. " + minutes + " min. " + seconds + " sek.\n\n" +
                        "Wymagane tempo: " + formatPace(requiredPace) + "\n" +
                        "Wymagana prędkość: " + formatNumber(requiredSpeed) + " km/h";

        textResult.setText(result);
    }

    private String formatTime(double paceMinutes, double distanceKm) {
        double totalSecondsDouble = paceMinutes * distanceKm * 60.0;
        int totalSeconds = (int) Math.round(totalSecondsDouble);

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return hours + " godz. " + minutes + " min. " + seconds + " sek.";
    }

    private String formatPace(double paceMinutes) {
        int min = (int) paceMinutes;
        int sec = (int) Math.round((paceMinutes - min) * 60);

        if (sec == 60) {
            min++;
            sec = 0;
        }

        return min + " min " + sec + " sek/km";
    }

    private double getDouble(EditText editText) {
        try {
            String value = editText.getText().toString().replace(",", ".");
            if (value.isEmpty()) return -1;
            return Double.parseDouble(value);
        } catch (Exception e) {
            return -1;
        }
    }

    private int getInt(EditText editText) {
        try {
            String value = editText.getText().toString();
            if (value.isEmpty()) return 0;
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    private String formatNumber(double number) {
        return String.format(Locale.US, "%.2f", number);
    }

    private void showError() {
        Toast.makeText(this, "Wprowadź poprawne dane", Toast.LENGTH_SHORT).show();
    }
}