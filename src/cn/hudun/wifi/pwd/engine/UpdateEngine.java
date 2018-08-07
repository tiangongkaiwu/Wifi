package cn.hudun.wifi.pwd.engine;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import cn.hudun.wifi.pwd.bean.UpdateInfo;
import cn.hudun.wifi.pwd.config.Constant;
import cn.hudun.wifi.pwd.ui.UpdateActivity;
import cn.hudun.wifi.pwd.utils.CharacterUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

public class UpdateEngine {
	private Context context;

	public UpdateEngine(Context context) {
		this.context = context;
	}

	/**
	 * 获取应用程序本地版本号
	 * 
	 * @return
	 */
	public float getVersion() {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return Float.parseFloat(info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}

	}

	public void getUpdate() {

		if (isNetworkConnected()) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(2000);
			//Wifi密码查看器，不传参数检查更新
			client.post(Constant.UPDATE_URL, new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String responseString) {
					try {
						UpdateInfo updateInfo = new UpdateInfo();
						JSONObject jsonObject = new JSONObject(responseString);
						updateInfo.setDescription(CharacterUtil
								.toGBK(jsonObject
										.getString("version_description")));
						float v = Float.parseFloat(jsonObject
								.getString("version"));
						updateInfo.setVersion(v);

						if (getVersion() < updateInfo.getVersion()) {
							Intent intent = new Intent(context,
									UpdateActivity.class);
							intent.putExtra("isUpdate", true);
							intent.putExtra("description",
									updateInfo.getDescription());
							intent.putExtra("version", updateInfo.getVersion());
							context.startActivity(intent);
						} else {
							Intent intent = new Intent(context,
									UpdateActivity.class);
							intent.putExtra("isUpdate", false);
							context.startActivity(intent);
						}

					} catch (JSONException e) {
						e.printStackTrace();

					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Toast.makeText(context, "检查更新失败！", Toast.LENGTH_SHORT)
							.show();
				}
			});

		}
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		} else {
			return false;
		}
	}
}
