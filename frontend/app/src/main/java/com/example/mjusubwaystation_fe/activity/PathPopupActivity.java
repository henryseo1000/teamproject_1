package com.example.mjusubwaystation_fe.activity;

import static java.sql.Types.NULL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

public class PathPopupActivity extends Activity {
    TextView Title;
    TextView Content;
    TextView btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_path_popup);
        btn1 = (TextView) findViewById(R.id.btn1);
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

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
