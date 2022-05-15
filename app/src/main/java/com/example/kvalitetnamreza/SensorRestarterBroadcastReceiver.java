package com.example.kvalitetnamreza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Servisot zastana vo BroadcastReceiver");
        context.startService(new Intent(context, SensorService.class));
    }
}