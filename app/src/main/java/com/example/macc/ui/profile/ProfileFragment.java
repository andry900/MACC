package com.example.macc.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.macc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView profile_TextViewName = root.findViewById(R.id.profile_TextViewName);
        TextView profile_TextViewSurname = root.findViewById(R.id.profile_TextViewSurname);

        TextView profile_labViewName = root.findViewById(R.id.profile_labName);
        TextView profile_labViewSurname = root.findViewById(R.id.profile_labSurname);

        TextView profile_TextViewEmail = root.findViewById(R.id.profile_TextViewEmail);
        EditText profile_edDepartment = root.findViewById(R.id.profile_edDepartment);
        Button profile_saveButton = root.findViewById(R.id.profile_btnSave);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String[] displayName;
            String provider = firebaseUser.getProviderData().get(1).getProviderId();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query = rootRef.child("users").orderByChild("id").equalTo(firebaseUser.getUid());

            if (provider.equals("facebook.com") || provider.equals("google.com")) {
                if (firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().equals("")) {
                    displayName = firebaseUser.getDisplayName().split(" ", 2);
                    profile_TextViewName.setText(displayName[0]);
                    profile_TextViewSurname.setText(displayName[1]);
                } else {
                    profile_labViewName.setVisibility(View.INVISIBLE);
                    profile_labViewSurname.setVisibility(View.INVISIBLE);
                }

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String university = snapshot.child("university").getValue(String.class);
                            String department = snapshot.child("department").getValue(String.class);

                            profile_edDepartment.setText(department);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.child("name").getValue(String.class);
                            String surname = snapshot.child("surname").getValue(String.class);
                            String university = snapshot.child("university").getValue(String.class);
                            String department = snapshot.child("department").getValue(String.class);

                            profile_edDepartment.setText(department);
                            profile_TextViewName.setText(name);
                            profile_TextViewSurname.setText(surname);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            profile_TextViewEmail.setText(firebaseUser.getEmail());

            //onClick on button Save
            profile_saveButton.setOnClickListener(v -> {
                if (profile_edDepartment.getText().toString().equals("")) {
                    profile_edDepartment.setError("Please enter a department!");
                } else {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String university = snapshot.child("university").getValue(String.class);
                                String department = snapshot.child("department").getValue(String.class);

                                // DA FARE UPDATE UNIVERSITA' E DIPARTIMENTO
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
        return root;
    }
}
