package com.dnake.desktop.model;

import android.text.TextUtils;

import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.dnake.v700.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ContactLogger {
    public static String dir = "/dnake/data/contact";
    public static String file_name = "logger.xml";
    public static String file_path = "/dnake/data/contact/logger.xml";

    public static int MAX = 60;
    public static boolean needReload = false;

    public static class data {
        public String id;
        public String name;
        public String apartmentNo;
        public String sipid;
        public String ipAddr;
        public String camera_url;
        public String login;
        public String password;
        public int type;//0: 房号 1：IP地址 2：SIP
        public String add_in;//0: phonebook，1: whitelist，可同时添加（参数用分号分隔）和单独添加，如0;1
        public String number;

        public data() {
        }

        public data(String name, int type, String number, String add_in) {
            this.type = type;
            this.name = name;
            this.number = number;
            this.add_in = add_in;
            switch (type) {
                case 0:
                    this.apartmentNo = number;
                    break;
                case 1:
                    this.ipAddr = number;
                    break;
                case 2:
                    this.sipid = number;
                    break;
            }
        }
    }


    public static int count() {
        int count = 0;

        File f = new File(dir);
        if (f != null && !f.exists())
            f.mkdir();

        f = new File(file_path);
        if (f != null && !f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dxml p = new dxml();
        if (p.load(file_path)) {
//            count = p.getTotalNodesCount();
            count = p.getInt("/logger/max", 0);
        }
        return count;
    }

    //获取所有记录
    public static List<data> loadAllList() {
        List<data> list = new ArrayList<>();

        File f = new File(dir);
        if (f != null && !f.exists())
            f.mkdir();

        f = new File(file_path);
        if (f != null && !f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dxml p = new dxml();
        if (p.load(file_path)) {
            int count = p.getInt("/logger/max", p.getTotalNodesCount());
            for (int i = 0; i < count; i++) {
                String s = "/logger/c" + i;
                String name = p.getText(s + "/name");
                int mType = p.getInt(s + "/type", 1);
                data d = new data();
                d.name = name;
                d.id = p.getText(s + "/id");
                d.apartmentNo = p.getText(s + "/apartment_no");
                d.sipid = p.getText(s + "/sipid");
                d.ipAddr = p.getText(s + "/ip_addr");
                d.camera_url = p.getText(s + "/camera_url");
                d.login = p.getText(s + "/login");
                d.password = p.getText(s + "/password");
                d.type = p.getInt(s + "/type", 0);
                d.add_in = p.getText(s + "/add_in", "0");
                d.number = p.getText(s + "/number");
                if (!d.add_in.equals("1")) {
                    list.add(d);
                }
            }
        }
        return list;
    }

    public static boolean isInWhiteList(String str) {
        File f = new File(dir);
        if (f != null && !f.exists())
            f.mkdir();
        f = new File(file_path);
        if (f != null && !f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dxml p = new dxml();
        if (p.load(file_path)) {
            int count = p.getInt("/logger/max", p.getTotalNodesCount());
            for (int i = 0; i < count; i++) {
                String s = "/logger/c" + i;
                String number = p.getText(s + "/number");
                String is_whitelist = p.getText(s + "/add_in", "0");
                if (is_whitelist.contains("1") && str.equals(number)) {//是否属于白名单
                    return true;
                }
            }
        }
        return false;
    }

    public static List<data> loadAllWhiteList() {
        List<data> list = new ArrayList<>();
        File f = new File(dir);
        if (f != null && !f.exists())
            f.mkdir();
        f = new File(file_path);
        if (f != null && !f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dxml p = new dxml();
        if (p.load(file_path)) {
            int count = p.getInt("/logger/max", p.getTotalNodesCount());
            for (int i = 0; i < count; i++) {
                String s = "/logger/c" + i;
                String is_whitelist = p.getText(s + "/add_in", "0");
                if (is_whitelist.contains("1")) {//是否属于白名单
                    data d = new data();
                    d.name = p.getText(s + "/name");
                    d.id = p.getText(s + "/id");
                    d.apartmentNo = p.getText(s + "/apartment_no");
                    d.sipid = p.getText(s + "/sipid");
                    d.ipAddr = p.getText(s + "/ip_addr");
                    d.camera_url = p.getText(s + "/camera_url");
                    d.login = p.getText(s + "/login");
                    d.password = p.getText(s + "/password");
                    d.type = p.getInt(s + "/type", 0);
                    d.add_in = is_whitelist;
                    d.number = p.getText(s + "/number");
                    list.add(d);
                }
            }
        }
        return list;
    }
}
