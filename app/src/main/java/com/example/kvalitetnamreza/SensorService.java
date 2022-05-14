package com.example.kvalitetnamreza;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service {

    public int counter = 0;

    public SensorService(Context applicationContext) {
        super();
        Log.i("Stefan","Tuka sum vo konstruktor na sensorservice!");
    }

    public SensorService(){    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT","onDestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterReceiver.class);
        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    private void startTimer() {
        timer = new Timer();//postavanje nov tajmer
        initializeTimerTask();//Inicijaliziranje na rabota za tajmer
        timer.schedule(timerTask,1000,1000);//rasporedi tajmer da se budi sekoja sekunda
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("In timer", "in timer ++++ " + (counter++));
            }
        };
    }

    private void stopTimerTask() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}