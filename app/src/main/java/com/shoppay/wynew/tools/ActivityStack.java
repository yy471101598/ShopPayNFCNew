package com.shoppay.wynew.tools;

import java.util.Stack;


import android.app.Activity;
import android.content.Context;

public class ActivityStack {
	/**
	 * 应用程序Activity管理类：用于Activity管理和应用程序退出<br>
	 * 
	 * <b>创建时间</b> 2014-2-28
	 * 
	 * @author kymjs (https://github.com/kymjs)
	 * @version 1.1
	 */
	    private static Stack<Activity> activityStack;
	    private static ActivityStack instance =null;

	    private ActivityStack() {}

	    public static ActivityStack create() {
	    	  if (instance == null) {
	    	        synchronized (ActivityStack.class) {
	    	            if (instance == null) {
	    	                instance = new ActivityStack();
	    	            }
	    	        }
	    	  }
	    	        return instance;
	    }

	    /**
	     * 获取当前Activity栈中元素个数
	     */
	    public int getCount() {
	        return activityStack.size();
	    }

	    /**
	     * 添加Activity到栈
	     */
	    public void addActivity(Activity activity) {
	        if (activityStack == null) {
	            activityStack = new Stack<Activity>();
	        }
	        activityStack.add(activity);
	    }

	    /**
	     * 获取当前Activity（栈顶Activity）
	     */
	    public Activity topActivity() {
	        if (activityStack == null) {
	            throw new NullPointerException(
	                    "Activity stack is Null,your Activity must extend KJActivity");
	        }
	        if (activityStack.isEmpty()) {
	            return null;
	        }
	        Activity activity = activityStack.lastElement();
	        return (Activity) activity;
	    }

	    /**
	     * 获取当前Activity（栈顶Activity） 没有找到则返回null
	     */
	    public Activity findActivity(Class<?> cls) {
	        Activity activity = null;
	        for (Activity aty : activityStack) {
	            if (aty.getClass().equals(cls)) {
	                activity = aty;
	                break;
	            }
	        }
	        return (Activity) activity;
	    }

	    /**
	     * 结束当前Activity（栈顶Activity）
	     */
	    public void finishActivity() {
	        Activity activity = activityStack.lastElement();
	        finishActivity((Activity) activity);
	    }

	    /**
	     * 结束指定的Activity(重载)
	     */
	    public void finishActivity(Activity activity) {
	        if (activity != null) {
	        	Stack<Activity> activityStacks=new Stack<Activity>();
	        	activityStacks.addAll(activityStack);
	        	activityStacks.remove(activity);
	        	activityStack.clear();
	            activityStack.addAll(activityStacks);
	            if(activityStacks.size()>0){
	            	activityStacks.clear();
	            }
	             activity.finish();//此处不用finish
	            activity = null;
	        }
	    }

	    /**
	     * 结束指定的Activity(重载)
	     */
	    public void finishActivity(Class<?> cls) {
	    	int size=activityStack.size();
	      for (int i = 0; i < size; i++) {
	            if (activityStack.get(i).getClass().equals(cls)) {
	                finishActivity((Activity) activityStack.get(i));
	                break;
	            }
	        }
	    }

	    /**
	     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
	     * 
	     * @param cls
	     */
	    public void finishOthersActivity(Class<?> cls) {
	        for (int r=0;r<activityStack.size();r++) {
	            if (!(activityStack.get(r).getClass().equals(cls))) {
	                finishActivity((Activity) activityStack.get(r));
	            }
	        }
	    }

	    /**
	     * 结束所有Activity
	     */
	    public void finishAllActivity() {
	        for (int i = 0, size = activityStack.size(); i < size; i++) {
	            if (null != activityStack.get(i)) {
	                ((Activity) activityStack.get(i)).finish();
	            }
	        }
	        activityStack.clear();
	        android.os.Process.killProcess(android.os.Process.myPid());
	    }

	    /**
	     * 应用程序退出
	     * 
	     */
	    public void AppExit(Context context) {
	    	 int nCount = activityStack.size();
	         for (int i = nCount - 1; i >= 0; i--) {
	             Activity activity = activityStack.get(i);
	             activity.finish();
	         }

	         activityStack.clear();
	         android.os.Process.killProcess(android.os.Process.myPid());
	     }
	}