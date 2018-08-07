package cn.hudun.wifi.pwd.engine;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.hudun.wifi.pwd.bean.Wifi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.text.TextUtils;

public class WifiEngine {
	private Context context;
	private WifiManager wifiManager;
	private List<ScanResult> scanResults;
	private ScanResult scanResult;
	private List<Wifi> wifis = new ArrayList<Wifi>();
	private List<Wifi> scanWifis;
	private List<Wifi> locationWifis;
	private SharedPreferences sp;

	// private List<WifiConfiguration> wifiConfigurations;

	public WifiEngine(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		sp = context.getSharedPreferences("wifiPWD", Context.MODE_PRIVATE);
	}

	/**
	 * 获取可用范围内的wifi
	 * 
	 * @return
	 */
	public List<Wifi> getWifiScan() {
		scanWifis = new ArrayList<Wifi>();
		scanResults = wifiManager.getScanResults();
		if (scanResults.size() > 0) {
			for (int i = 0; i < scanResults.size(); i++) {
				scanResult = scanResults.get(i);
				Wifi wifi = new Wifi();
				wifi.setSsid(scanResult.SSID);
				wifi.setSignalLevel(scanResult.level);
				wifi.setPwd("无密码");
				wifi.setState(4);
				wifi.setFlag(1);
				wifi.setMac(scanResult.BSSID);// Mac地址

				String capabilities = scanResult.capabilities;
				if (!TextUtils.isEmpty(capabilities)) {
					if (capabilities.contains("WPA")
							|| capabilities.contains("wpa")) {
						wifi.setFree(false);
						wifi.setType("wpa");
					} else if (capabilities.contains("WEP")
							|| capabilities.contains("wep")) {
						wifi.setFree(false);
						wifi.setType("wep");
					} else {
						wifi.setFree(true);
						wifi.setType("noPass");
					}
				}

				scanWifis.add(wifi);
			}

		}
		return scanWifis;
	}

