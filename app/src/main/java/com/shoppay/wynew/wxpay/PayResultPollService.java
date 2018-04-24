package com.shoppay.wynew.wxpay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynew.tools.PreferenceHelper;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PayResultPollService extends Service {
    private Intent intent = new Intent("com.example.communication.RECEIVER");
//	1457573655

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public PayResultPollService getService() {
            return PayResultPollService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // TODO 自动生成的方法存根
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO 自动生成的方法存根
        getPayResult();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        String s = PreferenceHelper.readString(getApplicationContext(), "PayOk", "time", "false");
        Log.d("xxService",s);
        if (s.equals("true")) {
            Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.cancel(pi);
        } else {
            int anHour =2000; // 这是一小时的毫秒数 60 * 60 * 1000
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getPayResult() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getApplicationContext());
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("orderNo",PreferenceHelper.readString(getApplicationContext(), "shoppay", "WxOrder", ""));
        Log.d("xxwinxin",params.toString());
        client.post( PreferenceHelper.readString(getApplicationContext(), "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=GetIsPaySuccess", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    Log.d("xxnotifyS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        intent.putExtra("success", "success");
                        sendBroadcast(intent);
                        stopSelf();
                    }else{
                        if(jso.getString("msg").equals("支付失败")){
                        }else {
                            intent.putExtra("success", "false");
                            intent.putExtra("msg", jso.getString("msg"));
                            sendBroadcast(intent);
                            stopSelf();
                        }

                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                try {
                    intent.putExtra("success", "Error");
                    sendBroadcast(intent);
                    stopSelf();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO 自动生成的方法存根
        super.onDestroy();
        Log.d("service", "onDestroy");
    }
}
