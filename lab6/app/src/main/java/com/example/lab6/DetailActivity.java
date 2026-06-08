package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private String type;
    private int id;

    private CafeDao dao;
    private CafeItem item;

    private TextView titleText, descriptionText;
    private ImageView imageView;
    private Button editButton, deleteButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        type = getIntent().getStringExtra("type");
        id = getIntent().getIntExtra("id", -1);

        dao = new CafeDao(this);

        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        imageView = findViewById(R.id.imageView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("id", id);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> confirmDelete());

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        item = dao.getById(type, id);

        if (item == null) {
            finish();
            return;
        }

        titleText.setText(item.name);
        imageView.setImageResource(item.imageId);

        if (type.equals("cafes")) {
            descriptionText.setText(
                    "Adres: " + item.address + "\n\n" +
                            "Godziny otwarcia: " + item.openingHours
            );
        } else {
            descriptionText.setText(
                    item.description + "\n\n" +
                            "Cena: " + item.price + " zł"
            );
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Usuwanie")
                .setMessage("Czy na pewno chcesz usunąć ten element?")
                .setPositiveButton("Tak", (dialog, which) -> {
                    dao.delete(type, id);
                    finish();
                })
                .setNegativeButton("Nie", null)
                .show();
    }
}