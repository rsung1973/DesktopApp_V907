package com.dnake.v700;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.dnake.desktop.DesktopActivity;
import com.dnake.desktop.SysService;
import com.dnake.desktop.model.AppModel;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

public class utils {

    //eHome为台湾中华电信eHome.apk特殊处理及接口
    public static final Boolean eHomeMode = false;

    public static String eRestart = null;

    public static class eHome {
        public static Boolean watchdog = eHomeMode;
        public static long ts = 0;
        public static int err = 6;
        public static long idle = 0;

        public static Boolean query(Context ctx) {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> r = am.getRunningAppProcesses();
            for (RunningAppProcessInfo p : r) {
                if (p.processName != null && p.processName.equals("com.cht.ehomev2")) return true;
            }
            return false;
        }

        public static void start(Context ctx) {
            PackageManager pm = ctx.getPackageManager();
            Intent it = pm.getLaunchIntentForPackage("com.cht.ehomev2");
            if (it != null) ctx.startActivity(it);
            System.out.println("Start com.cht.ehomev2");
        }

        public static void broadcast(Context ctx) {
            Intent it = new Intent("com.dnake.broadcast");
            it.putExtra("event", "com.dnake.talk.eHome.setup");
            it.putExtra("mode", true);
            ctx.sendBroadcast(it);
        }

        public static void process() {
            if (watchdog == false) return;

            if (idle != 0 && Math.abs(System.currentTimeMillis() - idle) >= 3 * 1000) {
                eHome.start(SysService.mContext);
                idle = 0;
            }

            if (eHome.query(SysService.mContext)) err = 0;
            else err++;
            if (err > 5) {
                err = 0;
                eHome.start(SysService.mContext);
            }
        }
    }

    public static void process() {
        if (utils.eRestart != null && SysService.mContext != null) {
            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setText("/params/name", utils.eRestart);
            req.to("/upgrade/system/kill", p.toString());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }

            PackageManager pm = SysService.mContext.getPackageManager();
            Intent it = pm.getLaunchIntentForPackage(utils.eRestart);
            if (it != null) SysService.mContext.startActivity(it);

            utils.eRestart = null;
        }

        eHome.process();
    }

    public static String getCurTimeStr() {
        String timeStr = System.currentTimeMillis() + "";
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    public static AppModel getNewApp(String newPkgName) {
        if (TextUtils.isEmpty(newPkgName)) {
            return null;
        }
        PackageManager pm = SysService.mContext.getPackageManager(); // 获得PackageManager对象
        Intent it = new Intent(Intent.ACTION_MAIN, null);
        it.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(it, 0);
        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo reInfo = resolveInfos.get(i);
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            if (pkgName.contains(newPkgName)) {
                // 为应用程序的启动Activity 准备Intent
                it = new Intent();
                it.setComponent(new ComponentName(pkgName, activityName));

                return new AppModel(10020 + i, appLabel, icon, it, pkgName, activityName);
            }
        }
        return null;
    }

    public static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if ((inetAddress instanceof Inet4Address) && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress())
                        return inetAddress.getHostAddress().toString();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void openThirdApp(String pkgName, String activityName) {
        if (!TextUtils.isEmpty(pkgName) && !TextUtils.isEmpty(activityName)) {
            String shell = "am start " + pkgName + "/" + activityName;
            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setText("/params/cmd", shell);
            req.to("/upgrade/root/cmd", p.toString());
        }
        if (pkgName.equals("com.xiaoquan.android.vncserver")) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setText("/params/cmd", "am startservice -n com.xiaoquan.android.vncserver/.ServerManager");
            req.to("/upgrade/root/cmd", p.toString());
        }
    }

    public static int getThirdAppsNum(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<PackageInfo> thirdAPP = new ArrayList<>();
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo pak = (PackageInfo) packageInfoList.get(i);
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0 && !TextUtils.isEmpty(pak.applicationInfo.name) && !pak.applicationInfo.packageName.equals("com.android.chrome")) {// 第三方应用,chrome是预安装应用
                thirdAPP.add(pak);
            }
        }
        return thirdAPP.size();
    }

    public static List<PackageInfo> getThirdAppList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<PackageInfo> thirdAPP = new ArrayList<>();
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo pak = (PackageInfo) packageInfoList.get(i);
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {// 第三方应用
                thirdAPP.add(pak);
            }
        }
        return thirdAPP;
    }

    public static String getVersionNameAll() {
        String version = "";
        try {
            FileInputStream in = new FileInputStream("/dnake/bin/ui_ver");
            int length;
            byte[] bytes = new byte[1024];
            while ((length = in.read(bytes)) != -1) {
                version = new String(bytes, 0, length);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void setCpuMaxScale(long num) {
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setText("/params/cmd", "echo " + num + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
        req.to("/upgrade/root/cmd", p.toString());
    }
}
