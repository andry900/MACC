package com.example.macc.ui.information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.macc.R;

public class InformationFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_information, container, false);
        TextView textView_desc = root.findViewById(R.id.text_information_desc);
        TextView textView_creators = root.findViewById(R.id.text_information_creators_desc);
        TextView textView_version = root.findViewById(R.id.text_information_version_desc);

        String description = "Welcome to Virgilio!\n" +
            "Virgilio is an application designed to help students choose which exam to include " +
            "in their study plan. The name \"Virgilio\" was chosen specifically to give the student the idea " +
            "of having a sort of guardian who guides him in his university choices, just like Virgilio in Dante " +
            "Alighieri's Divine Comedy.\n" +
            "In fact, how many times a student finds himself in the condition "+
            "of not knowing which exam to insert in his study plan and therefore relies on the reviews of students who have " +
            "previously taken it?. Thanks to Virgilio, each student will have access to information about the exams " +
            "of his university and department written by other students in completely anonymous form. " +
            "The student can insert reviews on the exams he has taken with the possibility of specifying " +
            "the grade with which the exam was passed, a value that indicates how interesting the topics covered " +
            "in the course were and finally a general comment that advises whether or not to include a certain exam " +
            "in your study plan.";

        String creators = "bellia.1586420@studenti.uniroma1.it\n" + "manzara.1876891@studenti.uniroma1.it";

        String version = "1.0.0";

        textView_desc.setText(description);
        textView_creators.setText(creators);
        textView_version.setText(version);

        return root;
    }
}
