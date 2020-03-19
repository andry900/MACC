package com.example.macc;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseAccess {
    void InsertUser(Users newUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
         database.child(newUser.getId()).setValue(newUser);
    }

    public void InsertReview(Reviews review){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("reviews");
        database.child(review.getIdReview()).setValue(review);
    }
}
