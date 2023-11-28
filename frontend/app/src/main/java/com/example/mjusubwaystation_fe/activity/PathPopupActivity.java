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

import java.util.ArrayList;

public class PathPopupActivity extends Activity {
    private TextView Title, Content, Prev, Next;
    public Button time_table, set_as_start, set_as_dest;
    private int prev_station = 0, next_station = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_path_popup);
        set_as_start = (Button) findViewById(R.id.set_as_start);
        set_as_dest = (Button) findViewById(R.id.set_as_dest);
        time_table = (Button) findViewById(R.id.time_table);

        Title = (TextView) findViewById(R.id.Title);
        Content = (TextView) findViewById(R.id.name);
        Prev = (TextView) findViewById(R.id.Prev);
        Next = (TextView) findViewById(R.id.Next);

        Intent intent = getIntent();
        float dataX = intent.getFloatExtra("X", 0);
        float dataY = intent.getFloatExtra("Y", 0);
        ArrayList<Integer> line_list = intent.getIntegerArrayListExtra("lines");
        String line = "| ";

        for(int i = 0; i < line_list.size(); i++){
            line += line_list.get(i) + "호선 | ";
        }
        prev_station = intent.getIntExtra("prev", 0);
        next_station = intent.getIntExtra("next", 0);

        Title.setText(line);
        Prev.setText("이전역\n" + prev_station);
        Next.setText("다음역\n" + next_station);

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

        time_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentText = Content.getText().toString();
                int station = Integer.parseInt(contentText);

                // "station"이라는 이름으로 station 값을 전달하는 Intent 생성
                Intent intent = new Intent(PathPopupActivity.this, DynamicTableActivity.class);
                intent.putExtra("station", station);

                // DynamicTableActivity 시작
                startActivity(intent);
            }
        });

    }

    private void findStation(int line){

    }

}
