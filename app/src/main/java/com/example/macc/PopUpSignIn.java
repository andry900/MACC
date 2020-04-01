package com.example.macc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PopUpSignIn extends Activity {
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int width, height;

        context = this;
        setContentView(R.layout.sign_in);
        Button sign_in = findViewById(R.id.popup_btnSignIn);
        EditText popup_name = findViewById(R.id.popup_edName);
        EditText popup_surname = findViewById(R.id.popup_edSurname);
        EditText popup_email = findViewById(R.id.popup_edEmail);
        EditText popup_password = findViewById(R.id.popup_edPassword);

        MainActivity mainActivity = new MainActivity();
        DisplayMetrics displayMetrics = new DisplayMetrics();

        sign_in.setOnClickListener(v -> {
            String txtName = popup_name.getText().toString();
            String txtSurname = popup_surname.getText().toString();
            String txtEmail = popup_email.getText().toString();
            String txtPassword = popup_password.getText().toString();

            if (TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtSurname) ||
                    (TextUtils.isEmpty(txtEmail) || !Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches())  ||
                    (TextUtils.isEmpty(txtPassword) || txtPassword.length() < 6)) {

                if (TextUtils.isEmpty(txtName)) {
                    popup_name.setError("Please enter your Name!");
                }

                if (TextUtils.isEmpty(txtSurname)) {
                    popup_surname.setError("Please enter your Surname!");
                }

                if (TextUtils.isEmpty(txtEmail) || !Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                    popup_email.setError("Please enter a valid Email!");
                }

                if (TextUtils.isEmpty(txtPassword) || txtPassword.length() < 6) {
                    popup_password.setError("Please enter a password of at least 6 characters!");
                }
            } else {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                Query query = rootRef.child("users").orderByChild("email").equalTo(txtEmail);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 1) {     // Email already present in the database
                            popup_email.setError("Email already present!");
                        } else {    // new email
                            finish();

                            mainActivity.SignInBasic(
                                    popup_name.getText().toString(), popup_surname.getText().toString(),
                                    popup_email.getText().toString(), popup_password.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAG", "Database error");
                    }
                });
            }
        });

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.53));
    }

    public static Context getSignInContext() {
        return context;
    }
}