	/**
	 * 获取本地存储的wifi
	 * 
	 * @return
	 */
	public List<Wifi> getWifiLocation() {
		locationWifis = new ArrayList<Wifi>();
		if (isRoot()) {
			Process process = null;
			DataOutputStream dataOutputStream = null;
			DataInputStream dataInputStream = null;
			StringBuffer wifiConf = new StringBuffer();
			try {
				process = Runtime.getRuntime().exec("su");
				dataOutputStream = new DataOutputStream(
						process.getOutputStream());
				dataInputStream = new DataInputStream(process.getInputStream());
				dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
				dataOutputStream.writeBytes("exit\n");
				dataOutputStream.flush();
				InputStreamReader inputStreamReader = new InputStreamReader(
						dataInputStream, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					wifiConf.append(line);
				}
				bufferedReader.close();
				inputStreamReader.close();
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (dataOutputStream != null) {
						dataOutputStream.close();
					}
					if (dataInputStream != null) {
						dataInputStream.close();
					}
					process.destroy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}",
					Pattern.DOTALL);
			if (wifiConf.toString().length() != 0) {
				Editor editor = sp.edit();
				editor.putBoolean("isRoot", true);
				editor.commit();
				Matcher networkMatcher = network.matcher(wifiConf.toString());
				while (networkMatcher.find()) {
					String networkBlock = networkMatcher.group();
					Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
					Matcher ssidMatcher = ssid.matcher(networkBlock);
					if (ssidMatcher.find()) {
						Wifi wifi = new Wifi();
						wifi.setSsid(ssidMatcher.group(1));
						Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
						Matcher pskMatcher = psk.matcher(networkBlock);
						if (pskMatcher.find()) {
							wifi.setPwd(pskMatcher.group(1));
							wifi.setFree(false);
						} else {
							wifi.setPwd("无密码");
							wifi.setFree(true);
						}
						locationWifis.add(wifi);
					}
				}
			} else {
				Editor editor = sp.edit();
				editor.putBoolean("isRoot", false);
				editor.commit();
			}
		}
		return locationWifis;
	}

	public List<Wifi> getWifis(List<Wifi> scanWifis, List<Wifi> locationWifis) {

		if (locationWifis.size() != 0) {
			if (scanWifis.size() != 0) {
				for (int i = 0; i < locationWifis.size(); i++) {
					for (int j = 0; j < scanWifis.size(); j++) {
						if (locationWifis.get(i).getSsid()
								.equals(scanWifis.get(j).getSsid())) {
							scanWifis.get(j).setPwd(
									locationWifis.get(i).getPwd());
						} else {
							locationWifis.get(i).setSignalLevel(-96);
							locationWifis.get(i).setFree(false);
							locationWifis.get(i).setFlag(2);
						}
					}
				}
				scanWifis.addAll(locationWifis);
				for (int i = 0; i < scanWifis.size() - 1; i++) {
					for (int j = scanWifis.size() - 1; j > i; j--) {
						if (scanWifis.get(j).getSsid().trim()
								.equals(scanWifis.get(i).getSsid().trim())) {
							scanWifis.remove(j);
						}
					}
				}

				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ssid = null;
				if (wifiInfo.getSSID() == null
						|| wifiInfo.getSSID().length() <= 0
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
					for (int i = 0; i < scanWifis.size(); i++) {
						if (ssid.trim().equals(
								scanWifis.get(i).getSsid().trim())) {
							scanWifis.get(i).setState(1);
							scanWifis.get(i).setFree(true);
						}
					}
				}
				Collections.sort(scanWifis, new Comparator<Wifi>() {
					@Override
					public int compare(Wifi wifi1, Wifi wifi2) {
						if (wifi1.isFree()) {
							return -1;
						} else {
							return 1;
						}
					}
				});
				Collections.sort(scanWifis, new Comparator<Wifi>() {

					@Override
					public int compare(Wifi wifi1, Wifi wifi2) {

						if (wifi1.getState() == 1) {
							return -1;
						} else {
							return 1;
						}
					}
				});
				return scanWifis;
			} else {
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ssid = null;
				if (wifiInfo.getSSID() == null
						|| wifiInfo.getSSID().length() <= 0
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
					for (int i = 0; i < locationWifis.size(); i++) {
						if (ssid.trim().equals(
								locationWifis.get(i).getSsid().trim())) {
							locationWifis.get(i).setState(1);
							locationWifis.get(i).setFree(true);
						}
					}
				}

				Collections.sort(locationWifis, new Comparator<Wifi>() {

					@Override
					public int compare(Wifi wifi1, Wifi wifi2) {
						if (wifi1.isFree()) {
							return -1;
						} else {
							return 1;
						}

					}
				});
				Collections.sort(locationWifis, new Comparator<Wifi>() {

					@Override
					public int compare(Wifi wifi1, Wifi wifi2) {

						if (wifi1.getState() == 1) {
							return -1;
						} else {
							return 1;
						}

					}
				});
				return locationWifis;
			}

		} else {
			if (scanWifis.size() != 0) {
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ssid = null;
				if (wifiInfo.getSSID() == null
						|| wifiInfo.getSSID().length() <= 0
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
					for (int i = 0; i < scanWifis.size(); i++) {
						if (ssid.trim().equals(
								scanWifis.get(i).getSsid().trim())) {
							scanWifis.get(i).setState(1);
							scanWifis.get(i).setFree(true);
						}
					}
				}

				Collections.sort(scanWifis, new Comparator<Wifi>() {

					@Override
					public int compare(Wifi wifi1, Wifi wifi2) {

						if (wifi1.isFree()) {
							if (!wifi2.isFree()) {
								return -1;
							} else {
								return 0;
							}
						} else {
							return 1;
						}
					}
				});
				// Collections.sort(scanWifis, new Comparator<Wifi>() {
				//
				// @Override
				// public int compare(Wifi wifi1, Wifi wifi2) {
				//
				// if (wifi1.getState() == 1) {
				// return -1;
				// } else {
				// return 1;
				// }
				// }
				// });
				// return scanWifis;
			}
			// else {
			// return scanWifis;
			// }
			return scanWifis;
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

	/**
	 * 获取手机当前版本
	 * 
	 * @return
	 */
	public int getSdkVersion() {
		return VERSION.SDK_INT;
	}
}
