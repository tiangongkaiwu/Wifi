package cn.hudun.wifi.pwd.utils;
import android.app.Application;
public class MyApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		WebAppInterface.validStatusCode();
	}

}
