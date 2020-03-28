package com.example.macc.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ShowFragmentHome extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_home, container, false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ListView totalReviews_fte_listView = root.findViewById(R.id.allReviewsForThatExam_ListView);
        String exam_item_selected;

        Bundle bundle = getArguments();

        assert bundle != null;
        exam_item_selected = bundle.getString("exam_item_selected");

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
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            ArrayList<String> comments = new ArrayList<String>();
                                            ArrayList<String> marks = new ArrayList<String>();
                                            ArrayList<String> niceness_values = new ArrayList<String>();
                                            for (DataSnapshot snapshot_review : dataSnapshot.getChildren()) {
                                                if (Objects.equals(snapshot_review.child("university").getValue(), university) &&
                                                        Objects.equals(snapshot_review.child("department").getValue(), department)) {

                                                    if (Objects.equals(snapshot_review.child("exam").getValue(),exam_item_selected)) {
                                                        comments.add((String) snapshot_review.child("comment").getValue());
                                                        marks.add((String) snapshot_review.child("mark").getValue());
                                                        niceness_values.add((String) snapshot_review.child("niceness").getValue());
                                                    }
                                                }
                                            }
                                            CustomAdapterShowFragmentHome adapter = new CustomAdapterShowFragmentHome((Activity)getContext(),comments,marks,niceness_values);
                                            totalReviews_fte_listView.setAdapter(adapter);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return root;
    }



    static ShowFragmentHome newInstance(String key, String value) {
        Bundle arguments = new Bundle();
        ShowFragmentHome showFragmentHome = new ShowFragmentHome();

        arguments.putString(key, value);
        showFragmentHome.setArguments(arguments);

        return showFragmentHome;
    }
}
