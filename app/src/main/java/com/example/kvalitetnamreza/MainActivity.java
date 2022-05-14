package com.example.kvalitetnamreza;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private SensorService mSensorService;

    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        //setContentView(R.layout.activity_main);
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if(!isMyServiceRunning(mSensorService.getClass())){
            startService(mServiceIntent);
        }
        Log.i("ONCREATE","Se pokrena");
        finish();
    }

    private boolean isMyServiceRunning(Class<? extends SensorService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                Log.i("isMyServiceRunning?",true+"");
                return true;
            }
        }
        Log.i("isMyServiceRunning?",false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("Stefan","onDestroyMAIN!");
        super.onDestroy();
    }
}