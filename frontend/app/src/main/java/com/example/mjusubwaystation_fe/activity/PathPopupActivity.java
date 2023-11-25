package com.example.mjusubwaystation_fe.activity;

import static java.sql.Types.NULL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

public class PathPopupActivity extends Activity {
    private TextView Title, Content;
    public Button time_table, set_as_start, set_as_dest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_path_popup);
        set_as_start = (Button) findViewById(R.id.set_as_start);
        set_as_dest = (Button) findViewById(R.id.set_as_dest);
        Title = (TextView) findViewById(R.id.Title);
        Content = (TextView) findViewById(R.id.name);

        Intent intent = getIntent();
        float dataX = intent.getFloatExtra("X", 0);
        float dataY = intent.getFloatExtra("Y", 0);
        Title.setText("터치 이벤트가 호출되었습니다!");

        int station = intent.getIntExtra("station", 0);

        if(station != NULL) {
            Content.setText("" + station);
        }

        set_as_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.startpoint_input.setText("" + MainActivity.station);
                finish();
            }
        });

        set_as_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.destination_input.setText("" + MainActivity.station);
                finish();
            }
        });
    }

}
