package com.example.macc.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.macc.NavigationActivity;
import com.example.macc.R;
import com.example.macc.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView profile_TextViewName = root.findViewById(R.id.profile_TextViewName);
        TextView profile_TextViewSurname = root.findViewById(R.id.profile_TextViewSurname);
        TextView profile_TextViewEmail = root.findViewById(R.id.profile_TextViewEmail);
        EditText profile_edDepartment = root.findViewById(R.id.profile_edDepartment);
        Button profile_saveButton = root.findViewById(R.id.profile_btnSave);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String[] displayName;

        if(firebaseUser != null){
            String provider = firebaseUser.getProviderData().get(1).getProviderId();

            if ( provider.equals("facebook.com") || provider.equals("google.com")){
                displayName = firebaseUser.getDisplayName().split(" ",2);
                profile_TextViewName.setText(displayName[0]);
                profile_TextViewSurname.setText(displayName[1]);
                profile_TextViewEmail.setText(firebaseUser.getEmail());
            }else{
                profile_TextViewName.setText("instance.getInstance().getName()");
                profile_TextViewSurname.setText("instance.getInstance().getSurname()");
                profile_TextViewEmail.setText("instance.getInstance().getEmail()");
        }
        }

        //onClick on button Save
        profile_saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile_edDepartment.getText().toString().equals("")){
                    profile_edDepartment.setError("please enter a department!");
                }else{
                    String newHint = profile_edDepartment.getText().toString();
                    profile_edDepartment.getText().clear();
                    profile_edDepartment.setHint(newHint);
                    Intent intent = new Intent(getContext(), NavigationActivity.class);
                    startActivity(intent);
                }
            }
        });
        return root;
    }

}

