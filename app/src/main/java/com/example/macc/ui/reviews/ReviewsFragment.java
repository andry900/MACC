package com.example.macc.ui.reviews;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ReviewsFragment extends Fragment {
    private String current_university;
    private String current_department;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reviews, container, false);

        Button insertReview_button = root.findViewById(R.id.insertReview_button);
        ListView myReview_listView = root.findViewById(R.id.myReviews_listView);

        //SET THE HEIGHT OF LIST_VIEW TO 0.75 OF THE SCREEN
        ViewGroup.LayoutParams params = myReview_listView.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        params.height  = (int) (height * 0.75);//(int) (params.height * 0.8);
        myReview_listView.setLayoutParams(params);

        //UPDATE THE LIST_VIEW BASED ON THE PRESENCE OF REVIEWS IN DB
        update_userListViewReview(root);

        //CLICK LISTENER ON ITEM OF LIST_VIEW
        myReview_listView.setOnItemClickListener((parent, view, position, id) -> {
            //PASS EXAM SELECTED
            ShowFragmentReview showFragmentReview = ShowFragmentReview
                    .newInstance(myReview_listView
                            .getItemAtPosition(position)
                            .toString());

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, showFragmentReview,"fragment_showReview")
                    .commit();
        });

        insertReview_button.setOnClickListener(v -> {
            FillFragmentReview fillFragmentReview = new FillFragmentReview();
            fillFragmentReview.setUniversity(current_university);
            fillFragmentReview.setDepartment(current_department);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fillFragmentReview,"fragment_fillReview")
                    .commit();
        });

        return root;
    }

    private void update_userListViewReview(View root) {
        ListView myReview_listView = root.findViewById(R.id.myReviews_listView);
        TextView no_review_textView = root.findViewById(R.id.no_reviews_textView);
        ImageView featherPen_imageView = root.findViewById(R.id.fillProfileSection_imageView);
        Button insertReview_button = root.findViewById(R.id.insertReview_button);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference rootRefReviews = FirebaseDatabase.getInstance().getReference("reviews");
            DatabaseReference rootRefUsers = FirebaseDatabase.getInstance().getReference("users");

            Query query_users = rootRefUsers.orderByChild("id").equalTo(firebaseUser.getUid());
            Query query_reviews = rootRefReviews.orderByChild("idUser").equalTo(firebaseUser.getUid());

            query_users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String department = (String) snapshot.child("department").getValue();
                            String university = (String) snapshot.child("university").getValue();
                            if (department != null &&  university != null && !department.equals("") && !university.equals("")){
                                current_university = university;
                                current_department = department;

                                query_reviews.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<String> exams = new ArrayList<>();
                                        ArrayList<String> marks = new ArrayList<>();
                                        ArrayList<String> niceness_values = new ArrayList<>();

                                        if (dataSnapshot.getChildrenCount() != 0) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("exam").exists() && snapshot.child("mark").exists() &&
                                                        snapshot.child("niceness").exists()) {

                                                    exams.add((String) snapshot.child("exam").getValue());
                                                    marks.add((String) snapshot.child("mark").getValue());
                                                    niceness_values.add((String) snapshot.child("niceness").getValue());
                                                }
                                            }

                                            CustomAdapter adapter = new CustomAdapter((Activity) getContext(), exams, marks, niceness_values);

                                            no_review_textView.setVisibility(View.INVISIBLE);
                                            featherPen_imageView.setVisibility(View.INVISIBLE);

                                            Collections.reverse(exams);
                                            Collections.reverse(marks);
                                            Collections.reverse(niceness_values);

                                            myReview_listView.setAdapter(adapter);
                                            myReview_listView.setVisibility(View.VISIBLE);
                                            insertReview_button.setVisibility(View.VISIBLE);
                                        } else {
                                            myReview_listView.setVisibility(View.INVISIBLE);
                                            no_review_textView.setText(R.string.no_review_inListView);
                                            no_review_textView.setVisibility(View.VISIBLE);
                                            featherPen_imageView.setImageResource(R.mipmap.ic_feather_pen);
                                            featherPen_imageView.setVisibility(View.VISIBLE);
                                            insertReview_button.setVisibility(View.VISIBLE);
                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("TAG", "Database: onCancelled");
                                    }
                                });
                            } else {
                                myReview_listView.setVisibility(View.INVISIBLE);
                                no_review_textView.setText(R.string.no_profile_section_set);
                                no_review_textView.setVisibility(View.VISIBLE);
                                featherPen_imageView.setImageResource(R.mipmap.ic_fill_profilesection);
                                featherPen_imageView.setVisibility(View.VISIBLE);
                                insertReview_button.setVisibility(View.INVISIBLE);
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
}
