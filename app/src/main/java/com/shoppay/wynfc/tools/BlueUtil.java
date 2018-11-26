package com.shoppay.wynfc.tools;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by songxiaotao on 2017/7/8.
 */

public class BlueUtil {
    public BlueUtil(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//串口全局唯一标识符
    private BluetoothDevice mDevice;//蓝牙设备
    private BluetoothSocket mBluetoothSocket;//蓝牙通信端口
    private OutputStream mOutputStream;//输出数据流

    private static byte[] mData;//接受打印小票数据的字节码

    /**
     * 发送数据
     */
    @SuppressLint("WrongConstant")
    public void send(byte[] data)
    {
        try
        {
//            mDevice = mBluetoothAdapter.getRemoteDevice(preferences.getString("address", ""));
            mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
            mBluetoothSocket.connect();

            mData = data;
            new Thread(mRunnable).start();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
            //            Toast.makeText(BlueToothActivity.this, "连接异常", Toast.LENGTH_SHORT).show();
        }
    }
    private Runnable mRunnable = new Runnable()
    {
        @SuppressLint("WrongConstant")
        @Override
        public void run()
        {
            for (int i = 1; i <= 2; i++)
            {
                try
                {
                    mOutputStream = mBluetoothSocket.getOutputStream();
                    mOutputStream.write(mData, 0, mData.length);
                    mOutputStream.flush();
                    Thread.sleep(3000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
//                    Toast.makeText(BlueToothActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
