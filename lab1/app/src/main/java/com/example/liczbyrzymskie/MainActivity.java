package com.example.liczbyrzymskie;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText arabicInput;
    TextView romanOutput;
    Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arabicInput = findViewById(R.id.arabicInput);
        romanOutput = findViewById(R.id.romanOutput);
        convertButton = findViewById(R.id.convertButton);

        convertButton.setOnClickListener(v -> {
            String inputStr = arabicInput.getText().toString();
            if (!inputStr.isEmpty()) {
                int number = Integer.parseInt(inputStr);
                romanOutput.setText(arabicToRoman(number));
            }
        });
    }

    private String arabicToRoman(int num) {
        String[] romanThousands = {"", "M", "MM", "MMM"};
        String[] romanHundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] romanTens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] romanOnes = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return romanThousands[num / 1000] +
                romanHundreds[(num % 1000) / 100] +
                romanTens[(num % 100) / 10] +
                romanOnes[num % 10];
    }
}