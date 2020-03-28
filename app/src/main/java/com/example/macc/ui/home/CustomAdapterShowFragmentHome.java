package com.example.macc.ui.home;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.macc.R;
import java.util.ArrayList;

public class CustomAdapterShowFragmentHome extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> comments;
    private final ArrayList<String> marks;
    private final ArrayList<String> niceness_values;

    CustomAdapterShowFragmentHome(Activity context, ArrayList<String> comments, ArrayList<String> marks, ArrayList<String> niceness_values) {
        super(context, R.layout.item_totalreview_for_thatexam, comments);
        this.context = context;
        this.comments = comments;
        this.marks = marks;
        this.niceness_values = niceness_values;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_totalreview_for_thatexam,null,true);

        TextView comment = rowView.findViewById(R.id.textView_itemListView_totalReview_fte);
        TextView mark = rowView.findViewById(R.id.textView_itemListView_mark_totalReview_fte);
        TextView niceness = rowView.findViewById(R.id.textView_itemListView_niceness_totalReview_fte);

        comment.setText(comments.get(position));
        mark.setText(marks.get(position));
        niceness.setText(niceness_values.get(position));

        return rowView;
    }
}
