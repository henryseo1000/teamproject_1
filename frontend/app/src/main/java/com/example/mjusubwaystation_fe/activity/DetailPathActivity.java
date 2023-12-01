package com.example.mjusubwaystation_fe.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.DTO.AlarmDTO;
import com.example.mjusubwaystation_fe.DTO.RouteDTO;
import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.service.AlarmReceiver;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPathActivity extends AppCompatActivity {

    RetrofitInterface service1;
    AlarmDTO alarmDTO;
    Call<AlarmDTO> getAlarmData;
    private ArrayList<Integer> totalLineList;
    private NotificationCompat.Builder n_builder;
    private int time, uniqueId=0;
    Callback alarm_fun;
    private int startpoint;
    private int destination;
    private ArrayList<String> shortest_path;
    private ArrayList<String>  totalTimeList, modifyTime;
    private ArrayList<String> alarmTimeList, titleList, contentList;
    private int expense;
    private int transfer;
    private TextView startpoint_val, destination_val, expense_val, transfer_val;

    private String timers;
    private ListView detailListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_path);

        //받아온 정보들
        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        startpoint = intent.getIntExtra("startpoint", 0);
        destination = intent.getIntExtra("destination", 0);
        shortest_path = intent.getStringArrayListExtra("path");
        expense = intent.getIntExtra("expense", 0);
        transfer = intent.getIntExtra("transfer", 0);
        totalLineList = intent.getIntegerArrayListExtra("totalLineList");
        totalTimeList = intent.getStringArrayListExtra("totalTimeList");
        timers = intent.getStringExtra("timeresult");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.202.63.57:8080/")
