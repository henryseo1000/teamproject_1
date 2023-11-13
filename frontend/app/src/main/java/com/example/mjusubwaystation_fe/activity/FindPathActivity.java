package com.example.mjusubwaystation_fe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

public class FindPathActivity extends AppCompatActivity {
    public TextView api_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        api_textview = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String result = intent.getStringExtra("response");

        api_textview.setText(result);
    }
}