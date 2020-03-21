package com.example.macc.ui.reviews;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
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


public class ReviewsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_reviews, container, false);

        Button insertReview_button = root.findViewById(R.id.insertReview_button);
        ListView myReview_listView = root.findViewById(R.id.myReviews_listView);

        //SET THE HEIGHT OF LISTVIEW TO 0.75 OF THE SCREEN
        ViewGroup.LayoutParams params = myReview_listView.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        params.height  = (int) (height*0.75);//(int) (params.height * 0.8);
        myReview_listView.setLayoutParams(params);

        //UPDATE THE LISTVIEW BASED ON THE PRESENCE OF REVIEWS IN DB
        update_userListViewReview(root);

        //CLICK LISTNER ON ITEM OF LISTVIEW
        myReview_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //PASS EXAM SELECTED
                ShowFragmentReview showFragmentReview = ShowFragmentReview.newInstance("exam_item_selected",myReview_listView.getItemAtPosition(position).toString());
                getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        showFragmentReview,"fragment_showReview").commit();
            }
        });

        insertReview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new FillFragmentReview(),"fragment_fillReview").commit();

            }
        });

        return root;

    }

    public void update_userListViewReview(View root){

        ListView myReview_listView = root.findViewById(R.id.myReviews_listView);
        TextView no_review_textView = root.findViewById(R.id.no_reviews_textView);
        ImageView featherPen_imageView = root.findViewById(R.id.feather_pen_imageView);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("reviews");
        Query query = rootRef.orderByChild("idUser").equalTo(firebaseUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> exams = new ArrayList<String>();
                ArrayList<String> marks = new ArrayList<String>();
                ArrayList<String> niceness_values = new ArrayList<String>();

                if (dataSnapshot.getChildrenCount() != 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if (snapshot.child("exam").exists() && snapshot.child("mark").exists() && snapshot.child("niceness").exists()){
                            exams.add(snapshot.child("exam").getValue().toString());
                            marks.add(snapshot.child("mark").getValue().toString());
                            niceness_values.add(snapshot.child("niceness").getValue().toString());
                        }
                    }
                    no_review_textView.setVisibility(View.INVISIBLE);
                    featherPen_imageView.setVisibility(View.INVISIBLE);

                    Collections.reverse(exams);
                    Collections.reverse(marks);
                    Collections.reverse(niceness_values);

                    CustomAdapter adapter = new CustomAdapter((Activity) getContext(),exams,marks,niceness_values);
                    myReview_listView.setAdapter(adapter);
                    myReview_listView.setVisibility(View.VISIBLE);
                }else{
                    myReview_listView.setVisibility(View.INVISIBLE);
                    no_review_textView.setVisibility(View.VISIBLE);
                    featherPen_imageView.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Database: onCancelled");
            }
        });

    }


}