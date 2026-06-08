package com.example.lab8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText gamesInput;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gamesInput = findViewById(R.id.gamesInput);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            String text = gamesInput.getText().toString().trim();

            if (text.isEmpty()) {
                Toast.makeText(this, "Podaj liczbę gier", Toast.LENGTH_SHORT).show();
                return;
            }

            int gamesCount;

            try {
                gamesCount = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Niepoprawna liczba", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gamesCount <= 0) {
                Toast.makeText(this, "Liczba gier musi być większa od 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("totalGames", gamesCount);
            startActivity(intent);
        });
    }
}