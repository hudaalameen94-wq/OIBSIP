package com.example.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ---------- Data tables (one row per category) ----------

    private static final String[] CATEGORY_NAMES = {
            "Length", "Weight", "Volume", "Temperature"
    };

    private static final int CAT_TEMPERATURE = 3;

    private static final String[][] UNIT_NAMES = {
            // Length
            {"Millimetre", "Centimetre", "Metre", "Kilometre", "Inch", "Foot", "Yard", "Mile"},
            // Weight
            {"Milligram", "Gram", "Kilogram", "Tonne", "Ounce", "Pound"},
            // Volume
            {"Millilitre", "Litre", "Cubic metre", "Teaspoon (US)", "Tablespoon (US)", "Cup (US)", "Gallon (US)"},
            // Temperature
            {"Celsius", "Fahrenheit", "Kelvin"}
    };

    private static final String[][] UNIT_SYMBOLS = {
            {"mm", "cm", "m", "km", "in", "ft", "yd", "mi"},
            {"mg", "g", "kg", "t", "oz", "lb"},
            {"mL", "L", "m\u00B3", "tsp", "tbsp", "cup", "gal"},
            {"\u00B0C", "\u00B0F", "K"}
    };

    // How many "base units" one of each unit equals.
    // Base units: metre (length), gram (weight), litre (volume).
    // The temperature row is unused because temperature is not a simple ratio.
    private static final double[][] UNIT_TO_BASE = {
            {0.001, 0.01, 1.0, 1000.0, 0.0254, 0.3048, 0.9144, 1609.344},
            {0.001, 1.0, 1000.0, 1_000_000.0, 28.349523125, 453.59237},
            {0.001, 1.0, 1000.0, 0.00492892159375, 0.01478676478125, 0.2365882365, 3.785411784},
            {}
    };

    // ---------- Views ----------

    private Spinner spinnerCategory;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private EditText inputValue;
    private TextView resultText;

    // ---------- Lifecycle ----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.buttonConvert);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CATEGORY_NAMES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Switching category reloads both unit dropdowns and clears the old result.
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadUnitsForCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing to do
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConvertClick();
            }
        });
    }

    // ---------- UI helpers ----------

    private void loadUnitsForCategory(int category) {
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNIT_NAMES[category]);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(unitAdapter);
        spinnerTo.setAdapter(unitAdapter);

        // Start with two different units so the first conversion is meaningful.
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(Math.min(1, UNIT_NAMES[category].length - 1));

        resultText.setText(R.string.result_placeholder);
    }

    private void handleConvertClick() {
        String raw = inputValue.getText().toString().trim();

        if (raw.isEmpty()) {
            Toast.makeText(this, R.string.error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        // parseDouble accepts words like "NaN" and "Infinity", so reject those too.
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            Toast.makeText(this, R.string.error_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        int category = spinnerCategory.getSelectedItemPosition();
        int from = spinnerFrom.getSelectedItemPosition();
        int to = spinnerTo.getSelectedItemPosition();

        double converted;
        if (category == CAT_TEMPERATURE) {
            double celsius = temperatureToCelsius(value, from);
            if (celsius < -273.15) {
                Toast.makeText(this, R.string.error_below_zero, Toast.LENGTH_SHORT).show();
                return;
            }
            converted = temperatureFromCelsius(celsius, to);
        } else {
            double inBase = value * UNIT_TO_BASE[category][from];
            converted = inBase / UNIT_TO_BASE[category][to];
        }

        String text = prettify(value) + " " + UNIT_SYMBOLS[category][from]
                + " = " + prettify(converted) + " " + UNIT_SYMBOLS[category][to];
        resultText.setText(text);
    }

    // ---------- Temperature maths ----------

    private double temperatureToCelsius(double v, int unit) {
        switch (unit) {
            case 1:  return (v - 32.0) * 5.0 / 9.0;   // Fahrenheit
            case 2:  return v - 273.15;               // Kelvin
            default: return v;                        // Celsius
        }
    }

    private double temperatureFromCelsius(double c, int unit) {
        switch (unit) {
            case 1:  return c * 9.0 / 5.0 + 32.0;
            case 2:  return c + 273.15;
            default: return c;
        }
    }

    // ---------- Formatting ----------

    // Keeps very large / very small numbers readable and trims floating-point noise.
    private String prettify(double v) {
        if (v == 0) {
            return "0";
        }
        double magnitude = Math.abs(v);
        if (magnitude >= 1e9 || magnitude < 1e-4) {
            return String.format(Locale.US, "%.4e", v);
        }
        DecimalFormat df = new DecimalFormat("#,##0.######", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(v);
    }
}