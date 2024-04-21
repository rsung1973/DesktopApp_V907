package com.dnake.desktop;

import com.dnake.desktop.fragment.MainScreenFragment;
import com.dnake.desktop.model.DesktopMainLogger;
import com.dnake.desktop.utils.Utils;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.dnake.v700.utils;
import com.google.android.material.textfield.TextInputEditText;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.Timer;

public class SysReceiver extends BroadcastReceiver {

    public static int sms_nread = 0;
    public static int msg_nread = 0;
    public static int miss_nread = 0;
    public static int mute = 0;
    public static int dnd = 0;

    public static int security = 0;

    public static int need_refresh_apps = 0;//1:安装;2:卸载
    public static boolean isReset = false;
    public static String new_app_pkg_name = "";
    public static boolean loadTuyaQr = false;
    public static boolean need_refresh_time = false;

    @Override
    public void onReceive(Context ctx, Intent it) {
        String a = it.getAction();
        if (a.equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intent = new Intent(ctx, SysService.class);
            ctx.startService(intent);
        } else if (a.equals("com.dnake.broadcast")) {
            String e = it.getStringExtra("event");
            if (e.equals("com.dnake.apps.sms")) {
                sms_nread = it.getIntExtra("nRead", 0);
            } else if (e.equals("com.dnake.talk.data")) {
                msg_nread = it.getIntExtra("msg", 0);
                miss_nread = it.getIntExtra("miss", 0);
                mute = it.getIntExtra("mute", 0);
            } else if (e.equals("com.dnake.talk.mute")) {
                mute = it.getIntExtra("mute", 0);
                dnd = it.getIntExtra("dnd", 0);
            } else if (e.equals("com.dnake.security.data")) {
                security = it.getIntExtra("defence", 0);
            } else if (e.equals("com.dnake.talk.eHome.monitor")) {
                utils.eHome.watchdog = it.getBooleanExtra("enable", false);
            } else if (e.equals("com.dnake.talk.eHome.restart")) {
                utils.eRestart = it.getStringExtra("package");
            } else if (e.equals("com.dnake.apps.install_apk")) {
                need_refresh_apps = it.getIntExtra("need_refresh_apps", 0);
                new_app_pkg_name = it.getStringExtra("new_app_pkg_name");
            } else if (e.equals("com.dnake.talk.time")) {//更新桌面时间
                sys.time.format = it.getIntExtra("date_format", sys.time.format);
                sys.time.is24 = it.getIntExtra("time_format", sys.time.is24);
            } else if (e.equals("set_desktop_bg")) {//背景更新
                DesktopActivity.layoutMain.setBackground(Utils.path2Drawable(SysService.mContext, Utils.getDesktopBgPath()));
            } else if (e.equals("set_params")) {//部分设置变更
                sys.sos.delay = it.getIntExtra("sos_delay", sys.sos.delay);
                sys.rtsp.url = it.getStringExtra("video_rtsp_url");
            } else if (e.equals("com.dnake.apps.tuya_qr")) {//涂鸦qrcode获取成功
//                sys.tuya.qr = it.getIntExtra("qrcode", sys.sos.delay);
                loadTuyaQr = true;
            } else if (e.equals("com.dnake.apps.time_refresh")) {
                need_refresh_time = true;
                DesktopActivity.web_dTs = System.currentTimeMillis();
                DesktopActivity.dTs = System.currentTimeMillis();
            }
        } else if (a.equals("android.intent.action.PACKAGE_ADDED")) {
            need_refresh_apps = 1;
            dmsg req = new dmsg();
            req.to("/apps/delete_apk", null);
            req.to("/apps/reset_status/read", null);
            dxml p = new dxml();
            p.parse(req.mBody);
            if (p.getInt("/params/status", 0) == 1) {//恢复出厂设置 true
                isReset = true;
                req.to("/apps/reset_desktop_main", null);
                req.to("/apps/reset_status/write", null);
            }
            recordInstallAppNum(1);
        } else if (a.equals("android.intent.action.PACKAGE_REMOVED")) {
            Log.i("aaa", "________________android.intent.action.PACKAGE_REMOVED!!!!!!!!!_____________________");
            String packageName = it.getData().getSchemeSpecificPart();
            need_refresh_apps = 2;
            dmsg req = new dmsg();
            req.to("/apps/delete_apk", null);
            recordInstallAppNum(2);
            DesktopMainLogger.removeAppByPkgName(SysService.mContext, packageName);
        }
    }

    private void recordInstallAppNum(int type) {// 1:add  2:remove
        dmsg req = new dmsg();
        dxml p = new dxml();
        req.to("/apps/web/install_apk_num/read", null);
        p.parse(req.mBody);
        int num = 0;
        if (type == 1) {
            num = p.getInt("/params/app_num", 0) + 1;
        } else if (type == 2) {
            num = p.getInt("/params/app_num", 0);
            if (num > 0) {
                num = num - 1;
            } else {
                num = 0;
            }
        }
        p = new dxml();
        p.setInt("/params/app_num", num);
        req.to("/apps/web/install_apk_num/write", p.toString());
    }
}
