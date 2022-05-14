package com.example.kvalitetnamreza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorRestarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(SensorRestarterReceiver.class.getSimpleName(),"Service stops in BroadcastReciever!");
        context.startService(new Intent(context, SensorService.class));
    }
}