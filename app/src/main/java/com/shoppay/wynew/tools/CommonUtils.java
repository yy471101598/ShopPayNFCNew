package com.shoppay.wynew.tools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.shoppay.wynew.http.ContansUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
	//�验证手机号是否正确ֻ��
	public static boolean isMobileNO(String mobiles) {
	    Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
	    Matcher m = p.matcher(mobiles);
	    return !m.matches();
	}
	public static String getAppInfo(Context context) {
 		try {
 			String pkName = context.getPackageName();
 			String versionName = context.getPackageManager().getPackageInfo(
 					pkName, 0).versionName;
 			int versionCode = context.getPackageManager()
 					.getPackageInfo(pkName, 0).versionCode;
 			return pkName + "   " + versionName + "  " + versionCode;
 		} catch (Exception e) {
 		}
 		return null;
 	}
	public static String mapToACII(Map<String, Object> map) {
		List<String> list = new ArrayList<String>();
		for (Entry entry : map.entrySet()) {
			list.add(entry.getKey() + entry.getValue().toString());
		}
		Log.d("xxxsi", CommonUtils.listSort(list));
		return listSort(list);
	}

	public static String listSort(List<String> list) {
		String[] array = (String[]) list.toArray(new String[list.size()]); // list转化为数组
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = i + 1; j < array.length; j++) {
				if (singleSort(array[i], array[j]) == 1) {
					String temp = array[i];
					array[i] = array[j];
					array[j] = temp;
				}
			}
		}
		// return Arrays.asList(array); // 数组转化为list
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * 汉字比较
	 *
	 * @param one
	 * @param two
	 * @param i
	 * @return
	 */
	private static int chineseCompare(String one, String two, int i) {
		String substringleft;
		String substringright;
		if (i > 0) {
			substringleft = one.substring(i - 1, i);
			substringright = two.substring(i - 1, i);
		} else {
			substringleft = one.substring(0, i);
			substringright = two.substring(0, i);
		}
		// 获得汉字拼音首字母的ASCII码
		// 把他里面的CharacterParser.convert方法 改成 public static 不然会报错
		int subLeft = stringToAscii(CharacterParser.convert(substringleft)
				.substring(0, 1))[0];
		int subRight = stringToAscii(CharacterParser.convert(substringright)
				.substring(0, 1))[0];
		System.out.println(CharacterParser.convert(substringleft).substring(0,
				1));
		return intCompare(subLeft, subRight);
	}

	/**
	 * 比较两个字符
	 *
	 * @param one
	 * @param two
	 * @return
	 */
	public static int singleSort(String one, String two) {
		int[] left = stringToAscii(one);
		int[] right = stringToAscii(two);
		int size = left.length < right.length ? left.length : right.length;
		for (int i = 0; i < size; i++) {
			// 大于10000说明是汉字 并且在判断一下是否相等 不相等在判断 减少判断次数
			if (left[i] > 10000 && right[i] > 10000 && left[i] != right[i]) {
				if (chineseCompare(one, two, i) != 0) {
					return chineseCompare(one, two, i);
				}
			} else {
				if (intCompare(left[i], right[i]) != 0) {
					return intCompare(left[i], right[i]);
				}
			}
		}
		return intCompare(left.length, right.length);
	}

	/**
	 * 数字比较
	 *
	 * @param subLeft
	 * @param subRight
	 * @return
	 */
	private static int intCompare(int subLeft, int subRight) {
		if (subLeft > subRight) {
			return 1;
		} else if (subLeft < subRight) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 获得ASCII码
	 *
	 * @param value
	 * @return
	 */
	public static int[] stringToAscii(String value) {
		char[] chars = value.toCharArray();
		int j = chars.length;
		int[] array = new int[j];
		for (int i = 0; i < chars.length; i++) {
			array[i] = (int) chars[i];
		}
		return array;
	}

	public static String obtainApiSign(Context context, String api,
			Object object, String time) {
		Gson gson = new Gson();
		StringBuffer sb = new StringBuffer();
		sb.append("UserLogin");
		sb.append(gson.toJson(object));
		sb.append(CommonUtils.getVersionName(context));
		sb.append(time);
		Log.d("xxapiSign", gson.toJson(object));
		Log.d("xxapimd5", CommonUtils.md5(sb.toString()).toUpperCase());
		return CommonUtils.md5(sb.toString()).toUpperCase();
	}

	/**
	 * 获取本地缓存的图片
	 */
	public static Bitmap getBitmap2(String fileName, Context cxt) {
		String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
		FileInputStream fis = null;
		try {
			fis = cxt.openFileInput(bitmapName);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			fis.close();
			Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			// 这里是读取文件产生异常
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// fis流关闭异常
					e.printStackTrace();
				}
			}
		}
		// 读取产生异常，返回null
		return null;
	}

	/**
	 * 判断本地的私有文件夹里面是否存在当前名字的文件
	 */
	public static boolean isFileExist(String fileName, Context cxt) {
		String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
		List<String> nameLst = Arrays.asList(cxt.fileList());
		if (nameLst.contains(bitmapName)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 把图片保存到本地
	 */
	public static void saveBitmap2(Bitmap mBitmap, String imageURL, Context cxt) {

		String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);

		FileOutputStream fos = null;

		try {
			fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			// 这里是保存文件产生异常
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// fos流关闭异常
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 判断网络是否连接
	 */
	public static boolean checkNet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null;// 网络是否连接
	}

	public static String obtainUrl(Context context, Map<String, Object> map,
			String url) {
		StringBuffer sb = new StringBuffer();
		sb.append(ContansUtils.BASE_URL + url + "?");
		Iterator<Entry<String, Object>> iter = CommonUtil.addSign(context, map)
				.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = (Entry<String, Object>) iter
					.next();
			// 拼接
			sb.append(entry.getKey() + "=");
			sb.append(entry.getValue() + "&");
		}
		return sb.toString();
	}


	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				/*
				 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
				 * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
				Log.i("xx", "此appimportace =" + appProcess.importance
						+ ",context.getClass().getName()="
						+ context.getClass().getName());
				if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i("xx", "处于后台" + appProcess.processName);
					return true;
				} else {
					Log.i("xx", "处于前台" + appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	public static boolean activityIs(Context context) {
		Intent intent = new Intent();
		intent.setClassName("com.yk.wy", "com.yk.wy.HostActivity");
		boolean isAppRunning = false;
		if (intent.resolveActivity(context.getPackageManager()) == null) {
			// 说明系统中不存在这个activity
			isAppRunning = false;
		} else {
			isAppRunning = true;
		}
		return isAppRunning;
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param v1
	 * @param v2
	 * @return 两个参数的和
	 */

	public static double add(double v1, double v2)

	{

		BigDecimal b1 = new BigDecimal(Double.toString(v1));

		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.add(b2).doubleValue();

	}

	/**
	 * 去除空格
	 *
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 提供精确的乘法运算
	 *
	 * @param v1
	 * @param v2
	 * @return 两个参数的数学积，以字符串格式返回
	 */

	public static String multiply(String v1, String v2)

	{

		BigDecimal b1 = new BigDecimal(replaceBlank(v1));

		BigDecimal b2 = new BigDecimal(replaceBlank(v2));

		return b1.multiply(b2).toString();

	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		if(v2==0){
			return  0;
		}else{
			BigDecimal b1 = new BigDecimal(Double.toString(v1));
			BigDecimal b2 = new BigDecimal(Double.toString(v2));
			return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
	public static double del(double d1,double d2){
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.subtract(bd2).doubleValue();
	}
	/**
	 * 得到当前系统上个月的时间
	 *
	 * @return
	 */
	public static String getAgoDate(int num) {
		Calendar c = Calendar.getInstance();
		// c.add(Calendar.MONTH, -1);
		c.add(Calendar.MONTH, num);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		return df.format(c.getTime());// new Date()为获取当前系统时间
	}

	/**
	 * 得到当前时间上个月的时间
	 *
	 * @return
	 */
	public static String getAgoDate(String time, int num) {
		String[] ti = time.split("/");
		// java月份是从0-11,月份设置时要减1.
		Calendar c = new GregorianCalendar(Integer.parseInt(ti[0]),
				Integer.parseInt(ti[1]) - 1, Integer.parseInt(ti[2]));
		// c.add(Calendar.MONTH, -1);
		c.add(c.MONTH, num);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		return df.format(c.getTime());// new Date()为获取当前系统时间
	}

	public static boolean checkWifiConnect(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static String getLocalIpAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAddress = wifiInfo.getIpAddress();

		// 返回整型地址转换成“*.*.*.*”地址
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}

	public static String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void playMusic(File file) {

		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024 * 1024 * 2];// 2M
			int len = fis.read(buffer);
			int pcmlen = 0;
			pcmlen += buffer[0x2b];
			pcmlen = pcmlen * 256 + buffer[0x2a];
			pcmlen = pcmlen * 256 + buffer[0x29];
			pcmlen = pcmlen * 256 + buffer[0x28];

			int channel = buffer[0x17];
			channel = channel * 256 + buffer[0x16];

			int bits = buffer[0x23];
			bits = bits * 256 + buffer[0x22];
			AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
					channel, AudioFormat.ENCODING_PCM_16BIT, pcmlen,
					AudioTrack.MODE_STATIC);
			at.write(buffer, 0x2C, pcmlen);
			at.play();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除保存于手机上的缓存
	 */

	// clear the cache before time numDays
	public static int clearCacheFolder(File dir, long numDays) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}
					if (child.lastModified() < numDays) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 清除cookie
	 */

	public static void clearCookies(Context context) {
		// Edge case: an illegal state exception is thrown if an instance of
		// CookieSyncManager has not be created. CookieSyncManager is normally
		// created by a WebKit view, but this might happen if you start the
		// app, restore saved state, and click logout before running a UI
		// dialog in a WebView -- in which case the app crashes
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * 获取软件版本号
	 *
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		String versionCode = 1 + "";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					ContansUtils.PACKAGE, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return String.valueOf(versionCode);
	}

	// 以下是获得版本信息的工具方法

	// 版本名
	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	// 版本号
	public static int getVersionCode2(Context context) {
		return getPackageInfo(context).versionCode;
	}

	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;

		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);

			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pi;
	}

	/**
	 * 检测当的网络（WLAN、3G/2G）状态
	 *
	 * @param context
	 *            Context
	 * @return true 表示网络可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断某个界面是否在前台
	 *
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	public static boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static boolean wifiIsUsed(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo != null && wifiNetworkInfo.isAvailable()) {

			return true;
		}
		return false;
	}

	/**
	 * MD5加密
	 */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	// public static String Base64ss(String string) {
	// // String base64Token =
	// // android.util.Base64.encodeToString(string.trim().getBytes(),
	// // Base64.NO_WRAP);
	// // byte[] mmmm = Base64.decode(base64Token,Base64.DEFAULT);
	// // return new String(mmmm);
	// return Base64.encode(string);
	// //
	// // byte[] buffer=string.trim().getBytes();
	// // return Base64.encodeToString(buffer, 0, buffer.length,
	// // Base64.DEFAULT);
	// }
	public static String lasttwo(double money) {

		java.math.BigDecimal bigDec = new java.math.BigDecimal(money);

		double total = bigDec.setScale(2, java.math.BigDecimal.ROUND_HALF_UP)

				.doubleValue();

		DecimalFormat df = new DecimalFormat("0.00");

		return df.format(total);

	}
}
