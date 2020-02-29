package com.example.macc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
        FirebaseUser user;

        Intent homeActivity = getIntent();
        Bundle bundle = homeActivity.getBundleExtra("bundle_data");
        Object object = bundle.getParcelable("user");
        user = (FirebaseUser) object;

        TextView loggedText1 = findViewById(R.id.textView1);
        TextView loggedText2 = findViewById(R.id.textView2);
        ImageView imageLogged = findViewById(R.id.imageView);

        String name = user.getDisplayName();
        String email = user.getEmail();;
        String path_photo = String.valueOf(user.getPhotoUrl());
        System.out.println("user photo url: " + user.getPhotoUrl());

        loggedText1.append("Name: " + name );
        loggedText2.append("Email: " + email);
        if (path_photo.equals("null")) {
            Picasso.get().load(R.drawable.ic_firebase_logo).resize(80,80).into(imageLogged);
        }else{
            Picasso.get().load(path_photo).resize(80,80).into(imageLogged);
        }
    }
}
