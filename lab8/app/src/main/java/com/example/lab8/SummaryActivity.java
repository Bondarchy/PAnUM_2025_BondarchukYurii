package com.example.lab8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    private TextView summaryText;
    private Button newMatchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summaryText = findViewById(R.id.summaryText);
        newMatchButton = findViewById(R.id.newMatchButton);

        int winsX = getIntent().getIntExtra("winsX", 0);
        int winsO = getIntent().getIntExtra("winsO", 0);
        int draws = getIntent().getIntExtra("draws", 0);
        int totalGames = getIntent().getIntExtra("totalGames", 0);

        double pointsX = getIntent().getDoubleExtra("pointsX", 0);
        double pointsO = getIntent().getDoubleExtra("pointsO", 0);

        String winner;

        if (pointsX > pointsO) {
            winner = "Zwycięzca meczu: Gracz X";
        } else if (pointsO > pointsX) {
            winner = "Zwycięzca meczu: Gracz O";
        } else {
            winner = "Mecz zakończył się remisem";
        }

        summaryText.setText(
                "Podsumowanie meczu\n\n" +
                        "Liczba gier: " + totalGames + "\n\n" +
                        "Zwycięstwa X: " + winsX + "\n" +
                        "Zwycięstwa O: " + winsO + "\n" +
                        "Remisy: " + draws + "\n\n" +
                        "Punkty X: " + formatPoints(pointsX) + "\n" +
                        "Punkty O: " + formatPoints(pointsO) + "\n\n" +
                        winner
        );

        newMatchButton.setOnClickListener(v -> {
            Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private String formatPoints(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }

        return String.valueOf(value);
    }
}