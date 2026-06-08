package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button drinksButton, snacksButton, cafesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drinksButton = findViewById(R.id.drinksButton);
        snacksButton = findViewById(R.id.snacksButton);
        cafesButton = findViewById(R.id.cafesButton);

        drinksButton.setOnClickListener(v -> openList("drinks"));
        snacksButton.setOnClickListener(v -> openList("snacks"));
        cafesButton.setOnClickListener(v -> openList("cafes"));
    }

    private void openList(String type) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}