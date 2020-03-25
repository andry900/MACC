package com.example.macc.ui.reviews;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.macc.R;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> exams;
    private final ArrayList<String> marks;
    private final ArrayList<String> niceness_values;

    CustomAdapter(Activity context, ArrayList<String> exams, ArrayList<String> marks, ArrayList<String> niceness_values){
        super(context, R.layout.item_myreview_listview,exams);
        this.context = context;
        this.exams = exams;
        this.marks = marks;
        this.niceness_values = niceness_values;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_myreview_listview,null,true);

        TextView exam = rowView.findViewById(R.id.textView_itemListView_review);
        TextView mark = rowView.findViewById(R.id.textView_itemListView_mark_review);
        TextView niceness = rowView.findViewById(R.id.textView_itemListView_niceness_review);

        exam.setText(exams.get(position));
        mark.setText(marks.get(position));
        niceness.setText(niceness_values.get(position));

        return rowView;
    }
}
