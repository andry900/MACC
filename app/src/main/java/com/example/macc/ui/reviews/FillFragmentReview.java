package com.example.macc.ui.reviews;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.example.macc.R;
import com.example.macc.Reviews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class FillFragmentReview extends Fragment {
    private String university;
    private String department;

    public FillFragmentReview() {} //default constructor

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int progress_mark = 18;

        View root = inflater.inflate(R.layout.fragment_fill_review, container, false);
        EditText insertReview_edExam = root.findViewById(R.id.insertReview_edExam);
        EditText insertReview_edProfessor = root.findViewById(R.id.insertReview_edProfessor);

        EditText insertReview_edComment = root.findViewById(R.id.insertReview_edComment);
        Button insertReview_saveButton = root.findViewById(R.id.insertReview_saveButton);

        TextView insertReview_txtMark = root.findViewById(R.id.insertReview_txtMark);
        SeekBar seekBar_mark = root.findViewById(R.id.seekBar_mark_fillReview);
        RatingBar ratingBar_niceness = root.findViewById(R.id.ratingBar_fillReview);

        ratingBar_niceness.setProgress(1);
        ratingBar_niceness.setRating(1.0f);
        ratingBar_niceness.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (ratingBar_niceness.getRating() < 1)
                ratingBar_niceness.setRating(1.0f);
        });

        seekBar_mark.setMax(31);
        seekBar_mark.setMin(progress_mark);
        seekBar_mark.setProgress(progress_mark);
        insertReview_txtMark.setText(String.valueOf(progress_mark));

        seekBar_mark.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                insertReview_txtMark.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //not important
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //not important
            }
        });

        insertReview_saveButton.setOnClickListener(v -> {
            //CHECK THE CORRECT USER INPUTS
            if (insertReview_edExam.getText().toString().equals("") || insertReview_edExam.getText().toString().equals(" "))
                insertReview_edExam.setError("Please enter an exam!");
            if (insertReview_edProfessor.getText().toString().equals("") || insertReview_edProfessor.getText().toString().equals(" "))
                insertReview_edProfessor.setError("Please enter a professor!");
            if (insertReview_edComment.getText().toString().equals("") || insertReview_edComment.getText().toString().equals(" "))
                insertReview_edComment.setError("Please enter a comment!");

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {
                //CHECKING THAT NO ERROR HAS BEEN SHOWED BEFORE INSERT VALUES INTO DATABASE
                if (insertReview_edExam.getError() == null && insertReview_edProfessor.getError() == null
                        && insertReview_edComment.getError() == null ) {

                    //GET REFERENCE ON DB
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("reviews");
                    Query query = rootRef.orderByChild("idUser").equalTo(firebaseUser.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //IF THERE IS ALREADY AN ENTRY FOR THE REVIEWS TABLE
                            if (dataSnapshot.getChildrenCount() != 0) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    //TAKE THE VALUE OF THE EXAM IN DB
                                    String currentExam = (String) snapshot.child("exam").getValue();

                                    //SET THE ERRROR IF YOU WANT TO INSERT 2 REVIEWS ON THE SAME EXAM
                                    if (insertReview_edExam.getText().toString().equalsIgnoreCase(currentExam)) {
                                        insertReview_edExam.setError("You cannot insert 2 reviews on the same exam!");
                                        break;
                                    }
                                }

                                if (insertReview_edExam.getError() == null) {
                                    Query query2 = rootRef.orderByKey().limitToLast(1);
                                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int last_idReview = 0;

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                last_idReview = Integer.parseInt((String) Objects.requireNonNull(snapshot.child("idReview").getValue())) + 1;
                                            }
                                            Reviews review = new Reviews(firebaseUser.getUid(), String.valueOf(last_idReview),
                                                    insertReview_edExam.getText().toString(), insertReview_edProfessor.getText().toString(),
                                                    insertReview_txtMark.getText().toString(), String.valueOf(ratingBar_niceness.getRating()),
                                                    insertReview_edComment.getText().toString(), getUniversity(),getDepartment());

                                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("reviews");
                                            database.child(review.getIdReview()).setValue(review);

                                            Toast toast = Toast.makeText(getContext(),"Your review has been saved!", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                            Objects.requireNonNull(getActivity())
                                                    .getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.nav_host_fragment, new ReviewsFragment(),"fragment_reviews")
                                                    .commit();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("TAG", "Database: onCancelled");
                                        }
                                    });
                                }
                            } else {
                                Query query3 = rootRef.orderByKey().limitToLast(1);
                                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int last_idReview = 0;
                                        if (dataSnapshot.getChildrenCount() != 0) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("idReview").exists())
                                                    last_idReview = Integer.parseInt((String) Objects.requireNonNull(snapshot.child("idReview").getValue())) + 1;
                                            }
                                        } else {
                                            last_idReview = 1;
                                        }
                                        Reviews review = new Reviews(firebaseUser.getUid(),String.valueOf(last_idReview),
                                                insertReview_edExam.getText().toString(), insertReview_edProfessor.getText().toString(),
                                                insertReview_txtMark.getText().toString(), String.valueOf(ratingBar_niceness.getRating()),
                                                insertReview_edComment.getText().toString(), getUniversity(), getDepartment());

                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("reviews");
                                        database.child(review.getIdReview()).setValue(review);

                                        Toast toast = Toast.makeText(getContext(),"Your first review has been saved!", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();

                                        Objects.requireNonNull(getActivity())
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.nav_host_fragment, new ReviewsFragment(),"fragment_reviews")
                                                .commit();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("TAG", "Database: onCancelled");
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("TAG", "Database: onCancelled");
                        }
                    });
                }
            }
        });

        return root;
    }

    private String getUniversity() {
        return university;
    }

    void setUniversity(String university) {
        this.university = university;
    }

    private String getDepartment() {
        return department;
    }

    void setDepartment(String department) {
        this.department = department;
    }
}
