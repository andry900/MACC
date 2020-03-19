package com.example.macc.ui.reviews;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.example.macc.DatabaseAccess;
import com.example.macc.R;
import com.example.macc.Reviews;
import com.example.macc.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import bolts.Bolts;

public class FillFragmentReview extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_fill_review, container, false);
        EditText insertReview_edExam = root.findViewById(R.id.insertReview_edExam);
        EditText insertReview_edProfessor = root.findViewById(R.id.insertReview_edProfessor);

        EditText insertReview_edMark = root.findViewById(R.id.insertReview_edMark);
        EditText insertReview_edNiceness = root.findViewById(R.id.insertReview_edNiceness);
        EditText insertReview_edComment = root.findViewById(R.id.insertReview_edComment);
        Button insertReview_saveButton = root.findViewById(R.id.insertReview_saveButton);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseAccess databaseAccess = new DatabaseAccess();

        insertReview_saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK THE CORRECT USER INPUTS
                if (insertReview_edExam.getText().toString().equals(""))
                    insertReview_edExam.setError("Please enter an exam!");
                if (insertReview_edProfessor.getText().toString().equals(""))
                    insertReview_edProfessor.setError("Please enter a Professor!");
                if (insertReview_edMark.getText().toString().equals(""))
                    insertReview_edMark.setError("Please enter a mark!");
                if (!insertReview_edMark.getText().toString().equals("")){
                    int value = Integer.parseInt(insertReview_edMark.getText().toString());
                    if( value < 18 || value > 31){
                        insertReview_edMark.setError("Please enter a value between 18 and 31");
                    }
                }
                if(insertReview_edNiceness.getText().toString().equals(""))
                    insertReview_edNiceness.setError("Please enter a niceness value!");
                if (!insertReview_edNiceness.getText().toString().equals("")){
                    Float value = Float.parseFloat(insertReview_edNiceness.getText().toString());
                    if( value < 1 || value > 5){
                        insertReview_edNiceness.setError("Please enter a value between 1 and 5");
                    }
                }
                if(insertReview_edComment.getText().toString().equals(""))
                    insertReview_edComment.setError("Please enter a comment!");


                //CHECKING THAT NO ERROR HAS BEEN SHOWED BEFORE INSERT VALUES INTO DATABASE
                if ( insertReview_edExam.getError() == null  &&  insertReview_edProfessor.getError() == null  &&
                        insertReview_edMark.getError() == null  && insertReview_edNiceness.getError() == null  &&
                        insertReview_edComment.getError() == null ){

                    //GET REFERENCE ON DB
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("reviews");
                    Query query = rootRef.orderByChild("idUser").equalTo(firebaseUser.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //IF THERE IS ALREADY AN ENTRY FOR THE REVIEWS TABLE
                            if (dataSnapshot.getChildrenCount() != 0){

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    //TAKE THE VALUE OF THE EXAM IN DB
                                    String currentExam = snapshot.child("exam").getValue().toString();

                                    //SET THE ERRROR IF YOU WANT TO INSERT 2 REVIEWS ON THE SAME EXAM
                                    if ( insertReview_edExam.getText().toString().equalsIgnoreCase(currentExam)) {
                                        insertReview_edExam.setError("You cannot insert 2 reviews on the same exam!");
                                        break;
                                    }
                                }

                                if(insertReview_edExam.getError() == null){

                                    Query query2 = rootRef.orderByKey().limitToLast(1);
                                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int last_idReview = 0;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                last_idReview = Integer.parseInt(snapshot.child("idReview").getValue().toString()) + 1;
                                            }
                                            Reviews reviews = new Reviews(firebaseUser.getUid().toString(),String.valueOf(last_idReview),insertReview_edExam.getText().toString(),
                                                    insertReview_edProfessor.getText().toString(),insertReview_edMark.getText().toString(),
                                                    insertReview_edNiceness.getText().toString(),insertReview_edComment.getText().toString());
                                            databaseAccess.InsertReview(reviews);
                                            Toast.makeText(getContext(),"Your review has been saved!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("TAG", "Database: onCancelled");
                                        }
                                    });
                                }
                            }else {
                                Query query3 = rootRef.orderByKey().limitToLast(1);
                                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int last_idReview = 0;
                                        if(dataSnapshot.getChildrenCount() != 0){
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                if(snapshot.child("idReview").exists())
                                                    last_idReview = Integer.parseInt(snapshot.child("idReview").getValue().toString()) + 1;
                                            }
                                        }else {
                                            last_idReview = 1;
                                        }
                                        Reviews reviews = new Reviews(firebaseUser.getUid(),String.valueOf(last_idReview),insertReview_edExam.getText().toString(),
                                                insertReview_edProfessor.getText().toString(),insertReview_edMark.getText().toString(),
                                                insertReview_edNiceness.getText().toString(),insertReview_edComment.getText().toString());
                                        databaseAccess.InsertReview(reviews);
                                        Toast.makeText(getContext(),"Your first review has been saved!", Toast.LENGTH_SHORT).show();
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


}
