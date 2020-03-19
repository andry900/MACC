package com.example.macc;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DatabaseAccess {
    public void InsertUser(Users newUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
         database.child(newUser.getId()).setValue(newUser);
    }
    public void InsertReview(Reviews review){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("reviews");
        database.child(review.getIdReview()).setValue(review);
    }
}
