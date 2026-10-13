package com.example.calculatorapp;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textField;
    Button[] numberButtons = new Button[10];
    Button addButton, subButton, mulButton, divButton;
    Button decButton, equButton, delButton, clrButton, negButton;

    double num1 = 0, num2 = 0, result = 0;
    char operator = ' ';          // ' ' means no operator is pending
    boolean startNew = false;     // true right after "=" so the next digit starts fresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textField = findViewById(R.id.textField);

        addButton = findViewById(R.id.btnAdd);
        subButton = findViewById(R.id.btnSub);
        mulButton = findViewById(R.id.btnMul);
        divButton = findViewById(R.id.btnDiv);
        decButton = findViewById(R.id.btnDec);
        equButton = findViewById(R.id.btnEqu);
        delButton = findViewById(R.id.btnDel);
        clrButton = findViewById(R.id.btnClr);
        negButton = findViewById(R.id.btnNeg);

        Button[] functionButtons = {addButton, subButton, mulButton, divButton,
                decButton, equButton, delButton, clrButton, negButton};
        for (Button b : functionButtons) {
            b.setOnClickListener(this);
        }

        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = findViewById(numberIds[i]);
            numberButtons[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            // Clear always works, even after an error
            if (v == clrButton) {
                clearAll();
                return;
            }

            boolean isDigit = false;
            for (int i = 0; i < 10; i++) {
                if (v == numberButtons[i]) isDigit = true;
            }

            String text = textField.getText().toString();

            // After "Error", only a digit or "." may continue; everything else is ignored
            if (text.equals("Error")) {
                clearAll();
                text = "";
                if (!isDigit && v != decButton) return;
            }

            // After "=", typing a digit or "." starts a new number
            if (startNew && (isDigit || v == decButton)) {
                text = "";
                startNew = false;
            }

            for (int i = 0; i < 10; i++) {
                if (v == numberButtons[i]) {
                    if (text.equals("0")) text = "";   // avoid 05, 007...
                    textField.setText(text + i);
                }
            }

            if (v == decButton) {
                if (!text.contains(".")) {             // only one decimal point
                    textField.setText(text.isEmpty() ? "0." : text + ".");
                }
            }

            if (v == addButton) handleOperator('+', text);
            if (v == subButton) handleOperator('-', text);
            if (v == mulButton) handleOperator('*', text);
            if (v == divButton) handleOperator('/', text);

            if (v == equButton) {
                if (operator == ' ' || text.isEmpty()) return;   // nothing to calculate
                num2 = Double.parseDouble(text);
                result = calculate(num1, num2, operator);
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    showError();
                } else {
                    textField.setText(format(result));
                    num1 = result;
                    operator = ' ';
                    startNew = true;
                }
            }

            if (v == delButton) {
                if (!text.isEmpty()) {
                    text = text.substring(0, text.length() - 1);
                    if (text.equals("-")) text = "";
                    textField.setText(text);
                }
            }

            if (v == negButton) {
                if (!text.isEmpty() && !text.equals("0")) {
                    textField.setText(text.startsWith("-") ? text.substring(1) : "-" + text);
                }
            }

        } catch (Exception e) {
            showError();   // safety net so rapid or odd taps never crash the app
        }
    }

    // Handles +, -, *, / (also supports chaining like 2 + 3 + 4)
    private void handleOperator(char op, String text) {
        startNew = false;
        if (text.isEmpty()) {
            if (operator != ' ') operator = op;   // user changed their mind about the operator
            return;
        }
        if (operator != ' ') {
            // an operator is already waiting, so finish that calculation first
            num2 = Double.parseDouble(text);
            num1 = calculate(num1, num2, operator);
            if (Double.isNaN(num1) || Double.isInfinite(num1)) {
                showError();
                return;
            }
        } else {
            num1 = Double.parseDouble(text);
        }
        operator = op;
        textField.setText("");
    }

    private double calculate(double a, double b, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) return Double.NaN;   // division by zero
                return a / b;
        }
        return b;
    }

    // 5.0 -> "5", 0.30000000000000004 -> "0.3"
    private String format(double value) {
        return BigDecimal.valueOf(value)
                .setScale(10, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    private void showError() {
        clearAll();
        textField.setText("Error");
    }

    private void clearAll() {
        textField.setText("");
        num1 = 0;
        num2 = 0;
        result = 0;
        operator = ' ';
        startNew = false;
    }
}