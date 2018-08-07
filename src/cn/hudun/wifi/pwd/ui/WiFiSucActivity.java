package cn.hudun.wifi.pwd.ui;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mobstat.StatService;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.adapter.WifinAdapterComm;
import cn.hudun.wifi.pwd.bean.Location;
import cn.hudun.wifi.pwd.bean.Wifi;
import cn.hudun.wifi.pwd.engine.UpdateEngine;
import cn.hudun.wifi.pwd.engine.WifiEngine;
import cn.hudun.wifi.pwd.utils.WebAppInterface;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class WiFiSucActivity extends Activity implements OnClickListener,
		OnDrawerOpenListener, OnDrawerCloseListener, OnRefreshListener,
		OnItemClickListener, OnHeaderClickListener {
	private boolean pwdMode = false;
	private TextView tv_top_name, tv_cp;
	private WifiManager wifiManager;
	private TextView tv_single;
	private TextView tv_speed;
	private TextView tv_net_speed;
	private TextView tv_wifi_dis;
	private RelativeLayout ll_share;
	private SlidingDrawer slidingDrawer;
	// private SwipeRefreshLayout ll_content;
	private ImageView iv_slide_icon;
	private WifinAdapterComm wifiAdapter;
	private List<Wifi> wifis;
	private List<Wifi> scanWifis;
	private List<Wifi> locationWifis;
	private ListView lv_wifi;
	private int pwd = 0;// 是否显示密码
	private WifiEngine wifiEngine;
	private Animation animation;
	private ImageView iv_top_prog;
	private int pos = -1;
	private ImageView iv_top, iv_no_wifi;
	// 声明PopupWindow对象的引用
	private PopupWindow popupWindow;
	private long exitTime = 0;// 返回键最后退出时间
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "bd09ll";// gcj02
	private JSONObject locationObject;
	private JSONArray locationArray;
	private JSONArray wifiArray;
	private View v;
	// private TextView textView2;
	private SharedPreferences sp;
	private List<Wifi> freeWifis;
	private List<Wifi> pwdWifis;
	private AlertDialog needRoot;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (pwd == 1) {
					WifiInfo wifiInfo2 = wifiManager.getConnectionInfo();
					if (wifiInfo2.getSSID() != null
							&& !wifiInfo2.getSSID().equals("0x")) {
						iv_top_prog.clearAnimation();
						iv_top_prog.setVisibility(View.GONE);
						Intent intent = new Intent(WiFiSucActivity.this,
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
					WifiInfo wifiInfo2 = wifiManager.getConnectionInfo();
					if (wifiInfo2.getSSID() != null
							&& !wifiInfo2.getSSID().equals("0x")) {
						iv_top_prog.clearAnimation();
						iv_top_prog.setVisibility(View.GONE);
						Intent intent = new Intent(WiFiSucActivity.this,
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
					Intent connIntent = new Intent(WiFiSucActivity.this,
							ConnDialogActivity.class);
					connIntent.putExtra("ssid", freeWifis.get(msg.arg1)
							.getSsid());
					connIntent.putExtra("type", freeWifis.get(msg.arg1)
							.getType());
					startActivityForResult(connIntent, 100);
				} else {
					Intent connIntent = new Intent(WiFiSucActivity.this,
							ConnDialogActivity.class);
					connIntent.putExtra("ssid", wifis.get(msg.arg1).getSsid());
					connIntent.putExtra("type", wifis.get(msg.arg1).getType());
					startActivityForResult(connIntent, 100);
				}

				break;
			case 3:
				iv_top_prog.clearAnimation();
				iv_top_prog.setVisibility(View.GONE);
				tv_wifi_dis.setText("已经断开");
				Intent intent = new Intent(WiFiSucActivity.this,
						WiFiFailActivity.class);
				startActivity(intent);
				finish();
				break;
			case 4:
				// ll_content.setRefreshing(false);
				if (wifiManager.isWifiEnabled() == false) {
					Toast.makeText(WiFiSucActivity.this, "WiFi未开启",
							Toast.LENGTH_SHORT).show();
					/*
					 * Intent intent2 = new Intent(WiFiSucActivity.this,
					 * WiFiNOActivity.class); startActivity(intent2); finish();
					 */
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
		setContentView(R.layout.wifi_conn_suc);
		
		WebView wv=(WebView) findViewById(R.id.wv);
		
		WebAppInterface.loadUrl(this, wv);
		
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		initView();
		sp = getSharedPreferences("wifiPWD", Context.MODE_PRIVATE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = null;
		// if (getSdkVersion() < 14) {
		if (wifiInfo.getSSID() == null || wifiInfo.getSSID().length() <= 0
				|| wifiInfo.getSSID().equals("unknown ssid")
				|| wifiInfo.getSSID().equals("UNINITIALIZED")) {
			ssid = null;
		} else {
			if (wifiInfo.getSSID().charAt(0) == '\"'
					&& wifiInfo.getSSID().charAt(
							wifiInfo.getSSID().length() - 1) == '\"') {
				ssid = wifiInfo.getSSID().substring(1,
						wifiInfo.getSSID().length() - 1);
			} else {
				ssid = wifiInfo.getSSID();
			}
		}
		// } else {
		// if (wifiInfo.getSSID() == null || wifiInfo.getSSID().length() <= 0) {
		// ssid = null;
		// } else {
		// ssid = wifiInfo.getSSID().substring(1,
		// wifiInfo.getSSID().length() - 1);
		// }
		// }
		if (ssid != null && !ssid.equals("0x")) {
			tv_top_name.setText(ssid);
		} else {
			tv_top_name.setText("WiFi密码查看器");
		}
		int sigLen = calculateSignalLevel(wifiInfo.getRssi(), 101);
		double speed = ((double) wifiInfo.getLinkSpeed()) / 8 / 8;
		if (sigLen == 0) {
			tv_single.setText(90 + "%");
		} else {
			tv_single.setText(sigLen + "%");
		}
		String relSpeed = getDouble(speed);
		if (speed <= 0) {
			tv_net_speed.setText(100 + "K/s");
			tv_speed.setText(100 + "K/s");
		} else {
			tv_net_speed.setText(relSpeed + "M/s");
			tv_speed.setText(relSpeed + "M/s");
		}
		wifiEngine = new WifiEngine(WiFiSucActivity.this);
		scanWifis = wifiEngine.getWifiScan();
		locationWifis = wifiEngine.getWifiLocation();
		wifis = wifiEngine.getWifis(scanWifis, locationWifis);
		if (wifis.size() == 0) {
			tv_cp.setVisibility(View.GONE);
			iv_no_wifi.setVisibility(View.VISIBLE);
		} else {
			tv_cp.setVisibility(View.VISIBLE);
			iv_no_wifi.setVisibility(View.GONE);
		}
		// for (int i = 0; i < wifis.size(); i++) {
		// if (wifis.get(i).isFree()) {
		// freeCount++;
		// } else {
		// pwdCount++;
		// }
		// }
		// if (freeCount == 0) {
		// Wifi freeWifi = new Wifi();
		// freeWifi.setSsid("hudun");
		// freeWifi.setFree(true);
		// wifis.add(freeWifi);
		// }
		//
		// if (pwdCount == 0) {
		// Wifi pwdWifi = new Wifi();
		// pwdWifi.setSsid("hudun2");
		// pwdWifi.setFree(false);
		// wifis.add(pwdWifi);
		// }
		//
		// Collections.sort(wifis, new Comparator<Wifi>() {
		//
		// @Override
		// public int compare(Wifi wifi1, Wifi wifi2) {
		// if (wifi1.isFree()) {// !=wifi2.isFree()
		// return -1;
		// }
		// return 1;
		//
		// }
		// });

		wifiAdapter = new WifinAdapterComm(WiFiSucActivity.this, wifis, false);
		lv_wifi.setAdapter(wifiAdapter);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				Location location2 = new Location();
				location2.setAddr(location.getAddrStr());
				location2.setTime(location.getTime());
				location2.setRadius(location.getRadius());
				location2.setLatItude(location.getLatitude());
				location2.setLongItude(location.getLongitude());
				locationArray = new JSONArray();
				locationObject = new JSONObject();
				JSONObject jsonObject2 = new JSONObject();
				try {
					jsonObject2.put("latitude", location2.getLatItude());
					jsonObject2.put("longitude", location2.getLongItude());
					jsonObject2.put("radius", location2.getRadius());
					jsonObject2.put("addr", location2.getAddr());
					jsonObject2.put("time", location2.getTime());
					locationArray.put(jsonObject2);
					locationObject.put("wifi", wifiArray);
					locationObject.put("location", locationArray);
					AsyncHttpClient client = new AsyncHttpClient();
					client.setTimeout(120000);
					RequestParams params = new RequestParams();// 设置参数
					params.put("wifi", locationObject);
					String url = "http://222.186.15.109:81/Api/Wifi/addWifi";
					client.post(url, params, new TextHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							// System.out.println(responseString);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								String responseString) {
							// System.out.println(responseString);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}); // 注册监听函数

		wifiArray = new JSONArray();
		try {
			for (int i = 0; i < wifis.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				if ((!wifis.get(i).getPwd().equals("无密码"))
						&& wifis.get(i).getFlag() != 2) {
					jsonObject.put("pwd", wifis.get(i).getPwd());
					jsonObject.put("ssid", wifis.get(i).getSsid());
					jsonObject.put("mac", wifis.get(i).getMac());
					wifiArray.put(jsonObject);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 3600000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

	
	}

	private void initView() {
		wifis = new ArrayList<Wifi>();
		scanWifis = new ArrayList<Wifi>();
		locationWifis = new ArrayList<Wifi>();
		tv_cp = (TextView) findViewById(R.id.tv_cp);
		tv_cp.setOnClickListener(this);

		iv_no_wifi = (ImageView) findViewById(R.id.iv_no_wifi);

		tv_top_name = (TextView) findViewById(R.id.tv_top_name);
		tv_single = (TextView) findViewById(R.id.tv_single);
		tv_speed = (TextView) findViewById(R.id.tv_speed);
		tv_wifi_dis = (TextView) findViewById(R.id.tv_wifi_dis);
		tv_net_speed = (TextView) findViewById(R.id.tv_net_speed);
		ll_share = (RelativeLayout) findViewById(R.id.ll_share);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.drawer);
		// ll_content = (SwipeRefreshLayout) findViewById(R.id.content);
		iv_slide_icon = (ImageView) findViewById(R.id.iv_slide_icon);
		lv_wifi = (ListView) findViewById(R.id.lv_wifi);
		slidingDrawer.getContent().setBackgroundColor(
				getResources().getColor(R.color.white1));
		slidingDrawer.setOnDrawerOpenListener(this);
		slidingDrawer.setOnDrawerCloseListener(this);
		ll_share.setOnClickListener(this);
		tv_wifi_dis.setOnClickListener(this);
		/*
		 * ll_content.setOnRefreshListener(this);
		 * ll_content.setColorScheme(android.R.color.holo_blue_bright,
		 * android.R.color.holo_green_light, android.R.color.holo_orange_light,
		 * android.R.color.holo_red_light);
		 */
		lv_wifi.setOnItemClickListener(this);
		iv_top_prog = (ImageView) findViewById(R.id.iv_top_prog);
		iv_top = (ImageView) findViewById(R.id.iv_top);
		iv_top.setOnClickListener(this);
		v = findViewById(R.id.v);
		// textView2 = (TextView) findViewById(R.id.textView2);
		slidingDrawer.animateOpen();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cp:
			if (!pwdMode) {
				pwdMode = !pwdMode;
				WifinAdapterComm adapter = new WifinAdapterComm(this, wifis,
						pwdMode);
				lv_wifi.setAdapter(adapter);
			}
			break;
		case R.id.ll_share:// 分享
			showShare();
			break;
		case R.id.tv_wifi_dis:// 断开
			if (tv_wifi_dis.getText().equals("已断开")) {
				Toast.makeText(WiFiSucActivity.this, "WiFi已断开",
						Toast.LENGTH_SHORT).show();
			} else {
				tv_wifi_dis.setText("正在断开...");
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				wifiManager.disableNetwork(wifiInfo.getNetworkId());
				iv_top_prog.setVisibility(View.VISIBLE);
				animation = AnimationUtils.loadAnimation(WiFiSucActivity.this,
						R.anim.rotate_animation);
				iv_top_prog.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(3, 2500);
			}
			break;

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
			UpdateEngine updateEngine = new UpdateEngine(WiFiSucActivity.this);
			updateEngine.getUpdate();
			break;
		case R.id.ll_pop_exit:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onDrawerOpened() {
		// ll_slide_title.setVisibility(View.VISIBLE);
		// iv_slide_icon.setVisibility(View.GONE);
		// // rl_suc.setVisibility(View.GONE);
		// ll_speed.setVisibility(View.GONE);
		// ll_share.setVisibility(View.GONE);
		v.setVisibility(View.GONE);
		iv_slide_icon.setBackgroundResource(R.drawable.ic_down);
	}

	@Override
	public void onDrawerClosed() {
		// ll_slide_title.setVisibility(View.GONE);
		// iv_slide_icon.setVisibility(View.VISIBLE);
		// // rl_suc.setVisibility(View.VISIBLE);
		// ll_speed.setVisibility(View.VISIBLE);
		// ll_share.setVisibility(View.VISIBLE);
		v.setVisibility(View.VISIBLE);
		iv_slide_icon.setBackgroundResource(R.drawable.ic_up);
	}

	@Override
	public void onRefresh() {
		// initDate();
		// mHandler.sendEmptyMessageDelayed(4, 1000);
		// ll_content.setRefreshing(false);
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
					wifiAdapter = new WifinAdapterComm(WiFiSucActivity.this,
							freeWifis, true);
					lv_wifi.setAdapter(wifiAdapter);
					pwd = 1;
				} else {
					wifiAdapter = new WifinAdapterComm(WiFiSucActivity.this,
							wifis, false);
					lv_wifi.setAdapter(wifiAdapter);
					pwd = 0;
				}
			} else {
				showNeedRootDialog();
			}

		}
	}

	private void showNeedRootDialog() {
		if (needRoot == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("需要root权限才能查看");
			builder.setNeutralButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							needRoot.dismiss();
						}
					});
			builder.setNegativeButton("一键ROOT",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							needRoot.dismiss();
							/*
							 * HsLibrary.autoDownOneKeyRoot(WiFiSucActivity.this)
							 * ;
							 */
						}
					});
			needRoot = builder.create();
		}
		needRoot.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*
		 * if ((position == 0 && wifis.get(position).getState() == 1) ||
		 * wifis.get(position).getSsid().equals("hudun") ||
		 * wifis.get(position).getSsid().equals("hudun2")) { return; }
		 * 
		 * if (pwd == 1) { if (freeWifis.get(position).getSignalLevel() <= -96)
		 * { Toast.makeText(WiFiSucActivity.this, "超出无线覆盖范围",
		 * Toast.LENGTH_SHORT).show(); } else { pos = position;
		 * iv_top_prog.setVisibility(View.VISIBLE); animation =
		 * AnimationUtils.loadAnimation(WiFiSucActivity.this,
		 * R.anim.rotate_animation); iv_top_prog.startAnimation(animation);
		 * WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		 * wifiManager.disableNetwork(wifiInfo.getNetworkId()); //
		 * wifiManager.setWifiEnabled(false); wifiManager.disconnect(); int
		 * index = -1; if (freeWifis.get(position).isFree()) { if
		 * (freeWifis.get(position).getPwd().equals("无密码")) { WifiConfiguration
		 * wcg = CreateWifiInfo( freeWifis.get(position).getSsid(), null,
		 * "noPass"); int wcgID = wifiManager.addNetwork(wcg); if (wcgID == -1)
		 * { Message message = new Message(); message.what = 1; message.arg1 =
		 * position; mHandler.sendMessageDelayed(message, 200); } else { boolean
		 * b = wifiManager.enableNetwork(wcgID, true); Message message = new
		 * Message(); message.what = 1; message.arg1 = position;
		 * wifis.get(position).setState(2); wifiAdapter.notifyDataSetChanged();
		 * mHandler.sendMessageDelayed(message, 3000); } } else {
		 * wifiConfigurations = wifiManager .getConfiguredNetworks(); for (int i
		 * = 0; i < wifiConfigurations.size(); i++) { String ssid =
		 * wifiConfigurations.get(i).SSID .substring(1,
		 * wifiConfigurations.get(i).SSID .length() - 1); if
		 * (freeWifis.get(position).getSsid().equals(ssid)) { index = i; } } if
		 * (index == -1) { Message message = new Message(); message.what = 2;
		 * message.arg1 = position; mHandler.sendMessageDelayed(message, 200); }
		 * else { wifiManager.enableNetwork(
		 * wifiConfigurations.get(index).networkId, true); Message message = new
		 * Message(); message.what = 1; message.arg1 = position;
		 * freeWifis.get(position).setState(2);
		 * wifiAdapter.notifyDataSetChanged();
		 * mHandler.sendMessageDelayed(message, 3000); } } } else {
		 * wifiConfigurations = wifiManager.getConfiguredNetworks(); for (int i
		 * = 0; i < wifiConfigurations.size(); i++) { String ssid =
		 * wifiConfigurations.get(i).SSID.substring( 1,
		 * wifiConfigurations.get(i).SSID.length() - 1); if
		 * (freeWifis.get(position).getSsid().equals(ssid)) { index = i; } } if
		 * (index == -1) { Message message = new Message(); message.what = 2;
		 * message.arg1 = position; mHandler.sendMessageDelayed(message, 200); }
		 * else { wifiManager.enableNetwork(
		 * wifiConfigurations.get(index).networkId, true); Message message = new
		 * Message(); message.what = 1; message.arg1 = position;
		 * freeWifis.get(position).setState(2);
		 * wifiAdapter.notifyDataSetChanged();
		 * mHandler.sendMessageDelayed(message, 3000); } }
		 * 
		 * } } else { if (wifis.get(position).getSignalLevel() <= -96) {
		 * Toast.makeText(WiFiSucActivity.this, "超出无线覆盖范围",
		 * Toast.LENGTH_SHORT).show(); } else { pos = position;
		 * iv_top_prog.setVisibility(View.VISIBLE); animation =
		 * AnimationUtils.loadAnimation(WiFiSucActivity.this,
		 * R.anim.rotate_animation); iv_top_prog.startAnimation(animation);
		 * WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		 * wifiManager.disableNetwork(wifiInfo.getNetworkId()); //
		 * wifiManager.setWifiEnabled(false); wifiManager.disconnect(); int
		 * index = -1; if (wifis.get(position).isFree()) { if
		 * (wifis.get(position).getPwd().equals("无密码")) { WifiConfiguration wcg
		 * = CreateWifiInfo( wifis.get(position).getSsid(), null, "noPass"); int
		 * wcgID = wifiManager.addNetwork(wcg); if (wcgID == -1) { Message
		 * message = new Message(); message.what = 1; message.arg1 = position;
		 * mHandler.sendMessageDelayed(message, 200); } else { boolean b =
		 * wifiManager.enableNetwork(wcgID, true); Message message = new
		 * Message(); message.what = 1; message.arg1 = position;
		 * wifis.get(position).setState(2); wifiAdapter.notifyDataSetChanged();
		 * mHandler.sendMessageDelayed(message, 3000); } } else {
		 * wifiConfigurations = wifiManager .getConfiguredNetworks(); for (int i
		 * = 0; i < wifiConfigurations.size(); i++) { String ssid =
		 * wifiConfigurations.get(i).SSID .substring(1,
		 * wifiConfigurations.get(i).SSID .length() - 1); if
		 * (wifis.get(position).getSsid().equals(ssid)) { index = i; } } if
		 * (index == -1) { Message message = new Message(); message.what = 2;
		 * message.arg1 = position; mHandler.sendMessageDelayed(message, 200); }
		 * else { wifiManager.enableNetwork(
		 * wifiConfigurations.get(index).networkId, true); Message message = new
		 * Message(); message.what = 1; message.arg1 = position;
		 * wifis.get(position).setState(2); wifiAdapter.notifyDataSetChanged();
		 * mHandler.sendMessageDelayed(message, 3000); } } } else {
		 * wifiConfigurations = wifiManager.getConfiguredNetworks(); for (int i
		 * = 0; i < wifiConfigurations.size(); i++) { String ssid =
		 * wifiConfigurations.get(i).SSID.substring( 1,
		 * wifiConfigurations.get(i).SSID.length() - 1); if
		 * (wifis.get(position).getSsid().equals(ssid)) { index = i; } } if
		 * (index == -1) { Message message = new Message(); message.what = 2;
		 * message.arg1 = position; mHandler.sendMessageDelayed(message, 200); }
		 * else { wifiManager.enableNetwork(
		 * wifiConfigurations.get(index).networkId, true); Message message = new
		 * Message(); message.what = 1; message.arg1 = position;
		 * wifis.get(position).setState(2); wifiAdapter.notifyDataSetChanged();
		 * mHandler.sendMessageDelayed(message, 3000); } }
		 * 
		 * } }
		 */
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
					Intent intent = new Intent(WiFiSucActivity.this,
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
					tv_top_name.setText("WiFi密码查看器");
					tv_single.setText(0 + "%");
					tv_net_speed.setText(0 + "K/s");
					tv_speed.setText(0 + "K/s");
					// textView2.setText("未连接");
					tv_wifi_dis.setText("已断开");
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
					tv_top_name.setText("WiFi密码查看器");
					tv_single.setText(0 + "%");
					tv_net_speed.setText(0 + "K/s");
					tv_speed.setText(0 + "K/s");
					// textView2.setText("未连接");
					tv_wifi_dis.setText("已断开");
					wifiAdapter.notifyDataSetChanged();
				}

			}
		}
	}

	public void initDate() {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = null;
		if (wifiInfo.getSSID() == null || wifiInfo.getSSID().length() <= 0
				|| wifiInfo.getSSID().equals("unknown ssid")
				|| wifiInfo.getSSID().equals("UNINITIALIZED")) {
			ssid = null;
		} else {
			if (wifiInfo.getSSID().charAt(0) == '\"'
					&& wifiInfo.getSSID().charAt(
							wifiInfo.getSSID().length() - 1) == '\"') {
				ssid = wifiInfo.getSSID().substring(1,
						wifiInfo.getSSID().length() - 1);
			} else {
				ssid = wifiInfo.getSSID();
			}
		}
		if (ssid != null && !ssid.equals("0x")) {
			tv_top_name.setText(ssid);
		} else {
			tv_top_name.setText("WiFi密码查看器");
		}
		int sigLen = calculateSignalLevel(wifiInfo.getRssi(), 101);
		double speed = ((double) wifiInfo.getLinkSpeed()) / 8 / 8;
		String units = WifiInfo.LINK_SPEED_UNITS;
		String relSpeed = getDouble(speed);
		if (sigLen == 0) {
			tv_single.setText(90 + "%");
		} else {
			tv_single.setText(sigLen + "%");
		}
		if (speed <= 0) {
			tv_net_speed.setText(100 + "K/s");
			tv_speed.setText(100 + "K/s");
		} else {
			tv_net_speed.setText(relSpeed + "M/s");
			tv_speed.setText(relSpeed + "M/s");
		}
		wifiEngine = new WifiEngine(WiFiSucActivity.this);
		scanWifis = wifiEngine.getWifiScan();
		locationWifis = wifiEngine.getWifiLocation();
		wifis = wifiEngine.getWifis(scanWifis, locationWifis);
		wifiAdapter = new WifinAdapterComm(WiFiSucActivity.this, wifis, false);
		lv_wifi.setAdapter(wifiAdapter);
		if (wifis.size() == 0) {
			tv_cp.setVisibility(View.GONE);
		} else {
			tv_cp.setVisibility(View.VISIBLE);
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
				Toast.makeText(WiFiSucActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
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
		if (wifiManager.isWifiEnabled() == false) {
			/*
			 * Intent intent = new Intent(WiFiSucActivity.this,
			 * WiFiNOActivity.class); startActivity(intent); finish();
			 */
			tv_top_name.setText("wifi密码查看器");
		}
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

	/**
	 * 获取手机当前版本
	 * 
	 * @return
	 */
	public int getSdkVersion() {
		return VERSION.SDK_INT;
	}

	/**
	 * 保留两位小数
	 * 
	 * @param d
	 * @return
	 */
	public String getDouble(Double d) {
		DecimalFormat format = new DecimalFormat("####0.0");
		return format.format(d);

	}

	public int calculateSignalLevel(int rssi, int numLevels) {

		int MIN_RSSI = -100;
		int MAX_RSSI = -55;
		int levels = 101;

		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return levels - 1;
		} else {
			float inputRange = (MAX_RSSI - MIN_RSSI);
			float outputRange = (levels - 1);
			return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
		}
	}

	/**
	 * 判断当前手机是否有ROOT权限
	 * 
	 * @return
	 */
	public boolean isRoot() {
		try {
			if ((!new File("/system/bin/su").exists())
					&& (!new File("/system/xbin/su").exists())) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

}
