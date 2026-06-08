package com.example.lab6;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    private String type;
    private int id;
    private boolean editMode;

    private CafeDao dao;
    private CafeItem editItem;

    private TextView titleText;
    private EditText nameInput, descriptionInput, secondInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        type = getIntent().getStringExtra("type");
        id = getIntent().getIntExtra("id", -1);
        editMode = id != -1;

        dao = new CafeDao(this);

        titleText = findViewById(R.id.titleText);
        nameInput = findViewById(R.id.nameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        secondInput = findViewById(R.id.secondInput);
        saveButton = findViewById(R.id.saveButton);

        configureScreen();

        if (editMode) {
            loadItem();
        }

        saveButton.setOnClickListener(v -> saveItem());
    }

    private void configureScreen() {
        titleText.setText(editMode ? "Edytuj element" : "Dodaj element");

        if (type.equals("cafes")) {
            descriptionInput.setHint("Adres lokalu");
            secondInput.setHint("Godziny otwarcia");
            secondInput.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            descriptionInput.setHint("Opis");
            secondInput.setHint("Cena");
            secondInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

    private void loadItem() {
        editItem = dao.getById(type, id);

        if (editItem == null) {
            finish();
            return;
        }

        nameInput.setText(editItem.name);

        if (type.equals("cafes")) {
            descriptionInput.setText(editItem.address);
            secondInput.setText(editItem.openingHours);
        } else {
            descriptionInput.setText(editItem.description);
            secondInput.setText(String.valueOf(editItem.price));
        }
    }

    private void saveItem() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String second = secondInput.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || second.isEmpty()) {
            Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        CafeItem item = new CafeItem();
        item.id = id;
        item.type = type;
        item.name = name;

        if (editMode && editItem != null) {
            item.imageId = editItem.imageId;
        } else {
            item.imageId = getDefaultImage();
        }

        if (type.equals("cafes")) {
            item.address = description;
            item.openingHours = second;
        } else {
            try {
                item.price = Double.parseDouble(second);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Podaj poprawną cenę", Toast.LENGTH_SHORT).show();
                return;
            }

            item.description = description;
        }

        if (editMode) {
            dao.update(type, item);
        } else {
            dao.insert(type, item);
        }

        finish();
    }

    private int getDefaultImage() {
        if (type.equals("drinks")) return android.R.drawable.ic_menu_compass;
        if (type.equals("snacks")) return android.R.drawable.ic_menu_gallery;
        return android.R.drawable.ic_menu_mylocation;
    }
}