package com.hocinedev.calculator;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private Timer _timer;
    private ImageButton backSpaceBtn;
    private TextView textViewResult;
    private boolean error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backSpaceBtn = findViewById(R.id.button_backspace);
        textViewResult = findViewById(R.id.text_result);
        display = findViewById(R.id.input);
        display.setShowSoftInputOnFocus(false);

        handleBackspaceBtn();

    }


    public void updateText(String textToAdd) {
        error = false;
        if (textToAdd.length() >= 15) {
            Toast.makeText(this, "Maximum number of digits (15) exceeded.", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldText = display.getText().toString();
        int cursorPosition = display.getSelectionStart();
        String textLeft = oldText.substring(0, cursorPosition);
        String textRight = oldText.substring(cursorPosition);
        String dataToCalculate = String.format("%s%s%s", textLeft, textToAdd, textRight);
        display.setText(dataToCalculate);
        display.setSelection(cursorPosition + 1);

        String finalResult = getResult(dataToCalculate);

        if (!finalResult.equals("Err") && !finalResult.equals("Infinity"))
            textViewResult.setText(finalResult);
        else{
            textViewResult.setText("");
            error = true;
        }

    }

    public void btnZero(View view) {
        updateText(getString(R.string.zero));
    }

    public void btnOne(View view) {
        updateText(getString(R.string.one));
    }

    public void btnTwo(View view) {
        updateText(getString(R.string.two));
    }

    public void btnThree(View view) {
        updateText(getString(R.string.three));
    }

    public void btnFour(View view) {
        updateText(getString(R.string.four));
    }

    public void btnFive(View view) {
        updateText(getString(R.string.five));
    }

    public void btnSix(View view) {
        updateText(getString(R.string.six));
    }

    public void btnSeven(View view) {
        updateText(getString(R.string.seven));
    }

    public void btnEight(View view) {
        updateText(getString(R.string.eight));
    }

    public void btnNine(View view) {
        updateText(getString(R.string.nine));
    }

    public void btnClear(View view) {
        display.setText("");
        textViewResult.setText("");
    }

    public void btnParentheses(View view) {
        int cursorPosition = display.getSelectionStart();
        int openPar = 0;
        int closePar = 0;
        int textLength = display.getText().length();

        for (int i = 0; i < cursorPosition; i++) {
            if (display.getText().toString().substring(i, i + 1).equals("("))
                openPar += 1;
            if (display.getText().toString().substring(i, i + 1).equals(")"))
                closePar += 1;
        }

        if (openPar == closePar || display.getText().toString().substring(textLength - 1, textLength).equals("("))
            updateText("(");
        else if (closePar < openPar && !display.getText().toString().substring(textLength - 1, textLength).equals("("))
            updateText(")");

        display.setSelection(cursorPosition + 1);

    }

    public void btnDivide(View view) {
        updateText(getString(R.string.divide));
    }

    public void btnMultiply(View view) {
        updateText(getString(R.string.multiply));
    }

    public void btnSubtract(View view) {
        updateText(getString(R.string.subtract));
    }

    public void btnAdd(View view) {
        updateText(getString(R.string.add));
    }

    public void btnEquals(View view) {

        String result = textViewResult.getText().toString();

        if (error)
            Toast.makeText(this, "You have a mistake in your expression", Toast.LENGTH_SHORT).show();
        else {
            display.setText(result);
            display.setSelection(result.length());
            textViewResult.setText("");
        }

    }

    public void btnPoint(View view) {
        updateText(getString(R.string.point));
    }

    public void btnPlusMines(View view) {
        updateText(getString(R.string.subtract));
    }


    public String getResult(String dataToCalculate) {

        String data = dataToCalculate.replaceAll(getString(R.string.divide), "/");
        data = data.replaceAll(getString(R.string.multiply), "*");

        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "");
            }
            return finalResult;
        } catch (Exception e) {
            return "Err";
        }

    }


    private void handleBackspaceBtn() {
        _timer = new Timer();
        TimerTask _task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(() -> {
                    int cursorPos = display.getSelectionStart();
                    int length = display.length();
                    if (cursorPos != 0 && backSpaceBtn.isPressed() && length > 0) {
                        SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
                        selection.replace(cursorPos - 1, cursorPos, "");
                        display.setText(selection);
                        display.setSelection(cursorPos - 1);

                        if (display.getSelectionStart() != 0) {
                            String finalResult = getResult(selection.toString());
                            if (!finalResult.equals("Err") && !finalResult.equals("Infinity"))
                                textViewResult.setText(finalResult);
                            else {
                                textViewResult.setText("");
                                error = true;
                            }
                        } else
                            textViewResult.setText("");
                    }
                });
            }
        };
        _timer.schedule(_task, 0, 120);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _timer.cancel();
    }
}