package com.shoppay.wynfc.tools;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

public class Util {
	public static void toast(Context context,int stringId){
		Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
	}
	public static void toast(Context context,String stringId){
		Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
	}
	@SuppressWarnings("finally")
	public static String[] json(String jsonStr){
		Log.e("json", jsonStr);
		String[] result = new String[4];
		JSONTokener jsonParser = new JSONTokener(jsonStr);
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			result[0] = jsonObject.getString("statusCode");
			result[1] = jsonObject.getString("message");
			result[2] = jsonObject.getString("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
			return result;
		}
	}
	public static String getSDPath(){
		  File sdDir = null;
		  boolean sdCardExist = Environment.getExternalStorageState()
		  .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		  if (sdCardExist)
		  {
		  sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		  }
		  return sdDir.toString();
		 }
	
	public static void openFile(Context context,File file) {  
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),  
                        "application/vnd.android.package-archive");  
        context.startActivity(intent);  
}  
	 public static String getMac16(String tempMac){
			if(tempMac.length() > 16){
				return tempMac.substring(0,16);
			}
			else if(tempMac.length() == 16){
				return tempMac;
			}
			else{
				return  getMac16("00" + tempMac);
			}
		}
	
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}


	
	public static Bitmap convertViewToBitmap(View view){
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.RGB_565);
        view.draw(new Canvas(bitmap));
        return bitmap;
	}
	
	  public static void closeBoard(Activity mcontext) {
			
		  InputMethodManager imm = (InputMethodManager) mcontext
				    .getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.hideSoftInputFromWindow(mcontext.getCurrentFocus().getWindowToken(), 
				  InputMethodManager.HIDE_NOT_ALWAYS);
				  
	  }
	  
	  public static void openBoard(Activity mcontext) {
		  InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);  
		  imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	  }
	  
	  /**
	     * 
	     * @param activity
	     * @return > 0 success; <= 0 fail
	     */
	    public static int getStatusHeight(Activity activity){
	        int statusHeight = 0;
	        Rect localRect = new Rect();
	        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
	        statusHeight = localRect.top;
	        if (0 == statusHeight){
	            Class<?> localClass;
	            try {
	                localClass = Class.forName("com.android.internal.R$dimen");
	                Object localObject = localClass.newInstance();
	                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
	                statusHeight = activity.getResources().getDimensionPixelSize(i5);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            } catch (InstantiationException e) {
	                e.printStackTrace();
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	            } catch (SecurityException e) {
	                e.printStackTrace();
	            } catch (NoSuchFieldException e) {
	                e.printStackTrace();
	            }
	        }
	        return statusHeight;
	    }
	    
	    public static  boolean isTablet(Context context) {
	    	return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE; 
	    }
	    
	    public static void compress(Context context,String srcPath,String savePath) { 
	    	DisplayMetrics dm = context.getResources().getDisplayMetrics();

	    	float hh = dm.heightPixels;
	    	float ww = dm.widthPixels;
	    	BitmapFactory.Options opts = new BitmapFactory.Options();
	    	opts.inJustDecodeBounds = true;
	    	Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
	    	opts.inJustDecodeBounds = false;
	    	int w = opts.outWidth;
	    	int h = opts.outHeight;
	    	int size = 0;
	    	if (w <= ww && h <= hh) {
	    	size = 1;
	    	} else {
	    	double scale = w >= h ? w / ww : h / hh;
	    	double log = Math.log(scale) / Math.log(2);
	    	double logCeil = Math.ceil(log);
	    	size = (int) Math.pow(2, logCeil);
	    	}
	    	opts.inSampleSize = size;
	    	bitmap = BitmapFactory.decodeFile(srcPath, opts);
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	int quality = 100;
	    	bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
	    	while (baos.toByteArray().length > 800 * 1024) {
	    	baos.reset();
	    	bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
	    		quality -= 10;
	    	}
	    	try {
	    	 baos.writeTo(new FileOutputStream(savePath));
	    	} catch (Exception e) {
	    	e.printStackTrace();
	    	} finally {
	    	try {
	    	baos.flush();
	    	baos.close();
	    	} catch (IOException e) {
	    	e.printStackTrace();
	    	}
	    	}
	    }
	
	    public static Bitmap ReadBitmapById(Context context, String path,
				int screenWidth, int screenHight) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			
			if(options.outWidth * 1.0f / screenWidth > 0.5){
				options.inSampleSize = 2;
			}
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			return getBitmap(bitmap, screenWidth, screenHight);
		}
	    
		public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
				int screenHight) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scale = (float) screenWidth / w;

			matrix.postScale(scale, scale);
			return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		}
		
		public static SSLSocketFactory setCertificates(InputStream... certificates)
		{
			try
			{
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
				KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				keyStore.load(null);
				int index = 0;
				for (InputStream certificate : certificates)
				{
					String certificateAlias = Integer.toString(index++);
					keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

					try
					{
						if (certificate != null)
							certificate.close();
					} catch (IOException e)
					{
					}
				}


/*

				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
				};*/
				//ctx.init(null, new TrustManager[] { tm }, null);
				SSLSocketFactory ssf = new SSLSocketFactory(keyStore, "itav110");
				
				return ssf;


			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		public static void loadImage(Context context,final ImageView icon,String headImage){
		// BitmapUtils bitmapUtils = new BitmapUtils(context);
		// bitmapUtils.display(icon, headImage,new BitmapLoadCallBack<View>() {
		//
		// @Override
		// public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
		// BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
		// if(arg2 == null){
		// icon.setImageResource(R.drawable.user_bg);
		// }
		// else{
		// icon.setImageBitmap(arg2);
		// }
		// }
		//
		// @Override
		// public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
		// icon.setImageResource(R.drawable.user_bg);
		// }
		// });
		}
}
