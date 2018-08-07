package cn.hudun.wifi.pwd.bean;

public class Wifi {
	private String ssid;
	private String pwd;
	private String mac;
	private int SignalLevel;
	private int flag;// 1 上传服务器 2，不上传
	private boolean isFree;
	private String type;// 加密类型
	private int state;// 状态 1.已经连接，2 切换中，3 ，切换失败,4 切换

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getSignalLevel() {
		return SignalLevel;
	}

	public void setSignalLevel(int signalLevel) {
		SignalLevel = signalLevel;
	}

}
