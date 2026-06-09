package com.example.liczbyrzymskie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputValue;
    TextView resultText;
    Button convertButton;
    Button arabicToRomanButton;
    Button romanToArabicButton;
    GridLayout arabicKeyboard;
    GridLayout romanKeyboard;

    boolean arabicToRoman = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);
        convertButton = findViewById(R.id.convertButton);
        arabicToRomanButton = findViewById(R.id.arabicToRomanButton);
        romanToArabicButton = findViewById(R.id.romanToArabicButton);
        arabicKeyboard = findViewById(R.id.arabicKeyboard);
        romanKeyboard = findViewById(R.id.romanKeyboard);

        arabicToRomanButton.setOnClickListener(v -> {
            arabicToRoman = true;
            clearAll();
            arabicKeyboard.setVisibility(View.VISIBLE);
            romanKeyboard.setVisibility(View.GONE);
            inputValue.setHint("Wprowadź liczbę arabską");
        });

        romanToArabicButton.setOnClickListener(v -> {
            arabicToRoman = false;
            clearAll();
            arabicKeyboard.setVisibility(View.GONE);
            romanKeyboard.setVisibility(View.VISIBLE);
            inputValue.setHint("Wprowadź liczbę rzymską");
        });

        convertButton.setOnClickListener(v -> {
            String text = inputValue.getText().toString();

            if (text.isEmpty()) {
                resultText.setText("Wprowadź wartość");
                return;
            }

            if (arabicToRoman) {
                int number = Integer.parseInt(text);
                resultText.setText(toRoman(number));
            } else {
                int number = fromRoman(text);
                resultText.setText(String.valueOf(number));
            }
        });
    }

    public void buttonClick(View view) {
        Button button = (Button) view;
        inputValue.append(button.getText().toString());
    }

    public void clearClick(View view) {
        clearAll();
    }

    public void deleteClick(View view) {
        String text = inputValue.getText().toString();

        if (!text.isEmpty()) {
            inputValue.setText(text.substring(0, text.length() - 1));
        }
    }

    private void clearAll() {
        inputValue.setText("");
        resultText.setText("");
    }

    private String toRoman(int number) {
        if (number <= 0 || number > 3999) {
            return "Liczba musi być od 1 do 3999";
        }

        int[] values = {
                1000, 900, 500, 400,
                100, 90, 50, 40,
                10, 9, 5, 4, 1
        };

        String[] symbols = {
                "M", "CM", "D", "CD",
                "C", "XC", "L", "XL",
                "X", "IX", "V", "IV", "I"
        };

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                result.append(symbols[i]);
                number -= values[i];
            }
        }

        return result.toString();
    }

    private int fromRoman(String roman) {
        int result = 0;
        int previous = 0;

        roman = roman.toUpperCase();

        for (int i = roman.length() - 1; i >= 0; i--) {
            int current = romanValue(roman.charAt(i));

            if (current < previous) {
                result -= current;
            } else {
                result += current;
            }

            previous = current;
        }

        return result;
    }

    private int romanValue(char letter) {
        switch (letter) {
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
                return 0;
        }
    }
}