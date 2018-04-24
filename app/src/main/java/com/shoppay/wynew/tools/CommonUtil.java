package com.shoppay.wynew.tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.shoppay.wynew.R;


/*
 */
public class CommonUtil {

    public static double getVersionName(Context context){
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return Double.parseDouble(packInfo.versionName);
        }catch(Exception e){
            Log.e("Version",e.getMessage());
        }
        return 0;
    }

    public static Map<String,Object> addSign(Context context,Map<String,Object> paramsValue){
    	paramsValue.put(JsonKey.appId, AppConstant.appId);
    	paramsValue.put(JsonKey.version, context.getString(R.string.version));
    	paramsValue.put(JsonKey.clientType, AppConstant.clientType);
    	
    	List<String> target = StringUtil.sort(new ArrayList<String>(paramsValue.keySet()));
    	StringBuilder encryptPre = new StringBuilder();
    	for (int i = 0,size = target.size(); i < size; i++) {
    		String paramName = target.get(i);
    		encryptPre.append(paramsValue.get(paramName));
    	}
    	encryptPre.append(AppConstant.appKey);
    	paramsValue.put(JsonKey.sign, MD5Util.MD5Encode(encryptPre.toString(), "UTF-8"));
    	return paramsValue;
    }

    
    
}
