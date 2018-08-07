package cn.hudun.wifi.pwd.ui;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mobstat.StatService;

import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.engine.UpdateEngine;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class WiFiConnActivity extends Activity implements OnClickListener {
	private TextView tv_conn;
	private TextView tv_wifi_conn;
	private WifiManager wifiManager;
	private List<ScanResult> scanResults;
	private List<WifiConfiguration> wifiConfigurations;
	private List<WifiConfiguration> wifiList;
	private ImageView iv_top;
	// 声明PopupWindow对象的引用
	private PopupWindow popupWindow;
	private long exitTime = 0;// 返回键最后退出时间
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (msg.arg1 < 10) {
					tv_conn.setText("0" + msg.arg1);

				} else {
					tv_conn.setText(msg.arg1 + "");
				}
				if (msg.arg1 == 1) {
					wifiManager.setWifiEnabled(true);
					tv_wifi_conn.setText("打开无线...");
				} else if (msg.arg1 == 50) {
					if (wifiManager.getWifiState() == 3) {// WIFI开启成功
						tv_wifi_conn.setText("获取可用无线信息...");
						getConnWifi();
						if (wifiList.size() != 0) {
							for (int i = 0; i < wifiList.size(); i++) {
								String ssid = wifiList.get(i).SSID.substring(1,
										wifiList.get(i).SSID.length() - 1);
								if (ssid != null && ssid.equals("0x")) {
									wifiManager.enableNetwork(
											wifiList.get(i).networkId, true);
								}
								if (isWifiConnected()) {
									tv_wifi_conn.setText("正在连接...");
									return;
								}
							}
						}
					}
				} else if (msg.arg1 == 99) {
					if (isWifiConnected()) {
						tv_wifi_conn.setText("已经连接...");
						WifiInfo wifiInfo = wifiManager.getConnectionInfo();
						Intent intent = new Intent(WiFiConnActivity.this,
								WiFiSucActivity.class);
						intent.putExtra("ssid", wifiInfo.getSSID());
						startActivity(intent);
						finish();
					} else {
						tv_wifi_conn.setText("连接失败...");
						Intent intent = new Intent(WiFiConnActivity.this,
								WiFiFailActivity.class);
						startActivity(intent);
						finish();
					}
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wifi_conn);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		initView();
		setProgress();
		
		
	}

	private void initView() {
		tv_conn = (TextView) findViewById(R.id.tv_conn);
		tv_conn.setOnClickListener(this);
		iv_top = (ImageView) findViewById(R.id.iv_top);
		iv_top.setOnClickListener(this);
		tv_wifi_conn = (TextView) findViewById(R.id.tv_wifi_conn);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_top:
			getPopupWindow();
			popupWindow.showAsDropDown(iv_top, iv_top.getWidth(),
					iv_top.getHeight() - 20);
			break;
		case R.id.ll_pop_share:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			showShare();
			break;
		case R.id.ll_pop_update:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			UpdateEngine updateEngine = new UpdateEngine(WiFiConnActivity.this);
			updateEngine.getUpdate();
			break;
		case R.id.ll_pop_exit:
			finish();
			break;
		default:
			break;
		}

	}

	/**
	 * 判断wifi是否连接成功
	 * 
	 * @return
	 */
	public boolean isWifiConnected() {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null) {
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}

	public void setProgress() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int count = 0;
				while (count < 99) {
					count++;
					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = count;
					handler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	/**
	 * 获取当前已保存的可用wifi
	 */
	public void getConnWifi() {
		scanResults = wifiManager.getScanResults();
		wifiConfigurations = wifiManager.getConfiguredNetworks();
		wifiList = new ArrayList<WifiConfiguration>();
		for (int i = 0; i < wifiConfigurations.size(); i++) {
			String ssid = wifiConfigurations.get(i).SSID.substring(1,
					wifiConfigurations.get(i).SSID.length() - 1);
			for (int j = 0; j < scanResults.size(); j++) {
				if (ssid.trim().equals(scanResults.get(j).SSID)) {
					wifiList.add(wifiConfigurations.get(i));
				}
			}
		}
	}

	/*
	 * *
	 * 创建PopupWindow
	 */
	protected void initPopuptWindow() {
		// TODO Auto-generated method stub
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_top,
				null, false);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		// 点击其他地方消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});

		LinearLayout ll_pop_share = (LinearLayout) popupWindow_view
				.findViewById(R.id.ll_pop_share);
		LinearLayout ll_pop_update = (LinearLayout) popupWindow_view
				.findViewById(R.id.ll_pop_update);
		LinearLayout ll_pop_exit = (LinearLayout) popupWindow_view
				.findViewById(R.id.ll_pop_exit);
		ll_pop_share.setOnClickListener(this);
		ll_pop_update.setOnClickListener(this);
		ll_pop_exit.setOnClickListener(this);
	}

	/***
	 * 获取PopupWindow实例
	 */
	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(WiFiConnActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
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

	private void showShare() {
		ShareSDK.initSDK(this);
		View view = getWindow().getDecorView();
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_mini_logo,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("WiFi密码查看器，一键查看已保存的WiFi密码");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		oks.setViewToShare(view);
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://www.huduntech.com/");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("WiFi密码查看器");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://www.huduntech.com/");
		// 启动分享GUI
		oks.show(this);
	}
}
