package cn.hudun.wifi.pwd.ui;

import java.util.ArrayList;
import java.util.List;
import com.baidu.mobstat.StatService;

import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.adapter.MyFragmentPagerAdapter;
import cn.hudun.wifi.pwd.ui.fragment.FourFragment;
import cn.hudun.wifi.pwd.ui.fragment.FourFragment.CheckStateListener;
import cn.hudun.wifi.pwd.ui.fragment.OneFragment;
import cn.hudun.wifi.pwd.ui.fragment.ThreeFragment;
import cn.hudun.wifi.pwd.ui.fragment.TwoFragment;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

public class GuideActivity extends FragmentActivity implements OnTouchListener,
		CheckStateListener {
	private ViewPager mViewPager;
	private List<Fragment> fragments;
	private int lastX;// 最后一次点击的X坐标
	private MyFragmentPagerAdapter adapter;
	private WifiManager wifiManager;
	private boolean isDownLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide_activity);
		initFragment();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
				fragments);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnTouchListener(this);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

	}

	/**
	 * 初始化Fragment
	 */
	public void initFragment() {
		fragments = new ArrayList<Fragment>();
		Fragment oneFragment = new OneFragment();
		Fragment twoFragment = new TwoFragment();
		Fragment threeFragment = new ThreeFragment();
		fragments.add(oneFragment);
		fragments.add(twoFragment);
		fragments.add(threeFragment);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	/**
	 * 主页
	 */
	private void goHome() {
		/*
		 * if (wifiManager.isWifiEnabled()) { WifiInfo wifiInfo =
		 * wifiManager.getConnectionInfo(); String ssid = null; if
		 * (wifiInfo.getSSID() == null || wifiInfo.getSSID().length() <= 0 ||
		 * wifiInfo.getSSID().equals("unknown ssid") ||
		 * wifiInfo.getSSID().equals("UNINITIALIZED")) { ssid = null; } else {
		 * if (wifiInfo.getSSID().charAt(0) == '\"' &&
		 * wifiInfo.getSSID().charAt( wifiInfo.getSSID().length() - 1) == '\"')
		 * { ssid = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length()
		 * - 1); } else { ssid = wifiInfo.getSSID(); } }
		 * 
		 * if (ssid == null || ssid.equals("0x") || ssid.equals("")) { Intent
		 * intent = new Intent(GuideActivity.this, WiFiFailActivity.class);
		 * startActivity(intent); finish();
		 * overridePendingTransition(R.anim.splash_in, R.anim.splash_out); }
		 * else { Intent intent = new Intent(GuideActivity.this,
		 * WiFiSucActivity.class); startActivity(intent); finish();
		 * overridePendingTransition(R.anim.splash_in, R.anim.splash_out); } }
		 * else { Intent intent = new Intent(GuideActivity.this,
		 * WiFiNOActivity.class); startActivity(intent); finish();
		 * overridePendingTransition(R.anim.splash_in, R.anim.splash_out); }
		 */
		Intent intent = new Intent(GuideActivity.this, WiFiSucActivity.class);
		startActivity(intent);
		finish();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 页面统计
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	/**
	 * 获取手机当前版本
	 * 
	 * @return
	 */
	public int getSdkVersion() {
		return VERSION.SDK_INT;

	}

	@Override
	public void checkState(boolean b) {
		// TODO Auto-generated method stub
		isDownLoad = b;
	}

	public boolean getIsDownLoad() {
		return isDownLoad;
	}

}
