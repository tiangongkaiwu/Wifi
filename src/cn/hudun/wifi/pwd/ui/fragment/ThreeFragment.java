package cn.hudun.wifi.pwd.ui.fragment;

import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.ui.GuideActivity;
import cn.hudun.wifi.pwd.ui.WiFiFailActivity;
import cn.hudun.wifi.pwd.ui.WiFiSucActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ThreeFragment extends Fragment {
	private WifiManager wifiManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.three, container, false);
		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		TextView tv_three = (TextView) view.findViewById(R.id.tv_three);
		tv_three.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goHome();
				getActivity().finish();
			}
		});
		return view;
	}

	/**
	 * 主页
	 */
	private void goHome() {
/*		if (wifiManager.isWifiEnabled()) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if (wifiInfo.getSSID() == null || wifiInfo.getSSID().equals("0x")) {
				Intent intent = new Intent(getActivity(),
						WiFiFailActivity.class);
				intent.putExtra("isDownLoad",
						((GuideActivity) getActivity()).getIsDownLoad());
				getActivity().startActivity(intent);
				getActivity().finish();
				
				 * getActivity().overridePendingTransition(R.anim.splash_in,
				 * R.anim.splash_out);
				 
			} else {
				Intent intent = new Intent(getActivity(), WiFiSucActivity.class);
				intent.putExtra("isDownLoad",
						((GuideActivity) getActivity()).getIsDownLoad());
				getActivity().startActivity(intent);
				getActivity().finish();
				
				 * getActivity().overridePendingTransition(R.anim.splash_in,
				 * R.anim.splash_out);
				 
			}
		} else {
			Intent intent = new Intent(getActivity(), WiFiNOActivity.class);
			intent.putExtra("isDownLoad",
					((GuideActivity) getActivity()).getIsDownLoad());
			getActivity().startActivity(intent);
			getActivity().finish();
			
			 * getActivity().overridePendingTransition(R.anim.splash_in,
			 * R.anim.splash_out);
			 
		}*/
		
		
		Intent intent = new Intent(getActivity(), WiFiSucActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();

	}
}
