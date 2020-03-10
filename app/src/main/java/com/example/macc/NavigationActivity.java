package com.example.macc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.macc.ui.home.HomeFragment;
import com.example.macc.ui.information.InformationFragment;
import com.example.macc.ui.profile.ProfileFragment;
import com.example.macc.ui.reviews.ReviewsFragment;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_reviews,
                R.id.nav_info, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadLoggedUserData(user);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void loadLoggedUserData(FirebaseUser user) {
        String provider = user.getProviderData().get(1).getProviderId();

        View nav_header_main = navigationView.getHeaderView(0);

        TextView nameTextView = nav_header_main.findViewById(R.id.nameTextView);
        TextView emailTextView =  nav_header_main.findViewById(R.id.emailTextView);
        ImageView imageView =  nav_header_main.findViewById(R.id.imageView);

        if (provider.equals("facebook.com") || provider.equals("google.com")) {
            nameTextView.setText(user.getDisplayName());
        } else {
            // access to DB
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query = rootRef.child("users").orderByChild("id").equalTo(user.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String surname = snapshot.child("surname").getValue(String.class);
                        String name_surname = name + " " + surname;

                        nameTextView.setText(name_surname);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("TAG", "Database: onCancelled");
                }
            });
        }

        emailTextView.append(user.getEmail());
        Uri photo = user.getPhotoUrl();

        if (photo == null) {
            imageView.setImageResource(R.mipmap.ic_avatar);
        } else {
            Picasso.get().load(photo).into(imageView);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FrameLayout fl = findViewById(R.id.nav_host_fragment);
        switch (item.getItemId()) {
            case R.id.nav_home:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new HomeFragment()).commit();
                break;

            case R.id.nav_profile:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new ProfileFragment()).commit();
                break;

            case R.id.nav_reviews:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new ReviewsFragment()).commit();
                break;

            case R.id.nav_info:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new InformationFragment()).commit();
                break;

            case R.id.nav_logout:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(intent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
