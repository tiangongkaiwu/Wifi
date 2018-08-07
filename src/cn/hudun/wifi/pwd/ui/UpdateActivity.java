package cn.hudun.wifi.pwd.ui;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.Header;
import com.baidu.mobstat.StatService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import cn.hudun.wifi.pwd.R;
import cn.hudun.wifi.pwd.config.Constant;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends Activity implements OnClickListener {
	private ImageView iv_close;
	private TextView tv_update;
	private TextView tv_sure;
	private String description;
	private boolean isUpdate;
	private ImageView imageView1;
	private Animation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_dialog);
		initView();
		animation = AnimationUtils.loadAnimation(UpdateActivity.this,
				R.anim.rotate_animation);
		Intent intent = getIntent();
		isUpdate = intent.getBooleanExtra("isUpdate", true);
		if (isUpdate) {
			description = intent.getStringExtra("description");
			tv_update.setText("检查到有新版本，是否更新！");
			tv_sure.setText("更新");
		} else {
			tv_update.setText("当前使用的是最新版本！");
			tv_sure.setText("确定");
		}

	}

	private void initView() {
		iv_close = (ImageView) findViewById(R.id.iv_close);
		tv_update = (TextView) findViewById(R.id.tv_update);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		iv_close.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_close:
			finish();
			break;
		case R.id.tv_sure:
			if (isUpdate) {
				imageView1.startAnimation(animation);
				Toast.makeText(UpdateActivity.this, "正在下载...",
						Toast.LENGTH_SHORT).show();
				getAPK(Constant.APK_URL);
				// finish();
			} else {
				imageView1.clearAnimation();

				finish();
			}

			break;
		default:
			break;
		}

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

	/**
	 * 下载apk
	 * 
	 * @param url
	 */
	public void getAPK(String url) {
		if (isNetworkConnected()) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(15);
			String[] allowedTypes = new String[] { ".*" };
			client.get(url, new BinaryHttpResponseHandler(allowedTypes) {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] binaryData) {

					try {
						if (android.os.Environment.getExternalStorageState()
								.equals(android.os.Environment.MEDIA_MOUNTED)) {
							String sdDir = Environment
									.getExternalStorageDirectory().toString();
							String path = sdDir + "/WiFiPwd.apk";
							final File file = new File(path);
							FileOutputStream fos = new FileOutputStream(file);
							fos.write(binaryData);
							fos.flush();
							fos.close();
							imageView1.clearAnimation();
							AlertDialog.Builder builder = new Builder(
									UpdateActivity.this);
							builder.setTitle("提示");
							builder.setIcon(R.drawable.ic_mini_logo);
							builder.setMessage("下载成功，是否立即安装！");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO:安装
											install(file);
											UpdateActivity.this.finish();
										}
									});
							builder.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											UpdateActivity.this.finish();
										}
									});
							builder.create().show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] binaryData, Throwable error) {
					imageView1.clearAnimation();
					Toast.makeText(UpdateActivity.this, "请求失败，稍后请重试...",
							Toast.LENGTH_SHORT).show();
				}

			});
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param file
	 */
	public void install(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		} else {
			return false;
		}
	}
}
