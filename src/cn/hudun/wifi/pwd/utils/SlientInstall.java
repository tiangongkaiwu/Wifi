package cn.hudun.wifi.pwd.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Auth: Joe
 * Date: 2015-05-13
 * Time: 15:04
 * FIXME
 */
public class SlientInstall {
    public static boolean installSilent(String apkPath) {
        String cmd1 = "chmod 777 " + apkPath + " \n";
        String cmd2 = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + apkPath + " \n";
        return execWithSID(cmd1, cmd2);
    }

    private static boolean execWithSID(String... args) {
        boolean isSuccess = false;
        Process process = null;
        OutputStream out = null;
        try {
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            for (String tmp : args) {
                dataOutputStream.writeBytes(tmp);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
            out.close();
            isSuccess = waitForProcess(process);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }

    private static boolean waitForProcess(Process p) {
        boolean isSuccess = false;
        int returnCode;
        try {
            returnCode = p.waitFor();
            switch (returnCode) {
                case 0:
                    isSuccess = true;
                    break;

                case 1:
                    break;

                default:
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }
}
