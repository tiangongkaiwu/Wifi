package cn.hudun.wifi.pwd.bean;

public class Location {
	private double longItude;// 经度
	private double latItude;// 维度
	private float radius;// 定位半径，单位米
	private String addr;// 地址
	private String time;// 时间

	public double getLongItude() {
		return longItude;
	}

	public void setLongItude(double longItude) {
		this.longItude = longItude;
	}

	public double getLatItude() {
		return latItude;
	}

	public void setLatItude(double latItude) {
		this.latItude = latItude;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
