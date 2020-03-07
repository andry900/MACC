package com.example.macc;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class DatabaseAccess {
    void InsertUser(Users newUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
         database.child(newUser.getId()).setValue(newUser);
    }
}
