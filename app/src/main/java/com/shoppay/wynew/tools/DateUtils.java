package com.shoppay.wynew.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
/**
 17.     * 掉此方法输入所要转换的时间输入例如（"2014年06月14日16时09分00秒"）返回时间戳
 18.     *
 19.     * @param time
 20.     * @return
 21.     */
     public static String timeTodata(String time) {
           SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd",
                          Locale.CHINA);
               Date date;
            String times = null;
            try {
                      date = sdr.parse(time);
                    long l = date.getTime();
                        String stf = String.valueOf(l);
                     times = stf.substring(0, 10);
                 } catch (Exception e) {
                    e.printStackTrace();
                 }
             return times;
         }
/**
 45.     * 获取当前时间
 46.     *
 47.     * @return
 48.     */
   public static String getCurrentTime_Today() {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          return sdf.format(new java.util.Date());
          }

    /**
     92.     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     93.     *
     94.     * @param time
     95.     * @return
     96.     */
     public static String dataTotime(String time) {
              SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            @SuppressWarnings("unused")
           long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
             String times = sdr.format(new Date(i * 1000L));
          return times;

       }


}