package com.example.mjusubwaystation_fe.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.service.CombinedItem;

import java.util.List;

public class CombinedArrayAdapter extends ArrayAdapter<CombinedItem> {
    private Context context;
    private List<CombinedItem> combinedItems;

    private int type;

    public CombinedArrayAdapter(Context context, int resource, List<CombinedItem> objects, int type) {
        super(context, resource, objects);
        this.context = context;
        this.combinedItems = objects;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (type == 1){
            rowView = inflater.inflate(R.layout.custom_list_item, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.custom_list_item2, parent, false);
        }
        ImageView imageView = rowView.findViewById(R.id.imageView);
        TextView textView = rowView.findViewById(R.id.textView_list);

        // Set image and text for the combined item
        CombinedItem combinedItem = combinedItems.get(position);
        imageView.setImageResource(combinedItem.getImageResource());
        textView.setText(combinedItem.getText());

        return rowView;
    }
}
