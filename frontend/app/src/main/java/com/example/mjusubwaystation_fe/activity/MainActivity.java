package com.example.mjusubwaystation_fe.activity;
import static android.content.ContentValues.TAG;

import static java.sql.Types.NULL;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.mjusubwaystation_fe.R;

import androidx.annotation.NonNull;
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

import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.service.RouteDTO;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "channel_1_ID";
    private static final String CHANNEL_NAME = "channel_1";
    private TextView output, settings;
    public static int station = 0;
    public static EditText startpoint_input, destination_input;
    public Button find_path, swap_path;
    public static String startpoint, destination;
    public Call<RouteDTO> call;
    RouteDTO result;
    float curX, curY;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
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

        RetrofitInterface service1 = retrofit.create(RetrofitInterface.class);
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

        Callback fun = new Callback<RouteDTO>() {
            @Override
            public void onResponse(Call<RouteDTO> call, Response<RouteDTO> response) {
                if(response.isSuccessful()){
                    result = response.body();
                    Intent intent = new Intent(MainActivity.this, FindPathActivity.class);
                    intent.putExtra("startpoint", result.getStart());
                    intent.putExtra("destination", result.getEnd());
                    intent.putExtra("time", result.getResult());
                    intent.putExtra("path", toArrayList(result.getShortestPath()));

                    startActivity(intent);
                    Log.d(TAG, "성공 : \n" + result.toString());
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
                    showNotification("버튼이 눌렸습니다!", "MJUSubwayStation");
                    startpoint = startpoint_input.getText().toString();
                    destination = destination_input.getText().toString();

                    startpoint = startpoint.replaceAll(" ", "");
                    destination = destination.replaceAll(" ", "");

                    int start, end;
                    start = Integer.parseInt(startpoint);
                    end = Integer.parseInt(destination);
                    call = service1.getRouteData(start, end, "time", "16:30");// 현재 시간을 디폴트로
                    call.enqueue(fun);
                }
                catch(Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

                printString("손가락 눌림 : " + curX + ", " + curY);
                if(curX < 0.346342447 && curX > 0.32620087 && curY < 0.06421511 && curY > 0.03996398){
                    station = 210;
                }

                printString("손가락 눌림 : " + station);
                if(station != NULL) {
                    mOnPopupClick(station);
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
        startActivityForResult(intent, 1);
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

    public void showNotification(String message, String title){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    importance
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.clock);
        builder.setContentTitle(title);
        builder.setContentText(message);

        notificationManager.notify(0, builder.build());
    }

    public static ArrayList toArrayList(LinkedList<Integer> path){
        ArrayList<String> path_list = new ArrayList<>();

        for(int i = 0; i < path.size(); i++) {
            path_list.add(path.get(i).toString());
        }

        return path_list;
    }

}
