package com.example.mjusubwaystation_fe.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mjusubwaystation_fe.DTO.StationTimeDTO;
import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DynamicTableActivity extends AppCompatActivity {

    private RetrofitInterface service;
    private Call<List<StationTimeDTO>> getTimeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_table);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.202.63.57:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RetrofitInterface 생성
        service = retrofit.create(RetrofitInterface.class);
        // Content에서 역 정보를 받아옴 (여기서 예시로 고정된 값을 사용하셔도 되고, Intent 등을 통해 받아와도 됩니다.)
        int station = getIntent().getIntExtra("station", 0);
        Log.e("DynamicTableActivity", "station값은 : "+station);

        // Retrofit을 사용하여 서버로부터 시간표 정보를 받아오는 요청
        getTimeInfo = service.selectStationTime(station);
        getTimeInfo.enqueue(new Callback<List<StationTimeDTO>>() {
            @Override
            public void onResponse(Call<List<StationTimeDTO>> call, Response<List<StationTimeDTO>> response) {
                if (response.isSuccessful()) {
                    // 서버로부터 받아온 시간표 정보
                    Log.d("DynamicTableActivity", "서버 응답 코드: " + response.code());
                    List<StationTimeDTO> stationTimeList = response.body();

                    // 받아온 정보로 동적으로 테이블 생성
                    createDynamicTable(stationTimeList);
                } else {
                    Toast.makeText(DynamicTableActivity.this, "서버로부터 시간표를 받아오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<StationTimeDTO>> call, Throwable t) {
                Log.e("DynamicTableActivity", "시간표 요청 실패", t);
                Toast.makeText(DynamicTableActivity.this, "시간표를 받아오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDynamicTable(List<StationTimeDTO> stationTimeList) {
        int fontSize = 22;
        // 기존의 XML에서 정의된 tableLayout을 사용하지 않고 동적으로 생성
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)); // 높이를 WRAP_CONTENT로 변경


        // 각 행에 셀 추가
        TableRow headerRow = new TableRow(this);

        TextView cell00 = new TextView(this);
        cell00.setText("   ");
        cell00.setPadding(10, 10, 10, 10);
        cell00.setGravity(Gravity.CENTER);
        cell00.setTextSize(fontSize); // 글자 크기 조절
        headerRow.addView(cell00);

        TextView headerCell1 = new TextView(this);
        headerCell1.setText("해당 호선  ");
        headerCell1.setPadding(10, 10, 10, 10);
        headerCell1.setGravity(Gravity.CENTER);
        headerCell1.setTextSize(fontSize); // 글자 크기 조절
        headerRow.addView(headerCell1);

        TextView headerCell2 = new TextView(this);
        headerCell2.setText("해당 역  ");
        headerCell2.setPadding(10, 10, 10, 10);
        headerCell2.setGravity(Gravity.CENTER);
        headerCell2.setTextSize(fontSize); // 글자 크기 조절
        headerRow.addView(headerCell2);

        TextView headerCell3 = new TextView(this);
        headerCell3.setText("열차 출발 시간");
        headerCell3.setPadding(10, 10, 10, 10);
        headerCell3.setGravity(Gravity.CENTER);
        headerCell3.setTextSize(fontSize); // 글자 크기 조절
        headerRow.addView(headerCell3);

        // 머릿말을 테이블에 추가
        tableLayout.addView(headerRow);




        // 행 추가
        for (StationTimeDTO stationTime : stationTimeList) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)); // 높이를 WRAP_CONTENT로 변경

            TextView cell10 = new TextView(this);
            cell10.setText("   ");
            cell10.setPadding(10, 10, 10, 10);
            cell10.setGravity(Gravity.CENTER);
            cell10.setTextSize(fontSize); // 글자 크기 조절
            tableRow.addView(cell10);

            // 각 행에 셀 추가
            TextView cell1 = new TextView(this);
            cell1.setText(String.valueOf(stationTime.getDirection()));
            cell1.setPadding(10, 10, 10, 10);
            cell1.setGravity(Gravity.CENTER);
            cell1.setTextSize(fontSize); // 글자 크기 조절
            tableRow.addView(cell1);

            TextView cell2 = new TextView(this);
            cell2.setText(String.valueOf(stationTime.getStation_id()));
            cell2.setPadding(10, 10, 10, 10);
            cell2.setGravity(Gravity.CENTER);
            cell2.setTextSize(fontSize); // 글자 크기 조절
            tableRow.addView(cell2);

            TextView cell3 = new TextView(this);
            cell3.setText(stationTime.getStart_time());
            cell3.setPadding(10, 10, 10, 10);
            cell3.setGravity(Gravity.CENTER);
            cell3.setTextSize(fontSize); // 글자 크기 조절
            tableRow.addView(cell3);
            // 필요에 따라 추가 셀을 계속 추가할 수 있습니다.

            // 행을 테이블에 추가
            tableLayout.addView(tableRow);
        }

        // ScrollView 동적 생성
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)); // 높이를 WRAP_CONTENT로 변경

        // ScrollView에 TableLayout 추가
        scrollView.addView(tableLayout);

        // ContentView 설정
        setContentView(scrollView);

    }
}