//                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service1 = retrofit.create(RetrofitInterface.class);

        alarm_fun = new Callback<AlarmDTO>() {
            @Override
            public void onResponse(Call<AlarmDTO> call, Response<AlarmDTO> response) {
                if(response.isSuccessful()){
                    alarmDTO = response.body();
                    Log.d(TAG, "성공 : \n" + alarmDTO.toString());
                    alarmTimeList = alarmDTO.getAlarmTimeList();
                    Log.d(TAG, "알람이 울릴 시간 : " + alarmTimeList);
                    titleList = alarmDTO.getTitleList();
                    contentList = alarmDTO.getContentList();
                    Log.d(TAG, "제목  : " + titleList);
                    Log.d(TAG, "내용 : " + contentList);

                    //////////////실제 알람 설정 코드/////////////////////////////////////////////////
                    setAlarm(alarmTimeList);
                    showNotification("경로를 선택하셨습니다!", "경로 선택 완료");
                    ///////////////////////////////////////////////////////////////////////////////

                }
                else{
                    Log.d(TAG, "실패 : \n");
                }
            }
            @Override
            public void onFailure(Call<AlarmDTO> call, Throwable t) {
                Log.d(TAG, "onFailure : " + t.getMessage());
            }
        };

        //화면에 매칭
        startpoint_val = findViewById(R.id.startpoint_val);
        destination_val = findViewById(R.id.destination_val);
        expense_val = findViewById(R.id.expense_val);
        transfer_val = findViewById(R.id.transfer_val);
        detailListview = findViewById(R.id.detaillistview);
        startpoint_val.setText(timers);
        destination_val.setText(""+destination);
        transfer_val.setText(transfer+"회");
        setPath(shortest_path);

        //서버와 통신
        try {
            getAlarmData = service1.getAlarmData(getPointTime(modifyTime));
            getAlarmData.enqueue(alarm_fun);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

    }



    private List<CombinedItem2> createCombinedItemList(ArrayList<String> path) {

        List<CombinedItem2> combinedItemList = new ArrayList<>();
        TypedArray typedArrayLine = getResources().obtainTypedArray(R.array.routeline_images);

        TypedArray typedArrayNodeStart = getResources().obtainTypedArray(R.array.startnode_images);
        TypedArray typedArrayNodeCenter = getResources().obtainTypedArray(R.array.centernode_images);
        TypedArray typedArrayNodeEnd = getResources().obtainTypedArray(R.array.endnode_images);
        TypedArray typedArrayNodeLine = getResources().obtainTypedArray(R.array.line_images);
        int temp = 9, temp2 =10;
        int blankIndex = typedArrayLine.getResourceId( temp, 0);
        int blankIndex2 = typedArrayLine.getResourceId( temp2, 0);
        int transferIndex = typedArrayNodeLine.getResourceId(temp, 0);

        //totalLineList를 조건에 맞춰 나타내기 위해 갱신함
        ArrayList<Integer> modifyTotalList = modifyTotalLineList(totalLineList);



        /////////////////화면의 역 배열에 맞게 시간표 재조정
        ArrayList<String> modifyTimeList = modifyTimeList(totalTimeList);
        expense_val.setText(modifyTimeList.get(modifyTimeList.size()-1));
        modifyTime = modifyTimeList;


        //첫번째 역에 대한 호선 정보 ( 변하지 않음 )
        int defaultLine = totalLineList.get(1);
        int resourceIdNode = typedArrayNodeStart.getResourceId(defaultLine - 1, 0);
        int resourceIdLine = typedArrayLine.getResourceId(defaultLine - 1, 0);
        int resourceIdLinetext = typedArrayNodeLine.getResourceId(defaultLine - 1, 0);
        combinedItemList.add(new CombinedItem2(resourceIdLinetext,resourceIdNode, shortest_path.get(0)+"       -        "+totalTimeList.get(0))); // modifyPath에 해당하는 텍스트 추가
        combinedItemList.add(new CombinedItem2(blankIndex2,resourceIdLine, ""));

        //그 이후의 역에 대해서 이미지 및 텍스트를 결합하여 CombinedItem 추가
        int k=2;    // 시간에 대한 순서 (나중에 생각)
        for (int i = 3; i < modifyTotalList.size(); i = i + 2) {
            int compLine = modifyTotalList.get(i);
            int station= modifyTotalList.get(i-1);

            //역 타입을 String으로 변환 ( 화면에 띄우기 위해)
            String stationS = String.valueOf(station);


            //끊기는 역(line==0)인 경우
            if (compLine == 0) {
                resourceIdNode = typedArrayNodeEnd.getResourceId(defaultLine - 1, 0);
                combinedItemList.add(new CombinedItem2(blankIndex2,resourceIdNode, stationS+"       -        "+modifyTimeList.get(i-1)));
                if (i != modifyTotalList.size()-1){
                    combinedItemList.add(new CombinedItem2(transferIndex,blankIndex, ""));
                } else {
                    //걷는 그림 넣음
                    combinedItemList.add(new CombinedItem2(blankIndex2,typedArrayLine.getResourceId( temp2, 0), ""));
                }
                continue;
            }

            // 중간 역인 경우
            if (compLine == defaultLine) {   //이전 역과 같은 경우
                resourceIdNode = typedArrayNodeCenter.getResourceId(compLine - 1, 0);
                resourceIdLine = typedArrayLine.getResourceId(compLine - 1, 0);
                combinedItemList.add(new CombinedItem2(blankIndex2,resourceIdNode, stationS));
                combinedItemList.add(new CombinedItem2(blankIndex2,resourceIdLine, ""));
            } else {    //이전 역과 다른 경우 == 환승
                resourceIdNode = typedArrayNodeStart.getResourceId(compLine - 1, 0);
                resourceIdLine = typedArrayLine.getResourceId(compLine - 1, 0);
                resourceIdLinetext = typedArrayNodeLine.getResourceId(compLine - 1, 0);
                combinedItemList.add(new CombinedItem2(resourceIdLinetext,resourceIdNode, stationS+"       -        "+modifyTimeList.get(i-1)));
                combinedItemList.add(new CombinedItem2(blankIndex2,resourceIdLine, ""));
                //기준 값 변경함
                defaultLine = compLine;
            }
            k=k+2;
        }
        typedArrayNodeStart.recycle();
        typedArrayNodeCenter.recycle();
        typedArrayNodeEnd.recycle();
        typedArrayLine.recycle();

        return combinedItemList;
    }

    private void setPath(ArrayList<String> path){
        List<CombinedItem2> combinedItemList = createCombinedItemList(path);

        // 결합된 데이터를 표시할 어댑터 생성
        CombinedArrayAdapter2 adapter = new CombinedArrayAdapter2(this, android.R.layout.simple_list_item_1, combinedItemList);

        // 결합된 항목을 표시할 단일 ListView 또는 다른 레이아웃 사용
        detailListview.setAdapter(adapter);
    }


    // 만약 기존 것과 다르면, 현재 역 하나 추가하고, 뒤에는 0을 붙임 구리고 해당 역
    private ArrayList<Integer> modifyTotalLineList(ArrayList<Integer> totalLineList){
        int defaultLine = totalLineList.get(1);
        ArrayList<Integer> modifyList = new ArrayList<>();

        Log.d(TAG, "기존 리스트  : " + totalLineList);
        for (int i=1;i<totalLineList.size();i=i+2){
            int station = totalLineList.get(i-1);
            int compLine = totalLineList.get(i);
            if ( defaultLine != compLine && compLine != 0) {
                modifyList.add(station);
                modifyList.add(0);
                modifyList.add(station);
                modifyList.add(compLine);
                defaultLine = compLine;
            } else if (compLine == 0){
                modifyList.add(station);
                modifyList.add(compLine);
                break;
            } else {
                modifyList.add(station);
                modifyList.add(compLine);
            }
        }
        Log.d(TAG, "수정된 리스트  : " + modifyList);
        return modifyList;
    }

    private ArrayList<String> modifyTimeList(ArrayList<String> totalTimeList){
        ArrayList<String> modifyList = new ArrayList<>();
        if (totalTimeList.size() == 2){
            modifyList.add(totalTimeList.get(0));
            modifyList.add(totalTimeList.get(1));
        } else {
            modifyList.add(totalTimeList.get(0));

            Log.d(TAG, "기존 리스트  : " + totalTimeList);
            for (int i = 2; i < totalTimeList.size(); i = i + 2) {
                String defaultTime = totalTimeList.get(i-1);
                String compTime = totalTimeList.get(i);

                Log.d(TAG, "도착시간  : " + defaultTime);
                Log.d(TAG, "출발시간  : " + compTime);
                if (defaultTime.equals(compTime)){
                    modifyList.add(defaultTime);
                    modifyList.add(compTime);
                } else {
                    modifyList.add(defaultTime);
                    modifyList.add(defaultTime);
                    modifyList.add("0");
                    modifyList.add(compTime);
                }

                if (i == totalTimeList.size()-2){
                    modifyList.add(totalTimeList.get(i+1));
                    modifyList.add(totalTimeList.get(i+1));
                    modifyList.add(totalTimeList.get(i+1));
                }
                Log.d(TAG, "-------------------");
            }
        }
        Log.d(TAG, "수정된 리스트  : " + modifyList);
        return modifyList;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showNotification(String message, String title){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(
                    "test1",
                    "Test1",
                    importance
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        n_builder = new NotificationCompat.Builder(this, "test1");

        n_builder.setSmallIcon(R.drawable.clock);
        n_builder.setContentTitle(title);
        n_builder.setContentText(message);

        notificationManager.notify(0, n_builder.build());
    }


    //알람 설정
    public void setAlarm(ArrayList<String> yourTimeArray) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 시간 배열에서 각 시간에 대해 알림 설정
        for (String time : yourTimeArray) {
            // "HH:mm" 포맷의 문자열을 밀리초로 변환
            long timeInMillis = convertTimeToMillis(time);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    uniqueId,  // 고유한 ID로 설정 (각 알림에 대해 다른 ID 사용)
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            // AlarmManager에 알림 설정
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            uniqueId++;
            Log.d(TAG,"시간 : " + timeInMillis + ", ID : " + uniqueId);
        }
    }





    private long convertTimeToMillis(String time) {
        // "HH:mm" 포맷의 문자열을 밀리초로 변환
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        int hour = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(3,5));
        Date date = new Date();

        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(0);

        Log.d(TAG, date.toString());
        return date.getTime();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private ArrayList<String> getPointTime(ArrayList<String> modifyTimeList){
        titleList = new ArrayList<>();
        contentList = new ArrayList<>();

        ArrayList<String> pointTimeList = new ArrayList<>();
        pointTimeList.add(modifyTimeList.get(0));
        for(int i=1;i<modifyTimeList.size();i++){
            String time = modifyTimeList.get(i-1);
            if ( time.equals("0") ){
                pointTimeList.add( modifyTimeList.get(i) );
            }
        }

        for(int i=3;i>=0;i--){
            //titleList.add("[출발역 열차 알림]");
            //contentList.add("출발역 열차 도착 "+i+"분 전입니다!");
        }


        return pointTimeList;
    }
}