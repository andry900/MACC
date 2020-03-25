package com.example.macc.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.macc.R;

public class ShowFragmentHome extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_home, container, false);
        TextView textView = root.findViewById(R.id.uaa);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String exam_item_selected = bundle.getString("exam_item_selected");
            textView.setText(exam_item_selected);
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
