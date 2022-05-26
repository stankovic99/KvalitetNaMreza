package com.example.kvalitetnamreza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private SensorService mSensorService;
    private Button povikajBackend;
    private Button pratiDoBackend;
    private NotificationManager mNotifyManager;
    private static final String PROMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;
    private static final String  ACTION_UPDATE_NOTIFICATION = "com.example.kvalitetnamreza.ACTION_UPDATE_NOTIFICATION";
    private NotificationReceiver mNotifyReciever = new NotificationReceiver();
    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        povikajBackend = findViewById(R.id.povikajBackend);
        pratiDoBackend = findViewById(R.id.pratiDoBackend);

        setNotificationButtonState(true,false);

        povikajBackend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationButtonState(false,true);
                try {
                    getJob(1);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        pratiDoBackend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationButtonState(true,false);
                try {
                    getJob(2);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        createNotificationChannel();
        registerReceiver(mNotifyReciever,new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        ctx = this;
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }

    private void setNotificationButtonState(boolean povikaj, boolean isprati) {
        povikajBackend.setEnabled(povikaj);
        pratiDoBackend.setEnabled(isprati);
    }

    private void getJob(Integer broj) throws ExecutionException, InterruptedException {
        ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if(connMng != null){
            networkInfo = connMng.getActiveNetworkInfo();
        }
        if (networkInfo!=null && networkInfo.isConnected()){
            if (broj == 1){
                String job = new FetchJob().execute("Get").get();
                sendNotification(job);
            }else if(broj == 2){
                new FetchJob().execute("Post");
                updateNotification();
            }
        }else{
            Log.i("NoConnection","Nema konekcija");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        unregisterReceiver(mNotifyReciever);
        Log.i("onDestroy", "onDestroy! vo MainActivity");
        super.onDestroy();
    }

    private void createNotificationChannel() {
        mNotifyManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PROMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot channel");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private void updateNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilderUpdate();
        Notification notification = notifyBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID,notification);
        setNotificationButtonState(true,false);
    }

    private void sendNotification(String job) {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilderSend(job);
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.addAction(R.drawable.ic_baseline_update_24,"Прати до backend",updatePendingIntent);
        Notification notification = notifyBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID, notification);
    }

    private NotificationCompat.Builder getNotificationBuilderSend(String job) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PROMARY_CHANNEL_ID);
        notifyBuilder.setContentTitle("Известување")
                .setContentText("Добиената работа од backend e: " + job)
                .setSmallIcon(R.drawable.ic_baseline_android_24)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return  notifyBuilder;
    }

    private NotificationCompat.Builder getNotificationBuilderUpdate() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PROMARY_CHANNEL_ID);
        notifyBuilder.setContentTitle("Известување")
                .setContentText("Успешно испраќање до backend")
                .setSmallIcon(R.drawable.ic_baseline_android_24)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return  notifyBuilder;
    }

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}