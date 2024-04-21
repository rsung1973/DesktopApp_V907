package com.dnake.desktop.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dnake.desktop.R;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class Utils {
    //    public static boolean isRTL() {
//        return isRTL(Locale.getDefault());
//    }
    public static boolean isRTL() {
        String locale = "";
        if (Locale.getDefault().getLanguage().equals("")) {
            locale = "en_US";
        } else {
            locale = Locale.getDefault().toString();
        }
//        return isRTL(Locale.getDefault());
        final int directionality = Character.getDirectionality(locale.charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static Drawable path2Drawable(Context context, String file) {
        if (file == null || file.isEmpty()) {
            return context.getResources().getDrawable(R.mipmap.bg_default);
        }
        Drawable drawable = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            drawable = new BitmapDrawable(context.getResources(), bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getDrawable(R.mipmap.bg_default);
        }
        return drawable;
    }

    public static String getDesktopBgPath() {
        String path = "";
        try {
            FileInputStream in = new FileInputStream("/dnake/cfg/desktop_bg_path");
            int length;
            byte[] bytes = new byte[1024];
            while ((length = in.read(bytes)) != -1) {
                path = new String(bytes, 0, length);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "/dnake/data/bg/bg_default.webp";
        } catch (IOException e) {
            e.printStackTrace();
            path = "";
        }
        if (TextUtils.isEmpty(path)) {
            path = "/dnake/data/bg/bg_default.webp";
            return path;
        } else {
            return path;
        }
    }

    public static boolean isInstalled(Context context, String pkgName) {
        if (pkgName != null && !"".equals(pkgName)) {
            try {
                context.getPackageManager().getApplicationInfo(pkgName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException var3) {
                try {
                    context.getPackageManager().getApplicationInfo(pkgName, 0);
                    return true;
                } catch (PackageManager.NameNotFoundException var2) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private void scaleToSmaller(View view) {
    }

    private void scaleToNormal(View v) {
    }
}
