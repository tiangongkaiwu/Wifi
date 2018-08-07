package cn.hudun.wifi.pwd.bean;

import java.util.List;

public class WifiGroup {
	private String group;
	private List<Wifi> wifis;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<Wifi> getWifis() {
		return wifis;
	}

	public void setWifis(List<Wifi> wifis) {
		this.wifis = wifis;
	}

}
