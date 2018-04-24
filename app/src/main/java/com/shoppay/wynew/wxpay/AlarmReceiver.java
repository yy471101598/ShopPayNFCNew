package com.shoppay.wynew.wxpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("xx","AlarmReceiver");
        Intent i = new Intent(context, PayResultPollService.class);
        context.startService(i);

    }
}
