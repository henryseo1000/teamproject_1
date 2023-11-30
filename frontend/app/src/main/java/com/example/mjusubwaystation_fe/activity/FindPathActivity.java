package com.example.mjusubwaystation_fe.activity;


import static android.content.ContentValues.TAG;

import static com.example.mjusubwaystation_fe.activity.MainActivity.toArrayListI;
import static com.example.mjusubwaystation_fe.activity.MainActivity.toArrayListS;

import com.example.mjusubwaystation_fe.service.CombinedArrayAdapter;
import com.example.mjusubwaystation_fe.service.CombinedItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.icu.util.Calendar;
import android.os.Build;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.service.AlarmReceiver;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.DTO.RouteDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindPathActivity extends AppCompatActivity {
    public TextView api_textview;
    public TextView type,startTime, totalTime, totalExpense, totalDistance;
    private Button choose_path, btn_dialog, find_path_retry, show_time;
    private EditText destination_input, startpoint_input;
    private int alarmHour = 0, alarmMinute = 0, time, startpoint, destination, expense = 0, transfer;
    private String option = "최소시간";
    private ArrayList<String> shortest_path;
    private ArrayList<Integer> totalLineList;
    private ArrayList<String> totalTimeList;
    private LinearLayout content;
    private ListView listview2;
    private String[] filters = {"최소시간", "최소비용", "최단거리"};
    private AlertDialog.Builder a_builder;
    private Call<RouteDTO> call;
    private Date now;
    private NotificationManager notificationManager;
    private AlarmManager alarmManager;
    private NotificationCompat.Builder n_builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.202.63.57:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service1 = retrofit.create(RetrofitInterface.class);

        //InputMethodManager keymanager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        now = new Date();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Callback fun = new Callback<RouteDTO>() {
            @Override
            public void onResponse(Call<RouteDTO> call, Response<RouteDTO> response) {
                if (response.isSuccessful()) {
                    RouteDTO result = response.body();
                    shortest_path = toArrayListS(result.getShortestPath());
                    totalLineList = toArrayListI(result.getTotalLineList());
                    totalTimeList = new ArrayList<>(result.getShortestTime());

                    time = result.getTime();
                    expense = result.getTotalPrice();
                    transfer = result.getTransferCount();
                    setContent();
                    Log.d(TAG, "성공 : \n" + result.toString());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPathActivity.this);
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

//        api_textview = (TextView) findViewById(R.id.textView);
        content = (LinearLayout) findViewById(R.id.content);
        listview2 = (ListView) findViewById(R.id.listview2);
        btn_dialog = (Button)findViewById(R.id.btn_dialog);


        type = (TextView) findViewById(R.id.type);
        startTime = (TextView) findViewById(R.id.start_time);
        totalTime = (TextView) findViewById(R.id.total_time);
        totalExpense = (TextView) findViewById(R.id.total_expense);
        totalDistance = (TextView) findViewById(R.id.total_distance);

        
        //시간설정
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        show_time = (Button) findViewById(R.id.Show_Time);
        find_path_retry = (Button)findViewById(R.id.find_path_retry);
        startpoint_input = (EditText) findViewById(R.id.edit_startpoint);
        destination_input = (EditText) findViewById(R.id.edit_destination);
        choose_path = (Button)findViewById(R.id.btn_choose_path);

        show_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog
                        (FindPathActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                now.setHours(hourOfDay);
                                now.setMinutes(minute);
                                Toast.makeText(getApplicationContext(), "설정된 시간은 : " + now.getHours() + "시 " + now.getMinutes() + "분입니다.", Toast.LENGTH_SHORT).show();

                                // 시간 설정 후 경로 재검색 버튼 자동으로 누르기
                                find_path_retry.performClick();

                            }
                        },alarmHour, alarmMinute, false);
                timePickerDialog.show();
            }
        });

        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        startpoint = intent.getIntExtra("startpoint", 0);
        destination = intent.getIntExtra("destination", 0);
        shortest_path = intent.getStringArrayListExtra("path");
        totalLineList = intent.getIntegerArrayListExtra("totalLineList");
        totalTimeList = intent.getStringArrayListExtra("totalTimeList");
        expense = intent.getIntExtra("expense", 0);
        transfer = intent.getIntExtra("transfer", 0);

        startpoint_input.setText(Integer.toString(startpoint));
        destination_input.setText(Integer.toString(destination));

        setContent();
        choose_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification("경로를 선택하셨습니다!", "경로 선택 완료");
                setAlarm("2023-11-27 20:08:00");

                Intent intent = new Intent(getApplicationContext(), DetailPathActivity.class);
                intent.putExtra("startpoint", startpoint);
                intent.putExtra("destination", destination);
                intent.putExtra("totalLineList", totalLineList);
                intent.putExtra("totalTimeList", totalTimeList);
                intent.putExtra("time", time);
                intent.putExtra("path", shortest_path);
                intent.putExtra("expense", expense);
                intent.putExtra("transfer", transfer);

                startActivity(intent);
            }
        });

        find_path_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //keymanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    String startpoint_str = "";
                    startpoint_str = startpoint_input.getText().toString().replaceAll(" ", "");

                    String destination_str = "";
                    destination_str = destination_input.getText().toString().replaceAll(" ", "");

                    //Log.d(TAG,"시작점 : " + startpoint_str + " 도착점 : " + destination_str);

                    startpoint = Integer.parseInt(startpoint_str);
                    destination = Integer.parseInt(destination_str);

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String gettime = format.format(now);
                    Log.d(TAG, "시간 " + gettime + "으로 요청 보내기");

                    call = service1.getPathData(startpoint, destination, option, gettime);// 현재 시간을 디폴트로
                    call.enqueue(fun);
                }
                catch(Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPathActivity.this);
                    builder.setMessage("출발역과 도착역 중 하나가 누락되었습니다! 다시 확인해주세요!");
                    builder.setTitle("경고!");
                    builder.show();
                }
            }
        });
    }
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

    public void setAlarm(String from) {
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);

        //날짜 포맷을 바꿔주는 소스코드
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datetime = now;
        try {
            datetime = dateFormat.parse(from);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);
        Log.d(TAG, "알람 세팅 시간은 : " + datetime);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    private String toTime(int seconds){
        int hours = 0;
        int minutes = 0;
        int left = 0;
        String str = "";

        hours = seconds / 3600;
        left = seconds % 3600;

        minutes = left / 60;
        left = left % 60;

        if (hours == 0){
            str = minutes+"분";
        } else {
            str = hours + "시간 " + hours + "분";
        }

        return str;
    }

    private void setPath(ArrayList<String> path){
        List<CombinedItem> combinedItemList = createCombinedItemList(path);

        // 결합된 데이터를 표시할 어댑터 생성
        CombinedArrayAdapter adapter = new CombinedArrayAdapter(this, android.R.layout.simple_list_item_1, combinedItemList,1);

        // 결합된 항목을 표시할 단일 ListView 또는 다른 레이아웃 사용
        listview2.setAdapter(adapter);
    }


    private void showDialog(){
        a_builder = new AlertDialog.Builder(this);
        a_builder.setTitle("검색 옵션을 선택하세요 : ");

        //다이얼로그에 리스트 담기
        a_builder.setItems(filters, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(filters[which].equals("최소비용")){
                    option = "최소비용";
                }
                else if(filters[which].equals("최소시간")){
                    option = "최소시간";
                }
                else if(filters[which].equals("최단거리")){
                    option = "최단거리";
                }
                Toast.makeText(getApplicationContext(), "다시 검색하시면 " + filters[which] + " 옵션으로 검색됩니다.", Toast.LENGTH_SHORT).show();
                find_path_retry.performClick();
            }
        });

        AlertDialog alertDialog = a_builder.create();
        alertDialog.show();
    }

    private void setContent(){
        setPath(shortest_path);
//        api_textview.setText(startpoint + "에서 " + destination + "까지 가는데 걸리는 시간은 : "
//                + time + "초\n약 " + toTime(time) + " 소요됩니다."
//        + "\n총 비용은 : " + expense + "원, 환승 횟수 : " + transfer + "회");

        type.setText("---");
        startTime.setText("---");
        totalTime.setText(toTime(time));
        totalExpense.setText(expense+"원");
        totalDistance.setText(""+transfer);





    }

    public ArrayList modifyPath (ArrayList<String> path){
        ArrayList<String> modifyPath = new ArrayList<>();

        for(int i = 0; i < path.size(); i++) {
            modifyPath.add(path.get(i).toString());
            if (i == path.size()-1) {
                break;
            } else {
                modifyPath.add(" ");
            }
        }

        return modifyPath;
    }

    ///////////////////////테스트////////////////////////////////////////////////////////
    private List<CombinedItem> createCombinedItemList(ArrayList<String> path) {

        List<CombinedItem> combinedItemList = new ArrayList<>();
        TypedArray typedArrayLine = getResources().obtainTypedArray(R.array.routeline_images);

        TypedArray typedArrayNodeStart = getResources().obtainTypedArray(R.array.routenodestart_images);
        TypedArray typedArrayNodeCenter = getResources().obtainTypedArray(R.array.routenodecenter_images);
        TypedArray typedArrayNodeEnd = getResources().obtainTypedArray(R.array.routenodeend_images);

        //첫번째 역에 대한 호선 정보
        int defaultLine = totalLineList.get(1);
        int resourceIdNode = typedArrayNodeStart.getResourceId(defaultLine - 1, 0);
        int resourceIdLine = typedArrayLine.getResourceId(defaultLine - 1, 0);
        combinedItemList.add(new CombinedItem(resourceIdNode, shortest_path.get(0)+"       -        "+totalTimeList.get(0))); // modifyPath에 해당하는 텍스트 추가
        combinedItemList.add(new CombinedItem(resourceIdLine, ""));

        //그 이후의 역에 대해서 이미지 및 텍스트를 결합하여 CombinedItem 추가
        int j=1;
        int k=2;
        for (int i = 3; i < totalLineList.size(); i = i + 2) {
            int compLine = totalLineList.get(i);

            //마지막 역인 경우
            if (compLine == 0) {
                resourceIdNode = typedArrayNodeEnd.getResourceId(defaultLine - 1, 0);
                combinedItemList.add(new CombinedItem(resourceIdNode, shortest_path.get(j)+"       -        "+totalTimeList.get(k-1))); // modifyPath에 해당하는 텍스트 추가
                break;
            }

            // 중간 역인 경우
            if (compLine == defaultLine) {   //이전 역과 같은 경우
                resourceIdNode = typedArrayNodeCenter.getResourceId(compLine - 1, 0);
                resourceIdLine = typedArrayLine.getResourceId(compLine - 1, 0);
                combinedItemList.add(new CombinedItem(resourceIdNode,shortest_path.get(j)+"       -        "+totalTimeList.get(k))); // modifyPath에 해당하는 텍스트 추가
                combinedItemList.add(new CombinedItem(resourceIdLine, ""));
            } else {    //이전 역과 다른 경우 == 환승
                resourceIdNode = typedArrayNodeCenter.getResourceId(compLine - 1, 0);
                resourceIdLine = typedArrayLine.getResourceId(compLine - 1, 0);
                combinedItemList.add(new CombinedItem(resourceIdNode, shortest_path.get(j)+"       -        "+totalTimeList.get(k))); // modifyPath에 해당하는 텍스트 추가
                combinedItemList.add(new CombinedItem(resourceIdLine, ""));
                //기준 값 변경함
                defaultLine = compLine;
            }
            j++;
            k=k+2;
        }
        typedArrayNodeStart.recycle();
        typedArrayNodeCenter.recycle();
        typedArrayNodeEnd.recycle();
        typedArrayLine.recycle();

        return combinedItemList;
    }
    ///////////////////////테스트////////////////////////////////////////////////////////
}

