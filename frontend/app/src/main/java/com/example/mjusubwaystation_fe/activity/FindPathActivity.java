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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        api_textview = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String result = intent.getStringExtra("response");

        api_textview.setText(result);
        choose_path = (Button)findViewById(R.id.btn_choose_path);
        choose_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
            }
        });
    }
}