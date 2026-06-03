package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {

    private Button btnDrinks;
    private Button btnSnacks;
    private Button btnLocation;

    private LinearLayout productSection;
    private LinearLayout locationSection;

    private TextView categoryTitle;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;

    private Spinner productSpinner;

    private boolean showingDrinks = true;

    private final String[] drinksNames = {
            "Espresso",
            "Cappuccino",
            "Lemoniada"
    };

    private final String[] drinksDescriptions = {
            "Aromatyczna, mocna kawa przygotowana ze świeżo mielonych ziaren.",
            "Delikatna kawa espresso z gorącym mlekiem i mleczną pianką.",
            "Orzeźwiający napój cytrynowy z miętą i kostkami lodu."
    };

    private final String[] drinksPrices = {
            "8,00 zł",
            "12,00 zł",
            "10,00 zł"
    };

    private final String[] snacksNames = {
            "Croissant",
            "Tost z serem",
            "Sernik"
    };

    private final String[] snacksDescriptions = {
            "Maślany rogalik francuski, świeżo wypiekany każdego dnia.",
            "Chrupiący tost z serem, szynką i dodatkiem warzyw.",
            "Domowy sernik podawany z sosem owocowym."
    };

    private final String[] snacksPrices = {
            "9,00 zł",
            "16,00 zł",
            "14,00 zł"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDrinks = findViewById(R.id.btnDrinks);
        btnSnacks = findViewById(R.id.btnSnacks);
        btnLocation = findViewById(R.id.btnLocation);

        productSection = findViewById(R.id.productSection);
        locationSection = findViewById(R.id.locationSection);

        categoryTitle = findViewById(R.id.categoryTitle);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);

        productSpinner = findViewById(R.id.productSpinner);

        btnDrinks.setOnClickListener(v -> showDrinks());
        btnSnacks.setOnClickListener(v -> showSnacks());
        btnLocation.setOnClickListener(v -> showLocation());

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showSelectedProduct(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        showDrinks();
    }

    private void showDrinks() {
        showingDrinks = true;

        productSection.setVisibility(View.VISIBLE);
        locationSection.setVisibility(View.GONE);

        categoryTitle.setText("Menu napojów");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                drinksNames
        );

        productSpinner.setAdapter(adapter);
        productSpinner.setSelection(0);
        showSelectedProduct(0);
    }

    private void showSnacks() {
        showingDrinks = false;

        productSection.setVisibility(View.VISIBLE);
        locationSection.setVisibility(View.GONE);

        categoryTitle.setText("Menu przekąsek");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                snacksNames
        );

        productSpinner.setAdapter(adapter);
        productSpinner.setSelection(0);
        showSelectedProduct(0);
    }

    private void showLocation() {
        productSection.setVisibility(View.GONE);
        locationSection.setVisibility(View.VISIBLE);
    }

    private void showSelectedProduct(int position) {
        if (showingDrinks) {
            productName.setText(drinksNames[position]);
            productDescription.setText(drinksDescriptions[position]);
            productPrice.setText("Cena: " + drinksPrices[position]);
        } else {
            productName.setText(snacksNames[position]);
            productDescription.setText(snacksDescriptions[position]);
            productPrice.setText("Cena: " + snacksPrices[position]);
        }
    }
}