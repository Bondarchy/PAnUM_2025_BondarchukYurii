package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText arabicInput;
    private EditText romanInput;
    private TextView romanOutput;
    private TextView arabicOutput;
    private Button convertToRomanButton;
    private Button convertToArabicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arabicInput = findViewById(R.id.arabicInput);
        romanInput = findViewById(R.id.romanInput);
        romanOutput = findViewById(R.id.romanOutput);
        arabicOutput = findViewById(R.id.arabicOutput);
        convertToRomanButton = findViewById(R.id.convertToRomanButton);
        convertToArabicButton = findViewById(R.id.convertToArabicButton);

        convertToRomanButton.setOnClickListener(v -> convertArabicToRoman());
        convertToArabicButton.setOnClickListener(v -> convertRomanToArabic());
    }

    private void convertArabicToRoman() {
        String input = arabicInput.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Wprowadź liczbę arabską", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int number = Integer.parseInt(input);

            if (number < 1 || number > 3999) {
                Toast.makeText(this,
                        "Podaj liczbę od 1 do 3999",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            romanOutput.setText("Wynik: " + arabicToRoman(number));

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Niepoprawna liczba arabska",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void convertRomanToArabic() {
        String input = romanInput.getText()
                .toString()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (input.isEmpty()) {
            Toast.makeText(this, "Wprowadź liczbę rzymską", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = romanToArabic(input);

        if (result == -1) {
            Toast.makeText(this,
                    "Niepoprawna liczba rzymska",
                    Toast.LENGTH_SHORT).show();
        } else {
            arabicOutput.setText("Wynik: " + result);
        }
    }

    private String arabicToRoman(int num) {
        String[] romanThousands = {"", "M", "MM", "MMM"};
        String[] romanHundreds = {"", "C", "CC", "CCC", "CD", "D",
                "DC", "DCC", "DCCC", "CM"};
        String[] romanTens = {"", "X", "XX", "XXX", "XL", "L",
                "LX", "LXX", "LXXX", "XC"};
        String[] romanOnes = {"", "I", "II", "III", "IV", "V",
                "VI", "VII", "VIII", "IX"};

        return romanThousands[num / 1000]
                + romanHundreds[(num % 1000) / 100]
                + romanTens[(num % 100) / 10]
                + romanOnes[num % 10];
    }

    private int romanToArabic(String roman) {
        int result = 0;
        int previousValue = 0;

        for (int i = roman.length() - 1; i >= 0; i--) {
            int currentValue = romanValue(roman.charAt(i));

            if (currentValue == -1) {
                return -1;
            }

            if (currentValue < previousValue) {
                result -= currentValue;
            } else {
                result += currentValue;
            }

            previousValue = currentValue;
        }

        if (result < 1 || result > 3999) {
            return -1;
        }

        if (!arabicToRoman(result).equals(roman)) {
            return -1;
        }

        return result;
    }

    private int romanValue(char symbol) {
        switch (symbol) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return -1;
        }
    }
}