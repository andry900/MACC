package com.example.macc;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class DatabaseAccess {
    // Write a message to the database

    void InsertUser(Users newUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
        String id = database.push().getKey();

        newUser.setId(id);

        if (id != null) {
            database.child(id).setValue(newUser);
        } else {
            throw new RuntimeException();
        }
    }
}
