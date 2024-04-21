package com.dnake.v700;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class sys {
    public static String url = "/dnake/cfg/sys.xml";

    public static float density = 1.0f;

    public static final class time {
        public static int format = 1;// 0：MM-DD-YYYY、1：DD-MM-YYYY、2：YYYY-MM-DD
        public static int is24 = 1;
        public static String tz = "Asia/Shanghai";//timezone id
        public static String ntp_server = "2.android.pool.ntp.org";
        public static int ntp_enable = 1;
    }

    public static void loadTimeSet() {
        dxml p = new dxml();
        boolean result = p.load(url);
        if (result) {
            //Settings--Date&Time
//            time.tz = p.getText("/sys/time/tz", time.tz);
            time.is24 = p.getInt("/sys/time/is24", time.is24);
            time.format = p.getInt("/sys/time/format", time.format);
//            time.ntp_enable = p.getInt("/sys/time/ntp_enable", time.ntp_enable);
//            time.ntp_server = p.getText("/sys/time/ntp_server", time.ntp_server);
        }
    }

    public static final class rtsp {
        public static String url = "";
    }

    public static final class sos {
        public static int delay = 0;//0s,3s可供选择
    }

    public static final class tuya {
        public static int channel = 1;
        public static String TUYA_UUID = "";
        public static String TUYA_AUTHKEY = "";
        public static String qr = "";
    }

    public static void loadOtherSet() {
        dxml p = new dxml();
        boolean result = p.load(url);
        if (result) {
            rtsp.url = p.getText("/sys/rtsp/url", rtsp.url);
            sos.delay = p.getInt("/sys/sos/delay", sos.delay);
            tuya.qr = p.getText("/sys/tuya/qr", tuya.qr);
        }
    }

    public static int sLimit = -1;

    public static int limit() {
        if (sLimit != -1)
            return sLimit;

        int limit = 0;
        try {
            FileInputStream in = new FileInputStream("/dnake/bin/limit");
            byte[] data = new byte[256];
            int ret = in.read(data);
            if (ret > 0) {
                String s = new String();
                char[] d = new char[1];
                for (int i = 0; i < ret; i++) {
                    if (data[i] >= '0' && data[i] <= '9') {
                        d[0] = (char) data[i];
                        s += new String(d);
                    } else
                        break;
                }
                limit = Integer.parseInt(s);
            }
            in.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        sLimit = limit;
        return limit;
    }

    public static class bar {
        public static Boolean force = true;
        public static String[] apk = new String[32];

        public static void load() {
            dxml p = new dxml();
            p.load("/dnake/cfg/toolbar.xml");
            for (int i = 0; i < 32; i++) {
                String s = "/sys/bar" + i;
                apk[i] = p.getText(s);
            }
        }

        public static void save() {
            dxml p = new dxml();
            for (int i = 0; i < 32; i++) {
                if (apk[i] != null) {
                    String s = "/sys/bar" + i;
                    p.setText(s, apk[i]);
                }
            }
            p.save("/dnake/cfg/toolbar.xml");

            force = true;
        }
    }
}
