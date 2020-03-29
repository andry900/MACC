package com.example.macc.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Objects;

public class HomeFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ListView totalExams_listView = root.findViewById(R.id.totalExams_listView);
        TextView no_exams_textView = root.findViewById(R.id.no_exams_textView);
        ImageView fillProfileSection_imageView = root.findViewById(R.id.fillProfileSection_imageView);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference rootRefReviews = FirebaseDatabase.getInstance().getReference("reviews");
            DatabaseReference rootRefUsers = FirebaseDatabase.getInstance().getReference("users");

            Query query_users = rootRefUsers.orderByChild("id").equalTo(firebaseUser.getUid());
            Query query_reviews = rootRefReviews.orderByChild("idReview");

            query_users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!Objects.equals(snapshot.child("department").getValue(), "") &&
                                    !Objects.equals(snapshot.child("university").getValue(), "")) {

                                String university = (String) snapshot.child("university").getValue();
                                String department = (String) snapshot.child("department").getValue();

                                query_reviews.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("DefaultLocale")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<String> exams = new ArrayList<>();

                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot snapshot_review : dataSnapshot.getChildren()) {
                                                if (Objects.equals(snapshot_review.child("university").getValue(), university) &&
                                                        Objects.equals(snapshot_review.child("department").getValue(), department)) {

                                                    if (!exams.contains(snapshot_review.child("exam").getValue())) {
                                                        exams.add((String) snapshot_review.child("exam").getValue());
                                                    }
                                                }
                                            }
                                            ArrayList<String> avg_mark_array = new ArrayList<>();
                                            ArrayList<String> avg_niceness_array = new ArrayList<>();
                                            float mark = 0;
                                            float niceness = 0;
                                            int num_exam_found = 0;

                                            for(int i = 0; i < exams.size(); i++){
                                                for (DataSnapshot snapshot_review : dataSnapshot.getChildren()) {
                                                    if (Objects.equals(snapshot_review.child("university").getValue(), university) &&
                                                            Objects.equals(snapshot_review.child("department").getValue(), department)) {

                                                        if (exams.get(i).equals(snapshot_review.child("exam").getValue())){
                                                            num_exam_found++;
                                                            mark += Float.parseFloat(Objects.requireNonNull(snapshot_review.child("mark").getValue()).toString());
                                                            niceness += Float.parseFloat(Objects.requireNonNull(snapshot_review.child("niceness").getValue()).toString());
                                                        }
                                                    }
                                                }
                                                float avg_mark = mark / num_exam_found;
                                                float avg_niceness = niceness / num_exam_found;
                                                avg_mark_array.add(String.format("%.2f",avg_mark));
                                                avg_niceness_array.add(String.format("%.2f",avg_niceness));
                                                mark = 0;
                                                niceness = 0;
                                                num_exam_found = 0;
                                            }

                                            CustomAdapterHome adapter = new CustomAdapterHome((Activity) getContext(), exams, avg_mark_array, avg_niceness_array);
                                            totalExams_listView.setAdapter(adapter);

                                            if (totalExams_listView.getAdapter().getCount() != 0) {
                                                no_exams_textView.setVisibility(View.INVISIBLE);
                                                fillProfileSection_imageView.setVisibility(View.INVISIBLE);
                                                totalExams_listView.setVisibility(View.VISIBLE);
                                            } else {
                                                totalExams_listView.setVisibility(View.INVISIBLE);
                                                no_exams_textView.setText(R.string.no_review_has_been_found);
                                                no_exams_textView.setVisibility(View.VISIBLE);
                                                fillProfileSection_imageView.setImageResource(R.mipmap.ic_noreview_intotal);
                                                fillProfileSection_imageView.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            totalExams_listView.setVisibility(View.INVISIBLE);
                                            no_exams_textView.setText(R.string.no_review_has_been_found);
                                            no_exams_textView.setVisibility(View.VISIBLE);
                                            fillProfileSection_imageView.setImageResource(R.mipmap.ic_noreview_intotal);
                                            fillProfileSection_imageView.setVisibility(View.VISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                totalExams_listView.setVisibility(View.INVISIBLE);
                                no_exams_textView.setText(R.string.no_profile_section_set);
                                no_exams_textView.setVisibility(View.VISIBLE);
                                fillProfileSection_imageView.setImageResource(R.mipmap.ic_fill_profilesection);
                                fillProfileSection_imageView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        totalExams_listView.setOnItemClickListener((parent, view, position, id) -> {
            ShowFragmentHome showFragmentHome = ShowFragmentHome
                    .newInstance(totalExams_listView
                            .getItemAtPosition(position)
                            .toString());

            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, showFragmentHome,"fragment_show_home")
                    .commit();
        });

        return root;
    }
}