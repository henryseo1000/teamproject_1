package com.example.mjusubwaystation_fe.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mjusubwaystation_fe.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetailPathActivity extends AppCompatActivity {

    private ArrayList<Integer> totalLineList;
    private int time;
    private int startpoint;
    private int destination;
    private ArrayList<String> shortest_path;
    private ArrayList<String>  totalTimeList;
    private ArrayList<String>  alarmTimeList;
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

        //화면의 역 배열에 맞게 시간표 재조정 -------------------------> 되는지 확인하기
        ArrayList<String> modifyTimeList = modifyTimeList(totalTimeList);
        expense_val.setText(modifyTimeList.get(modifyTimeList.size()-1));
        Log.d(TAG, "수정된  total : " + modifyTotalList);
        
        //알람 시간 구하기
        //alarmTimeList = getAlarmTimeList(modifyTimeList);

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

    public String minusMinutes(String time, int minutesToSubtract) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, -minutesToSubtract);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            // 예외 처리: 올바르지 않은 형식의 문자열이 들어온 경우
            return null;
        }
    }


    private ArrayList<String> getAlarmTimeList(ArrayList<String> modifyTimeList){
        ArrayList<String> alarmTimeList = new ArrayList<>();
        int gap = 3;
        for(int i=3;i>=0;i--){
            alarmTimeList.add(minusMinutes(modifyTimeList.get(0),i));
        }

        for(int i=1;i<modifyTimeList.size();i++){
            if (modifyTimeList.get(i-1).equals("0")){
                for(int j=3;i>=0;i--){
                    alarmTimeList.add(modifyTimeList.get(j));
                }
            }
        }
        Log.d(TAG, "시간 리스트  : " + alarmTimeList);
        return alarmTimeList;
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
}