package com.dnake.desktop.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dnake.desktop.R;
import com.dnake.desktop.SysService;
import com.dnake.desktop.utils.Utils;
import com.dnake.v700.dxml;
import com.dnake.v700.utils;

import java.util.ArrayList;
import java.util.List;

public class DesktopMainLogger {
    public final static int MAX = 4;
    public static String url = "/dnake/data/desktop_main.xml";
    public static String apps_url = "/dnake/data/desktop_apps.xml";

    public static List<AppModel> loadAllDefaultApps() {
        List<AppModel> list = new ArrayList<>();
        dxml p = new dxml();
        if (p.load(apps_url)) {
            int max = p.getInt("/sys/max", MAX);
            for (int i = 0; i < max; i++) {
                String s = "/sys/apps/app" + i;
                String name = p.getText(s + "/name");
                int id = p.getInt(s + "/id", -1);
                if (name != null) {
                    AppModel appModel = new AppModel();
                    appModel.setId(id);
                    appModel.setName(name);
                    appModel.setAction(p.getText(s + "/action"));
                    appModel.setPackageName(p.getText(s + "/pkg_name", ""));
                    list.add(appModel);
                }
            }
        }
        return list;
    }

    public static List<AppModel> loadSelectDefaultApps() {//排除sos项
        List<AppModel> list = new ArrayList<>();
        dxml p = new dxml();
        if (p.load(apps_url)) {
            int max = p.getInt("/sys/max", MAX);
            for (int i = 0; i < max; i++) {
                String s = "/sys/apps/app" + i;
                String name = p.getText(s + "/name");
                int id = p.getInt(s + "/id", -1);
                if (name != null && id != 10012) {
                    AppModel appModel = new AppModel();
                    appModel.setId(id);
                    appModel.setName(name);
                    appModel.setAction(p.getText(s + "/action"));
                    appModel.setPackageName(p.getText(s + "/pkg_name", ""));
                    list.add(appModel);
                }
            }
        }
        return list;
    }

    public static List<AppModel> loadApps(Context context) {
        List<AppModel> list = new ArrayList<>();
        dxml p = new dxml();
        if (p.load(url)) {
            int max = p.getInt("/sys/main_menu/max", MAX);
            for (int i = 0; i < max; i++) {
                String s = "/sys/main_menu/app" + i;
                String name = p.getText(s + "/name");
                int id = p.getInt(s + "/id", -1);
                String pkg_name = p.getText(s + "/pkg_name", "");
                if (name != null) {
                    AppModel appModel = new AppModel();
                    appModel.setId(id);
                    appModel.setName(name);
                    appModel.setAction(p.getText(s + "/action"));
                    appModel.setPackageName(pkg_name);
                    if (TextUtils.isEmpty(pkg_name) || Utils.isInstalled(context, pkg_name)) {
                        list.add(appModel);
                    }
                }
            }
            save(list);
        }
        return list;
    }

    public static void save(List<AppModel> list) {
        dxml p = new dxml();
        p.setInt("/sys/main_menu/max", MAX);
        for (int i = 0; i < list.size(); i++) {
            AppModel appModel = list.get(i);
            String s = "/sys/main_menu/app" + i;
            p.setInt(s + "/id", appModel.getId());
            p.setText(s + "/name", appModel.getName());
            p.setText(s + "/action", TextUtils.isEmpty(appModel.getAction()) ? ((appModel.getIntent() != null) ? appModel.getIntent().getAction() : "") : appModel.getAction());
//            p.setInt(s + "/icon", Integer.parseInt(appModel.getIcon().toString()));
            p.setText(s + "/pkg_name", appModel.getPackageName());
        }
        p.save(url);
    }

    public static boolean isAppExist(int id) {
        dxml p = new dxml();
        if (p.load(url)) {
            int max = p.getInt("/sys/main_menu/max", MAX);
            for (int i = 0; i < max; i++) {
                String s = "/sys/main_menu/app" + i;
                String name = p.getText(s + "/name");
                int tmp_id = p.getInt(s + "/id", -1);
                if (tmp_id == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void removeAppByPkgName(Context context, String pkgName) {
        Log.i("aaa", "________removeAppByPkgName____________" + pkgName);
        List<AppModel> list = loadApps(context);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPackageName().equals(pkgName)) {
                list.remove(i);
                break;
            }
        }
        save(list);
    }
}
