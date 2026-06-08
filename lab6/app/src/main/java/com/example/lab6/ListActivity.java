package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private String type;
    private CafeDao dao;

    private TextView titleText;
    private ListView listView;
    private Button addButton;

    private ArrayList<CafeItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        type = getIntent().getStringExtra("type");
        dao = new CafeDao(this);

        titleText = findViewById(R.id.titleText);
        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);

        titleText.setText(getTitleByType());

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CafeItem item = items.get(position);

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("id", item.id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        items = dao.getAll(type);

        ArrayAdapter<CafeItem> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                items
        );

        listView.setAdapter(adapter);
    }

    private String getTitleByType() {
        if (type.equals("drinks")) return "Napoje";
        if (type.equals("snacks")) return "Przekąski";
        return "Lokale";
    }
}