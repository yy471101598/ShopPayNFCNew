package com.shoppay.wynew.tools;


        import android.os.Handler;
        import android.os.Message;
        import android.os.RemoteException;
        import android.util.Log;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.shoppay.wynew.MyApplication;
        import com.sunmi.pay.hardware.aidl.bean.CardInfo;
        import com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback;

/**
 * pos机器卡片的操作
 * iauthor：Yc
 * date: 2017/7/6 10:44
 * email：jasoncheng9111@gmail.com
 */
public class CardOperationUtils {
    private TextView mView;
    private EditText mEditText;
    private Handler handler_sunmi_ReadCard = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                   CardInfo cardInfo= (CardInfo) msg.obj;
                    mEditText.setText(cardInfo.cardNo);
                    break;
                case 2:
                    CardInfo card= (CardInfo) msg.obj;
                    mEditText.setText(String.format("%010d",Long.parseLong(card.uuid,16)));
                    break;
            }
        }
    };


    public CardOperationUtils()
    {

    }

    public CardOperationUtils(EditText editText)
    {
        this.mEditText = editText;

        /**
         * SUNMI
         */
        handler_sunmi_ReadCard.post(runnable_sunmi_ReadCard);

    }


    /**
     * SUNMI 检卡 读卡
     */
    private Runnable runnable_sunmi_ReadCard = new Runnable()
    {
        @Override
        public void run()
        {
                try
                {
                    /**
                     * 7：磁条卡、IC卡、NFC卡
                     * 5：超时 秒为单位
                     */
                   MyApplication.sReadCardOpt.readCard(7, readCardCallback, 100);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
    };

    /**
     * SUNMI检卡回调
     * 刷磁条卡、IC卡、插卡、RF卡、M1卡、挥卡感应
     */
    private ReadCardCallback readCardCallback = new ReadCardCallback.Stub()
    {

        @Override
        public void onStartReadCard() throws RemoteException
        {
        }

        /**
         * 刷卡操作的回调
         * @param cardInfo
         * @throws RemoteException
         */
        @Override
        public void onFindMAGCard(final CardInfo cardInfo) throws RemoteException
        {
            Log.d("TAG", "onFindMAGCard成功");
//            new VoiceUtils().sound();
//            runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    mEditText.setText(cardInfo.cardNo);
//
//                }
//            });
            Message msg=handler_sunmi_ReadCard.obtainMessage();
            msg.what=1;
            msg.obj=cardInfo;
            handler_sunmi_ReadCard.sendMessage(msg);
            handler_sunmi_ReadCard.postDelayed(runnable_sunmi_ReadCard, 100);

        }


        /**
         * 感应卡操作回调
         * @param cardInfo
         * @throws RemoteException
         */
        @Override
        public void onFindNFCCard(final CardInfo cardInfo) throws RemoteException
        {

            Log.d("TAG", "onFindNFCCard成功");
            Message msg=handler_sunmi_ReadCard.obtainMessage();
            msg.what=2;
            msg.obj=cardInfo;
            handler_sunmi_ReadCard.sendMessage(msg);
            handler_sunmi_ReadCard.postDelayed(runnable_sunmi_ReadCard, 100);
        }

        /**
         * 插IC卡操作回调
         * @param cardInfo
         * @throws RemoteException
         */
        @Override
        public void onFindICCard(final CardInfo cardInfo) throws RemoteException
        {
            Log.d("TAG", "onFindICCard成功");
            Message msg=handler_sunmi_ReadCard.obtainMessage();
            msg.what=1;
            msg.obj=cardInfo;
            handler_sunmi_ReadCard.sendMessage(msg);
            handler_sunmi_ReadCard.postDelayed(runnable_sunmi_ReadCard, 100);
        }

        /**
         * 读卡失败
         * @param i
         * @throws RemoteException
         */
        @Override
        public void onError(int i) throws RemoteException
        {
            Log.d("TAG", "读卡失败");
            handler_sunmi_ReadCard.postDelayed(runnable_sunmi_ReadCard, 100);
        }

        /**
         * 读卡超时
         * @throws RemoteException
         */
        @Override
        public void onTimeOut() throws RemoteException
        {
            Log.d("TAG", "读卡超时");
            handler_sunmi_ReadCard.postDelayed(runnable_sunmi_ReadCard, 100);
        }
    };


    /**
     * 关闭所有pos检卡读卡程序
     */
    public void close()
    {
        try
        {
            /**
             * SUNMI pos
             */
            if (  MyApplication.sReadCardOpt != null)
            {
                MyApplication.sReadCardOpt.cancelCheckCard();
                handler_sunmi_ReadCard.removeCallbacks(runnable_sunmi_ReadCard);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
