package cn.hudun.wifi.pwd.ui;

import com.baidu.mobstat.StatService;

import cn.hudun.wifi.pwd.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ConnDialogActivity extends Activity implements OnClickListener {
	private TextView tv_wifi_name;
	private ImageView iv_close;
	private EditText ed_pwd;
	private TextView tv_conn;
	private WifiManager wifiManager;
	private String ssid;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.conn_dialog);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		initView();
		Intent intent = getIntent();
		tv_wifi_name.setText(intent.getStringExtra("ssid"));
		ssid = intent.getStringExtra("ssid");
		type = intent.getStringExtra("type");
	}

	private void initView() {
		tv_wifi_name = (TextView) findViewById(R.id.tv_wifi_name);
		iv_close = (ImageView) findViewById(R.id.iv_close);
		ed_pwd = (EditText) findViewById(R.id.ed_pwd);
		tv_conn = (TextView) findViewById(R.id.tv_conn);
		iv_close.setOnClickListener(this);
		tv_conn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_close:
			finish();
			break;
		case R.id.tv_conn:
			String pwd = ed_pwd.getText().toString();
			// System.out.println(ssid + type + pwd);
			WifiConfiguration wcg = CreateWifiInfo(ssid, pwd, type);
			int wcgID = wifiManager.addNetwork(wcg);
			if (wcgID == -1) {
				Toast.makeText(ConnDialogActivity.this, "连接失败！",
						Toast.LENGTH_SHORT).show();
				setResult(102);
			} else {
				boolean b = wifiManager.enableNetwork(wcgID, true);
				Toast.makeText(ConnDialogActivity.this, "连接成功！",
						Toast.LENGTH_SHORT).show();
				setResult(101);
			}

			finish();
			break;
		default:
			break;
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
		// 页面统计
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
}
