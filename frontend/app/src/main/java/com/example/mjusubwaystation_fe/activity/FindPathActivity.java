package com.example.mjusubwaystation_fe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FindPathActivity extends AppCompatActivity {
    public TextView api_textview;
    private Button choose_path;
    private int time;
    private int startpoint;
    private int destination;
    private ArrayList<String> shortest_path;
    private LinearLayout content;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        api_textview = (TextView) findViewById(R.id.textView);
        content = (LinearLayout) findViewById(R.id.content);
        listview = (ListView) findViewById(R.id.listview);

        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        startpoint = intent.getIntExtra("startpoint", 0);
        destination = intent.getIntExtra("destination", 0);
        shortest_path = intent.getStringArrayListExtra("path");

        setPath(shortest_path);


        api_textview.setText(startpoint + "에서 " + destination + "까지 가는데 걸리는 시간은 : " + time + "초\n약 " + toTime(time) + " 소요됩니다.");
        choose_path = (Button)findViewById(R.id.btn_choose_path);
        choose_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
            }
        });
    }

    private String toTime(int seconds){
        int hours = 0;
        int minutes = 0;
        int left = 0;

        hours = seconds / 3600;
        left = seconds % 3600;

        minutes = left / 60;
        left = left % 60;

        return hours + "시간 " + minutes + "분 "+ left + "초";
    }

    private void setPath(ArrayList<String> path){
        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shortest_path);
        listview.setAdapter(adpater);
    }

    private void makeList(){
        /*filter_list = new ArrayList<>();
        filter_list.add("최소 시간");
        filter_list.add("최소 비용");
        filter_list.add("최소 거리");

        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adpater);*/
    }
}