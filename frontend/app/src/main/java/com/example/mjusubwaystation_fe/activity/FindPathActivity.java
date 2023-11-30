package com.example.mjusubwaystation_fe.activity;

import static android.content.ContentValues.TAG;

import static com.example.mjusubwaystation_fe.activity.MainActivity.toArrayListI;
import static com.example.mjusubwaystation_fe.activity.MainActivity.toArrayListS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.net.ParseException;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mjusubwaystation_fe.DTO.AlarmDTO;
import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.service.AlarmReceiver;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.DTO.RouteDTO;

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

public class FindPathActivity extends AppCompatActivity {
    public TextView api_textview, Show_Time_Text, filter;
    private Button choose_path, btn_dialog, find_path_retry, show_time;
    private EditText destination_input, startpoint_input;
    private int alarmHour = 0, alarmMinute = 0, time, startpoint, destination, expense = 0, transfer, distance, uniqueId = 0;
    private String option = "최소시간", setting_time = "", alarmTime;
    private ArrayList<Integer> shortest_path;
    private ArrayList<Integer> totalLineList;
    private ArrayList<String> totalTimeList, alarmList;
    private LinearLayout content;
    private ListView listview2;
    private String[] filters = {"최소시간", "최소비용", "최단거리"};
    private AlertDialog.Builder a_builder;
    private Call<RouteDTO> call_route;
    private Call<RouteDTO> call_alarm;
    private Date now;
    private NotificationManager notificationManager;
    private AlarmManager alarmManager;
    private NotificationCompat.Builder n_builder;
    private RouteDTO result_dto;
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

        Callback path_fun = new Callback<RouteDTO>() {
            @Override
            public void onResponse(Call<RouteDTO> call, Response<RouteDTO> response) {
                if (response.isSuccessful()) {
                    result_dto = response.body();
                    shortest_path = toArrayListI(result_dto.getShortestPath());
                    totalLineList = toArrayListI(result_dto.getTotalLineList());
                    totalTimeList = new ArrayList<>(result_dto.getShortestTime());

                    time = result_dto.getTime();
                    expense = result_dto.getTotalPrice();
                    transfer = result_dto.getTransferCount();
                    distance = result_dto.getDistance();
                    setContent();
                    Log.d(TAG, "성공 : \n" + result_dto.toString());
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

        Callback alarm_fun = new Callback<RouteDTO>() {
            @Override
            public void onResponse(Call<RouteDTO> call, Response<RouteDTO> response) {
                if (response.isSuccessful()) {
                    RouteDTO result = response.body();

                    setAlarm(new ArrayList<>(result.getShortestTime()));

                    showNotification("경로를 선택하셨습니다!", "경로 선택 완료");

                    Intent intent = new Intent(getApplicationContext(), DetailPathActivity.class);
                    intent.putExtra("startpoint", startpoint);
                    intent.putExtra("destination", destination);
                    intent.putExtra("time", time);
                    intent.putExtra("path", shortest_path);
                    intent.putExtra("expense", expense);
                    intent.putExtra("transfer", transfer);

                    startActivity(intent);

                    Log.d(TAG, "성공 : \n" + result.toString());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPathActivity.this);
                    builder.setMessage("통신 오류입니다. 잠시 뒤에 다시 시도해주세요.");
                    builder.setTitle("통신 오류");
                    builder.show();
                    Log.d(TAG, "실패 : \n");
                }
            }
            @Override
            public void onFailure(Call<RouteDTO> call, Throwable t) {
                Log.d(TAG, "onFailure : " + t.getMessage());
            }
        };

        api_textview = (TextView) findViewById(R.id.textView);
        content = (LinearLayout) findViewById(R.id.content);
        listview2 = (ListView) findViewById(R.id.listview2);
        btn_dialog = (Button)findViewById(R.id.btn_dialog);
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
        Show_Time_Text = (TextView) findViewById(R.id.Show_Time_Text);
        filter = (TextView) findViewById(R.id.filter);

        show_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog
                        (FindPathActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                now.setHours(hourOfDay);
                                now.setMinutes(minute);
                                Show_Time_Text.setText("출발 시간 : " + hourOfDay + ":" + minute);
                                Toast.makeText(getApplicationContext(), "설정된 시간은 : " + now.getHours() + "시 " + now.getMinutes() + "분입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }, alarmHour, alarmMinute, false);
                timePickerDialog.show();
            }
        });

        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        startpoint = intent.getIntExtra("startpoint", 0);
        destination = intent.getIntExtra("destination", 0);
        shortest_path = intent.getIntegerArrayListExtra("path");
        totalLineList = intent.getIntegerArrayListExtra("totalLineList");
        totalTimeList = intent.getStringArrayListExtra("totalTimeList");
        expense = intent.getIntExtra("expense", 0);
        transfer = intent.getIntExtra("transfer", 0);
        alarmTime = intent.getStringExtra("alarmTime");
        distance = intent.getIntExtra("distance", 0);
        result_dto = (RouteDTO) intent.getSerializableExtra("result_dto");

        startpoint_input.setText(Integer.toString(startpoint));
        destination_input.setText(Integer.toString(destination));
        Show_Time_Text.setText("출발 시간 : " + alarmTime);
        filter.setText("최소시간");


        setContent();
        choose_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "result_dto : " + result_dto.toString());
                call_alarm = service1.getPathData(startpoint, destination, option, alarmTime);
                call_alarm.enqueue(alarm_fun);
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

                    call_route = service1.getPathData(startpoint, destination, option, gettime);// 현재 시간을 디폴트로
                    call_route.enqueue(path_fun);
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

    private String toTime(int seconds){
        int hours = 0;
        int minutes = 0;
        int left = 0;

        hours = seconds / 3600;
        left = seconds % 3600;

        minutes = left / 60;
        left = left % 60;

        return hours + "시간 " + minutes + "분 "+ left + "초";
    }

    private void setPath(ArrayList<Integer> path){
        List<CombinedItem> combinedItemList = createCombinedItemList(path);

        // 결합된 데이터를 표시할 어댑터 생성
        CombinedArrayAdapter adapter = new CombinedArrayAdapter(this, android.R.layout.simple_list_item_1, combinedItemList);

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
                filter.setText(option);
            }
        });

        AlertDialog alertDialog = a_builder.create();
        alertDialog.show();
    }

    private void setContent(){
        setPath(shortest_path);
        api_textview.setText(startpoint + "에서 " + destination + "까지 가는데 걸리는 시간은 : "
                + time + "초\n약 " + toTime(time) + " 소요됩니다. 거리는 " + distance
        + "\n총 비용은 : " + expense + "원, 환승 횟수 : " + transfer + "회");
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

    private long convertTimeToMillis(String time) {
        try {
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
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    ///////////////////////테스트////////////////////////////////////////////////////////
    private List<CombinedItem> createCombinedItemList(ArrayList<Integer> path) {
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




//////////////////////////////////////////////////////////////////////////////////

class CombinedItem {
    private int imageResource;
    private String text;

    public CombinedItem(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {return text;}
}

class CombinedArrayAdapter extends ArrayAdapter<CombinedItem> {
    private Context context;
    private List<CombinedItem> combinedItems;

    public CombinedArrayAdapter(Context context, int resource, List<CombinedItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.combinedItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);

        ImageView imageView = rowView.findViewById(R.id.imageView);
        TextView textView = rowView.findViewById(R.id.textView_list);

        // Set image and text for the combined item
        CombinedItem combinedItem = combinedItems.get(position);
        imageView.setImageResource(combinedItem.getImageResource());
        textView.setText(combinedItem.getText());

        return rowView;
    }
}