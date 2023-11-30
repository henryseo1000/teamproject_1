package com.example.mjusubwaystation_fe.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mjusubwaystation_fe.R;
import com.example.mjusubwaystation_fe.activity.FindPathActivity;

import android.icu.text.SimpleDateFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    String title, message;
    int uniqueId = 0;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 여기에 알림을 보내는 코드 작성
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        // 알림을 클릭했을 때 실행될 액티비티 설정
        Intent intent = new Intent(context, FindPathActivity.class);
        NotificationChannel channel;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        // 시간을 "HH:mm" 포맷으로 변환
        String formattedTime = formatTime(System.currentTimeMillis());

        // 알림 소리 설정
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        channel = new NotificationChannel("CHANNEL_ID" + uniqueId, "CHANNEL_NAME" + uniqueId, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        // 알림 생성
        Notification.Builder notificationBuilder = new Notification.Builder(context, "CHANNEL_ID" + uniqueId)
                .setSmallIcon(R.mipmap.ic_launcher)  // 알림 아이콘
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)  // 알림을 터치하면 자동으로 삭제
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        // 알림 표시
        notificationManager.notify(0, notificationBuilder.build());
    }

    private String formatTime(long timeInMillis) {
        // 시간을 "HH:mm" 포맷으로 변환하는 메서드
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(timeInMillis));
    }
}
