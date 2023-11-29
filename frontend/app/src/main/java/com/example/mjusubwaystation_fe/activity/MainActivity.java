package com.example.mjusubwaystation_fe.activity;
import static android.content.ContentValues.TAG;

import static java.sql.Types.NULL;

import android.content.Intent;
import android.os.Bundle;

import com.example.mjusubwaystation_fe.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView output, settings;
    public static int station = 0, prev_station = 0, next_station = 0, selected_line = 0;
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
    private ArrayList<ArrayList<Integer>> station_list;
    private ArrayList<Integer> modify_list;
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
                    intent.putExtra("path", toArrayListS(path_result.getShortestPath()));
                    intent.putExtra("totalLineList", toArrayListI(path_result.getTotalLineList()));
                    intent.putExtra("totalTimeList", toArrayListS(path_result.getShortestTime()));
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
                    stationlines = new ArrayList<>(station_result.getLineList());
                    station_list = getline(station_result.getSurroundStationList());
                    modify_list = modifySurroundList(station_result.getSurroundStationList());
                    selected_line = stationlines.get(0);

                    if(stationlines.size() == 1){
                        if(station_list.get(0).get(0) == 0){
                            prev_station = 0;
                            next_station = station_list.get(1).get(0);
                        }
                        else if(station_list.get(1).get(0) == 0){
                            prev_station = station_list.get(0).get(0);
                            next_station = 0;
                        }
                        else {
                            prev_station = station_list.get(0).get(0);
                            next_station = station_list.get(1).get(0);
                        }
                    }
                    else{
                        if(station_list.get(0).get(0) == 0){
                            prev_station = 0;
                            next_station = station_list.get(1).get(0);
                        }
                        else if(station_list.get(1).get(0) == 0){
                            prev_station = station_list.get(0).get(0);
                            next_station = 0;
                        }
                        else {
                            prev_station = station_list.get(0).get(0);
                            next_station = station_list.get(1).get(0);
                        }
                    }

                    mOnPopupClick(station,modify_list);
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
    public void mOnPopupClick(int station,ArrayList<Integer> surrList){
        Intent intent = new Intent(this, PathPopupActivity.class);
        intent.putExtra("X", curX);
        intent.putExtra("Y", curY);
        intent.putExtra("station", station);
        intent.putExtra("lines", stationlines);
        intent.putExtra("prev", prev_station);
        intent.putExtra("next", next_station);
        intent.putExtra("surrList", surrList);
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

    public static ArrayList toArrayListS(LinkedList<Integer> path){
        ArrayList<String> path_list = new ArrayList<>();

        for(int i = 0; i < path.size(); i++) {
            path_list.add(path.get(i).toString());
        }

        return path_list;
    }

    public static ArrayList toArrayListI(List<Integer> path){
        ArrayList<Integer> path_list = new ArrayList<>();
        for(int i = 0; i < path.size(); i++) {
            path_list.add(path.get(i));
        }
        return path_list;
    }
    public static ArrayList toArrayListS(List<String> path){
        ArrayList<String> path_list = new ArrayList<>();
        for(int i = 0; i < path.size(); i++) {
            path_list.add(path.get(i));
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
        else if(curX > 0.19682889 && curY > 0.9255692 && curX < 0.2206326 && curY < 0.95933104){
            station = 108;
        }
        else if(curX > 0.24999923 && curY > 0.9255692 && curX < 0.27619204 && curY < 0.95933104){
            station = 109;
        }
        else if(curX > 0.32221255 && curY > 0.91995144 && curX < 0.34999225 && curY < 0.96157825){
            station = 110;
        }
        else if(curX > 0.39681494 && curY > 0.9188275 && curX < 0.42459464 && curY < 0.96382546){
            station = 111;
        }
        else if(curX > 0.4682435 && curY > 0.92107475 && curX < 0.49680796 && curY < 0.96157825){
            station = 112;
        }
        else if(curX > 0.55872434 && curY > 0.915457 && curX < 0.58252805 && curY < 0.9570838){
            station = 113;
        }
        else if(curX > 0.5928517 && curY > 0.84354633 && curX < 0.6190271 && curY < 0.8783778){
            station = 114;
        }
        else if(curX > 0.59363645 && curY > 0.75590515 && curX < 0.6166554 && curY < 0.7918606){
            station = 115;
        }
        else if(curX > 0.5928517 && curY > 0.6288844 && curX < 0.6190271 && curY < 0.6682103){
            station = 116;
        }
        else if(curX > 0.5912474 && curY > 0.56371534 && curX < 0.6190271 && curY < 0.5929291){
            station = 117;
        }
        else if(curX > 0.5817259 && curY > 0.45472544 && curX < 0.6071339 && curY < 0.4850631){
            station = 118;
        }
        else if(curX > 0.47460046 && curY > 0.42433462 && curX < 0.4999911 && curY < 0.46146715){
            station = 119;
        }
        else if(curX > 0.39841115 && curY > 0.42658183 && curX < 0.42221487 && curY < 0.46259102){
            station = 120;
        }
        else if(curX > 0.32657295 && curY > 0.42433468 && curX < 0.35196358 && curY < 0.46259093){
            station = 121;
        }
        else if(curX > 0.2527553 && curY > 0.42770576 && curX < 0.27736118 && curY < 0.46371427){
            station = 122;
        }
        else if(curX > 0.14006697 && curY > 0.4232113 && curX < 0.16545759 && curY < 0.46371433){
            station = 123;
        }

        else if(curX > 0.03292411 && curY > 0.34455907 && curX < 0.062290736 && curY < 0.3782672){
            station = 201;
        }
        else if(curX > 0.036097936 && curY > 0.26703003 && curX < 0.06070382 && curY < 0.3018621){
            station = 202;
        }
        else if(curX > 0.03690011 && curY > 0.19287223 && curX < 0.061506 && curY < 0.22320975){
            station = 203;
        }
        else if(curX > 0.03292411 && curY > 0.111919485 && curX < 0.06070382 && curY < 0.14562762){
            station = 204;
        }
        else if(curX > 0.03768485 && curY > 0.035514362 && curX < 0.061506 && curY < 0.070345856){
            station = 205;
        }
        else if(curX > 0.088483535 && curY > 0.033267155 && curX < 0.112287246 && curY < 0.07259306){
            station = 206;
        }
        else if(curX > 0.1408517 && curY > 0.0355144 && curX < 0.16467285 && curY < 0.071469694){
            station = 207;
        }
        else if(curX > 0.19641113 && curY > 0.037761606 && curX < 0.2194301 && curY < 0.0737169){
            station = 208;
        }
        else if(curX > 0.2527553 && curY > 0.033267193 && curX < 0.27736118 && curY < 0.07034582){
            station = 209;
        }
        else if(curX > 0.32498604 && curY > 0.029896103 && curX < 0.34640068 && curY < 0.06922249){
            station = 210;
        }
        else if(curX > 0.47576535 && curY > 0.0355144 && curX < 0.501156 && curY < 0.071469694){
            station = 211;
        }
        else if(curX > 0.5440201 && curY > 0.029896103 && curX < 0.5678238 && curY < 0.06922249){
            station = 212;
        }
        else if(curX > 0.39841115 && curY > 0.42658183 && curX < 0.42221487 && curY < 0.46259102){
            station = 213;
        }
        else if(curX > 0.6983802 && curY > 0.0388849 && curX < 0.7237708 && curY < 0.073716946){
            station = 214;
        }
        else if(curX > 0.7642458 && curY > 0.036637694 && curX < 0.78726476 && curY < 0.077087484){
            station = 215;
        }
        else if(curX > 0.82615286 && curY > 0.033267155 && curX < 0.8523457 && curY < 0.070345856){
            station = 216;
        }
        else if(curX > 0.9360875 && curY > 0.033267155 && curX < 0.9614781 && curY < 0.069222525){
            station = 217;
        }

        else if(curX > 0.13933726 && curY > 0.11304282 && curX < 0.16551262 && curY < 0.14338036){
            station = 301;
        }
        else if(curX > 0.13775034 && curY > 0.19287217 && curX < 0.16472788 && curY < 0.22545697){
            station = 302;
        }
        else if(curX > 0.14092417 && curY > 0.27264833 && curX < 0.16551262 && curY < 0.30635652){
            station = 303;
        }
        else if(curX > 0.13933726 && curY > 0.3490534 && curX < 0.16551262 && curY < 0.3827616){
            station = 304;
        }
        else if(curX > 0.14249365 && curY > 0.5637154 && curX < 0.17027336 && curY < 0.5974235){
            station = 305;
        }
        else if(curX > 0.14090674 && curY > 0.6637159 && curX < 0.16629736 && curY < 0.69747776){
            station = 306;
        }
        else if(curX > 0.14249365 && curY > 0.7570285 && curX < 0.16629736 && curY < 0.79298383){
            station = 307;
        }
        else if(curX > 0.14329582 && curY > 0.8536585 && curX < 0.16947119 && curY < 0.88399607){
            station = 308;
        }

        else if(curX > 0.089268275 && curY > 0.7559052 && curX < 0.11308943 && curY < 0.7941077){
            station = 401;
        }
        else if(curX > 0.1972133 && curY > 0.7570285 && curX < 0.22180176 && curY < 0.7941077){
            station = 402;
        }
        else if(curX > 0.2527553 && curY > 0.7570285 && curX < 0.2789481 && curY < 0.79298383){
            station = 403;
        }
        else if(curX > 0.32657295 && curY > 0.753658 && curX < 0.34959194 && curY < 0.7918605){
            station = 404;
        }
        else if(curX > 0.3987948 && curY > 0.75478137 && curX < 0.4225985 && curY < 0.7941077){
            station = 405;
        }
        else if(curX > 0.4733972 && curY > 0.753658 && curX < 0.49959 && curY < 0.79298383){
            station = 406;
        }
        else if(curX > 0.5345021 && curY > 0.7547813 && curX < 0.559108 && curY < 0.79410774){
            station = 407;
        }
        else if(curX > 0.6424471 && curY > 0.7547813 && curX < 0.6654661 && curY < 0.79523104){
            station = 408;
        }
        else if(curX > 0.6976105 && curY > 0.7570285 && curX < 0.72219896 && curY < 0.7929839){
            station = 409;
        }
        else if(curX > 0.7642609 && curY > 0.75590515 && curX < 0.789669 && curY < 0.79410774){
            station = 410;
        }
        else if(curX > 0.8257708 && curY > 0.7559052 && curX < 0.85274833 && curY < 0.7907366){
            station = 411;
        }
        else if(curX > 0.82498604 && curY > 0.6277605 && curX < 0.8511614 && curY < 0.6704575){
            station = 412;
        }
        else if(curX > 0.8241839 && curY > 0.5625915 && curX < 0.8511614 && curY < 0.60191786){
            station = 413;
        }
        else if(curX > 0.82657295 && curY > 0.4513549 && curX < 0.8535505 && curY < 0.48731023){
            station = 414;
        }
        else if(curX > 0.82498604 && curY > 0.34568238 && curX < 0.85274833 && curY < 0.38276154){
            station = 415;
        }
        else if(curX > 0.8273577 && curY > 0.27040118 && curX < 0.8511614 && curY < 0.3074798){
            station = 416;
        }
        else if(curX > 0.82657295 && curY > 0.13214436 && curX < 0.85037667 && curY < 0.1714702){
            station = 417;
        }

        else if(curX > 0.25158536 && curY > 0.10967223 && curX < 0.28016725 && curY < 0.1433804){
            station = 501;
        }
        else if(curX > 0.25238755 && curY > 0.19287217 && curX < 0.27858034 && curY < 0.22545701){
            station = 502;
        }
        else if(curX > 0.25238755 && curY > 0.27264833 && curX < 0.27777818 && curY < 0.30298543){
            station = 503;
        }
        else if(curX > 0.25158536 && curY > 0.34568232 && curX < 0.27858034 && curY < 0.38051432){
            station = 504;
        }
        else if(curX > 0.24841154 && curY > 0.5614681 && curX < 0.27619126 && curY < 0.59629965){
            station = 505;
        }
        else if(curX > 0.25158536 && curY > 0.6603453 && curX < 0.27858034 && curY < 0.6929834){
            station = 506;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }

        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }
        else if(curX > 0.25238755 && curY > 0.8502879 && curX < 0.28016725 && curY < 0.88511944){
            station = 507;
        }

        else if(curX > 0.3277588 && curY > 0.2732029 && curX < 0.3555298 && curY < 0.30859643){
            station = 601;
        }
        else if(curX > 0.32635498 && curY > 0.34988937 && curX < 0.35412598 && curY < 0.38041395){
            station = 602;
        }
        else if(curX > 0.3277588 && curY > 0.56328005 && curX < 0.3555298 && curY < 0.5927751){
            station = 603;
        }
        else if(curX > 0.39996338 && curY > 0.62919813 && curX < 0.427063 && curY < 0.66459167){
            station = 604;
        }
        else if(curX > 0.4721985 && curY > 0.6311649 && curX < 0.49926758 && curY < 0.66955477){
            station = 605;
        }
        else if(curX > 0.5325928 && curY > 0.6302286 && curX < 0.5555115 && curY < 0.66852427){
            station = 606;
        }
        else if(curX > 0.6395569 && curY > 0.6282618 && curX < 0.663147 && curY < 0.66852427){
            station = 607;
        }
        else if(curX > 0.6985779 && curY > 0.6302286 && curX < 0.7221985 && curY < 0.66852427){
            station = 608;
        }
        else if(curX > 0.763855 && curY > 0.6311649 && curX < 0.7888489 && curY < 0.66852427){
            station = 609;
        }
        else if(curX > 0.8999634 && curY > 0.6321944 && curX < 0.9228821 && curY < 0.66758794){
            station = 610;
        }
        else if(curX > 0.9367676 && curY > 0.56328005 && curX < 0.96313477 && curY < 0.599704){
            station = 611;
        }
        else if(curX > 0.9319153 && curY > 0.45026466 && curX < 0.9589844 && curY < 0.48762402){
            station = 612;
        }
        else if(curX > 0.9339905 && curY > 0.34698623 && curX < 0.9589844 && curY < 0.38237977){
            station = 613;
        }
        else if(curX > 0.93814087 && curY > 0.26730445 && curX < 0.9645386 && curY < 0.3046638){
            station = 614;
        }
        else if(curX > 0.9374695 && curY > 0.19558106 && curX < 0.96313477 && curY < 0.2309746){
            station = 615;
        }
        else if(curX > 0.8777466 && curY > 0.12966296 && curX < 0.9027405 && curY < 0.16599283){
            station = 616;
        }
        else if(curX > 0.7645569 && curY > 0.13256513 && curX < 0.7860718 && curY < 0.16992544){
            station = 617;
        }
        else if(curX > 0.6950989 && curY > 0.13649774 && curX < 0.7207947 && curY < 0.17189127){
            station = 618;
        }
        else if(curX > 0.6263428 && curY > 0.13649774 && curX < 0.6492615 && curY < 0.17292172){
            station = 619;
        }
        else if(curX > 0.5444031 && curY > 0.13359557 && curX < 0.5673218 && curY < 0.16992544){
            station = 620;
        }
        else if(curX > 0.47427368 && curY > 0.13256513 && curX < 0.49926758 && curY < 0.17095493){
            station = 621;
        }
        else if(curX > 0.39996338 && curY > 0.13453192 && curX < 0.4242859 && curY < 0.17292172){
            station = 622;
        }

        else if(curX > 0.399292 && curY > 0.2692703 && curX < 0.42565918 && curY < 0.30859643){
            station = 701;
        }
        else if(curX > 0.47357178 && curY > 0.2692703 && curX < 0.49719238 && curY < 0.3066306){
            station = 702;
        }
        else if(curX > 0.5437317 && curY > 0.26833394 && curX < 0.5687256 && curY < 0.30569428){
            station = 703;
        }
        else if(curX > 0.6263428 && curY > 0.26730445 && curX < 0.6513672 && curY < 0.30372748){
            station = 704;
        }
        else if(curX > 0.6992798 && curY > 0.26636812 && curX < 0.7228699 && curY < 0.30569428){
            station = 705;
        }
        else if(curX > 0.7659302 && curY > 0.27030075 && curX < 0.791626 && curY < 0.3066306){
            station = 706;
        }
        else if(curX > 0.8833008 && curY > 0.26730445 && curX < 0.9082947 && curY < 0.30569428){
            station = 707;
        }

        else if(curX > 0.6360779 && curY > 0.9242383 && curX < 0.6603699 && curY < 0.9586014){
            station = 801;
        }
        else if(curX > 0.6937256 && curY > 0.92124206 && curX < 0.7200928 && curY < 0.9537325){
            station = 802;
        }
        else if(curX > 0.6958008 && curY > 0.8445556 && curX < 0.7228699 && curY < 0.8790128){
            station = 803;
        }
        else if(curX > 0.6992798 && curY > 0.56328005 && curX < 0.7263489 && curY < 0.59773725){
            station = 804;
        }
        else if(curX > 0.6971741 && curY > 0.45513362 && curX < 0.7235718 && curY < 0.48762402){
            station = 805;
        }
        else if(curX > 0.6958008 && curY > 0.3450204 && curX < 0.7242737 && curY < 0.38434657){
            station = 806;
        }

        else if(curX > 0.47427368 && curY > 0.8436193 && curX < 0.5013733 && curY < 0.8790128){
            station = 901;
        }
        else if(curX > 0.4729004 && curY > 0.56328005 && curX < 0.5006714 && curY < 0.59773725){
            station = 902;
        }
        else if(curX > 0.47427368 && curY > 0.3450204 && curX < 0.5020447 && curY < 0.38237977){
            station = 903;
        }
        else if(curX > 0.4721985 && curY > 0.19258478 && curX < 0.49926758 && curY < 0.22704199){
            station = 904;
        }
        else {
            station = NULL;
        }
    }

    private ArrayList getline(List<List<Integer>> lines){
        ArrayList line_list = new ArrayList<>(lines);

        for(int i = 0; i < lines.size(); i++){
            for(int j = 0; j < lines.get(i).size(); j++){
                ArrayList<Integer> temp = new ArrayList<>(lines.get(i));
                line_list.set(i, temp);
            }
        }
        return line_list;
    }


    private ArrayList<Integer> modifySurroundList(List<List<Integer>> lines){
        ArrayList<Integer> modifyList = new ArrayList<>();

        if (lines.size() == 4) {
            modifyList.add(lines.get(0).get(1));
            modifyList.add(lines.get(0).get(0));
            modifyList.add(lines.get(1).get(0));
            modifyList.add(lines.get(2).get(1));
            modifyList.add(lines.get(2).get(0));
            modifyList.add(lines.get(3).get(0));
        } else {
            modifyList.add(lines.get(0).get(1));
            modifyList.add(lines.get(0).get(0));
            modifyList.add(lines.get(1).get(0));
        }
        return modifyList;
    }

}
