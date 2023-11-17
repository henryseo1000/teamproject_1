import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
package com.example.mjusubwaystation_fe.activity;
import static android.content.ContentValues.TAG;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.mjusubwaystation_fe.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.service.TestDTO;
import com.github.chrisbanes.photoview.PhotoView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class pop_up extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_station);

        // 역을 나타내는 버튼
        Button stationButton = findViewById(R.id.stationButton);

        // 팝업에 표시될 내용
        final String popupContent = "역에 대한 정보";

        // 버튼 클릭 이벤트 처리
        stationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 팝업 창 생성
                showPopup(view, popupContent);
            }
        });
    }

    // 팝업 창을 보여주는 메서드
    private void showPopup(View anchorView, String content) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        // 팝업에 표시될 내용 설정
        TextView popupTextView = popupView.findViewById(R.id.popupTextView);
        popupTextView.setText(content);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        // 팝업 창이 닫히지 않도록
        popupWindow.setOutsideTouchable(false);

        // 화면 중앙에 표시
        popupWindow.showAtLocation(anchorView, 0, 0, 0);
    }
}