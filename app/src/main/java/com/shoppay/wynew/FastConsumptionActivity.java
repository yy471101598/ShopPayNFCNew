package com.shoppay.wynew;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.wynew.tools.ActivityStack;

public class FastConsumptionActivity extends FragmentActivity implements
		OnClickListener, OnPageChangeListener {
	private TextView[] tabTextView = null;
	private ImageView tabBottomLine = null;
	private RelativeLayout rl_left;
	private TextView mTextView;
	private ViewPager viewPager = null;
	private List<Fragment> listFragments = null;
	private FragmentPagerAdapter fmPagerAdapter = null;
	private int tabWidth = 0;
	private int curTabIndex = VIEW_ID_VIP;
	private static final int VIEW_ID_VIP = 0;
	private static final int VIEW_ID_SAN = 1;
	boolean[] fragmentsUpdateFlag = { false, false};
	private LinearLayout li_vip, li_san;
	private Activity ac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fastconsumption);
		ac=this;
		ActivityStack.create().addActivity(ac);
		setOverflowShowingAlways();
		initView();
	}

	private void initView() {

		mTextView = (TextView) findViewById(R.id.tv_title);

		mTextView.setText("快速消费");
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		rl_left.setOnClickListener(this);

		// 初始化tab文字
		tabTextView = new TextView[2];
		tabTextView[0] = (TextView) findViewById(R.id.consumption_tv_vip);
		tabTextView[1] = (TextView) findViewById(R.id.consumption_tv_san);
		li_vip = (LinearLayout) findViewById(R.id.consumption_li_vip);
		li_san = (LinearLayout) findViewById(R.id.consumption_li_san);
		li_vip.setOnClickListener(this);
		li_san.setOnClickListener(this);

		// // 初始化tab标识线
		tabBottomLine = (ImageView) findViewById(R.id.consumption_iv_icon);
		LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) tabBottomLine
				.getLayoutParams();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		lParams.width = dm.widthPixels / 2;
		tabBottomLine.setLayoutParams(lParams);
		tabWidth = dm.widthPixels / 2;
		Matrix matrix = new Matrix();
		tabBottomLine.setImageMatrix(matrix);// 设置动画初始位置
		listFragments = new ArrayList<Fragment>();
		listFragments.add(new VipFragment());
		listFragments.add(new SanFragment());

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(1);
		fmPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(fmPagerAdapter);
		viewPager.setCurrentItem(VIEW_ID_VIP);
		viewPager.setOnPageChangeListener(this);
		updateTabTextStatus(VIEW_ID_VIP);
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		FragmentManager fm;

		MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		@Override
		public int getCount() {
			return listFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = listFragments.get(position
					% listFragments.size());

			return fragment;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 得到缓存的fragment
			Fragment fragment = (Fragment) super.instantiateItem(container,
					position);
			// 得到tag，这点很重要
			String fragmentTag = fragment.getTag();
			if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
				// 如果这个fragment需要更新

				FragmentTransaction ft = fm.beginTransaction();
				// 移除旧的fragment
				ft.remove(fragment);
				// 换成新的fragment
				fragment = listFragments.get(position % listFragments.size());
				// 添加新fragment时必须用前面获得的tag，这点很重要
				ft.add(container.getId(), fragment, fragmentTag);
				ft.attach(fragment);
				ft.commit();

				// 复位更新标志
				fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
			}

			return fragment;
		}
	}

	// 更新tab文字选中状态
	private void updateTabTextStatus(int index) {
		for (TextView tv : tabTextView) {
			tv.setTextColor(getResources().getColor(R.color.text_30));
		}

		tabTextView[index].setTextColor(getResources().getColor(R.color.theme_red));
//		switch (index) {
//		case 0:
//			li_vip.setBackgroundResource(R.drawable.shape_blue_blueleft);
//			break;
//		case 1:
//			li_san.setBackgroundColor(getResources().getColor(R.color.text_theme));
//			break;
//		case 2:
//			li_card.setBackgroundResource(R.drawable.shape_blue_blueright);
//			break;
//		}
	}

	// 切换tab选项
	private void changeTabItem(int index) {
		Animation animation = new TranslateAnimation(curTabIndex * tabWidth,
				index * tabWidth, 0, 0);
		curTabIndex = index;
		animation.setFillAfter(true);
		animation.setDuration(300);
		tabBottomLine.startAnimation(animation);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束,更新文字选中状态
				updateTabTextStatus(curTabIndex);
			}
		});
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		// 左右滑动切换tab页
		changeTabItem(arg0);
	}

	@Override
	public void onClick(View v) {
		// 点击切换tab页
		switch (v.getId()) {
		case R.id.consumption_li_vip:
			viewPager.setCurrentItem(VIEW_ID_VIP);
			break;
		case R.id.consumption_li_san:
			viewPager.setCurrentItem(VIEW_ID_SAN);
			break;
		case R.id.rl_left:
			ActivityStack.create().finishActivity(ac);
			
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

}
