package com.example.mjusubwaystation_fe.activity;

import static android.content.ContentValues.TAG;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.sql.Types.NULL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

import java.util.ArrayList;

public class PathPopupActivity extends Activity {
    private TextView Title, Content, Prev, Next, nowLine;
    public Button time_table, set_as_start, set_as_dest, prevStation, nextStation;
    private int prev_station = 0, next_station = 0, now_Line;
    private ArrayList<Integer> surrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_path_popup);
        set_as_start = (Button) findViewById(R.id.set_as_start);
        set_as_dest = (Button) findViewById(R.id.set_as_dest);
        time_table = (Button) findViewById(R.id.time_table);

        nowLine = (TextView) findViewById(R.id.nowline);
        Content = (TextView) findViewById(R.id.name);
        Prev = (TextView) findViewById(R.id.Prev);
        Next = (TextView) findViewById(R.id.Next);

        Intent intent = getIntent();
        float dataX = intent.getFloatExtra("X", 0);
        float dataY = intent.getFloatExtra("Y", 0);
        ArrayList<Integer> line_list = intent.getIntegerArrayListExtra("lines");

        prev_station = intent.getIntExtra("prev", 0);
        next_station = intent.getIntExtra("next", 0);
        now_Line = intent.getIntExtra("nowline", 0);
        surrList = intent.getIntegerArrayListExtra("surrList");

        Prev.setText("이전역\n" + prev_station);
        Next.setText("다음역\n" + next_station);
        nowLine.setText(now_Line+"호선");

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



        LinearLayout buttonLayout = findViewById(R.id.ButtonPosition);
        ////////////  버튼 동적 추가  ///////////////////////
        Log.d(TAG, "처리할 리스트 : " + line_list);
        for (int i = 0; i < line_list.size(); i++) {
            Log.d("Button Creation", "Creating button for line: " + line_list.get(i));

            int lineTmp = line_list.get(i);
            Button dynamicButton = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(60), // 90dp를 픽셀로 변환하여 설정
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, dpToPx(10), 0); // 간격 설정
            dynamicButton.setLayoutParams(params);

            dynamicButton.setText(lineTmp + "호선");
            dynamicButton.setTextColor(Color.parseColor("#FF6200EE"));
            dynamicButton.setTextSize(13);
            dynamicButton.setGravity(Gravity.CENTER);
            dynamicButton.setBackgroundColor(Color.parseColor("#DDDDFF"));

            // 동적으로 생성한 버튼에 대한 클릭 이벤트 처리
            dynamicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDynamicButtonClick(lineTmp); // 클릭 이벤트 처리 함수 호출
                }
            });

            // 생성한 버튼을 레이아웃에 추가
            buttonLayout.addView(dynamicButton);
        }

        /////////////////////////////////////////////////


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

    private void onDynamicButtonClick(final int line) {
        // 여기서 Prev와 Next의 값을 업데이트
        int prev=0,next=0;
        if (surrList.size() ==6) {
            if (surrList.get(0) == line) {
                prev = min(surrList.get(1), surrList.get(2));
                next = max(surrList.get(1), surrList.get(2));
            } else if (surrList.get(3) == line) {
                prev = min(surrList.get(4), surrList.get(5));
                next = max(surrList.get(4), surrList.get(5));
            }

            if (prev == 0) {
                Prev.setText("이전역\n" + "");
            } else {
                Prev.setText("이전역\n" + prev);
            }
            nowLine.setText(line + "호선");
            Next.setText("다음역\n" + next);
        }
    }



    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


}
