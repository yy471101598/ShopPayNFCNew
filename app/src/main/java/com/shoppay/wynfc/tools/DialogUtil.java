package com.shoppay.wynfc.tools;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynfc.MyApplication;
import com.shoppay.wynfc.R;
import com.shoppay.wynfc.adapter.DengjiChoseAdapter;
import com.shoppay.wynfc.adapter.TimeChoseAdapter;
import com.shoppay.wynfc.bean.Dengji;
import com.shoppay.wynfc.bean.JifenDk;
import com.shoppay.wynfc.bean.ShopCar;
import com.shoppay.wynfc.bean.VipPayMsg;
import com.shoppay.wynfc.db.DBAdapter;
import com.shoppay.wynfc.http.ContansUtils;
import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.view.wheelview.OnWheelChangedListener;
import com.shoppay.wynfc.view.wheelview.OnWheelScrollListener;
import com.shoppay.wynfc.view.wheelview.WheelView;
import com.shoppay.wynfc.view.wheelview.adapter.NumericWheelAdapter;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DialogUtil {
	public final static int DATE_DIALOG = 0;
	public final static int TIME_DIALOG = 1;
	private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
	private static String roomstate = "所有";
	private static String hotelinstate = "在住";
	private static String recordstate = "所有";
	public static int curYear;
	public static int curMonth;
	public static int curDate;
	public static  WheelView wl_start_year;
	public static WheelView wl_start_month;
	public static WheelView wl_start_day;
	public static TextView tv_start_time;
	public static  boolean isMoney=true,isYue=false,isJifen=false,isWx=false;
	public static double money = 0;
	public static double yue = 0;
	public static double jifen = 0;
	public static double dkmoney = 0;
	private static boolean isPwdCanClick=true;
	// 显示缺失权限提示
	public static void showMissingPermissionDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("温馨提示");
		builder.setMessage("当前应用缺少摄像头权限，请到设置界面打开摄像头权限");

		// 拒绝, 退出应用
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startAppSettings(context);
			}
		});

		builder.show();
	}
