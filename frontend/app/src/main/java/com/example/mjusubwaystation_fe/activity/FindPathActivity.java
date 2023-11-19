package com.example.mjusubwaystation_fe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

public class FindPathActivity extends AppCompatActivity {
    public TextView api_textview;
    private Button choose_path;
    private int time;
    private int startpoint;
    private int destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        api_textview = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        startpoint = intent.getIntExtra("startpoint", 0);
        destination = intent.getIntExtra("destination", 0);

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
}