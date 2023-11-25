package com.example.mjusubwaystation_fe.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.service.RouteDTO;

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
    private Button choose_path, btn_dialog, find_path_retry;
    private EditText destination_input, startpoint_input;
    private int alarmHour = 0, alarmMinute = 0, time, startpoint, destination, expense = 0, transfer;
    private String option = "최소시간";
    private ArrayList<String> shortest_path;
    private LinearLayout content;
    private ListView listview1;
    private ListView listview2;
    private String[] filters = {"최소시간", "최소비용", "최단거리"};
    private AlertDialog.Builder builder;
    private TextView show_time;
    private Call<RouteDTO> call;
    private Date now;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.202.63.57:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service1 = retrofit.create(RetrofitInterface.class);

        InputMethodManager keymanager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        now = new Date();

        Callback fun = new Callback<RouteDTO>() {
            @Override
            public void onResponse(Call<RouteDTO> call, Response<RouteDTO> response) {

                if (response.isSuccessful()) {
                    RouteDTO result = response.body();
                    shortest_path = MainActivity.toArrayList(result.getShortestPath());
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

        api_textview = (TextView) findViewById(R.id.textView);
        content = (LinearLayout) findViewById(R.id.content);

        //listView부분
        listview1 = (ListView) findViewById(R.id.listview1);
        listview2 = (ListView) findViewById(R.id.listview2);
        
        btn_dialog = (Button)findViewById(R.id.btn_dialog);
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        show_time = (TextView) findViewById(R.id.Show_Time);
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
                                now.setTime(hourOfDay);
                                now.setMinutes(minute);
                                Toast.makeText(getApplicationContext(), "설정된 시간은 : " + hourOfDay + "시 " + minute + "분입니다.", Toast.LENGTH_SHORT).show();
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
        expense = intent.getIntExtra("expense", 0);
        transfer = intent.getIntExtra("transfer", 0);

        startpoint_input.setText(Integer.toString(startpoint));
        destination_input.setText(Integer.toString(destination));

        setContent();
        choose_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
            }
        });

        find_path_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String startpoint_str = "";
                    startpoint_str = startpoint_input.getText().toString().replaceAll(" ", "");

                    String destination_str = "";
                    destination_str = destination_input.getText().toString().replaceAll(" ", "");

                    //Log.d(TAG,"시작점 : " + startpoint_str + " 도착점 : " + destination_str);

                    startpoint = Integer.parseInt(startpoint_str);
                    destination = Integer.parseInt(destination_str);

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String gettime = format.format(now);

                    call = service1.getStationData(startpoint, destination, option, gettime);// 현재 시간을 디폴트로
                    call.enqueue(fun);

                    keymanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

    private void setPath(ArrayList<String> path){
        ArrayList<Integer> imageResources = new ArrayList<>();

        // 시작 지점의 이미지를 추가
        imageResources.add(R.drawable.routestart);
        // 중간 경로에 대한 이미지를 추가
//        TypedArray typedArray = getResources().obtainTypedArray(R.array.route_images);
//        for (int i = 0; i < shortest_path.size() - 2; i++) {
//            int resourceId = typedArray.getResourceId(i, 0);
//            imageResources.add(resourceId);
//        }
//        typedArray.recycle();


        for (int i = 0; i < shortest_path.size() - 2; i++) {
            imageResources.add(R.drawable.route1);
        }

        // 종료 지점의 이미지를 추가
        imageResources.add(R.drawable.routeend);

        // listview1에 할당될 이미지를 설정
        ImageArrayAdapter adapter1 = new ImageArrayAdapter(this, android.R.layout.simple_list_item_1, imageResources);
        listview1.setAdapter(adapter1);


        //경로 표시 --텍스트
        ArrayAdapter<String> adpater2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shortest_path);
        listview2.setAdapter(adpater2);
    }

    private void showDialog(){
        builder = new AlertDialog.Builder(this);
        builder.setTitle("검색 옵션을 선택하세요 : ");

        //다이얼로그에 리스트 담기
        builder.setItems(filters, new DialogInterface.OnClickListener() {
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
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setContent(){
        setPath(shortest_path);

        api_textview.setText(startpoint + "에서 " + destination + "까지 가는데 걸리는 시간은 : "
                + time + "초\n약 " + toTime(time) + " 소요됩니다."
        + "\n총 비용은 : " + expense + "원, 환승 횟수 : " + transfer + "회");

    }


}




class ImageArrayAdapter extends ArrayAdapter<Integer> {
    private Context context;
    private List<Integer> imageResources;

    public ImageArrayAdapter(Context context, int resource, List<Integer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.imageResources = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(imageResources.get(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // 이미지뷰에 대한 높이 설정
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // 이 부분을 조절하여 이미지의 크기를 변경할 수 있습니다.
        layoutParams.height = 132; // 원하는 크기로 수정

        imageView.setLayoutParams(layoutParams);

        return imageView;
    }
}