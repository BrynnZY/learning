package com.example.paindiaryapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class myReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "log log log");
        Toast.makeText(context, intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
    }
}
