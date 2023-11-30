package com.example.mjusubwaystation_fe.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

import java.util.List;

public class CombinedArrayAdapter2 extends ArrayAdapter<CombinedItem2> {
    private Context context;
    private List<CombinedItem2> combinedItems;


    public CombinedArrayAdapter2(Context context, int resource, List<CombinedItem2> objects) {
        super(context, resource, objects);
        this.context = context;
        this.combinedItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        rowView = inflater.inflate(R.layout.custom_list_item2, parent, false);

        ImageView imageView2 = rowView.findViewById(R.id.imageView_line);
        ImageView imageView = rowView.findViewById(R.id.imageView);
        TextView textView = rowView.findViewById(R.id.textView_list);

        // Set image and text for the combined item
        CombinedItem2 combinedItem = combinedItems.get(position);
        imageView2.setImageResource(combinedItem.getImageResource2());
        imageView.setImageResource(combinedItem.getImageResource());
        textView.setText(combinedItem.getText());

        return rowView;
    }
}
