package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnSystems;
    private Button btnCurrencies;
    private Button btnUnits;

    private LinearLayout systemsSection;
    private LinearLayout currencySection;
    private LinearLayout unitsSection;

    private EditText numberInput;
    private Spinner fromBaseSpinner;
    private Spinner toBaseSpinner;
    private TextView systemResult;

    private EditText currencyInput;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private TextView currencyResult;

    private Spinner unitTypeSpinner;
    private EditText unitInput;
    private Spinner fromUnitSpinner;
    private Spinner toUnitSpinner;
    private TextView unitResult;

    private final String[] systemNames = {
            "Dziesiętny (10)",
            "Dwójkowy (2)",
            "Czwórkowy (4)",
            "Ósemkowy (8)",
            "Szesnastkowy (16)"
    };

    private final int[] systemBases = {10, 2, 4, 8, 16};

    private final String[] currencies = {
            "PLN", "USD", "EUR", "GBP", "UAH"
    };

    /*
       Stałe kursy edukacyjne:
       wartość jednej jednostki waluty wyrażona w PLN.
    */
    private final double[] currencyRatesInPln = {
            1.00,   // PLN
            3.90,   // USD
            4.25,   // EUR
            5.05,   // GBP
            0.093   // UAH
    };

    private final String[] unitTypes = {
            "Długość",
            "Pole powierzchni"
    };

    private final String[] lengthUnits = {
            "mm", "cm", "cale", "stopy", "yardy", "m", "km"
    };

    /*
       Wartości długości przeliczone na metry.
    */
    private final double[] lengthFactors = {
            0.001,      // mm
            0.01,       // cm
            0.0254,     // cale
            0.3048,     // stopy
            0.9144,     // yardy
            1.0,        // m
            1000.0      // km
    };

    private final String[] areaUnits = {
            "mm²", "cm²", "m²", "km²", "ary", "hektary"
    };

    /*
       Wartości pola powierzchni przeliczone na m².
    */
    private final double[] areaFactors = {
            0.000001,       // mm²
            0.0001,         // cm²
            1.0,            // m²
            1000000.0,      // km²
            100.0,          // ary
            10000.0         // hektary
    };

    private DecimalFormat numberFormat;
    private DecimalFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DecimalFormatSymbols polishSymbols =
                DecimalFormatSymbols.getInstance(new Locale("pl", "PL"));

        numberFormat = new DecimalFormat("0.######", polishSymbols);
        currencyFormat = new DecimalFormat("0.00", polishSymbols);

        initializeViews();
        initializeSpinners();
        initializeListeners();

        showSystemsSection();
    }

    private void initializeViews() {
        btnSystems = findViewById(R.id.btnSystems);
        btnCurrencies = findViewById(R.id.btnCurrencies);
        btnUnits = findViewById(R.id.btnUnits);

        systemsSection = findViewById(R.id.systemsSection);
        currencySection = findViewById(R.id.currencySection);
        unitsSection = findViewById(R.id.unitsSection);

        numberInput = findViewById(R.id.numberInput);
        fromBaseSpinner = findViewById(R.id.fromBaseSpinner);
        toBaseSpinner = findViewById(R.id.toBaseSpinner);
        systemResult = findViewById(R.id.systemResult);

        currencyInput = findViewById(R.id.currencyInput);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        currencyResult = findViewById(R.id.currencyResult);

        unitTypeSpinner = findViewById(R.id.unitTypeSpinner);
        unitInput = findViewById(R.id.unitInput);
        fromUnitSpinner = findViewById(R.id.fromUnitSpinner);
        toUnitSpinner = findViewById(R.id.toUnitSpinner);
        unitResult = findViewById(R.id.unitResult);
    }

    private void initializeSpinners() {
        setSpinnerAdapter(fromBaseSpinner, systemNames);
        setSpinnerAdapter(toBaseSpinner, systemNames);

        fromBaseSpinner.setSelection(0);
        toBaseSpinner.setSelection(1);

        setSpinnerAdapter(fromCurrencySpinner, currencies);
        setSpinnerAdapter(toCurrencySpinner, currencies);

        fromCurrencySpinner.setSelection(0);
        toCurrencySpinner.setSelection(1);

        setSpinnerAdapter(unitTypeSpinner, unitTypes);
        setupUnitSpinners(0);
    }

    private void initializeListeners() {
        btnSystems.setOnClickListener(v -> showSystemsSection());
        btnCurrencies.setOnClickListener(v -> showCurrencySection());
        btnUnits.setOnClickListener(v -> showUnitsSection());

        findViewById(R.id.btnConvertSystem)
                .setOnClickListener(v -> convertNumberSystem());

        findViewById(R.id.btnConvertCurrency)
                .setOnClickListener(v -> convertCurrency());

        findViewById(R.id.btnConvertUnit)
                .setOnClickListener(v -> convertUnit());

        unitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                setupUnitSpinners(position);
                unitResult.setText("Wynik:");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSpinnerAdapter(Spinner spinner, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void showSystemsSection() {
        systemsSection.setVisibility(View.VISIBLE);
        currencySection.setVisibility(View.GONE);
        unitsSection.setVisibility(View.GONE);
    }

    private void showCurrencySection() {
        systemsSection.setVisibility(View.GONE);
        currencySection.setVisibility(View.VISIBLE);
        unitsSection.setVisibility(View.GONE);
    }

    private void showUnitsSection() {
        systemsSection.setVisibility(View.GONE);
        currencySection.setVisibility(View.GONE);
        unitsSection.setVisibility(View.VISIBLE);
    }

    private void convertNumberSystem() {
        String input = numberInput.getText()
                .toString()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (input.isEmpty()) {
            showToast("Wprowadź liczbę");
            return;
        }

        int fromBase = systemBases[fromBaseSpinner.getSelectedItemPosition()];
        int toBase = systemBases[toBaseSpinner.getSelectedItemPosition()];

        try {
            BigInteger number = new BigInteger(input, fromBase);

            if (number.signum() < 0) {
                showToast("Wprowadź liczbę dodatnią");
                return;
            }

            String result = number.toString(toBase).toUpperCase(Locale.ROOT);
            systemResult.setText("Wynik: " + result);

        } catch (NumberFormatException e) {
            showToast("Nieprawidłowa liczba dla wybranego systemu");
        }
    }

    private void convertCurrency() {
        String input = currencyInput.getText().toString().trim();

        if (input.isEmpty()) {
            showToast("Wprowadź kwotę");
            return;
        }

        try {
            double amount = parseNumber(input);

            if (amount < 0) {
                showToast("Kwota nie może być ujemna");
                return;
            }

            int fromPosition = fromCurrencySpinner.getSelectedItemPosition();
            int toPosition = toCurrencySpinner.getSelectedItemPosition();

            double amountInPln = amount * currencyRatesInPln[fromPosition];
            double result = amountInPln / currencyRatesInPln[toPosition];

            String text = currencyFormat.format(amount)
                    + " " + currencies[fromPosition]
                    + " = "
                    + currencyFormat.format(result)
                    + " " + currencies[toPosition];

            currencyResult.setText("Wynik: " + text);

        } catch (NumberFormatException e) {
            showToast("Wprowadź poprawną kwotę");
        }
    }

    private void setupUnitSpinners(int typePosition) {
        if (typePosition == 0) {
            setSpinnerAdapter(fromUnitSpinner, lengthUnits);
            setSpinnerAdapter(toUnitSpinner, lengthUnits);

            fromUnitSpinner.setSelection(0);
            toUnitSpinner.setSelection(5);
        } else {
            setSpinnerAdapter(fromUnitSpinner, areaUnits);
            setSpinnerAdapter(toUnitSpinner, areaUnits);

            fromUnitSpinner.setSelection(0);
            toUnitSpinner.setSelection(2);
        }
    }

    private void convertUnit() {
        String input = unitInput.getText().toString().trim();

        if (input.isEmpty()) {
            showToast("Wprowadź wartość");
            return;
        }

        try {
            double value = parseNumber(input);

            if (value < 0) {
                showToast("Wartość nie może być ujemna");
                return;
            }

            int typePosition = unitTypeSpinner.getSelectedItemPosition();
            int fromPosition = fromUnitSpinner.getSelectedItemPosition();
            int toPosition = toUnitSpinner.getSelectedItemPosition();

            String fromUnit;
            String toUnit;
            double result;

            if (typePosition == 0) {
                double valueInMeters = value * lengthFactors[fromPosition];
                result = valueInMeters / lengthFactors[toPosition];

                fromUnit = lengthUnits[fromPosition];
                toUnit = lengthUnits[toPosition];
            } else {
                double valueInSquareMeters = value * areaFactors[fromPosition];
                result = valueInSquareMeters / areaFactors[toPosition];

                fromUnit = areaUnits[fromPosition];
                toUnit = areaUnits[toPosition];
            }

            String text = numberFormat.format(value)
                    + " " + fromUnit
                    + " = "
                    + numberFormat.format(result)
                    + " " + toUnit;

            unitResult.setText("Wynik: " + text);

        } catch (NumberFormatException e) {
            showToast("Wprowadź poprawną wartość");
        }
    }

    private double parseNumber(String value) {
        return Double.parseDouble(value.replace(",", "."));
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}