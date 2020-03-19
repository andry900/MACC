package com.example.macc.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.macc.HerokuService;
import com.example.macc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://virgilio-proj.herokuapp.com/")
                    .build();

            HerokuService service = retrofit.create(HerokuService.class);
            AutoCompleteTextView university_name = root.findViewById(R.id.profile_UniName);

            Call<ResponseBody> call = service.getRequest();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> _, Response<ResponseBody> response) {
                    try {
                        ArrayList<String> universities = new ArrayList<>();
                        String universities_JSON = response.body().string();
                        JSONArray jsonArray = new JSONArray(universities_JSON);

                        for (int index = 0; index < jsonArray.length(); index++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(index);
                            universities.add(jsonObject.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<> (Objects.requireNonNull(getActivity()),
                                android.R.layout.simple_list_item_1, universities);

                        university_name.setAdapter(adapter);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        university_name.setText(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> _, Throwable t) {
                    t.printStackTrace();
                }
            });

            TextView profile_labViewName = root.findViewById(R.id.profile_labName);
            TextView profile_TextViewName = root.findViewById(R.id.profile_TextViewName);

            TextView profile_labViewSurname = root.findViewById(R.id.profile_labSurname);
            TextView profile_TextViewSurname = root.findViewById(R.id.profile_TextViewSurname);

            TextView profile_TextViewEmail = root.findViewById(R.id.profile_TextViewEmail);
            EditText profile_edDepartment = root.findViewById(R.id.profile_edDepartment);

            Button profile_saveButton = root.findViewById(R.id.profile_btnSave);

            String provider = firebaseUser.getProviderData().get(1).getProviderId();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query = rootRef.child("users").orderByChild("id").equalTo(firebaseUser.getUid());

            if (provider.equals("facebook.com") || provider.equals("google.com")) {
                if (firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().equals("")) {
                    String[] displayName = firebaseUser.getDisplayName().split(" ", 2);
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

                            if (university == null || university.equals("")) {
                                university_name.setHint("Insert your university!");
                            } else {
                                university_name.setText(university);
                            }

                            if (department == null || department.equals("")) {
                                profile_edDepartment.setHint("Insert your department!");
                            } else {
                                profile_edDepartment.setText(department);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG", "Database: onCancelled");
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

                            profile_TextViewName.setText(name);
                            profile_TextViewSurname.setText(surname);

                            if (university == null || university.equals("")) {
                                university_name.setHint("Insert your university!");
                            } else {
                                university_name.setText(university);
                            }

                            if (department == null || department.equals("")) {
                                profile_edDepartment.setHint("Insert your department!");
                            } else {
                                profile_edDepartment.setText(department);
                            }
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
                if (university_name.getText().toString().equals("") || profile_edDepartment.getText().toString().equals("")) {
                    if (university_name.getText().toString().equals("")) {
                        university_name.setError("Please insert your University!");
                    }

                    if (profile_edDepartment.getText().toString().equals("")) {
                        profile_edDepartment.setError("Please insert your department!");
                    }
                } else {
                    String new_university = university_name.getText().toString();
                    String new_department = profile_edDepartment.getText().toString();

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("university").setValue(new_university);
                                snapshot.getRef().child("department").setValue(new_department);

                                Toast.makeText(getContext(), "The changes have been applied!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("TAG", "Database: onCancelled");
                        }
                    });
                }
            });
        }
        return root;
    }
}
