package cn.hudun.wifi.pwd.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.baidu.mobstat.StatService;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;

import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.adapter.WifiAdapter;
import cn.hudun.wifi.pwd.bean.Wifi;
import cn.hudun.wifi.pwd.engine.UpdateEngine;
import cn.hudun.wifi.pwd.engine.WifiEngine;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class WiFiFailActivity extends Activity implements OnRefreshListener,
		OnItemClickListener, OnHeaderClickListener, OnClickListener {
	private TextView tv_top_name;
	private WifiManager wifiManager;
	private StickyListHeadersListView lv_wifi_no_conn;
	private WifiAdapter wifiAdapter;
	private boolean flag = true;// 线程停止标记
	private List<Wifi> wifis;
	private List<Wifi> scanWifis;
	private List<Wifi> locationWifis;
	private SwipeRefreshLayout swipeLayout;// 刷新
	private WifiEngine wifiEngine;
	private int freeCount;
	private int pwdCount;
	private int pwd = 0;// 是否显示密码
	private ImageView iv_top;
	private int pos = -1;
	private List<WifiConfiguration> wifiConfigurations;
	private Animation animation;
	private ImageView iv_top_prog;
	// 声明PopupWindow对象的引用
	private PopupWindow popupWindow;
	private long exitTime = 0;// 返回键最后退出时间
	private SharedPreferences sp;
	private List<Wifi> freeWifis;
	private List<Wifi> pwdWifis;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (pwd == 1) {
					if (isWifiConnected()) {
						iv_top_prog.clearAnimation();
						iv_top_prog.setVisibility(View.GONE);
						Intent intent = new Intent(WiFiFailActivity.this,
								WiFiSucActivity.class);
						startActivity(intent);
						finish();
					} else {
						iv_top_prog.clearAnimation();
						iv_top_prog.setVisibility(View.GONE);
						freeWifis.get(msg.arg1).setState(3);
						wifiAdapter.notifyDataSetChanged();
					}
				} else {
					if (isWifiConnected()) {
						iv_top_prog.clearAnimation();
						iv_top_prog.setVisibility(View.GONE);
						Intent intent = new Intent(WiFiFailActivity.this,
								WiFiSucActivity.class);
						startActivity(intent);
						finish();
					} else {
						iv_top_prog.clearAnimation();
						iv_top_prog.setVisibility(View.GONE);
						wifis.get(msg.arg1).setState(3);
						wifiAdapter.notifyDataSetChanged();
					}
				}

				break;
			case 2:
				if (pwd == 1) {
					Intent connIntent = new Intent(WiFiFailActivity.this,
							ConnDialogActivity.class);
					connIntent.putExtra("ssid", freeWifis.get(msg.arg1)
							.getSsid());
					connIntent.putExtra("type", freeWifis.get(msg.arg1)
							.getType());
					startActivityForResult(connIntent, 100);
				} else {
					Intent connIntent = new Intent(WiFiFailActivity.this,
							ConnDialogActivity.class);
					connIntent.putExtra("ssid", wifis.get(msg.arg1).getSsid());
					connIntent.putExtra("type", wifis.get(msg.arg1).getType());
					startActivityForResult(connIntent, 100);
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
		setContentView(R.layout.wifi_conn_fail);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		initView();
		// wifiManager.setWifiEnabled(false);
		// wifiManager.setWifiEnabled(true);
		sp = getSharedPreferences("wifiPWD", Context.MODE_PRIVATE);
		wifiEngine = new WifiEngine(WiFiFailActivity.this);
		scanWifis = wifiEngine.getWifiScan();
		locationWifis = wifiEngine.getWifiLocation();
		wifis = wifiEngine.getWifis(scanWifis, locationWifis);
		for (int i = 0; i < wifis.size(); i++) {
			if (wifis.get(i).isFree()) {
				freeCount++;
			} else {
				pwdCount++;
			}
		}

		if (freeCount == 0) {
			Wifi freeWifi = new Wifi();
			freeWifi.setSsid("hudun");
			freeWifi.setFree(true);
			wifis.add(freeWifi);
		}

		if (pwdCount == 0) {
			Wifi pwdWifi = new Wifi();
			pwdWifi.setSsid("hudun2");
			pwdWifi.setFree(false);
			wifis.add(pwdWifi);
		}
		Collections.sort(wifis, new Comparator<Wifi>() {

			@Override
			public int compare(Wifi wifi1, Wifi wifi2) {
				if (wifi1.isFree()) {// !=wifi2.isFree()
					return -1;
				}
				return 1;
			}
		});
		wifiAdapter = new WifiAdapter(WiFiFailActivity.this, wifis, false);
		lv_wifi_no_conn.setAdapter(wifiAdapter);
		if (isWifiConnected()) {
			Intent intent = new Intent(WiFiFailActivity.this,
					WiFiSucActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initView() {
		wifis = new ArrayList<Wifi>();
		scanWifis = new ArrayList<Wifi>();
		locationWifis = new ArrayList<Wifi>();
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		tv_top_name = (TextView) findViewById(R.id.tv_top_name);
		lv_wifi_no_conn = (StickyListHeadersListView) findViewById(R.id.lv_wifi_no_conn);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		lv_wifi_no_conn.setOnItemClickListener(this);
		lv_wifi_no_conn.setOnHeaderClickListener(this);
		tv_top_name.setText("WiFi密码查看器");
		iv_top = (ImageView) findViewById(R.id.iv_top);
		iv_top.setOnClickListener(this);
		iv_top_prog = (ImageView) findViewById(R.id.iv_top_prog);
		WebView wv=(WebView) findViewById(R.id.wv);

	}

	@Override
	public void onRefresh() {
		if (wifiManager.isWifiEnabled() == false) {
			Toast.makeText(WiFiFailActivity.this, "WiFi未开启", Toast.LENGTH_SHORT)
					.show();
			/*Intent intent = new Intent(WiFiFailActivity.this,
					WiFiNOActivity.class);
			startActivity(intent);
			finish();*/
		} else {
			wifiEngine = new WifiEngine(WiFiFailActivity.this);
			scanWifis = wifiEngine.getWifiScan();
			locationWifis = wifiEngine.getWifiLocation();
			wifis = wifiEngine.getWifis(scanWifis, locationWifis);
			for (int i = 0; i < wifis.size(); i++) {
				if (wifis.get(i).isFree()) {
					freeCount++;
				} else {
					pwdCount++;
				}
			}
			if (freeCount == 0) {
				Wifi freeWifi = new Wifi();
				freeWifi.setSsid("hudun");
				freeWifi.setFree(true);
				wifis.add(freeWifi);
			}

			if (pwdCount == 0) {
				Wifi pwdWifi = new Wifi();
				pwdWifi.setSsid("hudun2");
				pwdWifi.setFree(false);
				wifis.add(pwdWifi);
			}

			Collections.sort(wifis, new Comparator<Wifi>() {

				@Override
				public int compare(Wifi wifi1, Wifi wifi2) {
					if (wifi1.isFree()) {// !=wifi2.isFree()
						return -1;
					}
					return 1;

				}
			});
			swipeLayout.setRefreshing(false);
			wifiAdapter = new WifiAdapter(WiFiFailActivity.this, wifis, false);
			lv_wifi_no_conn.setAdapter(wifiAdapter);
			if (isWifiConnected()) {
				Intent intent = new Intent(WiFiFailActivity.this,
						WiFiSucActivity.class);
				startActivity(intent);
				finish();
			}
		}

	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header,
			int itemPosition, long headerId, boolean currentlySticky) {

		if (headerId == 1) {
			boolean isRoot = sp.getBoolean("isRoot", false);
			if (isRoot == true) {
				if (pwd == 0) {
					freeWifis = new ArrayList<Wifi>();
					pwdWifis = new ArrayList<Wifi>();
					for (int i = 0; i < wifis.size(); i++) {
						if (wifis.get(i).isFree()
								|| !(wifis.get(i).getPwd().equals("无密码"))) {
							freeWifis.add(wifis.get(i));
						} else {
							pwdWifis.add(wifis.get(i));
						}
					}
					freeWifis.addAll(pwdWifis);
					wifiAdapter = new WifiAdapter(WiFiFailActivity.this,
							freeWifis, true);
					lv_wifi_no_conn.setAdapter(wifiAdapter);
					pwd = 1;
				} else {
					wifiAdapter = new WifiAdapter(WiFiFailActivity.this, wifis,
							false);
					lv_wifi_no_conn.setAdapter(wifiAdapter);
					pwd = 0;
				}
			} else {
				Toast.makeText(WiFiFailActivity.this, "需要Root权限才能查看",
						Toast.LENGTH_SHORT).show();
			}

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
		if (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
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
			UpdateEngine updateEngine = new UpdateEngine(WiFiFailActivity.this);
			updateEngine.getUpdate();
			break;
		case R.id.ll_pop_exit:
			finish();
			break;
		default:
			break;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			if (resultCode == 101) {
				WifiInfo wifiInfo2 = wifiManager.getConnectionInfo();
				if (wifiInfo2.getSSID() != null
						&& !wifiInfo2.getSSID().equals("0x")) {
					iv_top_prog.clearAnimation();
					iv_top_prog.setVisibility(View.GONE);
					Intent intent = new Intent(WiFiFailActivity.this,
							WiFiSucActivity.class);
					startActivity(intent);
					finish();
				}
			} else {
				if (pwd == 1) {
					for (int i = 0; i < wifis.size(); i++) {
						if (freeWifis.get(i).getState() == 1) {
							freeWifis.get(i).setState(5);
						}
					}
					iv_top_prog.clearAnimation();
					iv_top_prog.setVisibility(View.GONE);
					freeWifis.get(pos).setState(3);
					wifiAdapter.notifyDataSetChanged();
				} else {
					for (int i = 0; i < wifis.size(); i++) {
						if (wifis.get(i).getState() == 1) {
							wifis.get(i).setState(5);
						}
					}
					iv_top_prog.clearAnimation();
					iv_top_prog.setVisibility(View.GONE);
					wifis.get(pos).setState(3);
					wifiAdapter.notifyDataSetChanged();
				}

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(WiFiFailActivity.this, "再按一次退出程序",
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ((position == 0 && wifis.get(position).getState() == 1)
				|| wifis.get(position).getSsid().equals("hudun")
				|| wifis.get(position).getSsid().equals("hudun2")) {
			return;
		}

		if (pwd == 1) {
			if (freeWifis.get(position).getSignalLevel() <= -96) {
				Toast.makeText(WiFiFailActivity.this, "超出无线覆盖范围",
						Toast.LENGTH_SHORT).show();
			} else {
				pos = position;
				iv_top_prog.setVisibility(View.VISIBLE);
				animation = AnimationUtils.loadAnimation(WiFiFailActivity.this,
						R.anim.rotate_animation);
				iv_top_prog.startAnimation(animation);
				int index = -1;
				if (freeWifis.get(position).isFree()) {

					if (freeWifis.get(position).getPwd().equals("无密码")) {
						WifiConfiguration wcg = CreateWifiInfo(
								wifis.get(position).getSsid(), null, "noPass");
						int wcgID = wifiManager.addNetwork(wcg);
						if (wcgID == -1) {
							Message message = new Message();
							message.what = 1;
							message.arg1 = position;
							mHandler.sendMessageDelayed(message, 200);
						} else {
							boolean b = wifiManager.enableNetwork(wcgID, true);
							Message message = new Message();
							message.what = 1;
							message.arg1 = position;
							freeWifis.get(position).setState(2);
							wifiAdapter.notifyDataSetChanged();
							mHandler.sendMessageDelayed(message, 3000);
						}
					} else {
						wifiConfigurations = wifiManager
								.getConfiguredNetworks();
						for (int i = 0; i < wifiConfigurations.size(); i++) {
							String ssid = wifiConfigurations.get(i).SSID
									.substring(1,
											wifiConfigurations.get(i).SSID
													.length() - 1);
							if (freeWifis.get(position).getSsid().equals(ssid)) {
								index = i;
							}
						}
						if (index == -1) {
							Message message = new Message();
							message.what = 2;
							message.arg1 = position;
							mHandler.sendMessageDelayed(message, 200);

						} else {
							wifiManager.enableNetwork(
									wifiConfigurations.get(index).networkId,
									true);
							Message message = new Message();
							message.what = 1;
							message.arg1 = position;
							freeWifis.get(position).setState(2);
							wifiAdapter.notifyDataSetChanged();
							mHandler.sendMessageDelayed(message, 3000);

						}
					}

				} else {
					wifiConfigurations = wifiManager.getConfiguredNetworks();
					for (int i = 0; i < wifiConfigurations.size(); i++) {
						String ssid = wifiConfigurations.get(i).SSID.substring(
								1, wifiConfigurations.get(i).SSID.length() - 1);
						if (freeWifis.get(position).getSsid().equals(ssid)) {
							index = i;
						}
					}
					if (index == -1) {
						Message message = new Message();
						message.what = 2;
						message.arg1 = position;
						mHandler.sendMessageDelayed(message, 200);

					} else {
						wifiManager.enableNetwork(
								wifiConfigurations.get(index).networkId, true);
						Message message = new Message();
						message.what = 1;
						message.arg1 = position;
						freeWifis.get(position).setState(2);
						wifiAdapter.notifyDataSetChanged();
						mHandler.sendMessageDelayed(message, 3000);
					}
				}

			}
		} else {
			if (wifis.get(position).getSignalLevel() <= -96) {
				Toast.makeText(WiFiFailActivity.this, "超出无线覆盖范围",
						Toast.LENGTH_SHORT).show();
			} else {
				pos = position;
				iv_top_prog.setVisibility(View.VISIBLE);
				animation = AnimationUtils.loadAnimation(WiFiFailActivity.this,
						R.anim.rotate_animation);
				iv_top_prog.startAnimation(animation);
				int index = -1;
				if (wifis.get(position).isFree()) {

					if (wifis.get(position).getPwd().equals("无密码")) {
						WifiConfiguration wcg = CreateWifiInfo(
								wifis.get(position).getSsid(), null, "noPass");
						int wcgID = wifiManager.addNetwork(wcg);
						if (wcgID == -1) {
							Message message = new Message();
							message.what = 1;
							message.arg1 = position;
							mHandler.sendMessageDelayed(message, 200);
						} else {
							boolean b = wifiManager.enableNetwork(wcgID, true);
							Message message = new Message();
							message.what = 1;
							message.arg1 = position;
							wifis.get(position).setState(2);
							wifiAdapter.notifyDataSetChanged();
							mHandler.sendMessageDelayed(message, 3000);
						}
					} else {
						wifiConfigurations = wifiManager
								.getConfiguredNetworks();
						for (int i = 0; i < wifiConfigurations.size(); i++) {
							String ssid = wifiConfigurations.get(i).SSID
									.substring(1,
											wifiConfigurations.get(i).SSID
													.length() - 1);
							if (wifis.get(position).getSsid().equals(ssid)) {
								index = i;
							}
						}
						if (index == -1) {
							Message message = new Message();
							message.what = 2;
							message.arg1 = position;
							mHandler.sendMessageDelayed(message, 200);
						} else {
							wifiManager.enableNetwork(
									wifiConfigurations.get(index).networkId,
									true);
							Message message = new Message();
							message.what = 1;
							message.arg1 = position;
							wifis.get(position).setState(2);
							wifiAdapter.notifyDataSetChanged();
							mHandler.sendMessageDelayed(message, 3000);
						}
					}

				} else {
					wifiConfigurations = wifiManager.getConfiguredNetworks();
					for (int i = 0; i < wifiConfigurations.size(); i++) {
						String ssid = wifiConfigurations.get(i).SSID.substring(
								1, wifiConfigurations.get(i).SSID.length() - 1);
						if (wifis.get(position).getSsid().equals(ssid)) {
							index = i;
						}
					}
					if (index == -1) {
						Message message = new Message();
						message.what = 2;
						message.arg1 = position;
						mHandler.sendMessageDelayed(message, 200);
						// mHandler.sendEmptyMessageDelayed(2, 100);

					} else {
						wifiManager.enableNetwork(
								wifiConfigurations.get(index).networkId, true);
						Message message = new Message();
						message.what = 1;
						message.arg1 = position;
						wifis.get(position).setState(2);
						wifiAdapter.notifyDataSetChanged();
						mHandler.sendMessageDelayed(message, 3000);
						// mHandler.sendEmptyMessageDelayed(1, 100);
					}
				}
			}
		}

	}

	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
			String type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		if (type.equals("noPass")) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (type.equals("wep")) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (type.equals("wpa")) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// flag = true;
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (flag) {
		// if (isWifiConnected()) {
		// flag = false;
		// Intent intent = new Intent(WiFiFailActivity.this,
		// WiFiSucActivity.class);
		// startActivity(intent);
		// finish();
		// }
		// }
		//
		// }
		// }).start();
		if (wifiManager.isWifiEnabled() == false) {
		/*	Intent intent = new Intent(WiFiFailActivity.this,
					WiFiNOActivity.class);
			startActivity(intent);
			finish();*/
		}
		// 页面统计
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		flag = false;
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
