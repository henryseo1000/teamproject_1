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

class CombinedArrayAdapter extends ArrayAdapter<CombinedItem> {
    private Context context;
    private List<CombinedItem> combinedItems;

    public CombinedArrayAdapter(Context context, int resource, List<CombinedItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.combinedItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);

        ImageView imageView = rowView.findViewById(R.id.imageView);
        TextView textView = rowView.findViewById(R.id.textView_list);

        // Set image and text for the combined item
        CombinedItem combinedItem = combinedItems.get(position);
        imageView.setImageResource(combinedItem.getImageResource());
        textView.setText(combinedItem.getText());

        return rowView;
    }
}
