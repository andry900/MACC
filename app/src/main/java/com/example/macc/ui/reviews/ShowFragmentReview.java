package com.example.macc.ui.reviews;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
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

public class ShowFragmentReview extends Fragment {

    int progress;
    @SuppressLint("NewApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_show_review, container, false);
        EditText showReview_edExam = root.findViewById(R.id.showReview_edExam);
        EditText showReview_edProfessor = root.findViewById(R.id.showReview_edProfessor);
        EditText showReview_edComment = root.findViewById(R.id.showReview_edComment);
        TextView showReview_txtMark = root.findViewById(R.id.showReview_txtMark);

        Button showReview_updateButton = root.findViewById(R.id.showReview_updateButton);
        Button showReview_deleteButton = root.findViewById(R.id.showReview_deleteButton);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        RatingBar ratingBar_niceness = root.findViewById(R.id.ratingBar_fillReview);
        ratingBar_niceness.setProgress(1);
        ratingBar_niceness.setRating(1.0f);
        ratingBar_niceness.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (ratingBar_niceness.getRating() < 1)
                    ratingBar_niceness.setRating(1.0f);
            }
        });

        SeekBar seekBar_mark = root.findViewById(R.id.seekBar_mark_showReview);
        seekBar_mark.setMax(31);
        seekBar_mark.setMin(18);

        seekBar_mark.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showReview_txtMark.setText(String.valueOf(progress));
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

        Bundle bundle = getArguments();
        String exam_item_selected = bundle.getString("exam_item_selected");

        //SHOW YOUR REVIEW FROM THE DATABASE
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("reviews");
        Query query = rootRef.orderByChild("idUser").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("exam").getValue().toString().equals(exam_item_selected)){
                            String exam = snapshot.child("exam").getValue().toString();
                            showReview_edExam.setText(exam);

                            String professor = snapshot.child("professor").getValue().toString();
                            showReview_edProfessor.setText(professor);

                            String mark = snapshot.child("mark").getValue().toString();
                            progress = Integer.parseInt(mark);
                            seekBar_mark.setProgress(progress);
                            showReview_txtMark.setText(mark);

                            String niceness = snapshot.child("niceness").getValue().toString();
                            ratingBar_niceness.setRating(Float.parseFloat(niceness));

                            String comment = snapshot.child("comment").getValue().toString();
                            showReview_edComment.setText(comment);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Database: onCancelled");
            }
        });

        showReview_updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CHECK THE CORRECT USER INPUTS
                if (showReview_edExam.getText().toString().equals(""))
                    showReview_edExam.setError("Please enter an exam!");
                if (showReview_edProfessor.getText().toString().equals(""))
                    showReview_edProfessor.setError("Please enter a Professor!");
                if(showReview_edComment.getText().toString().equals(""))
                    showReview_edComment.setError("Please enter a comment!");

                //CHECKING THAT NO ERROR HAS BEEN SHOWED BEFORE UPDATE VALUES INTO DATABASE
                if ( showReview_edExam.getError() == null  &&  showReview_edProfessor.getError() == null
                        && showReview_edComment.getError() == null ){

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() != 0){
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    if (showReview_edExam.getText().toString().equalsIgnoreCase(snapshot.child("exam").getValue().toString())){
                                        if(!showReview_edExam.getText().toString().equalsIgnoreCase(exam_item_selected)){
                                            showReview_edExam.setError("The review for this exam already exists!");
                                        }
                                    }
                                }
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    if(showReview_edExam.getError() == null){
                                        if(snapshot.child("exam").getValue().toString().equals(exam_item_selected)){
                                            String exam = showReview_edExam.getText().toString();
                                            snapshot.getRef().child("exam").setValue(exam);

                                            String professor = showReview_edProfessor.getText().toString();
                                            snapshot.getRef().child("professor").setValue(professor);

                                            String mark = showReview_txtMark.getText().toString();
                                            snapshot.getRef().child("mark").setValue(mark);

                                            String niceness = String.valueOf(ratingBar_niceness.getRating());
                                            snapshot.getRef().child("niceness").setValue(niceness);

                                            String comment = showReview_edComment.getText().toString();
                                            snapshot.getRef().child("comment").setValue(comment);
                                            Toast toast = Toast.makeText(getContext(),"Your review has been updated!", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                            getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                                    new ReviewsFragment(),"fragment_reviews").commit();
                                        }
                                    }
                                }
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

        showReview_deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.child("exam").getValue().toString().equals(exam_item_selected)){
                                    snapshot.getRef().removeValue();

                                    Toast toast = Toast.makeText(getContext(),"Your review has been deleted!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                    getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                            new ReviewsFragment(),"fragment_reviews").commit();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG", "Database: onCancelled");
                    }
                });
            }
        });


        return root;
    }

    //Default constructor
    public ShowFragmentReview(){}

    //To get the right instance of ShowFragmentReview for bundle data
    public static ShowFragmentReview newInstance(String key,String value) {
        ShowFragmentReview showFragmentReview = new ShowFragmentReview();
        Bundle arguments = new Bundle();
        arguments.putString(key, value);
        showFragmentReview.setArguments(arguments);

        return showFragmentReview;
    }

}

