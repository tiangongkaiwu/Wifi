package cn.hudun.wifi.pwd.adapter;

import java.util.List;

import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.bean.Wifi;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifinAdapterComm extends BaseAdapter {
	Context context;
	List<Wifi> wifis;
	boolean isPwd;

	public WifinAdapterComm(Context context, List<Wifi> wifis, boolean isPwd) {
		this.context = context;
		this.wifis = wifis;
		this.isPwd = isPwd;
		// sections.add("免费上网");
		// sections.add(" 需要密码");
		/*
		 * freeWifis = new ArrayList<Wifi>(); pwdWifis = new ArrayList<Wifi>();
		 * for (Wifi wifi : wifis) { if (wifi.isFree()) { freeWifis.add(wifi); }
		 * else { pwdWifis.add(wifi); } }
		 */
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wifis.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return wifis.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		FreeHolder freeHolder;
		Wifi wifi = wifis.get(position);
		if (convertView == null) {
			freeHolder = new FreeHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.wifi_item_free, null);
			freeHolder.iv_wifi_free_single = (ImageView) convertView
					.findViewById(R.id.iv_wifi_free_single);
			freeHolder.tv_wifi_free_ssid = (TextView) convertView
					.findViewById(R.id.tv_wifi_free_ssid);
			freeHolder.tv_wifi_free_conn = (TextView) convertView
					.findViewById(R.id.tv_wifi_free_conn);
			convertView.setTag(freeHolder);
		} else {
			freeHolder = (FreeHolder) convertView.getTag();
		}
		freeHolder.tv_wifi_free_ssid.setText(wifis.get(position).getSsid());
		if (isPwd) {
			freeHolder.tv_wifi_free_conn.setText(wifi.getPwd());
		} else {
			if (wifi.getPwd().equals("无密码")) {
				freeHolder.tv_wifi_free_conn.setText("无密码");
			} else {
				freeHolder.tv_wifi_free_conn.setText("已保存密码");
			}

		}

		if (wifis.get(position).getSignalLevel() >= -55) {
			freeHolder.iv_wifi_free_single
					.setBackgroundResource(R.drawable.ic_single_full);
		} else if (wifis.get(position).getSignalLevel() >= -70
				&& wifis.get(position).getSignalLevel() <= -56) {
			freeHolder.iv_wifi_free_single
					.setBackgroundResource(R.drawable.ic_single_three);
		} else if (wifis.get(position).getSignalLevel() >= -85
				&& wifis.get(position).getSignalLevel() <= -71) {
			freeHolder.iv_wifi_free_single
					.setBackgroundResource(R.drawable.ic_single_two);
		} else if (wifis.get(position).getSignalLevel() >= -95
				&& wifis.get(position).getSignalLevel() <= -86) {
			freeHolder.iv_wifi_free_single
					.setBackgroundResource(R.drawable.ic_single_one);
		} else {
			freeHolder.iv_wifi_free_single
					.setBackgroundResource(R.drawable.ic_single_no);
		}

		// TODO Auto-generated method stub
		/*
		 * if (wifis.get(position).isFree()) { FreeHolder freeHolder = new
		 * FreeHolder(); convertView = View.inflate(context,
		 * R.layout.wifi_item_free, null); freeHolder.iv_wifi_free_single =
		 * (ImageView) convertView .findViewById(R.id.iv_wifi_free_single);
		 * freeHolder.tv_wifi_free_ssid = (TextView) convertView
		 * .findViewById(R.id.tv_wifi_free_ssid); freeHolder.tv_wifi_free_conn =
		 * (TextView) convertView .findViewById(R.id.tv_wifi_free_conn);
		 * freeHolder.tv_wifi_free_conning = (TextView) convertView
		 * .findViewById(R.id.tv_wifi_free_conning);
		 * freeHolder.tv_wifi_free_conned = (TextView) convertView
		 * .findViewById(R.id.tv_wifi_free_conned); freeHolder.tv_wifi_free_fail
		 * = (TextView) convertView .findViewById(R.id.tv_wifi_free_fail);
		 * freeHolder.tv_wifi_free_ssid.setText(wifis.get(position).getSsid());
		 * if (wifis.get(position).getState() == 1) {
		 * freeHolder.tv_wifi_free_conned.setVisibility(View.VISIBLE);
		 * freeHolder.tv_wifi_free_fail.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_conning.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_conn.setVisibility(View.GONE); } else if
		 * (wifis.get(position).getState() == 2) {
		 * freeHolder.tv_wifi_free_conned.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_fail.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_conning.setVisibility(View.VISIBLE);
		 * freeHolder.tv_wifi_free_conn.setVisibility(View.GONE); } else if
		 * (wifis.get(position).getState() == 3) {
		 * freeHolder.tv_wifi_free_conned.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_fail.setVisibility(View.VISIBLE);
		 * freeHolder.tv_wifi_free_conning.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_conn.setVisibility(View.GONE); } else {
		 * freeHolder.tv_wifi_free_conned.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_fail.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_conning.setVisibility(View.GONE);
		 * freeHolder.tv_wifi_free_conn.setVisibility(View.VISIBLE); } if
		 * (wifis.get(position).getSignalLevel() >= -55) {
		 * freeHolder.iv_wifi_free_single
		 * .setBackgroundResource(R.drawable.ic_single_full); } else if
		 * (wifis.get(position).getSignalLevel() >= -70 &&
		 * wifis.get(position).getSignalLevel() <= -56) {
		 * freeHolder.iv_wifi_free_single
		 * .setBackgroundResource(R.drawable.ic_single_three); } else if
		 * (wifis.get(position).getSignalLevel() >= -85 &&
		 * wifis.get(position).getSignalLevel() <= -71) {
		 * freeHolder.iv_wifi_free_single
		 * .setBackgroundResource(R.drawable.ic_single_two); } else if
		 * (wifis.get(position).getSignalLevel() >= -90 &&
		 * wifis.get(position).getSignalLevel() <= -86) {
		 * freeHolder.iv_wifi_free_single
		 * .setBackgroundResource(R.drawable.ic_single_one); } else {
		 * freeHolder.iv_wifi_free_single
		 * .setBackgroundResource(R.drawable.ic_single_no); } } else { PwdHolder
		 * pwdHolder = new PwdHolder(); convertView = View.inflate(context,
		 * R.layout.wifi_item_pwd, null); pwdHolder.iv_wifi_item_single =
		 * (ImageView) convertView .findViewById(R.id.iv_wifi_item_single);
		 * pwdHolder.tv_wifi_item_conn = (TextView) convertView
		 * .findViewById(R.id.tv_wifi_item_conn); pwdHolder.tv_wifi_item_ssid =
		 * (TextView) convertView .findViewById(R.id.tv_wifi_item_ssid); if
		 * (isPwd) { pwdHolder.tv_wifi_item_ssid.setText(wifis.get(position)
		 * .getSsid()); pwdHolder.tv_wifi_item_ssid.setTextColor(Color.GRAY);
		 * 
		 * if (wifis.get(position).getState() == 5) {
		 * pwdHolder.tv_wifi_item_conn.setText("无法连接"); } else if
		 * (wifis.get(position).getState() == 5) {
		 * pwdHolder.tv_wifi_item_conn.setText("已保存"); } else if
		 * (wifis.get(position).getState() == 2) {
		 * pwdHolder.tv_wifi_item_conn.setText("正在连接"); } else { if
		 * (wifis.get(position).getPwd() != null &&
		 * (!wifis.get(position).getPwd().equals("无密码"))) {
		 * pwdHolder.tv_wifi_item_conn.setText(wifis.get(position) .getPwd());
		 * pwdHolder.tv_wifi_item_conn .setBackgroundResource(R.color.white1);
		 * pwdHolder.tv_wifi_item_conn.setPadding(0, 0, 10, 0);
		 * pwdHolder.tv_wifi_item_conn.setTextColor(context
		 * .getResources().getColor(R.color.textColor));
		 * 
		 * } else { // pwdHolder.tv_wifi_item_conn.setText("需要密码");
		 * pwdHolder.tv_wifi_item_conn.setTextColor(Color.GRAY); } } } else {
		 * pwdHolder.tv_wifi_item_ssid.setText(wifis.get(position) .getSsid());
		 * pwdHolder.tv_wifi_item_ssid.setTextColor(Color.GRAY); if
		 * (wifis.get(position).getState() == 5) {
		 * pwdHolder.tv_wifi_item_conn.setText("无法连接"); } else if
		 * (wifis.get(position).getState() == 5) {
		 * pwdHolder.tv_wifi_item_conn.setText("已保存密码"); } else if
		 * (wifis.get(position).getState() == 2) {
		 * pwdHolder.tv_wifi_item_conn.setText("正在连接"); } else {
		 * pwdHolder.tv_wifi_item_conn.setText("已保存密码"); } } if
		 * (wifis.get(position).getSignalLevel() >= -55) {
		 * pwdHolder.iv_wifi_item_single
		 * .setBackgroundResource(R.drawable.ic_single_full); } else if
		 * (wifis.get(position).getSignalLevel() >= -70 &&
		 * wifis.get(position).getSignalLevel() <= -56) {
		 * pwdHolder.iv_wifi_item_single
		 * .setBackgroundResource(R.drawable.ic_single_three); } else if
		 * (wifis.get(position).getSignalLevel() >= -85 &&
		 * wifis.get(position).getSignalLevel() <= -71) {
		 * pwdHolder.iv_wifi_item_single
		 * .setBackgroundResource(R.drawable.ic_single_two); } else if
		 * (wifis.get(position).getSignalLevel() >= -95 &&
		 * wifis.get(position).getSignalLevel() <= -86) {
		 * pwdHolder.iv_wifi_item_single
		 * .setBackgroundResource(R.drawable.ic_single_one); } else {
		 * pwdHolder.iv_wifi_item_single
		 * .setBackgroundResource(R.drawable.ic_single_no); } }
		 */
		return convertView;
	}

	class FreeHolder {
		ImageView iv_wifi_free_single;
		TextView tv_wifi_free_ssid, tv_wifi_free_conn, tv_wifi_free_conning,
				tv_wifi_free_conned, tv_wifi_free_fail;
	}

	class PwdHolder {
		ImageView iv_wifi_item_single;
		TextView tv_wifi_item_conn, tv_wifi_item_ssid;
	}

}
