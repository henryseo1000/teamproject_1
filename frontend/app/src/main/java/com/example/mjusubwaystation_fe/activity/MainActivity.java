package com.example.mjusubwaystation_fe.activity;
import static android.content.ContentValues.TAG;

import static java.sql.Types.NULL;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;

import com.example.mjusubwaystation_fe.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.mjusubwaystation_fe.service.AlarmReceiver;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.DTO.RouteDTO;
import com.example.mjusubwaystation_fe.DTO.StationDTO;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView output, settings;
    public static int station = 0;
    public static EditText startpoint_input, destination_input;
    public Button find_path, swap_path;
    public static String startpoint, destination;
    public Call<RouteDTO> getPath;
    public Call<StationDTO> getStationInfo;
    private RouteDTO path_result;
    private StationDTO station_result;
    float curX, curY;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Date now;
    RetrofitInterface service1;
    Callback path_fun, station_fun;

    private ArrayList<Integer> stationlines;
    private int[] surroundstation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.main_screen);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        PhotoView photoView = findViewById(R.id.photoView);
        photoView.setImageResource(R.drawable.image4);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.202.63.57:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service1 = retrofit.create(RetrofitInterface.class);
        PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        // 위젯에 대한 참조.
        output = (TextView) findViewById(R.id.output);
        find_path = (Button) findViewById(R.id.find_path);
        swap_path = (Button) findViewById(R.id.swap);
        startpoint_input = (EditText) findViewById(R.id.edit_startpoint);
        destination_input = (EditText) findViewById(R.id.edit_destination);
        settings = (TextView) findViewById(R.id.settings);

        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        path_fun = new Callback<RouteDTO>() {
            @Override
            public void onResponse(Call<RouteDTO> call, Response<RouteDTO> response) {
                if(response.isSuccessful()){
                    path_result = response.body();
                    Log.d(TAG, "성공 : \n" + path_result.toString());

                    Intent intent = new Intent(MainActivity.this, FindPathActivity.class);
                    intent.putExtra("startpoint", path_result.getStart());
                    intent.putExtra("destination", path_result.getEnd());
                    intent.putExtra("time", path_result.getTime());
                    intent.putExtra("path", toArrayList(path_result.getShortestPath()));
                    intent.putExtra("expense", path_result.getTotalPrice());
                    intent.putExtra("transfer", path_result.getTransferCount());

                    startActivity(intent);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(startpoint + "또는 " + destination + "은(는) 노선도에 존재하지 않습니다. 노선도를 다시 확인해주세요.");
                    builder.setTitle("잘못된 경로입니다!");
                    builder.show();
                    Log.d(TAG, "실패 : \n");
                }
            }

            @Override
            public void onFailure(Call<RouteDTO> call, Throwable t) {
                Log.d(TAG, "onFailure : " + t.getMessage());
            }
        };

        station_fun = new Callback<StationDTO>() {
            @Override
            public void onResponse(Call<StationDTO> call, Response<StationDTO> response) {
                if(response.isSuccessful()){
                    station_result = response.body();
                    stationlines = getline(station_result);

                    mOnPopupClick(station);
                    Log.d(TAG, "각 호선은 : " + stationlines.toString());
                    Log.d(TAG, "성공 : \n" + station_result.getSurroundStationList().toString());
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("서버와 통신 중 오류가 발생했습니다.");
                    builder.setTitle("서버 통신 오류");
                    builder.show();
                    Log.d(TAG, "실패 : \n");
                }
            }

            @Override
            public void onFailure(Call<StationDTO> call, Throwable t) {
                Log.d(TAG, "onFailure : " + t.getMessage());
            }
        };

        swap_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startpoint = startpoint_input.getText().toString();
                destination = destination_input.getText().toString();

                startpoint_input.setText(destination);
                destination_input.setText(startpoint);
            }
        });

        find_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startpoint = startpoint_input.getText().toString();
                    destination = destination_input.getText().toString();

                    startpoint = startpoint.replaceAll(" ", "");
                    destination = destination.replaceAll(" ", "");

                    int start, end;
                    start = Integer.parseInt(startpoint);
                    end = Integer.parseInt(destination);

                    now = new Date();
                    Log.d(TAG, "" + now.toString());

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String gettime = format.format(now);

                    //Log.d(TAG, "버튼을 누른 시점 : " + gettime);

                    getPath = service1.getPathData(start, end, "최소시간", gettime);// 현재 시간을 디폴트로
                    getPath.enqueue(path_fun);
                }
                catch(Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    Log.d(TAG, e.getMessage());
                    builder.setMessage("출발역과 도착역 중 하나가 누락되었습니다! 다시 확인해주세요!");
                    builder.setTitle("경고!");
                    builder.show();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        attacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            private void printString(String s) {
                //좌표 출력
                output.setText(s); //한 줄씩 추가
            }
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                curX = x;  //눌린 곳의 X좌표
                curY = y;  //눌린 곳의 Y좌표
                station = NULL;

                pressed_location(curX, curY);

                printString("손가락 눌림 : " + curX + ", " + curY);
                Log.d(TAG,"손가락 눌림 : " + curX + ", " + curY);

                if(station != NULL) {
                    getStationInfo = service1.getStationInfo(station);
                    getStationInfo.enqueue(station_fun);
                }
            }
        });
    }

    //터치 시 팝업
    public void mOnPopupClick(int station){
        Intent intent = new Intent(this, PathPopupActivity.class);
        intent.putExtra("X", curX);
        intent.putExtra("Y", curY);
        intent.putExtra("station", station);
        intent.putExtra("lines", stationlines);

        //intent.putExtra("result", station_result.getSurroundStationList());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                output.setText(result);
            }
        }
    }

    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static ArrayList toArrayList(LinkedList<Integer> path){
        ArrayList<String> path_list = new ArrayList<>();

        for(int i = 0; i < path.size(); i++) {
            path_list.add(path.get(i).toString());
        }

        return path_list;
    }

    public static void pressed_location(float curX, float curY){
        if(curX > 0.038879395 && curY > 0.4860061 && curX < 0.056935627 && curY < 0.5073234){ // 101
            station = 101;
        }
        else if(curX > 0.037953693 && curY > 0.5718994 && curX < 0.056247063 && curY < 0.5938825){
            station = 102;
        }
        else if(curX > 0.037722893 && curY > 0.6705997 && curX < 0.056247063 && curY < 0.69057494){
            station = 103;
        }
        else if(curX > 0.03888256 && curY > 0.7613932 && curX < 0.05717276 && curY < 0.78564435){
            station = 104;
        }
        else if(curX > 0.03749593 && curY > 0.86030537 && curX < 0.058095295 && curY < 0.8832768){
            station = 105;
        }
        else if(curX > 0.06874593 && curY > 0.92784643 && curX < 0.0893453 && curY < 0.9524097){
            station = 106;
        }
        else if(curX > 0.1443925 && curY > 0.92981267 && curX < 0.16430014 && curY < 0.95506257){
            station = 107;
        }
        else if(curX > 0.32620087 && curY > 0.03996398 && curX < 0.346342447 && curY < 0.06421511){
            station = 210;
        }
        else {
            station = NULL;
        }
    }

    private ArrayList getline(StationDTO result){
        ArrayList<Integer> line_list= new ArrayList<>();
        int length = result.getSurroundStationList().size();
        int count = 0;

        line_list.add(result.getSurroundStationList().get(count).get(1));
        for(int i = 0; i < length; i++){
            if(result.getSurroundStationList().get(count).get(1) != result.getSurroundStationList().get(i).get(1)){
                line_list.add(result.getSurroundStationList().get(i).get(1));
                count ++;
            }
        }

        return line_list;
    }
}
