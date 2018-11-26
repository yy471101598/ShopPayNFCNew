package com.shoppay.wynfc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.tools.DialogUtil;

/**
 * Created by songxiaotao on 2017/7/5.
 */

public class TestActivity extends Activity {
    private Button bt;
    private TextView tv;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    tv.setText(msg.obj.toString());
                    break;
                case 2:
                    DialogUtil.textDialog(TestActivity.this, 1, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message=handler.obtainMessage();
                                    message.what=3;
                                    message.obj="备份2";
                                    handler.sendMessage(message);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message message1=handler.obtainMessage();
                                    message1.what=4;
                                    message1.obj="备份3";
                                    handler.sendMessage(message1);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message message2=handler.obtainMessage();
                                    message2.what=5;
                                    message2.obj="完成";
                                    handler.sendMessage(message2);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });
                    break;
                case 3:
                    tv.setText(msg.obj.toString());
                    break;
                case 4:
                    tv.setText(msg.obj.toString());
                    break;
                case 5:
                    tv.setText(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        bt= (Button) findViewById(R.id.bt);
        tv= (TextView) findViewById(R.id.tv);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message=handler.obtainMessage();
                        message.what=1;
                        message.obj="备份1";
                        handler.sendMessage(message);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message1=handler.obtainMessage();
                        message1.what=2;
                        handler.sendMessage(message1);

                    }
                }).start();
            }
        });

    }
}
