package cn.hudun.wifi.pwd.adapter;

import java.util.ArrayList;
import java.util.List;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;

import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.bean.Wifi;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class WifiAdapter extends BaseAdapter implements
		StickyListHeadersAdapter, SectionIndexer {
	/** 内容 */
	private List<Wifi> wifis;
	/** head */
	private List<String> sections = new ArrayList<String>();
	private Context context;
	private List<Wifi> freeWifis;
	private List<Wifi> pwdWifis;
	private boolean isPwd;

	public WifiAdapter(Context context, List<Wifi> wifis, boolean isPwd) {
		this.context = context;
		this.wifis = wifis;
		this.isPwd = isPwd;
		// sections.add("免费上网");
		// sections.add(" 需要密码");
		freeWifis = new ArrayList<Wifi>();
		pwdWifis = new ArrayList<Wifi>();
		for (Wifi wifi : wifis) {
			if (wifi.isFree()) {
				freeWifis.add(wifi);
			} else {
				pwdWifis.add(wifi);
			}
		}
	}

	@Override
	public int getCount() {
		return wifis.size();
	}

	@Override
	public Object getItem(int position) {
		return wifis.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ((wifis.get(position).getSsid().equals("hudun") && wifis.get(
				position).isFree())
				|| wifis.get(position).getSsid().equals("hudun2")) {
			convertView = View.inflate(context, R.layout.wifi_not_found, null);
		} else {
			if (wifis.get(position).isFree()) {
				FreeHolder freeHolder = new FreeHolder();
				convertView = View.inflate(context, R.layout.wifi_item_free,
						null);
				freeHolder.iv_wifi_free_single = (ImageView) convertView
						.findViewById(R.id.iv_wifi_free_single);
				freeHolder.tv_wifi_free_ssid = (TextView) convertView
						.findViewById(R.id.tv_wifi_free_ssid);
				freeHolder.tv_wifi_free_conn = (TextView) convertView
						.findViewById(R.id.tv_wifi_free_conn);
				freeHolder.tv_wifi_free_conning = (TextView) convertView
						.findViewById(R.id.tv_wifi_free_conning);
				freeHolder.tv_wifi_free_conned = (TextView) convertView
						.findViewById(R.id.tv_wifi_free_conned);
				freeHolder.tv_wifi_free_fail = (TextView) convertView
						.findViewById(R.id.tv_wifi_free_fail);
				freeHolder.tv_wifi_free_ssid.setText(wifis.get(position)
						.getSsid());
				if (wifis.get(position).getState() == 1) {
					freeHolder.tv_wifi_free_conned.setVisibility(View.VISIBLE);
					freeHolder.tv_wifi_free_fail.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_conning.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_conn.setVisibility(View.GONE);
				} else if (wifis.get(position).getState() == 2) {
					freeHolder.tv_wifi_free_conned.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_fail.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_conning.setVisibility(View.VISIBLE);
					freeHolder.tv_wifi_free_conn.setVisibility(View.GONE);
				} else if (wifis.get(position).getState() == 3) {
					freeHolder.tv_wifi_free_conned.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_fail.setVisibility(View.VISIBLE);
					freeHolder.tv_wifi_free_conning.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_conn.setVisibility(View.GONE);
				} else {
					freeHolder.tv_wifi_free_conned.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_fail.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_conning.setVisibility(View.GONE);
					freeHolder.tv_wifi_free_conn.setVisibility(View.VISIBLE);
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
				} else if (wifis.get(position).getSignalLevel() >= -90
						&& wifis.get(position).getSignalLevel() <= -86) {
					freeHolder.iv_wifi_free_single
							.setBackgroundResource(R.drawable.ic_single_one);
				} else {
					freeHolder.iv_wifi_free_single
							.setBackgroundResource(R.drawable.ic_single_no);
				}
			} else {
				PwdHolder pwdHolder = new PwdHolder();
				convertView = View.inflate(context, R.layout.wifi_item_pwd,
						null);
				pwdHolder.iv_wifi_item_single = (ImageView) convertView
						.findViewById(R.id.iv_wifi_item_single);
				pwdHolder.tv_wifi_item_conn = (TextView) convertView
						.findViewById(R.id.tv_wifi_item_conn);
				pwdHolder.tv_wifi_item_ssid = (TextView) convertView
						.findViewById(R.id.tv_wifi_item_ssid);
				if (isPwd) {
					pwdHolder.tv_wifi_item_ssid.setText(wifis.get(position)
							.getSsid());
					pwdHolder.tv_wifi_item_ssid.setTextColor(Color.GRAY);

					if (wifis.get(position).getState() == 5) {
						pwdHolder.tv_wifi_item_conn.setText("无法连接");
					} else if (wifis.get(position).getState() == 5) {
						pwdHolder.tv_wifi_item_conn.setText("连接");
					} else if (wifis.get(position).getState() == 2) {
						pwdHolder.tv_wifi_item_conn.setText("正在连接");
					} else {
						if (wifis.get(position).getPwd() != null
								&& (!wifis.get(position).getPwd().equals("无密码"))) {
							pwdHolder.tv_wifi_item_conn.setText(wifis.get(
									position).getPwd());
							pwdHolder.tv_wifi_item_conn
									.setBackgroundResource(R.color.white1);
							pwdHolder.tv_wifi_item_conn.setPadding(0, 0, 10, 0);
							pwdHolder.tv_wifi_item_conn
									.setTextColor(context.getResources()
											.getColor(R.color.textColor));

						} else {
							pwdHolder.tv_wifi_item_conn.setText("密码访问");
							pwdHolder.tv_wifi_item_conn
									.setTextColor(Color.GRAY);
						}
					}
				} else {
					pwdHolder.tv_wifi_item_ssid.setText(wifis.get(position)
							.getSsid());
					pwdHolder.tv_wifi_item_ssid.setTextColor(Color.GRAY);
					if (wifis.get(position).getState() == 5) {
						pwdHolder.tv_wifi_item_conn.setText("无法连接");
					} else if (wifis.get(position).getState() == 5) {
						pwdHolder.tv_wifi_item_conn.setText("连接");
					} else if (wifis.get(position).getState() == 2) {
						pwdHolder.tv_wifi_item_conn.setText("正在连接");
					} else {
						pwdHolder.tv_wifi_item_conn.setText("连接");
					}
				}
				if (wifis.get(position).getSignalLevel() >= -55) {
					pwdHolder.iv_wifi_item_single
							.setBackgroundResource(R.drawable.ic_single_full);
				} else if (wifis.get(position).getSignalLevel() >= -70
						&& wifis.get(position).getSignalLevel() <= -56) {
					pwdHolder.iv_wifi_item_single
							.setBackgroundResource(R.drawable.ic_single_three);
				} else if (wifis.get(position).getSignalLevel() >= -85
						&& wifis.get(position).getSignalLevel() <= -71) {
					pwdHolder.iv_wifi_item_single
							.setBackgroundResource(R.drawable.ic_single_two);
				} else if (wifis.get(position).getSignalLevel() >= -95
						&& wifis.get(position).getSignalLevel() <= -86) {
					pwdHolder.iv_wifi_item_single
							.setBackgroundResource(R.drawable.ic_single_one);
				} else {
					pwdHolder.iv_wifi_item_single
							.setBackgroundResource(R.drawable.ic_single_no);
				}
			}
		}
		return convertView;
	}

	@Override
	public Object[] getSections() {
		return sections.toArray(new String[sections.size()]);
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= sections.size()) {
			section = sections.size() - 1;
		} else if (section < 0) {
			section = 0;
		}
		return section;
	}

	@Override
	public int getSectionForPosition(int position) {
		if (wifis.get(position).isFree()) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder headerViewHolder;
		if (convertView == null) {
			headerViewHolder = new HeaderViewHolder();
			convertView = View.inflate(context, R.layout.group, null);
			headerViewHolder.tv_group = (TextView) convertView
					.findViewById(R.id.tv_group);
			headerViewHolder.tv_check = (TextView) convertView
					.findViewById(R.id.tv_check);
			headerViewHolder.iv_group = (ImageView) convertView
					.findViewById(R.id.iv_group);
			convertView.setTag(headerViewHolder);
		} else {
			headerViewHolder = (HeaderViewHolder) convertView.getTag();
		}
		if (getHeaderId(position) == 0) {
			headerViewHolder.tv_group.setText("免费上网");
			headerViewHolder.tv_check.setVisibility(View.GONE);
			headerViewHolder.iv_group
					.setBackgroundResource(R.drawable.ic_unlock);
		} else {
			headerViewHolder.tv_group.setText(" 需要密码");
			headerViewHolder.tv_check.setVisibility(View.VISIBLE);
			headerViewHolder.iv_group.setBackgroundResource(R.drawable.ic_lock);
		}
		convertView.setVisibility(View.GONE);
		return convertView;

	}

	@Override
	public long getHeaderId(int position) {
		if (wifis.get(position).isFree()) {
			return 0;
		} else {
			return 1;
		}
	}

	public class HeaderViewHolder {
		ImageView iv_group;
		TextView tv_group;
		TextView tv_check;
	}

	public class FreeHolder {
		ImageView iv_wifi_free_single;
		TextView tv_wifi_free_ssid, tv_wifi_free_conn, tv_wifi_free_conning,
				tv_wifi_free_conned, tv_wifi_free_fail;
	}

	public class PwdHolder {
		ImageView iv_wifi_item_single;
		TextView tv_wifi_item_conn, tv_wifi_item_ssid;
	}
}
