package com.example.macc;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

public class PopUpSignIn extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int width, height;

        setContentView(R.layout.sign_in);
        Button sign_in = findViewById(R.id.popup_btnSignIn);
        EditText popup_email = findViewById(R.id.popup_edEmail);
        EditText popup_password = findViewById(R.id.popup_edPassword);

        MainActivity mainActivity = new MainActivity();
        DisplayMetrics displayMetrics = new DisplayMetrics();

        sign_in.setOnClickListener(v -> mainActivity.SignInBasic(
                popup_email.getText().toString(), popup_password.getText().toString()));

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.53));
    }
}