//	/**
//	 * 日期选择器
//	 *
//	 * @param context
//	 * @param showingLocation 0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
//	 */
//	public static Dialog testDialog(final Context context,
//									   int showingLocation, final InterfaceBack handler) {
//		final Dialog dialog;
//		LayoutInflater inflater = LayoutInflater.from(context);
//		View view = inflater.inflate(R.layout.dialog_datechose, null);
//		wl_start_year= (WheelView) view.findViewById(R.id.wl_year);
//		wl_start_month= (WheelView) view.findViewById(R.id.wl_month);
//		wl_start_day= (WheelView) view.findViewById(R.id.wl_day);
//		tv_start_time= (TextView) view.findViewById(R.id.dialog_time);
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		final String date = sDateFormat.format(new java.util.Date());
//		tv_start_time.setText(date);
//		RelativeLayout rl_confirm= (RelativeLayout) view.findViewById(R.id.rl_confirm);
//		RelativeLayout rl_today= (RelativeLayout) view.findViewById(R.id.rl_today);
//		RelativeLayout rl_cancel= (RelativeLayout) view.findViewById(R.id.rl_cancle);
//		dialog = new Dialog(context, R.style.DialogNotitle1);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(true);
//		int screenWidth = ((WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//				.getWidth();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(
//				screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
//		dialog.show();
//
//		rl_cancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				dialog.dismiss();
//			}
//		});
//		rl_confirm.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				pwdDialog(type,context,1,handler);
//			}
//		});
//		rl_today.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				dialog.dismiss();
//			}
//		});
//		Window window = dialog.getWindow();
//		switch (showingLocation) {
//			case 0:
//				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
//				break;
//			case 1:
//				window.setGravity(Gravity.CENTER);
//				break;
//			case 2:
//				window.setGravity(Gravity.BOTTOM);
//				break;
//			case 3:
//				WindowManager.LayoutParams params = window.getAttributes();
//				dialog.onWindowAttributesChanged(params);
//				params.x = screenWidth-dip2px(context,100);// 设置x坐标
//				params.gravity = Gravity.TOP;
//				params.y = dip2px(context, 45);// 设置y坐标
//				Log.d("xx", params.y + "");
//				window.setGravity(Gravity.TOP);
//				window.setAttributes(params);
//				break;
//			default:
//				window.setGravity(Gravity.CENTER);
//				break;
//		}
//		return dialog;
//	}
	public static Dialog
	pwdDialogDialog(final String type,final Context context,
										 int showingLocation, final InterfaceBack handle, final VipPayMsg vipPayMsg) {
		final Dialog dialog ;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_pwd, null);
		RelativeLayout rl_confirm= (RelativeLayout) view.findViewById(R.id.pwd_rl_confirm);
		final EditText et_pwd= (EditText) view.findViewById(R.id.pwd_et_pwd);

		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();
		isPwdCanClick=true;
		rl_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(isPwdCanClick) {
					isPwdCanClick = false;
					vippwdchecked(type, context, et_pwd.getText().toString(), handle, dialog, true, vipPayMsg);
				}
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
		return dialog;
	}

	public static Dialog pwdDialog(final String type,final Context context,
								   int showingLocation, final InterfaceBack handle) {
		final Dialog dialog ;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_pwd, null);
		RelativeLayout rl_confirm= (RelativeLayout) view.findViewById(R.id.pwd_rl_confirm);
		final EditText et_pwd= (EditText) view.findViewById(R.id.pwd_et_pwd);

		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();
       isPwdCanClick=true;
		rl_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(isPwdCanClick) {
					isPwdCanClick = false;
					vippwdchecked(type, context, et_pwd.getText().toString(), handle, dialog, false, null);
				}
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
		return dialog;
	}

	public static void vippwdchecked(final String type,final Context ac, String pwd, final InterfaceBack handle, final Dialog dialog, final boolean isDialog,final VipPayMsg msg) {
		AsyncHttpClient client = new AsyncHttpClient();
		final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
		client.setCookieStore(myCookieStore);
		RequestParams params = new RequestParams();
		params.put("MemCard", PreferenceHelper.readString(ac, "shoppay", "vipcar", "123"));
		params.put("Pwd",pwd);
		Log.d("xxx",params.toString());
		client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppChkMemPwd", params, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				try {
					Log.d("xxvippwdS",new String(responseBody,"UTF-8"));
					JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
					if(jso.getBoolean("success")){
						if(isDialog){
							dialog.dismiss();
							jiesuan(type,true,handle,dialog,ac,msg);
						}else {
							handle.onResponse("");
							dialog.dismiss();
						}
					}else{
						isPwdCanClick=true;
						Toast.makeText(ac,jso.getString("msg"),Toast.LENGTH_SHORT).show();
					}
				}catch (Exception e){
					isPwdCanClick=true;
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				isPwdCanClick=true;
			}
		});
	}

	// 显示切换小区提示
	public static void showChangeComDialog(final Context context, String comname) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("温馨提示");
		builder.setMessage("查看该条消息需要切换到" + comname + ",是否进行切换？");

		// 拒绝, 退出应用
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startAppSettings(context);
			}
		});

		builder.show();
	}

	// 启动应用的设置
	public static void startAppSettings(Context context) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse(PACKAGE_URL_SCHEME + ContansUtils.WXSHARE));
		context.startActivity(intent);
		// Intent intent=new Intent()
	}
	private static void initWheelView(final Context context) {
		Calendar c = Calendar.getInstance();
		 curYear = c.get(Calendar.YEAR);
		  curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		curDate = c.get(Calendar.DATE);

		NumericWheelAdapter numericWheelAdapterStart1=new NumericWheelAdapter(context,1900, 2100);
		numericWheelAdapterStart1.setLabel(" ");
		wl_start_year.setViewAdapter(numericWheelAdapterStart1);
		numericWheelAdapterStart1.setTextColor(R.color.black);
		numericWheelAdapterStart1.setTextSize(20);
		wl_start_year.setCyclic(true);//是否可循环滑动
		wl_start_year.addScrollingListener(startScrollListener);

		wl_start_year.setCurrentItem(curYear - 1900);
		wl_start_year.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				curYear = newValue+2000;
				initStartDayAdapter(context);
			}
		});

		NumericWheelAdapter numericWheelAdapterStart2=new NumericWheelAdapter(context,1, 12, "%02d");
		numericWheelAdapterStart2.setLabel(" ");
		wl_start_month.setViewAdapter(numericWheelAdapterStart2);
		numericWheelAdapterStart2.setTextColor(R.color.black);
		numericWheelAdapterStart2.setTextSize(20);
		wl_start_month.setCyclic(true);
		wl_start_month.addScrollingListener(startScrollListener);
		wl_start_month.setCurrentItem(curMonth-1);
		wl_start_month.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				curMonth = newValue+1;
				initStartDayAdapter(context);
			}
		});
		initStartDayAdapter(context);
	}
	private static void initStartDayAdapter(Context context){
		NumericWheelAdapter numericWheelAdapterStart3=new NumericWheelAdapter(context,1,getDay(curYear,curMonth), "%02d");
		numericWheelAdapterStart3.setLabel(" ");
		wl_start_day.setViewAdapter(numericWheelAdapterStart3);
		numericWheelAdapterStart3.setTextColor(R.color.black);
		numericWheelAdapterStart3.setTextSize(20);
		wl_start_day.setCyclic(true);
		wl_start_day.addScrollingListener(startScrollListener);
		wl_start_day.setCurrentItem(curDate-1);
	}
	/**
	 * 根据年月获得 这个月总共有几天
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
			case 0:
				flag = true;
				break;
			default:
				flag = false;
				break;
		}
		switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = 31;
				break;
			case 2:
				day = flag ? 29 : 28;
				break;
			default:
				day = 30;
				break;
		}
		return day;
	}
	static OnWheelScrollListener startScrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
		}
		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = wl_start_year.getCurrentItem() + 1900;//年
			int n_month = wl_start_month.getCurrentItem() + 1;//月
			int n_day = wl_start_day.getCurrentItem() + 1;//日
			String month=String.valueOf(n_month);
			if(n_month<10){
				month="0"+month;
			}
			String day=String.valueOf(n_day);
			if(n_day<10){
				day="0"+day;
			}
			tv_start_time.setText(n_year+"-"+month+"-"+day);
		}
	};

	/**
	 * 日期选择器
	 *
	 * @param context
	 * @param showingLocation 0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
	 */
	public static void dateChoseDialog(final Context context,
									   int showingLocation, final InterfaceBack handler) {
		final Dialog dialog;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_datechose, null);
		wl_start_year= (WheelView) view.findViewById(R.id.wl_year);
		wl_start_month= (WheelView) view.findViewById(R.id.wl_month);
		wl_start_day= (WheelView) view.findViewById(R.id.wl_day);
		tv_start_time= (TextView) view.findViewById(R.id.dialog_time);
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final String date = sDateFormat.format(new java.util.Date());
		tv_start_time.setText(date);
		RelativeLayout rl_confirm= (RelativeLayout) view.findViewById(R.id.rl_confirm);
		RelativeLayout rl_today= (RelativeLayout) view.findViewById(R.id.rl_today);
		RelativeLayout rl_cancel= (RelativeLayout) view.findViewById(R.id.rl_cancle);
		initWheelView(context);
		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();

		rl_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		rl_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				handler.onResponse(tv_start_time.getText().toString());
			}
		});
		rl_today.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				handler.onErrorResponse(date);
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
	}

	/**
	 * 时间选择
	 *
	 * @param context
	 * @param showingLocation 0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
	 */
	public static void timeChoseDialog(final Context context,
										  int showingLocation, final InterfaceBack handler) {
		final Dialog dialog;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_timechose, null);
		ListView listview = (ListView) view.findViewById(R.id.listview_timechose);
		final List<String> list=new ArrayList<>();
		list.add("昨天");
		list.add("7天");
		list.add("本月");
		list.add("30天");
		TimeChoseAdapter carListAdapter = new TimeChoseAdapter(context, list);
		listview.setAdapter(carListAdapter);
		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				dip2px(context,100), LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				handler.onResponse(list.get(position));
				dialog.dismiss();
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
	}

	/**
	 * 时间选择
	 *
	 * @param context
	 * @param showingLocation 0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
	 */
	public static void dengjiChoseDialog(final Context context,final List<Dengji> list,
									   int showingLocation, final InterfaceBack handler) {
		final Dialog dialog;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_timechose, null);
		ListView listview = (ListView) view.findViewById(R.id.listview_timechose);
		DengjiChoseAdapter carListAdapter = new DengjiChoseAdapter(context, list);
		listview.setAdapter(carListAdapter);
		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				handler.onResponse(list.get(position));
				dialog.dismiss();
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
	}
	public static Dialog loadingDialog(final Context context,
										 int showingLocation) {
		final Dialog dialog;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_loading, null);
		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
		return dialog;
	}


	public static Dialog jiesuanDialog(final String type,final Context context,
								  int showingLocation,final boolean isVip,final double yfmoney,final double xfmoney,final double jf ,final InterfaceBack handler) {
		final Dialog dialog;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_shoppay, null);
		final EditText et_zfmoney = (EditText) view.findViewById(R.id.shoppay_et_money);
		final EditText et_yuemoney = (EditText) view.findViewById(R.id.shoppay_et_yue);
		final EditText et_jfmoney = (EditText) view.findViewById(R.id.shoppay_et_jifen);

		final TextView tv_yfmoney = (TextView) view.findViewById(R.id.shoppay_tv_yfmoney);
		final TextView tv_maxdk= (TextView) view.findViewById(R.id.shoppay_tv_maxdk);
		final TextView tv_dkmoney = (TextView) view.findViewById(R.id.shoppay_tv_dkmoney);
		final TextView tv_money = (TextView) view.findViewById(R.id.tv_money);
		final TextView tv_yue = (TextView) view.findViewById(R.id.tv_yue);
		final TextView tv_jf = (TextView) view.findViewById(R.id.tv_jifen);
		final TextView tv_wx = (TextView) view.findViewById(R.id.tv_wx);
		final TextView tv_jiesuan = (TextView) view.findViewById(R.id.tv_jiesuan);
		final RelativeLayout rl_wx= (RelativeLayout) view.findViewById(R.id.rl_wx);
		final RelativeLayout rl_yue= (RelativeLayout) view.findViewById(R.id.rl_yue);
		final RelativeLayout rl_money= (RelativeLayout) view.findViewById(R.id.rl_money);
		final RelativeLayout rl_jifen= (RelativeLayout) view.findViewById(R.id.rl_jifen);
		final RelativeLayout rl_jiesuan= (RelativeLayout) view.findViewById(R.id.shoppay_rl_jiesuan);
		final RelativeLayout rl_pay_money= (RelativeLayout) view.findViewById(R.id.shoppay_rl_money);
		final RelativeLayout rl_pay_jifen= (RelativeLayout) view.findViewById(R.id.shoppay_rl_jifen);
		final RelativeLayout rl_pay_jifendkm= (RelativeLayout) view.findViewById(R.id.shoppay_rl_jfdk);
		final RelativeLayout rl_pay_jifenmaxdk= (RelativeLayout) view.findViewById(R.id.shoppay_rl_maxdk);
		final RelativeLayout rl_pay_yue= (RelativeLayout) view.findViewById(R.id.shoppay_rl_yue);
		isMoney=true;isYue=false;isJifen=false;isWx=false;
		tv_yfmoney.setText(StringUtil.twoNum(yfmoney+""));
		obtainJifenDkMoney(xfmoney,context);
		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-10, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();


		et_zfmoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.toString().equals("")) {

				} else {
					if (tv_yfmoney.getText().toString().equals("") || tv_yfmoney.getText().toString().equals("获取中")) {
						Toast.makeText(context, "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
						et_zfmoney.setText("");
					} else {
						if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
							money = 0;
						} else {
							money = Double.parseDouble(editable.toString());
						}
						if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
							jifen = 0;
							dkmoney = 0;
							tv_dkmoney.setText("0");
						} else {
							jifen = Double.parseDouble(et_jfmoney.getText().toString());
							dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "jifenpercent", "1"));
							tv_dkmoney.setText(dkmoney + "");
						}
						if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
							yue = 0;
						} else {
							yue = Double.parseDouble(et_yuemoney.getText().toString());
						}
						if (Double.parseDouble(tv_yfmoney.getText().toString()) - yue - dkmoney - money < 0) {
							et_zfmoney.setText("");
							Toast.makeText(context, "超出折后金额，请减少支付金额", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});

		et_yuemoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.toString().equals("")) {

				} else {
					if (tv_yfmoney.getText().toString().equals("") || tv_yfmoney.getText().toString().equals("获取中")) {
						Toast.makeText(context, "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
						et_yuemoney.setText("");
					} else {
						if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
							yue = 0;
						} else {
							yue = Double.parseDouble(et_yuemoney.getText().toString());
						}
						if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
							jifen = 0;
							dkmoney = 0;
							tv_dkmoney.setText("0");
						} else {
							jifen = Double.parseDouble(et_jfmoney.getText().toString());
							dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "jifenpercent", "1"));
							tv_dkmoney.setText(dkmoney + "");
						}
						if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
							money = 0;
						} else {
							money = Double.parseDouble(et_zfmoney.getText().toString());
						}
						if (Double.parseDouble(tv_yfmoney.getText().toString()) - yue - dkmoney - money < 0) {
							Toast.makeText(context, "超出折后金额，请减少余额", Toast.LENGTH_SHORT).show();
							et_yuemoney.setText("");
						}
					}
				}
			}
		});

		et_jfmoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.toString().equals("")) {

				} else {
					if (tv_yfmoney.getText().toString().equals("") || tv_yfmoney.getText().toString().equals("获取中") ) {
						Toast.makeText(context, "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
						et_jfmoney.setText("");
					} else {
						if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
							jifen = 0;
							dkmoney = 0;
							tv_dkmoney.setText("0");
						} else {
							jifen = Double.parseDouble(editable.toString());
							dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "jifenpercent", "1"));
							tv_dkmoney.setText(dkmoney + "");
						}
						if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {

						} else {
							money = Double.parseDouble(et_zfmoney.getText().toString());
						}
						if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {

						} else {
							yue = Double.parseDouble(et_yuemoney.getText().toString());
						}
						if (Double.parseDouble(tv_yfmoney.getText().toString()) - yue - dkmoney - money < 0) {
							et_jfmoney.setText("");
							tv_dkmoney.setText("0");
							Toast.makeText(context, "超出折后金额，请减少输入积分", Toast.LENGTH_SHORT).show();
						} else if (jifen- Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "jifenall",  "0")) > 0) {
							et_jfmoney.setText("");
							tv_dkmoney.setText("0");
							Toast.makeText(context, "输入积分超过会员积分", Toast.LENGTH_SHORT).show();
						} else if (dkmoney > Double.parseDouble(tv_maxdk.getText().toString())) {
							et_jfmoney.setText("");
							tv_dkmoney.setText("0");
							Toast.makeText(context, "输入积分超过最大抵扣", Toast.LENGTH_SHORT).show();
						} else if (dkmoney > Double.parseDouble(tv_yfmoney.getText().toString())) {
							et_jfmoney.setText("");
							tv_dkmoney.setText("0");
							Toast.makeText(context, "输入积分超过折后金额", Toast.LENGTH_SHORT).show();
						}

					}
				}
			}
		});
		if(isMoney&&!isYue&&!isJifen){
			et_zfmoney.setText(StringUtil.twoNum(yfmoney+""));
		}
    rl_jifen.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View view) {
			if(isVip) {
				if (!isMoney && !isYue&&!isWx) {
					Toast.makeText(context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
				} else {
					if (PreferenceHelper.readString(context, "shoppay", "jifenmaxdk", "123").equals("123")) {
						obtainJifenDkMoney(xfmoney, context);
					} else {
						if (isJifen) {
							rl_jifen.setBackgroundColor(context.getResources().getColor(R.color.white));
							tv_jf.setTextColor(context.getResources().getColor(R.color.text_30));
							isJifen = false;
							rl_pay_jifenmaxdk.setVisibility(View.GONE);
							rl_pay_jifen.setVisibility(View.GONE);
							rl_pay_jifendkm.setVisibility(View.GONE);
							et_jfmoney.setText("");
							tv_dkmoney.setText("");
							jifen = 0;
							dkmoney = 0;
						} else {
							rl_jifen.setBackgroundColor(context.getResources().getColor(R.color.theme_red));
							tv_jf.setTextColor(context.getResources().getColor(R.color.white));
							rl_pay_jifen.setVisibility(View.VISIBLE);
							rl_pay_jifendkm.setVisibility(View.VISIBLE);
							rl_pay_jifenmaxdk.setVisibility(View.VISIBLE);
							isJifen = true;
							et_zfmoney.setText("");
							money=0;
							tv_maxdk.setText(	PreferenceHelper.readString(context, "shoppay", "jifenmaxdk", "123"));
							rl_wx.setBackgroundColor(context.getResources().getColor(R.color.white));
							tv_wx.setTextColor(context.getResources().getColor(R.color.text_30));
							isWx = false;
							tv_jiesuan.setText("结算");
						}
					}
				}
			}else{
				Toast.makeText(context, "散客不能选择积分支付", Toast.LENGTH_SHORT).show();
			}
		}
	});

  rl_money.setOnClickListener(new OnClickListener() {
	  @Override
	  public void onClick(View view) {
		  if (!isJifen && !isYue&&!isWx) {
			  Toast.makeText(context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
		  } else {
			  if (isMoney) {
				  if(isMoney&&!isYue&&!isJifen){
					  et_zfmoney.setText(StringUtil.twoNum(yfmoney+""));
				  }
				  rl_money.setBackgroundColor(context.getResources().getColor(R.color.white));
				  tv_money.setTextColor(context.getResources().getColor(R.color.text_30));
				  isMoney = false;
				  et_zfmoney.setText("");
				  money=0;
				  rl_pay_money.setVisibility(View.GONE);
			  } else {
				  rl_money.setBackgroundColor(context.getResources().getColor(R.color.theme_red));
				  tv_money.setTextColor(context.getResources().getColor(R.color.white));
				  rl_pay_money.setVisibility(View.VISIBLE);
				  isMoney = true;
				  rl_wx.setBackgroundColor(context.getResources().getColor(R.color.white));
				  tv_wx.setTextColor(context.getResources().getColor(R.color.text_30));
				  isWx = false;
				  tv_jiesuan.setText("结算");
			  }
		  }
	  }
  });

		rl_yue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(isVip) {
					if (!isJifen && !isMoney&&!isWx) {
						Toast.makeText(context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
					} else {
						if (isYue) {
							if(!isMoney&&isYue&&!isJifen){
								et_yuemoney.setText(yfmoney+"");
							}
							rl_yue.setBackgroundColor(context.getResources().getColor(R.color.white));
							tv_yue.setTextColor(context.getResources().getColor(R.color.text_30));
							isYue = false;
							rl_pay_yue.setVisibility(View.GONE);
							et_yuemoney.setText("");
							yue = 0;
						} else {
							et_zfmoney.setText("");
							money=0;
							rl_yue.setBackgroundColor(context.getResources().getColor(R.color.theme_red));
							tv_yue.setTextColor(context.getResources().getColor(R.color.white));
							rl_pay_yue.setVisibility(View.VISIBLE);
							isYue = true;
							rl_wx.setBackgroundColor(context.getResources().getColor(R.color.white));
							tv_wx.setTextColor(context.getResources().getColor(R.color.text_30));
							isWx = false;
							tv_jiesuan.setText("结算");
						}
					}
				}else{
					Toast.makeText(context, "散客不能选择余额支付",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
rl_wx.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View view) {
		if (isWx) {
			Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
//                        rl_wx.setBackgroundColor(getResources().getColor(R.color.white));
//                        tv_wx.setTextColor(getResources().getColor(R.color.text_30));
//                        isWx = false;
//                        tv_jiesuan.setText("结算");
		} else {
			rl_wx.setBackgroundColor(context.getResources().getColor(R.color.theme_red));
			tv_wx.setTextColor(context.getResources().getColor(R.color.white));
			tv_jiesuan.setText("扫码支付");
			isWx = true;
			isYue=false;
			isJifen=false;
			isMoney=false;
			rl_jifen.setBackgroundColor(context.getResources().getColor(R.color.white));
			tv_jf.setTextColor(context.getResources().getColor(R.color.text_30));
			rl_pay_jifenmaxdk.setVisibility(View.GONE);
			rl_pay_jifen.setVisibility(View.GONE);
			rl_pay_jifendkm.setVisibility(View.GONE);
			et_jfmoney.setText("");
			tv_dkmoney.setText("");
			jifen=0;
			dkmoney=0;
			rl_yue.setBackgroundColor(context.getResources().getColor(R.color.white));
			tv_yue.setTextColor(context.getResources().getColor(R.color.text_30));
			rl_pay_yue.setVisibility(View.GONE);
			et_yuemoney.setText("");
			yue=0;
			rl_money.setBackgroundColor(context.getResources().getColor(R.color.white));
			tv_money.setTextColor(context.getResources().getColor(R.color.text_30));
			et_zfmoney.setText("");
			money=0;
			rl_pay_money.setVisibility(View.GONE);
		}
	}
});
		rl_jiesuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (CommonUtils.checkNet(context)) {

					if(isWx){
						VipPayMsg vipPayMsg = savaPayMsg(context, et_jfmoney, et_yuemoney, et_zfmoney, tv_dkmoney, jf, xfmoney, yfmoney);
						handler.onErrorResponse(vipPayMsg);
					}else {

						if (Double.parseDouble(tv_yfmoney.getText().toString()) - money - yue - dkmoney < 0) {
							Toast.makeText(context, "超过折后金额，请检查输入信息",
									Toast.LENGTH_SHORT).show();
						} else if (Double.parseDouble(tv_yfmoney.getText().toString()) - money - yue - dkmoney > 0) {
							Toast.makeText(context, "少于折后金额，请检查输入信息",
									Toast.LENGTH_SHORT).show();
						} else {
							if (isVip) {
								if (isMoney && !isJifen && !isYue) {
									VipPayMsg vipPayMsg = savaPayMsg(context, et_jfmoney, et_yuemoney, et_zfmoney, tv_dkmoney, jf, xfmoney, yfmoney);
									jiesuan(type,isVip,handler,dialog,context,vipPayMsg);
//									jiesuan(type, isVip, handler, dialog, context, yfmoney, xfmoney, jf, et_jfmoney, tv_dkmoney, et_yuemoney, et_zfmoney);
								} else {
									if (PreferenceHelper.readBoolean(context, "shoppay", "IsChkPwd", false)) {
										VipPayMsg vipPayMsg = savaPayMsg(context, et_jfmoney, et_yuemoney, et_zfmoney, tv_dkmoney, jf, xfmoney, yfmoney);
										pwdDialogDialog(type, context, 1, handler, vipPayMsg);
									} else {
//										jiesuan(type, isVip, handler, dialog, context, yfmoney, xfmoney, jf, et_jfmoney, tv_dkmoney, et_yuemoney, et_zfmoney);
										VipPayMsg vipPayMsg = savaPayMsg(context, et_jfmoney, et_yuemoney, et_zfmoney, tv_dkmoney, jf, xfmoney, yfmoney);
										jiesuan(type,isVip,handler,dialog,context,vipPayMsg);
									}
								}
							} else {
//								jiesuan(type, isVip, handler, dialog, context, yfmoney, xfmoney, jf, et_jfmoney, tv_dkmoney, et_yuemoney, et_zfmoney);
								VipPayMsg vipPayMsg = savaPayMsg(context, et_jfmoney, et_yuemoney, et_zfmoney, tv_dkmoney, jf, xfmoney, yfmoney);
								jiesuan(type,isVip,handler,dialog,context,vipPayMsg);
							}
						}
					}
				} else {
					Toast.makeText(context, "请检查网络是否可用",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
		return dialog;
	}


	public static void obtainJifenDkMoney(double xfmoney,final Context context) {
		AsyncHttpClient client = new AsyncHttpClient();
		final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
		client.setCookieStore(myCookieStore);
		RequestParams params = new RequestParams();
		params.put("Money", xfmoney+"");
		client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetPointOffset", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					LogUtils.d("xxJifendkMoneyS", new String(responseBody, "UTF-8"));
					JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
					if (jso.getBoolean("success")) {
						Gson gson = new Gson();
						JifenDk jf = gson.fromJson(jso.getString("data"), JifenDk.class);
//                        Type listType = new TypeToken<List<FastShopZhehMoney>>(){}.getType();
//                        List<FastShopZhehMoney> list = gson.fromJson(jso.getString("data"), listType);
						PreferenceHelper.write(context, "shoppay", "jifenpercent", jf.PontToMoneyRatio);
						PreferenceHelper.write(context, "shoppay", "jifenmaxdk", jf.MaxMoney);
					} else {
						PreferenceHelper.write(context, "shoppay", "jifenpercent", "123");
						PreferenceHelper.write(context, "shoppay", "jifenmaxdk", "123");
					}
				} catch (Exception e) {
					PreferenceHelper.write(context, "shoppay", "jifenpercent", "123");
					PreferenceHelper.write(context, "shoppay", "jifenmaxdk", "123");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				PreferenceHelper.write(context, "shoppay", "jifenpercent", "123");
				PreferenceHelper.write(context, "shoppay", "jifenmaxdk", "123");
			}
		});
	}
	public static void textDialog(final Context context,
									   int showingLocation, final InterfaceBack handler) {
		final Dialog dialog;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_text, null);
		RelativeLayout rl_confirm= (RelativeLayout) view.findViewById(R.id.rl_confirm);
		RelativeLayout rl_cancle= (RelativeLayout) view.findViewById(R.id.rl_cancle);
		dialog = new Dialog(context, R.style.DialogNotitle1);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		int screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		dialog.setContentView(view, new LinearLayout.LayoutParams(
				screenWidth-10, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.show();

		rl_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				handler.onResponse("");
			}
		});
		rl_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		Window window = dialog.getWindow();
		switch (showingLocation) {
			case 0:
				window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
				break;
			case 1:
				window.setGravity(Gravity.CENTER);
				break;
			case 2:
				window.setGravity(Gravity.BOTTOM);
				break;
			case 3:
				WindowManager.LayoutParams params = window.getAttributes();
				dialog.onWindowAttributesChanged(params);
				params.x = screenWidth-dip2px(context,100);// 设置x坐标
				params.gravity = Gravity.TOP;
				params.y = dip2px(context, 45);// 设置y坐标
				Log.d("xx", params.y + "");
				window.setGravity(Gravity.TOP);
				window.setAttributes(params);
				break;
			default:
				window.setGravity(Gravity.CENTER);
				break;
		}
	}
	//
	// public static Dialog loadingDialog(final Context context, String title,
	// int showingLocation, final InterfaceNologin handler) {
	// // Dialog pd = new Dialog(context, R.style.new_circle_progress);
	// // pd.setCancelable(true);
	// final Dialog dialog;
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View view = inflater.inflate(R.layout.loading_dialog, null);
	//
	// // final AnimationSet set = new AnimationSet(true);
	// // 构建3D旋转动画对象，旋转角度为0到90度，这使得ListView将会从可见变为不可见
	//
	// final ImageView d_img = (ImageView) view.findViewById(R.id.loading_img);
	// float centerX = getViewWith(d_img)[1] / 2f;
	// float centerY = getViewWith(d_img)[0] / 2f;
	// // 构建3D旋转动画对象，旋转角度为0到90度，这使得ListView将会从可见变为不可见
	// final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 190,
	// centerX, centerY, 310.0f, true);
	// // 动画持续时间500毫秒
	// rotation.setDuration(800);
	// // 动画完成后保持完成的状态
	// // rotation.setFillAfter(t);
	// rotation.setInterpolator(new AccelerateInterpolator());
	// d_img.setAnimation(rotation);
	// // 设置动画的监听器
	// rotation.setAnimationListener(new AnimationListener() {
	//
	// @Override
	// public void onAnimationStart(Animation animation) {
	// // TODO 自动生成的方法存根
	//
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	// // TODO 自动生成的方法存根
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// // TODO 自动生成的方法存根
	// d_img.startAnimation(rotation);
	// }
	// });
	//
	// // WebView web = (WebView) view.findViewById(R.id.loading_web);
	// // web.getSettings().setJavaScriptEnabled(true);
	// // web.getSettings().setDefaultTextEncodingName("utf-8");
	// // web.setBackgroundColor(0);
	// // // web.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
	// // // wView.loadUrl("file:///android_asset/index.html"); 0
	// //
	// // // -----打开本包内asset目录下的index.html文件
	// // //
	// //
	// wView.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");
	// //
	// // // -----打开本地sd卡内的index.html文件
	// // // wView.loadUrl("http://wap.baidu.com");
	// //
	// // web.loadUrl("file:///android_asset/loading_yishequ.html");
	// // web.setWebViewClient(new WebViewClient() {
	// //
	// // @Override
	// // public void onPageFinished(WebView view, String url) {
	// // super.onPageFinished(view, url);
	// // }
	// //
	// // });
	//
	// dialog = new MyDialog(context, R.style.DialogNotitle1);
	// // dialog = new Dialog(context, R.style.new_circle_progress);
	//
	// dialog.setCancelable(true);
	// dialog.setCanceledOnTouchOutside(true);
	//
	// // WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
	// // //
	// // 模糊度getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
	// // WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	// // dialog.getWindow().setAttributes(lp);
	// // lp.alpha=0f;//透明度，黑暗度为lp.dimAmount=1.0f;
	// int screenWidth = ((WindowManager) context
	// .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
	// .getWidth();
	// dialog.setContentView(view, new LinearLayout.LayoutParams(
	// screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
	// Window window = dialog.getWindow();
	// switch (showingLocation) {
	// case 0:
	// window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
	// break;
	// case 1:
	// window.setGravity(Gravity.CENTER);
	// break;
	// case 2:
	// window.setGravity(Gravity.BOTTOM);
	// break;
	// case 3:
	// WindowManager.LayoutParams params = window.getAttributes();
	// dialog.onWindowAttributesChanged(params);
	// params.x = 10;// 设置x坐标
	// params.gravity = Gravity.TOP;
	// params.y = 100;// 设置y坐标
	// Log.d("xx", params.y + "");
	// window.setGravity(Gravity.BOTTOM);
	// window.setAttributes(params);
	// break;
	// default:
	// window.setGravity(Gravity.CENTER);
	// break;
	// }
	// return dialog;
	// }
	//

	public static int[] getViewWith(View view) {
		int

		width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);

		int

		height = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);

		view.measure(width, height);
		int[] wh = new int[2];
		wh[0] = view.getMeasuredHeight();
		wh[1] = view.getMeasuredWidth();
		return wh;
	}

	/**
	 * @param context
	 * @param title
	 * @param handler
	 * @param showingLocation
	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
	 */
	// public static void buildDialog(final Context context, String title,
	// int showingLocation, final InterfaceNologin handler) {
	// final Dialog dialog;
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View view = inflater.inflate(R.layout.dialgon_nologin, null);
	// TextView tvTitle = (TextView) view.findViewById(R.id.dialog_title);
	// tvTitle.setText(title);
	// RelativeLayout cancel = (RelativeLayout) view
	// .findViewById(R.id.dialog_cancel);
	// RelativeLayout confirm = (RelativeLayout) view
	// .findViewById(R.id.dialog_confirm);
	// dialog = new Dialog(context, R.style.DialogNotitle1);
	// dialog.setCancelable(true);
	// dialog.setCanceledOnTouchOutside(false);
	// int screenWidth = ((WindowManager) context
	// .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
	// .getWidth();
	// dialog.setContentView(view, new LinearLayout.LayoutParams(
	// screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
	// dialog.show();
	// cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	// handler.onErrorResponse("取消");
	// }
	// });
	// confirm.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	// handler.onResponse("确定");
	// }
	// });
	// Window window = dialog.getWindow();
	// switch (showingLocation) {
	// case 0:
	// window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
	// break;
	// case 1:
	// window.setGravity(Gravity.CENTER);
	// break;
	// case 2:
	// window.setGravity(Gravity.BOTTOM);
	// break;
	// case 3:
	// WindowManager.LayoutParams params = window.getAttributes();
	// dialog.onWindowAttributesChanged(params);
	// params.x = 10;// 设置x坐标
	// params.gravity = Gravity.TOP;
	// params.y = 100;// 设置y坐标
	// Log.d("xx", params.y + "");
	// window.setGravity(Gravity.BOTTOM);
	// window.setAttributes(params);
	// break;
	// default:
	// window.setGravity(Gravity.CENTER);
	// break;
	// }
	//
	// }

	/**
	 * 提示对话框
	 *
	 * @param context
	 * @param title
	 * @param handler
	 * @param showingLocation
	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
	 */
	// public static void buildTDialog(final Context context, String title,
	// int showingLocation, final InterfaceNologin handler) {
	// final Dialog dialog;
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View view = inflater.inflate(R.layout.dialog_prompt, null);
	// TextView tvTitle = (TextView) view.findViewById(R.id.content);
	// tvTitle.setText(title);
	// dialog = new Dialog(context, R.style.DialogNotitle1);
	// dialog.setCancelable(true);
	// dialog.setCanceledOnTouchOutside(true);
	// int screenWidth = ((WindowManager) context
	// .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
	// .getWidth();
	// dialog.setContentView(view, new LinearLayout.LayoutParams(
	// screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
	// dialog.show();
	// Window window = dialog.getWindow();
	// switch (showingLocation) {
	// case 0:
	// window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
	// break;
	// case 1:
	// window.setGravity(Gravity.CENTER);
	// break;
	// case 2:
	// window.setGravity(Gravity.BOTTOM);
	// break;
	// case 3:
	// WindowManager.LayoutParams params = window.getAttributes();
	// dialog.onWindowAttributesChanged(params);
	// params.x = 10;// 设置x坐标
	// params.gravity = Gravity.TOP;
	// params.y = 100;// 设置y坐标
	// Log.d("xx", params.y + "");
	// window.setGravity(Gravity.BOTTOM);
	// window.setAttributes(params);
	// break;
	// default:
	// window.setGravity(Gravity.CENTER);
	// break;
	// }
	// new Thread() {
	// public void run() {
	// try {
	// Thread.sleep(5000);
	// dialog.dismiss();
	// } catch (InterruptedException e) {
	// // TODO 自动生成的 catch 块
	// e.printStackTrace();
	// }
	//
	// }
	//
	// ;
	//
	// }.start();
	// }

	/**
	 * 锁车提示对话框
	 *
	 * @param context
	 * @param showingLocation
	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
	 */
	// public static void buildSuocheRemindDialog(final Context context,
	// String content, int showingLocation) {
	// final Dialog dialog;
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View view = inflater.inflate(R.layout.dialog_suocheremind, null);
	// RelativeLayout rl_confirm = (RelativeLayout) view
	// .findViewById(R.id.dialog_suo_confirm);
	// TextView tv = (TextView) view.findViewById(R.id.content);
	// tv.setText(content);
	// dialog = new Dialog(context, R.style.DialogNotitle1);
	// dialog.setCancelable(true);
	// dialog.setCanceledOnTouchOutside(true);
	// int screenWidth = ((WindowManager) context
	// .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
	// .getWidth();
	// dialog.setContentView(view, new LinearLayout.LayoutParams(
	// screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
	// dialog.show();
	// rl_confirm.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	// }
	// });
	// Window window = dialog.getWindow();
	// switch (showingLocation) {
	// case 0:
	// window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
	// break;
	// case 1:
	// window.setGravity(Gravity.CENTER);
	// break;
	// case 2:
	// window.setGravity(Gravity.BOTTOM);
	// break;
	// case 3:
	// WindowManager.LayoutParams params = window.getAttributes();
	// dialog.onWindowAttributesChanged(params);
	// params.x = 10;// 设置x坐标
	// params.gravity = Gravity.TOP;
	// params.y = 100;// 设置y坐标
	// Log.d("xx", params.y + "");
	// window.setGravity(Gravity.BOTTOM);
	// window.setAttributes(params);
	// break;
	// default:
	// window.setGravity(Gravity.CENTER);
	// break;
	// }
	// }

