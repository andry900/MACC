package com.example.macc;

import android.content.Intent;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements Serializable {
    static final int GOOGLE_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button signIn = findViewById(R.id.sign_in);

        Button google_login = findViewById(R.id.google_login);
        LoginButton facebook_login = findViewById(R.id.facebook_login);
        progressBar = findViewById(R.id.progress_circular);

        mAuth = FirebaseAuth.getInstance();

        //EMAIL & PASSWORD
        login.setOnClickListener(v -> LoginBasic(email.getText().toString(), password.getText().toString()));
        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PopUpSignIn.class);
            startActivity(intent);
        });

        //GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_login.setOnClickListener(v -> SignInGoogle());

        //FACEBOOK
        callbackManager = CallbackManager.Factory.create();
        facebook_login.setPermissions("email", "public_profile");
        facebook_login.setOnClickListener(v -> {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    progressBar.setVisibility(View.VISIBLE);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d("TAG", "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("TAG", "facebook:onError", error);
                }
            });
        });

        //CHECK USER STATE
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI("", "", user);
        }
    }

    public void LoginBasic(String pEmail, String pPassword) {
        if (TextUtils.isEmpty(pEmail) || TextUtils.isEmpty(pPassword)) {
            if (TextUtils.isEmpty(pEmail)) {
                email.setError("Email can not be empty!");
            }

            if (TextUtils.isEmpty(pPassword)) {
                password.setError("Password can not be empty!");
            }
        } else {
            mAuth.signInWithEmailAndPassword(pEmail, pPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI("", "", user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI("", "", null);
                        }
                    });
        }
    }

    public void SignInBasic(String name, String surname, String email, String password) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(name, surname, user);

                        //CreateToast("You have successfully registered!");
                    } else {
                        // CreateToast("Error in registration!");
                    }
                });
    }

    public void SignInGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI("", "", user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);

                        updateUI("", "", null);
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI("", "", user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);

                        updateUI("", "", null);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI(String name, String surname, FirebaseUser user) {
        if (user != null) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query = rootRef.child("users").orderByChild("email").equalTo(user.getEmail());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 1) {     // User already present in the database
                        Intent navigationActivity = new Intent(getApplicationContext(), NavigationActivity.class);
                        startActivity(navigationActivity);
                    } else {    // new User
                        Users newUser;
                        DatabaseAccess dbAccess = new DatabaseAccess();

                        if (name.equals("") && surname.equals("")) {    // Google & Facebook sign in
                            Intent navigationActivity = new Intent(getApplicationContext(), NavigationActivity.class);
                            startActivity(navigationActivity);

                            newUser = new Users(user.getUid(), user.getDisplayName(), "",
                                    user.getEmail(), "", "", "");
                        } else {    // email & password sign in
                            newUser = new Users(user.getUid(), name, surname, user.getEmail(),
                                    "", "", "");
                        }

                        dbAccess.InsertUser(newUser);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("TAG", "Database error");
                }
            });
        } else {
            CreateToast("Authentication failed.");
        }
    }

    public void CreateToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
