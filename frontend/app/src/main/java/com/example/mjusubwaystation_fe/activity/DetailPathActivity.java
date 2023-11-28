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

import java.util.ArrayList;
import java.util.List;

public class DetailPathActivity extends AppCompatActivity {

    private ArrayList<Integer> totalLineList;
    private int time;
    private int startpoint;
    private int destination;
    private ArrayList<String> shortest_path;
    private ArrayList<String>  totalTimeList;
    private int expense;
    private int transfer;
    private TextView startpoint_val, destination_val, expense_val, transfer_val;

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

        //화면에 매칭
        startpoint_val = findViewById(R.id.startpoint_val);
        destination_val = findViewById(R.id.destination_val);
        expense_val = findViewById(R.id.expense_val);
        transfer_val = findViewById(R.id.transfer_val);
        detailListview = findViewById(R.id.detaillistview);

        startpoint_val.setText(""+startpoint);
        destination_val.setText(""+destination);
        expense_val.setText(""+expense);
        transfer_val.setText(""+transfer);
        setPath(shortest_path);
    }



    private List<CombinedItem> createCombinedItemList(ArrayList<String> path) {

        List<CombinedItem> combinedItemList = new ArrayList<>();
        TypedArray typedArrayLine = getResources().obtainTypedArray(R.array.routeline_images);

        TypedArray typedArrayNodeStart = getResources().obtainTypedArray(R.array.routenodestart_images);
        TypedArray typedArrayNodeCenter = getResources().obtainTypedArray(R.array.routenodecenter_images);
        TypedArray typedArrayNodeEnd = getResources().obtainTypedArray(R.array.routenodeend_images);
        int temp = 9;
        int blankIndex = typedArrayLine.getResourceId( temp, 0);

        //totalLineList를 조건에 맞춰 나타내기 위해 갱신함
        ArrayList<Integer> modifyTotalList = modifyTotalLineList(totalLineList);

        //첫번째 역에 대한 호선 정보 ( 변하지 않음 )
        int defaultLine = totalLineList.get(1);
        int resourceIdNode = typedArrayNodeStart.getResourceId(defaultLine - 1, 0);
        int resourceIdLine = typedArrayLine.getResourceId(defaultLine - 1, 0);
        combinedItemList.add(new CombinedItem(resourceIdNode, shortest_path.get(0)+"       -        "+totalTimeList.get(0))); // modifyPath에 해당하는 텍스트 추가
        combinedItemList.add(new CombinedItem(resourceIdLine, ""));

        //그 이후의 역에 대해서 이미지 및 텍스트를 결합하여 CombinedItem 추가
        int k=2;    // 시간에 대한 순서 (나중에 생각)
        for (int i = 3; i < modifyTotalList.size(); i = i + 2) {
            Log.d(TAG, "---------------------------------");
            Log.d(TAG, "현재 i 는 " + i);
            int compLine = modifyTotalList.get(i);
            int station= modifyTotalList.get(i-1);

            Log.d(TAG, "기준 line 는 " + defaultLine);
            Log.d(TAG, "비교 line 는 " + compLine);
            //역 타입을 String으로 변환 ( 화면에 띄우기 위해)
            String stationS = String.valueOf(station);


            //끝 역(line==0)인 경우
            if (compLine == 0) {
                resourceIdNode = typedArrayNodeEnd.getResourceId(defaultLine - 1, 0);
                combinedItemList.add(new CombinedItem(resourceIdNode, stationS));
                Log.d(TAG, "예상 오류 시작 지점 ");
                combinedItemList.add(new CombinedItem(blankIndex, ""));
                Log.d(TAG, "예상 오류 종료 지점 ");
                continue;
            }

            // 중간 역인 경우
            if (compLine == defaultLine) {   //이전 역과 같은 경우
                resourceIdNode = typedArrayNodeCenter.getResourceId(compLine - 1, 0);
                resourceIdLine = typedArrayLine.getResourceId(compLine - 1, 0);
                combinedItemList.add(new CombinedItem(resourceIdNode, stationS));
                combinedItemList.add(new CombinedItem(resourceIdLine, ""));
            } else {    //이전 역과 다른 경우 == 환승
                resourceIdNode = typedArrayNodeStart.getResourceId(compLine - 1, 0);
                resourceIdLine = typedArrayLine.getResourceId(compLine - 1, 0);
                combinedItemList.add(new CombinedItem(resourceIdNode, stationS));
                combinedItemList.add(new CombinedItem(resourceIdLine, ""));
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
        List<CombinedItem> combinedItemList = createCombinedItemList(path);

        // 결합된 데이터를 표시할 어댑터 생성
        CombinedArrayAdapter adapter = new CombinedArrayAdapter(this, android.R.layout.simple_list_item_1, combinedItemList);

        // 결합된 항목을 표시할 단일 ListView 또는 다른 레이아웃 사용
        detailListview.setAdapter(adapter);
    }


    //  전 101, 1, 123, 1, 122 , 2 , ...
    //  후 101, 1, 123, 1, 122 , 0 , 122, 2
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
}