package com.example.macc.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.macc.HerokuService;
import com.example.macc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.List;
import java.util.Objects;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment implements OnMapReadyCallback {
    private View root;
    private Query query;
    private GoogleMap mMap;
    private FirebaseUser firebaseUser;
    private AutoCompleteTextView university_name;
    private AutoCompleteTextView profile_edDepartment;
    private final int TAG_CODE_PERMISSION_LOCATION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        MapView mapView = root.findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        if (firebaseUser != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://virgilio-proj.herokuapp.com/")
                    .build();

            HerokuService service = retrofit.create(HerokuService.class);

            university_name = root.findViewById(R.id.profile_UniName);

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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                                android.R.layout.simple_list_item_1, universities);

                        university_name.setAdapter(adapter);

                    } catch (IOException | JSONException e) {
                        university_name.setText(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> _, Throwable t) {
                    Log.d("TAG", "Callback: onFailure");
                }
            });

            profile_edDepartment = root.findViewById(R.id.profile_edDepartment);

            String[] departments = new String[]{
                    "Agriculture", "Architecture", "Architecture", "Design", "Cultural Heritage",
                    "Veterinary Medicine", "Statistical Sciences", "Economy", "Biotechnology",
                    "Pharmacy", "Philosophy", "Motor Sciences", "Law", "Aerospace Engineering",
                    "Biomedical Engineering", "Civil Engineering", "Chemical Engineering",
                    "Telecommunication Engineering", "Computer Engineering", "Mechanical Engineering",
                    "Management Engineering", "Physical Engineering", "DAMS", "Letters",
                    "Communication Sciences", "History", "Foreign Languages and Literatures",
                    "Nursing", "Medicine and Surgery", "Psychology", "Education Sciences",
                    "Mathematical, Physical and Natural Sciences", "International and Diplomatic Sciences"
                    , "Political, Social and International Sciences", "Sociology"
            };

            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                    android.R.layout.simple_list_item_1, departments);

            profile_edDepartment.setAdapter(adapter);

            TextView profile_TextViewEmail = root.findViewById(R.id.profile_TextViewEmail);
            Button profile_saveButton = root.findViewById(R.id.profile_btnSave);

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            query = rootRef.child("users").orderByChild("id").equalTo(firebaseUser.getUid());

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

                                ReloadFragment();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Thread t1 = new Thread(new RunnableImpl(), "t1");
        t1.start();
    }

    private void RequestPermissions() {
        // Permission is not granted and request the permission
        requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                TAG_CODE_PERMISSION_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == TAG_CODE_PERMISSION_LOCATION) {  // check if is the location permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // check if the user denied or allowed location permission
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }

    private void ReloadFragment() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, new ProfileFragment())
                .commit();
    }

    private class RunnableImpl extends Activity implements Runnable {
        @Override
        public void run() {
            TextView profile_labViewName = root.findViewById(R.id.profile_labName);
            TextView profile_TextViewName = root.findViewById(R.id.profile_TextViewName);

            TextView profile_labViewSurname = root.findViewById(R.id.profile_labSurname);
            TextView profile_TextViewSurname = root.findViewById(R.id.profile_TextViewSurname);

            String provider = firebaseUser.getProviderData().get(1).getProviderId();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = "", surname = "";

                        if (provider.equals("facebook.com") || provider.equals("google.com")) {
                            if (firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().equals("")) {
                                String[] displayName = firebaseUser.getDisplayName().split(" ", 2);
                                name = displayName[0];
                                surname = displayName[1];
                            } else {
                                profile_labViewName.setVisibility(View.INVISIBLE);
                                profile_labViewSurname.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            name = snapshot.child("name").getValue(String.class);
                            surname = snapshot.child("surname").getValue(String.class);
                        }

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

                        LoadMap();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("TAG", "Database: onCancelled");
                }
            });
        }

        private void LoadMap() {
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(getActivity());

            try {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    RequestPermissions();
                } else {    // permission already granted
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }

                addresses = geocoder.getFromLocationName(university_name.getText().toString(), 1);

                if (addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    mMap.addMarker(new MarkerOptions().position(latLng).title("University"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }

            } catch (IOException e) {
                Log.d("TAG", "Google Maps: onMapReady");
            }
        }
    }
}
