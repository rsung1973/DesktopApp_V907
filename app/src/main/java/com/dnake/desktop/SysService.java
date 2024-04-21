package com.dnake.desktop;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.util.Log;

import com.dnake.desktop.model.DesktopMainLogger;
import com.dnake.desktop.utils.Utils;
import com.dnake.v700.FileUtils;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.dnake.v700.utils;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.io.IOException;

public class SysService extends Service {

    public static Context mContext = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ToastUtils.init(getApplication());
        initData();
        dmsg.setup_port();

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            //android4.4 service会延迟启动，先由桌面提前启动APK Service
            BackgroundThread pt = new BackgroundThread();
            Thread t = new Thread(pt);
            t.start();
        }
        Intent it = new Intent("com.dnake.broadcast");
        it.putExtra("event", "com.dnake.boot");
        sendBroadcast(it);
//        ProcessThread p = new ProcessThread();
//        Thread t = new Thread(p);
//        t.start();
        sys.loadTimeSet();
        sys.loadOtherSet();
    }

    public static class BackgroundThread implements Runnable {
        @Override
        public void run() {
            this.start("com.dnake.eSettings", "com.dnake.v700.settings");
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
            }
            if (sys.limit() < 970) {
                this.start("com.dnake.talk", "com.dnake.v700.talk");
                this.start("com.dnake.smart", "com.dnake.v700.smart");
                this.start("com.dnake.security", "com.dnake.v700.security");
            } else {
                this.start("com.dnake.panel", "com.dnake.misc.SysTalk");
            }
            this.start("com.dnake.apps", "com.dnake.v700.apps");
        }

        private void start(String apk, String name) {
            try {
                Intent it = new Intent();
                it.setComponent(new ComponentName(apk, name));
                mContext.startService(it);
            } catch (RuntimeException e) {
            }
        }
    }

    public static class ProcessThread implements Runnable {
        @Override
        public void run() {
            if (utils.eHome.watchdog) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                utils.eHome.broadcast(mContext);
            }

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                utils.process();
            }
        }
    }

    public final static String desktop_bg_path = "/dnake/cfg/desktop_bg_path";

    private void initData() {
        File f = new File(desktop_bg_path);
        if (f != null && !f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