//	/**
//	 * 管理开房记录筛选
//	 *
//	 * @param context
//	 * @param showingLocation
//	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
//	 */
//	public static void manageRecordDialog(final Context context,
//			int showingLocation, RecordChose home,
//			final InterfaceNologin handler) {
//		final Dialog dialog;
//		LayoutInflater inflater = LayoutInflater.from(context);
//		final View view = inflater.inflate(R.layout.dialog_manage_record, null);
//		RelativeLayout rl_cancel = (RelativeLayout) view
//				.findViewById(R.id.rl_dialogrecord_cancel);
//		RelativeLayout rl_confirm = (RelativeLayout) view
//				.findViewById(R.id.rl_dialogrecord_confirm);
//		final EditText et_door = (EditText) view
//				.findViewById(R.id.et_dialog_record_door);
//
//		final TextView tv_in_first = (TextView) view
//				.findViewById(R.id.tv_dialog_record_first);
//		final TextView tv_in_next = (TextView) view
//				.findViewById(R.id.tv_dialog_record_next);
//		dialog = new Dialog(context, R.style.DialogNotitle1);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(true);
//		int screenWidth = ((WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//				.getWidth();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(
//				screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
//		dialog.show();
//		if (null != home) {
//			if (!home.room.equals("")) {
//				et_door.setText(home.room);
//			}
//			if (!home.starttime.equals("")) {
//				tv_in_first.setText(home.starttime);
//			}
//			if (!home.endtime.equals("")) {
//				tv_in_next.setText(home.endtime);
//			}
//		}
//		tv_in_first.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Calendar c = Calendar.getInstance();
//				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//				new DatePickerDialog(context,
//				// 绑定监听器
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//									int monthOfYear, int dayOfMonth) {
//								int month = monthOfYear + 1;
//
//								String date = year + "-" + month + "-"
//										+ dayOfMonth;
//
//								tv_in_first.setText(date);
//							}
//						}
//						// 设置初始日期
//						, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//								.get(Calendar.DAY_OF_MONTH)).show();
//			}
//
//		});
//
//		tv_in_next.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Calendar c = Calendar.getInstance();
//				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//				new DatePickerDialog(context,
//				// 绑定监听器
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//									int monthOfYear, int dayOfMonth) {
//								int month = monthOfYear + 1;
//
//								String date = year + "-" + month + "-"
//										+ dayOfMonth;
//
//								tv_in_next.setText(date);
//							}
//						}
//						// 设置初始日期
//						, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//								.get(Calendar.DAY_OF_MONTH)).show();
//			}
//		});
//
//		rl_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				et_door.setText("");
//				tv_in_first.setText("");
//				tv_in_next.setText("");
//			}
//		});
//
//		rl_confirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				RecordChose record = new RecordChose();
//				record.room = et_door.getText().toString();
//				record.starttime = tv_in_first.getText().toString();
//				record.endtime = tv_in_next.getText().toString();
//				record.state = recordstate;
//				handler.onResponse(record);
//				dialog.dismiss();
//			}
//		});
//		Window window = dialog.getWindow();
//		switch (showingLocation) {
//		case 0:
//			window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
//			break;
//		case 1:
//			window.setGravity(Gravity.CENTER);
//			break;
//		case 2:
//			window.setGravity(Gravity.BOTTOM);
//			break;
//		case 3:
//			WindowManager.LayoutParams params = window.getAttributes();
//			dialog.onWindowAttributesChanged(params);
//			params.x = 10;// 设置x坐标
//			params.gravity = Gravity.TOP;
//			params.y = dip2px(context, 90);// 设置y坐标
//			Log.d("xx", params.y + "");
//			window.setGravity(Gravity.TOP);
//			window.setAttributes(params);
//			break;
//		default:
//			window.setGravity(Gravity.CENTER);
//			break;
//		}
//	}
//
//	/**
//	 * 客房入住筛选
//	 *
//	 * @param context
//	 * @param showingLocation
//	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
//	 */
//	public static void hotelInDialog(final Context context,
//			int showingLocation, final ChearchCheckinRoom chekin,
//			final InterfaceNologin handler) {
//		final Dialog dialog;
//		LayoutInflater inflater = LayoutInflater.from(context);
//		final View view = inflater.inflate(R.layout.dialog_hotel_in, null);
//		RelativeLayout rl_cancel = (RelativeLayout) view
//				.findViewById(R.id.rl_dialog_cancel);
//		RelativeLayout rl_confirm = (RelativeLayout) view
//				.findViewById(R.id.rl_dialog_confirm);
//		final EditText et_floor = (EditText) view
//				.findViewById(R.id.et_dialog_in_floor);
//		final EditText et_door = (EditText) view
//				.findViewById(R.id.et_dialog_in_door);
//		final EditText et_phone = (EditText) view
//				.findViewById(R.id.et_dialog_in_phone);
//		final EditText et_name = (EditText) view
//				.findViewById(R.id.et_dialog_in_name);
//
//		final TextView tv_in_first = (TextView) view
//				.findViewById(R.id.tv_dialog_in_first);
//		final TextView tv_in_next = (TextView) view
//				.findViewById(R.id.tv_dialog_in_next);
//		final TextView tv_out_first = (TextView) view
//				.findViewById(R.id.tv_dialog_out_first);
//		final TextView tv_out_next = (TextView) view
//				.findViewById(R.id.tv_dialog_out_next);
//		final RadioGroup group = (RadioGroup) view
//				.findViewById(R.id.chose_radiogroup);
//		final RadioButton rb_all = (RadioButton) view
//				.findViewById(R.id.chose_all);
//		RadioButton rb_no = (RadioButton) view.findViewById(R.id.chose_no);
//		final RadioButton rb_has = (RadioButton) view
//				.findViewById(R.id.chose_has);
//		// 绑定一个匿名监听器
//		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(RadioGroup arg0, int arg1) {
//				// TODO Auto-generated method stub
//				// 获取变更后的选中项的ID
//				int radioButtonId = arg0.getCheckedRadioButtonId();
//				// 根据ID获取RadioButton的实例
//				RadioButton rb = (RadioButton) view.findViewById(radioButtonId);
//				// 更新文本内容，以符合选中项
//				hotelinstate = rb.getText().toString();
//				Log.d("xxx", rb.getText().toString());
//			}
//		});
//
//		dialog = new Dialog(context, R.style.DialogNotitle1);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(true);
//		int screenWidth = ((WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//				.getWidth();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(
//				screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
//		dialog.show();
//		if (null != chekin) {
//			if (!chekin.floor.equals("")) {
//				et_floor.setText(chekin.floor);
//			}
//			if (!chekin.room.equals("")) {
//				et_door.setText(chekin.room);
//			}
//			if (!chekin.name.equals("")) {
//				et_name.setText(chekin.name);
//			}
//			if (!chekin.phone.equals("")) {
//				et_phone.setText(chekin.phone);
//			}
//			if (!chekin.starttimeleft.equals("")) {
//				tv_in_first.setText(chekin.starttimeleft);
//			}
//			if (!chekin.starttimeright.equals("")) {
//				tv_in_next.setText(chekin.starttimeright);
//			}
//			if (!chekin.outtimeleft.equals("")) {
//				tv_out_first.setText(chekin.outtimeleft);
//			}
//			if (!chekin.outtimeright.equals("")) {
//				tv_out_next.setText(chekin.outtimeright);
//			}
//			if (chekin.state.equals("所有")) {
//				rb_all.setChecked(true);
//			} else if (chekin.state.equals("已退")) {
//				rb_no.setChecked(true);
//			} else if (chekin.state.equals("在住")) {
//				rb_has.setChecked(true);
//			}
//		} else {
//			rb_has.setChecked(true);
//		}
//		// et_phone.addTextChangedListener(new TextWatcher() {
//		//
//		// @Override
//		// public void onTextChanged(CharSequence s, int start, int before,
//		// int count) {
//		// // TODO Auto-generated method stub
//		// }
//		//
//		// @Override
//		// public void beforeTextChanged(CharSequence s, int start, int count,
//		// int after) {
//		// // TODO Auto-generated method stub
//		//
//		// }
//		//
//		// @Override
//		// public void afterTextChanged(Editable s) {
//		// // TODO Auto-generated method stub
//		// if (hotelinstate.equals("所有")) {
//		// Toast.makeText(context, "所有选项下不能进行筛选", 0).show();
//		// // 在afterTextChanged中，调用setText()方法会循环递归触发监听器，必须合理退出递归，不然会产生异常
//		// if (s.length() > 1) {
//		//
//		// et_phone.setText("");
//		// }
//		// }
//		// }
//		// });
//
//		tv_in_first.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Calendar c = Calendar.getInstance();
//				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//				new DatePickerDialog(context,
//				// 绑定监听器
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//									int monthOfYear, int dayOfMonth) {
//								int month = monthOfYear + 1;
//
//								String date = year + "-" + month + "-"
//										+ dayOfMonth;
//
//								tv_in_first.setText(date);
//							}
//						}
//						// 设置初始日期
//						, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//								.get(Calendar.DAY_OF_MONTH)).show();
//			}
//		});
//
//		tv_in_next.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Calendar c = Calendar.getInstance();
//				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//				new DatePickerDialog(context,
//				// 绑定监听器
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//									int monthOfYear, int dayOfMonth) {
//								int month = monthOfYear + 1;
//
//								String date = year + "-" + month + "-"
//										+ dayOfMonth;
//
//								tv_in_next.setText(date);
//							}
//						}
//						// 设置初始日期
//						, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//								.get(Calendar.DAY_OF_MONTH)).show();
//			}
//		});
//
//		tv_out_first.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Calendar c = Calendar.getInstance();
//				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//				new DatePickerDialog(context,
//				// 绑定监听器
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//									int monthOfYear, int dayOfMonth) {
//								int month = monthOfYear + 1;
//
//								String date = year + "-" + month + "-"
//										+ dayOfMonth;
//
//								tv_out_first.setText(date);
//							}
//						}
//						// 设置初始日期
//						, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//								.get(Calendar.DAY_OF_MONTH)).show();
//			}
//		});
//
//		tv_out_next.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Calendar c = Calendar.getInstance();
//				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//				new DatePickerDialog(context,
//				// 绑定监听器
//						new DatePickerDialog.OnDateSetListener() {
//
//							@Override
//							public void onDateSet(DatePicker view, int year,
//									int monthOfYear, int dayOfMonth) {
//								int month = monthOfYear + 1;
//
//								String date = year + "-" + month + "-"
//										+ dayOfMonth;
//
//								tv_out_next.setText(date);
//							}
//						}
//						// 设置初始日期
//						, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//								.get(Calendar.DAY_OF_MONTH)).show();
//			}
//		});
//		rl_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				et_floor.setText("");
//				et_door.setText("");
//				et_name.setText("");
//				et_phone.setText("");
//				tv_in_first.setText("");
//				tv_in_next.setText("");
//				tv_out_first.setText("");
//				tv_out_next.setText("");
//				rb_has.setChecked(true);
//			}
//		});
//
//		rl_confirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				ChearchCheckinRoom room = new ChearchCheckinRoom();
//				Log.d("xxx", et_door.getText().toString());
//				room.floor = et_floor.getText().toString();
//				room.room = et_door.getText().toString();
//				room.phone = et_phone.getText().toString();
//				room.name = et_name.getText().toString();
//				room.starttimeleft = tv_in_first.getText().toString();
//				room.starttimeright = tv_in_next.getText().toString();
//				room.outtimeleft = tv_out_first.getText().toString();
//				room.outtimeright = tv_out_next.getText().toString();
//				room.state = hotelinstate;
//				Log.d("xxx", new Gson().toJson(room));
//				handler.onResponse(room);
//				dialog.dismiss();
//			}
//		});
//		Window window = dialog.getWindow();
//		switch (showingLocation) {
//		case 0:
//			window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
//			break;
//		case 1:
//			window.setGravity(Gravity.CENTER);
//			break;
//		case 2:
//			window.setGravity(Gravity.BOTTOM);
//			break;
//		case 3:
//			WindowManager.LayoutParams params = window.getAttributes();
//			dialog.onWindowAttributesChanged(params);
//			params.x = 10;// 设置x坐标
//			params.gravity = Gravity.TOP;
//			params.y = dip2px(context, 90);// 设置y坐标
//			Log.d("xx", params.y + "");
//			window.setGravity(Gravity.TOP);
//			window.setAttributes(params);
//			break;
//		default:
//			window.setGravity(Gravity.CENTER);
//			break;
//		}
//	}
//
//	/**
//	 * 选房筛选
//	 *
//	 * @param context
//	 * @param showingLocation
//	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
//	 */
//	public static void hotelChoseDialog(final Context context,
//			int showingLocation, final SearchHome home,
//			final InterfaceNologin handler) {
//		final Dialog dialog;
//		LayoutInflater inflater = LayoutInflater.from(context);
//		final View view = inflater.inflate(R.layout.dialog_hotel_chose, null);
//		RelativeLayout rl_confirm = (RelativeLayout) view
//				.findViewById(R.id.rl_dialogchose_confirm);
//		RelativeLayout rl_cancel = (RelativeLayout) view
//				.findViewById(R.id.rl_dialogchose_cancel);
//		final EditText et_floor = (EditText) view
//				.findViewById(R.id.et_dialog_floor);
//		final EditText et_door = (EditText) view
//				.findViewById(R.id.et_dialog_door);
//		final RadioGroup group = (RadioGroup) view
//				.findViewById(R.id.chose_radiogroup);
//		final RadioButton rb_all = (RadioButton) view
//				.findViewById(R.id.chose_all);
//		RadioButton rb_no = (RadioButton) view.findViewById(R.id.chose_no);
//		RadioButton rb_has = (RadioButton) view.findViewById(R.id.chose_has);
//		if (null != home) {
//			if (!home.floor.equals("")) {
//				et_floor.setText(home.floor);
//			}
//			if (!home.room.equals("")) {
//				et_door.setText(home.room);
//			}
//			if (home.roomstate.equals("所有")) {
//				rb_all.setChecked(true);
//			} else if (home.roomstate.equals("空房")) {
//				rb_no.setChecked(true);
//			} else if (home.roomstate.equals("住客房")) {
//				rb_has.setChecked(true);
//			}
//		} else {
//			rb_all.setChecked(true);
//		}
//		// 绑定一个匿名监听器
//		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(RadioGroup arg0, int arg1) {
//				// TODO Auto-generated method stub
//				// 获取变更后的选中项的ID
//				int radioButtonId = arg0.getCheckedRadioButtonId();
//				// 根据ID获取RadioButton的实例
//				RadioButton rb = (RadioButton) view.findViewById(radioButtonId);
//				// 更新文本内容，以符合选中项
//				roomstate = rb.getText().toString();
//				Log.d("xxx", rb.getText().toString());
//			}
//		});
//		dialog = new Dialog(context, R.style.DialogNotitle1);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(true);
//		int screenWidth = ((WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//				.getWidth();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(
//				screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
//		dialog.show();
//
//		rl_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				et_floor.setText("");
//				et_door.setText("");
//				rb_all.setChecked(true);
//			}
//		});
//
//		rl_confirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				SearchHome home = new SearchHome();
//				home.roomstate = roomstate;
//				home.floor = et_floor.getText().toString();
//				home.room = et_door.getText().toString();
//				Log.d("xxx", new Gson().toJson(home));
//				handler.onResponse(home);
//				dialog.dismiss();
//			}
//		});
//		Window window = dialog.getWindow();
//		switch (showingLocation) {
//		case 0:
//			window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
//			break;
//		case 1:
//			window.setGravity(Gravity.CENTER);
//			break;
//		case 2:
//			window.setGravity(Gravity.BOTTOM);
//			break;
//		case 3:
//			WindowManager.LayoutParams params = window.getAttributes();
//			dialog.onWindowAttributesChanged(params);
//			params.x = 10;// 设置x坐标
//			params.gravity = Gravity.TOP;
//			params.y = dip2px(context, 90);// 设置y坐标
//			Log.d("xx", params.y + "");
//			window.setGravity(Gravity.TOP);
//			window.setAttributes(params);
//			break;
//		default:
//			window.setGravity(Gravity.CENTER);
//			break;
//		}
//	}
//
//	/**
//	 * 更新对话框
//	 *
//	 * @param context
//	 * @param showingLocation
//	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
//	 */
//	public static void updateRecommendDialog(final Context context,
//			int showingLocation, Uptate update, final InterfaceNologin handler) {
//		final Dialog dialog;
//		LayoutInflater inflater = LayoutInflater.from(context);
//		final View view = inflater.inflate(R.layout.dialog_updaterecommend,
//				null);
//		RelativeLayout rl_confirm = (RelativeLayout) view
//				.findViewById(R.id.updaterecommend_confirm);
//		RelativeLayout rl_cancel = (RelativeLayout) view
//				.findViewById(R.id.updaterecommend_cancel);
//		final TextView tv_text1 = (TextView) view
//				.findViewById(R.id.updaterecommend_text1);
//		final TextView tv_text2 = (TextView) view
//				.findViewById(R.id.updaterecommend_text2);
//		final TextView tv_text3 = (TextView) view
//				.findViewById(R.id.updaterecommend_text3);
//		final TextView tv_vison = (TextView) view
//				.findViewById(R.id.update_version);
//		dialog = new Dialog(context, R.style.DialogNotitle1);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(false);
//		int screenWidth = ((WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//				.getWidth();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(
//				screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
//		dialog.show();
//		String[] msg = update.extend3.split(";");
//		Log.d("xxx", update.extend3 + ";" + msg[0]);
//		tv_text1.setText(msg[0]);
//		tv_text2.setText(msg[1]);
//		tv_text3.setText(msg[2]);
//		tv_vison.setText("V" + update.extend2);
//		rl_confirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				handler.onResponse("");
//				dialog.dismiss();
//			}
//		});
//		rl_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				handler.onErrorResponse("");
//				dialog.dismiss();
//			}
//		});
//		Window window = dialog.getWindow();
//		switch (showingLocation) {
//		case 0:
//			window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
//			break;
//		case 1:
//			window.setGravity(Gravity.CENTER);
//			break;
//		case 2:
//			window.setGravity(Gravity.BOTTOM);
//			break;
//		case 3:
//			WindowManager.LayoutParams params = window.getAttributes();
//			dialog.onWindowAttributesChanged(params);
//			params.x = 10;// 设置x坐标
//			params.gravity = Gravity.TOP;
//			params.y = dip2px(context, 90);// 设置y坐标
//			Log.d("xx", params.y + "");
//			window.setGravity(Gravity.TOP);
//			window.setAttributes(params);
//			break;
//		default:
//			window.setGravity(Gravity.CENTER);
//			break;
//		}
//	}
//
//	/**
//	 * 强制更新对话框
//	 *
//	 * @param context
//	 * @param showingLocation
//	 *            0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
//	 */
//	public static void updateDialog(final Context context, int showingLocation,
//			Uptate update, final InterfaceNologin handler) {
//		final Dialog dialog;
//		LayoutInflater inflater = LayoutInflater.from(context);
//		final View view = inflater.inflate(R.layout.dialog_update, null);
//		RelativeLayout rl_confirm = (RelativeLayout) view
//				.findViewById(R.id.update_confirm);
//		final TextView tv_text1 = (TextView) view
//				.findViewById(R.id.update_text1);
//		final TextView tv_text2 = (TextView) view
//				.findViewById(R.id.update_text2);
//		final TextView tv_text3 = (TextView) view
//				.findViewById(R.id.update_text3);
//		final TextView tv_vison = (TextView) view
//				.findViewById(R.id.update_version);
//		dialog = new Dialog(context, R.style.DialogNotitle1);
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(false);
//		int screenWidth = ((WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//				.getWidth();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(
//				screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
//		dialog.show();
//		String[] msg = update.extend3.split(";");
//		tv_text1.setText(msg[0]);
//		tv_text2.setText(msg[1]);
//		tv_text3.setText(msg[2]);
//		tv_vison.setText("V" + update.extend2);
//
//		rl_confirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log.d("xx", "cccc");
//				handler.onResponse("");
//				dialog.dismiss();
//			}
//		});
//		Window window = dialog.getWindow();
//		switch (showingLocation) {
//		case 0:
//			window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
//			break;
//		case 1:
//			window.setGravity(Gravity.CENTER);
//			break;
//		case 2:
//			window.setGravity(Gravity.BOTTOM);
//			break;
//		case 3:
//			WindowManager.LayoutParams params = window.getAttributes();
//			dialog.onWindowAttributesChanged(params);
//			params.x = 10;// 设置x坐标
//			params.gravity = Gravity.TOP;
//			params.y = dip2px(context, 90);// 设置y坐标
//			Log.d("xx", params.y + "");
//			window.setGravity(Gravity.TOP);
//			window.setAttributes(params);
//			break;
//		default:
//			window.setGravity(Gravity.CENTER);
//			break;
//		}
//	}

	/**
	 * 创建日期及时间选择对话框
	 */
	public static Dialog dayOrTimeDialog(int id, Context context) {
		Calendar c;
		Dialog dialog = null;
		switch (id) {
		case DATE_DIALOG:
			c = Calendar.getInstance();
			dialog = new DatePickerDialog(context,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,
								int month, int dayOfMonth) {
						}
					}, c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
			);
			break;
		case TIME_DIALOG:
			c = Calendar.getInstance();
			dialog = new TimePickerDialog(context,
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
						}
					}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
					false);
			break;
		}
		return dialog;
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 *
	 * @param dipValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
   public static int obtainScreenW(Context context){

	   WindowManager wm = (WindowManager) context

			   .getSystemService(Context.WINDOW_SERVICE);


	   int width = wm.getDefaultDisplay().getWidth();

	   int height = wm.getDefaultDisplay().getHeight();
	   return width;
   }
	public static void jiesuan(final String type,final boolean isVip,final InterfaceBack handle, final Dialog dialog, final Context context,final VipPayMsg msg) {
	if(type.equals("num")){
		AsyncHttpClient client = new AsyncHttpClient();
		final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
		client.setCookieStore(myCookieStore);
		final DBAdapter dbAdapter = DBAdapter.getInstance(context);
		List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
		List<ShopCar> shoplist = new ArrayList<>();
		int datalength = 0;
		for (ShopCar numShop : list) {
			if (numShop.count == 0) {
			} else {
				datalength = datalength + 1;
				shoplist.add(numShop);
			}
		}
		RequestParams params = new RequestParams();
		params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
		params.put("DiscountMoney", msg.zhMoney);
		params.put("Money", msg.xfMoney);
		params.put("Point",msg.obtainJifen);
		params.put("DataCount", datalength + "");
		params.put("bolIsPoint", msg.isJifen);
		params.put("PointPayMoney", msg.jifenDkmoney);
		params.put("UsePoint", msg.useJifen);
		params.put("bolIsCard", msg.isYue);//1：真 0：假
		params.put("CardPayMoney", msg.yueMoney);
		params.put("bolIsCash", msg.isMoney);//1：真 0：假
		params.put("CashPayMoney", msg.xjMoney);
			params.put("bolIsWeiXin",msg.isWx);//1：真 0：假
			params.put("WeiXinPayMoney",msg.wxMoney);

		for (int i = 0; i < shoplist.size(); i++) {
			params.put("Data[" + i + "][ExpPoint]", shoplist.get(i).point);
			params.put("Data[" + i + "][ExpMoney]", shoplist.get(i).discountmoney);
			params.put("Data[" + i + "][GoodsID]", shoplist.get(i).goodsid);
			params.put("Data[" + i + "][ExpNum]", shoplist.get(i).count);
		}
		Log.d("xx", params.toString());

		client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppMemRechargeCount", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
					JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
					if (jso.getBoolean("success")) {
							Toast.makeText(context, "结算成功",
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							PreferenceHelper.write(context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
//						printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
							if (PreferenceHelper.readBoolean(context, "shoppay", "IsPrint", false)) {
								BluetoothUtil.connectBlueTooth(context);
								BluetoothUtil.sendData(printReceipt_BlueTooth(type, context, msg), PreferenceHelper.readInt(context, "shoppay", "RechargeCountPrintNumber", 1));
							}
							dbAdapter.deleteShopCar();
							handle.onResponse("");
					} else {
						Toast.makeText(context, jso.getString("msg"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(context, "结算失败，请重新结算",
						Toast.LENGTH_SHORT).show();
			}
		});
	}else {
		AsyncHttpClient client = new AsyncHttpClient();
		final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
		client.setCookieStore(myCookieStore);
		final DBAdapter dbAdapter = DBAdapter.getInstance(context);
		List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
		List<ShopCar> shoplist = new ArrayList<>();
		int datalength = 0;
		for (ShopCar numShop : list) {
			if (numShop.count == 0) {

			} else {
				datalength = datalength + 1;
				shoplist.add(numShop);
			}

		}
		RequestParams params = new RequestParams();
		params.put("memid", PreferenceHelper.readString(context, "shoppay", "memid", ""));
		params.put("DiscountMoney", msg.zhMoney);
		params.put("totalMoney", msg.xfMoney);
		params.put("Point",msg.obtainJifen);
		params.put("Remark", "");
		params.put("Count", datalength + "");
		params.put("bolIsPoint", msg.isJifen);
		params.put("PointPayMoney", msg.jifenDkmoney);
		params.put("UsePoint", msg.useJifen);
		params.put("bolIsCard", msg.isYue);//1：真 0：假
		params.put("CardPayMoney", msg.yueMoney);
		params.put("bolIsCash", msg.isMoney);//1：真 0：假
		params.put("CashPayMoney", msg.xjMoney);
		params.put("bolIsWeiXin",msg.isWx);//1：真 0：假
		params.put("WeiXinPayMoney",msg.wxMoney);
		for (int i = 0; i < shoplist.size(); i++) {
			params.put("data[" + i + "][price]", shoplist.get(i).price);
			params.put("data[" + i + "][pointPercent]", shoplist.get(i).pointPercent);
			params.put("data[" + i + "][point]", shoplist.get(i).point);
			params.put("data[" + i + "][discount]", shoplist.get(i).discount);
			params.put("data[" + i + "][discountmoney]", shoplist.get(i).discountmoney);
			params.put("data[" + i + "][goodsid]", shoplist.get(i).goodsid);
			params.put("data[" + i + "][goodspoint]", shoplist.get(i).goodspoint);
			params.put("data[" + i + "][goodsType]", shoplist.get(i).goodsType);
			params.put("data[" + i + "][count]", shoplist.get(i).count);
		}
		Log.d("xx", params.toString());

		client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGoodsExpense", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
					JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
					if (jso.getBoolean("success")) {
							Toast.makeText(context, "结算成功",
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							PreferenceHelper.write(context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
//						printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
							if (PreferenceHelper.readBoolean(context, "shoppay", "IsPrint", false)) {
								BluetoothUtil.connectBlueTooth(context);
								BluetoothUtil.sendData(printReceipt_BlueTooth(type, context, msg), PreferenceHelper.readInt(context, "shoppay", "GoodsExpenesPrintNumber", 1));
							}
							dbAdapter.deleteShopCar();
							handle.onResponse("");
					} else {
						Toast.makeText(context, jso.getString("msg"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(context, "结算失败，请重新结算",
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	}

	public static void jiesuan(final String type,final boolean isVip,final InterfaceBack handle, final Dialog dialog, final Context context, final double yfmoney, final double xfmoney, final double jf, final EditText et_jfmoney, final TextView tv_dkmoney, final EditText et_yuemoney, final EditText et_zfmoney) {
		if(type.equals("num")){
			AsyncHttpClient client = new AsyncHttpClient();
			final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
			client.setCookieStore(myCookieStore);
			final DBAdapter dbAdapter = DBAdapter.getInstance(context);
			List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
			List<ShopCar> shoplist = new ArrayList<>();
			int datalength = 0;
			for (ShopCar numShop : list) {
				if (numShop.count == 0) {

				} else {
					datalength = datalength + 1;
					shoplist.add(numShop);
				}

			}
			RequestParams params = new RequestParams();
			params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
			params.put("DiscountMoney", yfmoney + "");
			params.put("Money", xfmoney + "");
			params.put("Point", (int) jifen);
			params.put("DataCount", datalength + "");
			if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
				params.put("bolIsPoint", 0);//1：真 0：假
				params.put("PointPayMoney", 0);
				params.put("UsePoint", 0);
			} else {
				params.put("bolIsPoint", 1);
				params.put("PointPayMoney", tv_dkmoney.getText().toString());
				params.put("UsePoint", et_jfmoney.getText().toString());
			}
			if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
				params.put("bolIsCard", 0);//1：真 0：假
				params.put("CardPayMoney", 0);
			} else {
				params.put("bolIsCard", 1);
				params.put("CardPayMoney", et_yuemoney.getText().toString());
			}

			if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
				params.put("bolIsCash", 0);//1：真 0：假
				params.put("CashPayMoney", 0);
			} else {
				params.put("bolIsCash", 1);
				params.put("CashPayMoney", et_zfmoney.getText().toString());
			}
			if(isWx){
				params.put("bolIsWeiXin",1);//1：真 0：假
				params.put("WeiXinPayMoney",yfmoney+"");
			}else{
				params.put("bolIsWeiXin",0);//1：真 0：假
				params.put("WeiXinPayMoney",0);
			}

			for (int i = 0; i < shoplist.size(); i++) {
				params.put("Data[" + i + "][ExpPoint]", shoplist.get(i).point);
				params.put("Data[" + i + "][ExpMoney]", shoplist.get(i).discountmoney);
				params.put("Data[" + i + "][GoodsID]", shoplist.get(i).goodsid);
				params.put("Data[" + i + "][ExpNum]", shoplist.get(i).count);
			}
			Log.d("xx", params.toString());

			client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppMemRechargeCount", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					try {
						LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
						JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
						if (jso.getBoolean("success")) {
							Toast.makeText(context, "充次成功",
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							PreferenceHelper.write(context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
//						printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
							if (PreferenceHelper.readBoolean(context, "shoppay", "IsPrint", false)) {
								BluetoothUtil.connectBlueTooth(context);
								BluetoothUtil.sendData(printReceipt_BlueTooth(type, context, xfmoney, yfmoney, jf, et_zfmoney, et_yuemoney, tv_dkmoney, et_jfmoney), PreferenceHelper.readInt(context, "shoppay", "RechargeCountPrintNumber", 1));
							}
							dbAdapter.deleteShopCar();
							handle.onResponse("");
						} else {
							Toast.makeText(context, jso.getString("msg"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
					}
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(context, "充次失败，请重新充次",
							Toast.LENGTH_SHORT).show();
				}
			});
		}else {

			AsyncHttpClient client = new AsyncHttpClient();
			final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
			client.setCookieStore(myCookieStore);
			final DBAdapter dbAdapter = DBAdapter.getInstance(context);
			List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
			List<ShopCar> shoplist = new ArrayList<>();
			int datalength = 0;
			for (ShopCar numShop : list) {
				if (numShop.count == 0) {

				} else {
					datalength = datalength + 1;
					shoplist.add(numShop);
				}

			}
			RequestParams params = new RequestParams();
			params.put("memid", PreferenceHelper.readString(context, "shoppay", "memid", ""));
			params.put("DiscountMoney", yfmoney + "");
			params.put("totalMoney", xfmoney + "");
			params.put("Point", (int) jifen);
			params.put("Remark", "");
			params.put("Count", datalength + "");
			if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
				params.put("bolIsPoint", 0);//1：真 0：假
				params.put("PointPayMoney", 0);
				params.put("UsePoint", 0);
			} else {
				params.put("bolIsPoint", 1);
				params.put("PointPayMoney", tv_dkmoney.getText().toString());
				params.put("UsePoint", et_jfmoney.getText().toString());
			}
			if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
				params.put("bolIsCard", 0);//1：真 0：假
				params.put("CardPayMoney", 0);
			} else {
				params.put("bolIsCard", 1);
				params.put("CardPayMoney", et_yuemoney.getText().toString());
			}

			if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
				params.put("bolIsCash", 0);//1：真 0：假
				params.put("CashPayMoney", 0);
			} else {
				params.put("bolIsCash", 1);
				params.put("CashPayMoney", et_zfmoney.getText().toString());
			}
			for (int i = 0; i < shoplist.size(); i++) {
				params.put("data[" + i + "][price]", shoplist.get(i).price);
				params.put("data[" + i + "][pointPercent]", shoplist.get(i).pointPercent);
				params.put("data[" + i + "][point]", shoplist.get(i).point);
				params.put("data[" + i + "][discount]", shoplist.get(i).discount);
				params.put("data[" + i + "][discountmoney]", shoplist.get(i).discountmoney);
				params.put("data[" + i + "][goodsid]", shoplist.get(i).goodsid);
				params.put("data[" + i + "][goodspoint]", shoplist.get(i).goodspoint);
				params.put("data[" + i + "][goodsType]", shoplist.get(i).goodsType);
				params.put("data[" + i + "][count]", shoplist.get(i).count);
			}
			Log.d("xx", params.toString());

			client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGoodsExpense", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					try {
						LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
						JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
						if (jso.getBoolean("success")) {
							Toast.makeText(context, "结算成功",
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							PreferenceHelper.write(context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
//						printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
							if (PreferenceHelper.readBoolean(context, "shoppay", "IsPrint", false)) {
								BluetoothUtil.connectBlueTooth(context);
								BluetoothUtil.sendData(printReceipt_BlueTooth(type, context, xfmoney, yfmoney, jf, et_zfmoney, et_yuemoney, tv_dkmoney, et_jfmoney), PreferenceHelper.readInt(context, "shoppay", "GoodsExpenesPrintNumber", 1));
							}
							dbAdapter.deleteShopCar();
							handle.onResponse("");
						} else {
							Toast.makeText(context, jso.getString("msg"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
					}
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(context, "结算失败，请重新结算",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}



	public static byte[] printReceipt_BlueTooth(final String type,Context context,final double xfm,final double yfm,double objifen,EditText et_zfmoney,EditText et_yuemoney,TextView tv_dkmoney ,final EditText et_jfmoney)
	{
		String danhao = "消费单号:" +PreferenceHelper.readString(context, "shoppay","OrderAccount","");
		String huiyuankahao = "会员卡号:" +PreferenceHelper.readString(context, "shoppay", "vipcar","无");
		String huiyuanming = "会员名称:" +PreferenceHelper.readString(context, "shoppay", "vipname", "散客");
		String xfmoney = "消费金额:" +xfm;
		String obtainjifen = "获得积分:" +objifen;
		Log.d("xx",PreferenceHelper.readString(context, "shoppay", "vipname", "散客"));
		try
		{
			byte[] next2Line = ESCUtil.nextLine(2);
			//            byte[] title = titleset.getBytes("gb2312");
			byte[] title = PreferenceHelper.readString(context,"shoppay","PrintTitle","").getBytes("gb2312");
			byte[] bottom = PreferenceHelper.readString(context,"shoppay","PrintFootNote","").getBytes("gb2312");
			byte[] tickname;
			if(type.equals("num")){
			tickname = "服务充次小票".getBytes("gb2312");
			}else {
			 tickname = "商品消费小票".getBytes("gb2312");
			}
			byte[] ordernum = danhao.getBytes("gb2312");
			byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
			byte[] vipname = huiyuanming.getBytes("gb2312");
			byte[] xfmmm = xfmoney.getBytes("gb2312");
			byte[] objfff = (obtainjifen+"").getBytes("gb2312");
			byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

			byte[] boldOn = ESCUtil.boldOn();
			byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
			byte[] center = ESCUtil.alignCenter();
			byte[] Focus = "网 507".getBytes("gb2312");
			byte[] boldOff = ESCUtil.boldOff();
			byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
			byte[] left = ESCUtil.alignLeft();
			boldOn = ESCUtil.boldOn();
			byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
			boldOff = ESCUtil.boldOff();
			byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
			next2Line = ESCUtil.nextLine(2);
			byte[] nextLine = ESCUtil.nextLine(1);
			nextLine = ESCUtil.nextLine(1);
			byte[] next4Line = ESCUtil.nextLine(4);
			byte[] breakPartial = ESCUtil.feedPaperCutPartial();
			byte[][] mytitle = {nextLine, center, boldOn, title, boldOff, next2Line, left, tickname, nextLine, left, ordernum, nextLine, left,
					vipcardnum, nextLine,
					left, vipname,nextLine,left,xfmmm,nextLine,left,objfff,nextLine,xiahuaxian};

			byte[] headerBytes =ESCUtil. byteMerger(mytitle);
			List<byte[]> bytesList = new ArrayList<>();
			bytesList.add(headerBytes);
			//商品头
			String shopdetai="商品名称   "+"单价   "+"数量   "+"合计";
			//商品头
			byte[] sh=shopdetai.getBytes("gb2312");
			byte[][] mticket1 = {nextLine, left, sh};
			bytesList.add(ESCUtil.byteMerger(mticket1));
			//商品明细
			DBAdapter dbAdapter=DBAdapter.getInstance(context);
			List<ShopCar> list= dbAdapter.getListShopCar(PreferenceHelper.readString(context,"shoppay","account","123"));
			for(ShopCar numShop:list){
				if(numShop.count==0){
				}else{
					StringBuffer sb=new StringBuffer();

					String sn=numShop.shopname;
					Log.d("xxleng",sb.length()+"");
					int sbl=sn.length();
					if(sbl<6){
						sb.append(sn);
						for(int i=0;i<7-sbl;i++) {
							sb.insert(sb.length(), " ");
						}
					}else{
                       sn=sn.substring(0,6);
						sb.append(sn);
						sb.append(" ");
					}
					Log.d("xxleng",sb.length()+"");
					byte[] a=(sb.toString()+"" +CommonUtils.lasttwo(Double.parseDouble(numShop.price))+"    "+numShop.count+"    "+numShop.discountmoney ).getBytes("gb2312");
					byte[][] mticket = {nextLine, left, a};
					bytesList.add(ESCUtil.byteMerger(mticket));
				}
			}
			byte[][] mtickets = {nextLine,xiahuaxian};
			bytesList.add(ESCUtil.byteMerger(mtickets));

			byte[] yfmoney =( "应付金额:" +yfm).getBytes("gb2312");
			String js=(yfm-xfm)+"";
			byte[] jinshengmoney =( "节省金额:" +js).getBytes("gb2312");

			byte[][] mticketsn = {nextLine,left,yfmoney,nextLine,left,jinshengmoney};
			bytesList.add(ESCUtil.byteMerger(mticketsn));
			if(isMoney){
				byte[] moneys=( "现金支付:" +et_zfmoney.getText().toString()).getBytes("gb2312");
				byte[][] mticketsm= {nextLine,left,moneys};
				bytesList.add(ESCUtil.byteMerger(mticketsm));
			}
			if(isYue){
				byte[] yue=( "余额支付:" +et_yuemoney.getText().toString()).getBytes("gb2312");
				byte[][] mticketyue= {nextLine,left,yue};
				bytesList.add(ESCUtil.byteMerger(mticketyue));
			}
			if(isJifen){
				byte[] jifen=( "积分抵扣:" +tv_dkmoney.getText().toString()).getBytes("gb2312");
				byte[][] mticketjin= {nextLine,left,jifen};
				bytesList.add(ESCUtil.byteMerger(mticketjin));
			}
			double	syjf = Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "jifenall", "0")) - jifen+objifen ;
			byte[] syjinfen=( "剩余积分:" +syjf).getBytes("gb2312");
			byte[][] mticketsyjf= {nextLine,left,syjinfen};
			bytesList.add(ESCUtil.byteMerger(mticketsyjf));
			if(isYue){
				double sy=CommonUtils.del(Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "MemMoney","")),Double.parseDouble(et_yuemoney.getText().toString()));
//				double sy= Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "MemMoney",""))-Double.parseDouble(et_yuemoney.getText().toString());
				byte[] shengyu=( "卡内余额:"+sy).getBytes("gb2312");
				byte[][] mticketsy= {nextLine,left,shengyu};
				bytesList.add(ESCUtil.byteMerger(mticketsy));
			}

			byte[] ha=( "操作人员:"+PreferenceHelper.readString(context
					,"shoppay","UserName","")).trim().getBytes("gb2312");
			byte[] time=( "消费时间:"+getStringDate()).trim().getBytes("gb2312");
			byte[] qianming=( "客户签名:").getBytes("gb2312");
			Log.d("xx",PreferenceHelper.readString(context
					,"shoppay","UserName",""));
			byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
					nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

			bytesList.add(ESCUtil.byteMerger(footerBytes));
			return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

			//            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
//			Log.d("xx","异常");
		}
		return null;
	}


	public static byte[] printReceipt_BlueTooth(final String type,Context context,VipPayMsg msg)
	{
		String danhao = "消费单号:" +PreferenceHelper.readString(context, "shoppay","OrderAccount","");
		String huiyuankahao = "会员卡号:" +PreferenceHelper.readString(context, "shoppay", "vipcar","无");
		String huiyuanming = "会员名称:" +PreferenceHelper.readString(context, "shoppay", "vipname", "散客");
		String xfmoney = "消费金额:" +StringUtil.twoNum(msg.xfMoney);
		String obtainjifen = "获得积分:" +(int)Double.parseDouble(msg.obtainJifen);
		Log.d("xx",PreferenceHelper.readString(context, "shoppay", "vipname", "散客"));
		try
		{
			byte[] next2Line = ESCUtil.nextLine(2);
			//            byte[] title = titleset.getBytes("gb2312");
			byte[] title = PreferenceHelper.readString(context,"shoppay","PrintTitle","").getBytes("gb2312");
			byte[] bottom = PreferenceHelper.readString(context,"shoppay","PrintFootNote","").getBytes("gb2312");
			byte[] tickname;
			if(type.equals("num")){
				tickname = "服务充次小票".getBytes("gb2312");
			}else {
				tickname = "商品消费小票".getBytes("gb2312");
			}
			byte[] ordernum = danhao.getBytes("gb2312");
			byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
			byte[] vipname = huiyuanming.getBytes("gb2312");
			byte[] xfmmm = xfmoney.getBytes("gb2312");
			byte[] objfff = (obtainjifen+"").getBytes("gb2312");
			byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

			byte[] boldOn = ESCUtil.boldOn();
			byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
			byte[] center = ESCUtil.alignCenter();
			byte[] Focus = "网 507".getBytes("gb2312");
			byte[] boldOff = ESCUtil.boldOff();
			byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
			byte[] left = ESCUtil.alignLeft();
			boldOn = ESCUtil.boldOn();
			byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
			boldOff = ESCUtil.boldOff();
			byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
			next2Line = ESCUtil.nextLine(2);
			byte[] nextLine = ESCUtil.nextLine(1);
			nextLine = ESCUtil.nextLine(1);
			byte[] next4Line = ESCUtil.nextLine(4);
			byte[] breakPartial = ESCUtil.feedPaperCutPartial();
			byte[][] mytitle = {nextLine, center, boldOn, title, boldOff, next2Line, left, tickname, nextLine, left, ordernum, nextLine, left,
					vipcardnum, nextLine,
					left, vipname,nextLine,left,xfmmm,nextLine,left,objfff,nextLine,xiahuaxian};

			byte[] headerBytes =ESCUtil. byteMerger(mytitle);
			List<byte[]> bytesList = new ArrayList<>();
			bytesList.add(headerBytes);
			//商品头
			String shopdetai;
			if(type.equals("num")){
				shopdetai="服务名称    "+"单价    "+"次数    "+"合计";
			}else {
				 shopdetai = "商品名称    " + "单价    " + "数量    " + "合计";
			}
			//商品头
			byte[] sh=shopdetai.getBytes("gb2312");
			byte[][] mticket1 = {nextLine, left, sh};
			bytesList.add(ESCUtil.byteMerger(mticket1));
			//商品明细
			DBAdapter dbAdapter=DBAdapter.getInstance(context);
			List<ShopCar> list= dbAdapter.getListShopCar(PreferenceHelper.readString(context,"shoppay","account","123"));
			for(ShopCar numShop:list){
				if(numShop.count==0){
				}else{
					StringBuffer sb=new StringBuffer();

					String sn=numShop.shopname;
					Log.d("xxleng",sb.length()+"");
					int sbl=sn.length();
					if(sbl<6){
						sb.append(sn);
						for(int i=0;i<7-sbl;i++) {
							sb.insert(sb.length(), " ");
						}
					}else{
						sn=sn.substring(0,6);
						sb.append(sn);
						sb.append(" ");
					}
					Log.d("xxleng",sb.length()+"");
					byte[] a=(sb.toString()+"" +CommonUtils.lasttwo(Double.parseDouble(numShop.price))+"    "+numShop.count+"    "+numShop.discountmoney ).getBytes("gb2312");
					byte[][] mticket = {nextLine, left, a};
					bytesList.add(ESCUtil.byteMerger(mticket));
				}
			}
			byte[][] mtickets = {nextLine,xiahuaxian};
			bytesList.add(ESCUtil.byteMerger(mtickets));

			byte[] yfmoney =( "应付金额:" +StringUtil.twoNum(msg.zhMoney)).getBytes("gb2312");
			byte[] jinshengmoney =( "节省金额:" +StringUtil.twoNum(msg.jieshengMoney)).getBytes("gb2312");

			byte[][] mticketsn = {nextLine,left,yfmoney,nextLine,left,jinshengmoney};
			bytesList.add(ESCUtil.byteMerger(mticketsn));
			if(isMoney){
				byte[] moneys=( "现金支付:" +StringUtil.twoNum(msg.xjMoney)).getBytes("gb2312");
				byte[][] mticketsm= {nextLine,left,moneys};
				bytesList.add(ESCUtil.byteMerger(mticketsm));
			}
			if(isYue){
				byte[] yue=( "余额支付:" +StringUtil.twoNum(msg.yueMoney)).getBytes("gb2312");
				byte[][] mticketyue= {nextLine,left,yue};
				bytesList.add(ESCUtil.byteMerger(mticketyue));
			}
			if(isWx){
				byte[] weixin=( "微信支付:" +StringUtil.twoNum(msg.wxMoney)).getBytes("gb2312");
				byte[][] weixins= {nextLine,left,weixin};
				bytesList.add(ESCUtil.byteMerger(weixins));
			}
			if(isJifen){
				byte[] jifen=( "积分抵扣:" +msg.jifenDkmoney).getBytes("gb2312");
				byte[][] mticketjin= {nextLine,left,jifen};
				bytesList.add(ESCUtil.byteMerger(mticketjin));
			}
			if(!msg.vipName.equals("散客")){
				byte[] syjinfen=( "剩余积分:" +(int)Double.parseDouble(msg.vipSyJifen)).getBytes("gb2312");
				byte[][] mticketsyjf= {nextLine,left,syjinfen};
				bytesList.add(ESCUtil.byteMerger(mticketsyjf));
			}
			if(isYue){
//				double sy=CommonUtils.del(Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "MemMoney","")),Double.parseDouble(et_yuemoney.getText().toString()));
				byte[] shengyu=( "卡内余额:"+StringUtil.twoNum(msg.vipYue)).getBytes("gb2312");
				byte[][] mticketsy= {nextLine,left,shengyu};
				bytesList.add(ESCUtil.byteMerger(mticketsy));
			}

			byte[] ha=( "操作人员:"+PreferenceHelper.readString(context
					,"shoppay","UserName","")).trim().getBytes("gb2312");
			byte[] time=( "消费时间:"+getStringDate()).trim().getBytes("gb2312");
			byte[] qianming=( "客户签名:").getBytes("gb2312");
			Log.d("xx",PreferenceHelper.readString(context
					,"shoppay","UserName",""));
			byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
					nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

			bytesList.add(ESCUtil.byteMerger(footerBytes));
			return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

			//            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
//			Log.d("xx","异常");
		}
		return null;
	}
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	private static VipPayMsg savaPayMsg(Context context,EditText et_jfmoney,EditText et_yuemoney,EditText et_zfmoney,TextView tv_dkmoney ,double obtaiJinfen,double xfm,double yfm) {
		VipPayMsg vpm=new VipPayMsg();
		if (et_jfmoney.getText().toString() == null||et_jfmoney.getText().toString().equals("")) {
			vpm.isJifen=0;
			vpm.jifenDkmoney="0";
			vpm.useJifen="0";
		}else{
			vpm.isJifen=1;
			vpm.jifenDkmoney=tv_dkmoney.getText().toString();
			vpm.useJifen= et_jfmoney.getText().toString();
		}
		if (et_yuemoney.getText().toString() == null||et_yuemoney.getText().toString().equals("")) {
			vpm.isYue=0;
			vpm.yueMoney="0";
		}else{
			vpm.isYue=1;
			vpm.yueMoney=et_yuemoney.getText().toString();
		}

		if(isWx){
			vpm.isWx=1;
			vpm.wxMoney=yfm+"";
		}else{
			vpm.isWx=0;
			vpm.wxMoney="0";
		}
		if (et_zfmoney.getText().toString() == null||et_zfmoney.getText().toString().equals("")) {
			vpm.isMoney=0;
			vpm.xjMoney="0";
		}else{
			vpm.isMoney=1;
			vpm.xjMoney= et_zfmoney.getText().toString();
		}

		vpm.zhMoney=String.valueOf(yfm);
		vpm.dataLength="0";
		vpm.jieshengMoney=String.valueOf(CommonUtils.del(xfm,yfm));
		vpm.obtainJifen=String.valueOf(obtaiJinfen);
		double yuemoney=0;
		if(et_yuemoney.getText().toString()==null||et_yuemoney.getText().toString().equals("")){
		}else{
			yuemoney=Double.parseDouble(et_yuemoney.getText().toString());
		}

		double sy= Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "MemMoney","0"))-yuemoney;
		vpm.vipYue=String.valueOf(sy);
		vpm.vipSyJifen=String.valueOf(Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "jifenall", "0")) - jifen+obtaiJinfen);
		vpm.vipCard=PreferenceHelper.readString(context, "shoppay", "vipcar","无");
		vpm.vipName=PreferenceHelper.readString(context, "shoppay", "vipname", "散客");
		vpm.vipId=PreferenceHelper.readString(context, "shoppay", "memid", "123");
		vpm.xfMoney=String.valueOf(xfm);
		return vpm;
	}



}
