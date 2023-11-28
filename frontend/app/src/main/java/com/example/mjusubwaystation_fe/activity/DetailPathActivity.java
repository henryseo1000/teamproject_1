package com.example.mjusubwaystation_fe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

import java.util.ArrayList;

public class DetailPathActivity extends AppCompatActivity {
    int time;
    int startpoint;
    int destination;
    ArrayList<String> shortest_path;
    int expense;
    int transfer;
    TextView startpoint_val, destination_val, expense_val, transfer_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_path);

        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        startpoint = intent.getIntExtra("startpoint", 0);
        destination = intent.getIntExtra("destination", 0);
        shortest_path = intent.getStringArrayListExtra("path");
        expense = intent.getIntExtra("expense", 0);
        transfer = intent.getIntExtra("transfer", 0);

        startpoint_val = findViewById(R.id.startpoint_val);
        destination_val = findViewById(R.id.destination_val);
        expense_val = findViewById(R.id.expense_val);
        transfer_val = findViewById(R.id.transfer_val);

        startpoint_val.setText("" + startpoint);
        destination_val.setText("" + destination);
        expense_val.setText("" + expense);
        transfer_val.setText("" + transfer);
    }
}