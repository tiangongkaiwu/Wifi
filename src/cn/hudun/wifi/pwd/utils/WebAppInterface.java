package cn.hudun.wifi.pwd.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebAppInterface {
	Context mContext;
	DownloadList downloadList = new DownloadList();
	private static boolean isWebViewValiable = false;// 静态变量保存链接是否可以访问状态
	private static final String WEBURL="http://apk.zmd-power.com/password/android_webview";

	public WebAppInterface(Context c) {
		mContext = c;
	}

	// 如果target 大于等于API 17，则需要加上如下注解
	@JavascriptInterface
	public void add(String url, String filename, String apkname) {
		downloadList.start_download(url, filename, apkname); // 添加到下载列表中

	}

	private void downloadToFile(Download download) {
		try {

			String newFilename = Environment.getExternalStorageDirectory()
					+ "/" + Environment.DIRECTORY_DOWNLOADS + "/"
					+ download.filename;
			String _urlStr = download.url;
			File file = new File(newFilename);
			// 如果目标文件已经存在，由直接调用安装
			if (file.exists()) {
				installApk(newFilename);
				return;

			}

			downloadList.add(download);
			// 构造URL
			URL url = new URL(_urlStr);
			// 打开连接
			URLConnection con = url.openConnection();
			// 获得文件的长度
			int contentLength = con.getContentLength();
			System.out.println("长度 :" + contentLength);
			// 输入流
			InputStream is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			OutputStream os = new FileOutputStream(newFilename);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// Toast.makeText(mContext , "下载完成 " + apkname , Toast.LENGTH_LONG
			// ).show();

			// 完毕，关闭所有链接
			os.close();
			is.close();

			// 安装APK
			installApk(newFilename);
			downloadList.remove(download);

		} catch (Exception e) {
			e.printStackTrace();
			downloadList.remove(download);

			Looper.prepare();
			Toast.makeText(mContext, "下载失败 ", Toast.LENGTH_SHORT).show();
			Looper.loop();

		}

	}

	/* 安装apk */
	private void installApk(String fileName) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + fileName),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	private class Download {
		public String url;
		public String filename;
		public String apkname;

		Download(String u, String f, String a) {
			url = u;
			filename = f;
			apkname = a;
		}

	}

	private class DownloadList {
		private List<Download> list = new ArrayList<Download>();

		public void start_download(String url, String filename, String apkname) {
			final Download download = new Download(url, filename, apkname);
			// 若不存在，则添加
			if (isExist(download) == false) {
				Toast.makeText(mContext, "开始下载 " + apkname, Toast.LENGTH_SHORT)
						.show();
				// 在多线程中开始下载
				new Thread(new Runnable() {
					public void run() {
						downloadToFile(download);
					}
				}).start();

			} else {

				// TODO 需要提示 已经重复下载
				Toast.makeText(mContext, "正在下载 " + apkname, Toast.LENGTH_SHORT)
						.show();

			}

		}

		// 判断是否已经存在的下载对象
		private boolean isExist(Download download) {
			for (int i = 0; i < list.size(); i++) {
				Download tmp = (Download) list.get(i);
				if (tmp.url.equals(download.url)) {
					// 已经存在
					return true;
				}
			}
			return false;
		}

		// 下载失败时，调用该方法移除对象
		public void remove(Download download) {
			list.remove(download);
		}

		public void add(Download download) {
			list.add(download);
		}

	}

	/** 判断网络链接是否可以访问 */
	public static void validStatusCode() {
		int status = -1;
		try {
			HttpHead head = new HttpHead(WEBURL);
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(head);
			status = resp.getStatusLine().getStatusCode();
			if (status == 404 || status == 405 || status == 504) {
				isWebViewValiable = false;
				return;
			}
			isWebViewValiable = true;
		} catch (Exception e) {
			e.printStackTrace();
			isWebViewValiable = false;
		}
	}

	public static void loadUrl(Context context, WebView webView) {
		if (!isWebViewValiable) {
			webView.setVisibility(View.GONE);
			return;
		}
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true); // 设置允许js
		webView.addJavascriptInterface(new WebAppInterface(context), "Android");
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				view.setVisibility(View.GONE);

			}

		});
		webView.loadUrl(WEBURL);
	}

}