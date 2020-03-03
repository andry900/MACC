package com.example.macc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.macc.ui.tools.ToolsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_reviews,
                R.id.nav_tools, R.id.nav_info, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

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
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void loadLoggedUserData(FirebaseUser user) {
        String name = user.getDisplayName();
        String email = user.getEmail();;
        Uri photo = user.getPhotoUrl();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        View nav_header_main = navigationView.getHeaderView(0);

        TextView nameTextView = nav_header_main.findViewById(R.id.nameTextView);
        TextView emailTextView =  nav_header_main.findViewById(R.id.emailTextView);
        ImageView imageView =  nav_header_main.findViewById(R.id.imageView);

        nameTextView.append(name);
        emailTextView.append(email);
        if (photo == null) {
            imageView.setImageResource(R.drawable.ic_firebase_logo);
        } else {
            Picasso.get().load(photo).into(imageView);
        }
    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
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

            case R.id.nav_tools:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new ToolsFragment()).commit();
                break;

            case R.id.nav_info:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new InformationFragment()).commit();
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
