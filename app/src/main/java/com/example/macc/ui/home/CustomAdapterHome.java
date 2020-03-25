package com.example.macc.ui.home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.macc.R;
import java.util.ArrayList;

public class CustomAdapterHome extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> exams;

    CustomAdapterHome(Activity context, ArrayList<String> exams) {
        super(context, R.layout.item_myreview_listview, exams);
        this.context = context;
        this.exams = exams;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_totalexam_listview,null,true);

        TextView exam = rowView.findViewById(R.id.textView_itemListView_totalExam);
        exam.setText(exams.get(position));

        return rowView;
    }
}
