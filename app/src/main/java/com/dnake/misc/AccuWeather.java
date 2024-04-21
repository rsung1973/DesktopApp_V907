package com.dnake.misc;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("DefaultLocale")
public class AccuWeather {
    public static String text;
    public static int icon;
    public static double temp;
    public static int humidity;

    public static Boolean haved = false;

    private String name = null;
    private String ip = null;

    public static int aqi = 0;
    public static int pm25 = 0;
    public static String quality = null;

    public String getIp() {
        ip = null;
        name = null;
        try {
//            URL url = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(http.getInputStream());
            BufferedReader bReader = new BufferedReader(in);
            String body = new String();
            String s;
            while ((s = bReader.readLine()) != null)
                body += s;
            in.close();
            http.disconnect();
            int n = body.indexOf('{');
            if (body.length() > 0 && n >= 0) {
                try {
                    body = body.substring(n);
                    JSONObject js = new JSONObject(body);
//                    ip = js.getString("cip");
//                    String cname = js.getString("cname");
//                    int ss = cname.indexOf("省");
//                    int ee = cname.indexOf("市");
//                    if (ss >= 0 && ee > 0) {
//                        name = cname.substring(ss + 1, ee);
//                    }
                    ip = js.getString("query");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return ip;
    }

    public String getKey(String ip) {
        String key = null;
        try {
            URL url = new URL("http://api.accuweather.com/locations/v1/cities/ipaddress?q=" + ip + "&apikey=f6130725364d45b1a95875b6fa21f512");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(http.getInputStream());
            BufferedReader bReader = new BufferedReader(in);
            String body = new String();
            String s;
            while ((s = bReader.readLine()) != null)
                body += s;
            in.close();
            http.disconnect();
            try {
                JSONObject data = new JSONObject(body);
                JSONObject p = data.getJSONObject("ParentCity");
                if (p != null)
                    data = p;

                key = data.getString("Key");
                if (name == null)
                    name = data.getString("LocalizedName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (Exception e) {
        }
        return key;
    }

    public void getWeather(String key) {
        try {
            String lang;
            if (Locale.getDefault().getCountry().isEmpty())
//                lang = Locale.getDefault().getLanguage();
                lang = "en";
            else
                lang = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();

            URL url = new URL("http://api.accuweather.com/currentconditions/v1/" + key + ".json?details=true&apikey=f6130725364d45b1a95875b6fa21f512&language=" + lang);
//            Log.i("aaa", "_________________url___________________" + "http://api.accuweather.com/currentconditions/v1/" + key + ".json?details=true&apikey=f6130725364d45b1a95875b6fa21f512&language=" + lang);
//            Log.i("aaa", "_________________key___________________" + key);
//            Log.i("aaa", "_________________lang___________________" + lang);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(http.getInputStream());
            BufferedReader bReader = new BufferedReader(in);
            String body = new String();
            String s;
            while ((s = bReader.readLine()) != null)
                body += s;
            in.close();
            http.disconnect();
            try {
                JSONArray js = new JSONArray(body);
                JSONObject obj = js.getJSONObject(0);
                AccuWeather.text = obj.getString("WeatherText");
                AccuWeather.icon = obj.getInt("WeatherIcon");
                AccuWeather.humidity = obj.getInt("RelativeHumidity");
                JSONObject m = obj.getJSONObject("Temperature").getJSONObject("Metric");
                AccuWeather.temp = m.getDouble("Value");
                System.out.println("Weather icon: " + AccuWeather.icon);
                onFinished();
                haved = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void getPm25(String n) {
        n = URLEncoder.encode(n.toLowerCase());

        aqi = 0;
        pm25 = 0;
        quality = null;

        try {
            final String token[] = {
                    "8Ep3VW2KvcHQviTqiDG8",
                    "qE7sWDZR7KrzFfzPSfn6",
                    "UtzrAKmb7zS43bZ51kb3",
                    "yxGgXcc7YJq1Dxx9ahBL",
                    "5j1znBVAsnSf5xQyNQyq",
                    "Xp51QrouaGA2kCCBvssL",
                    "8Ep3VW2KvcHQ",
            };
            int tr = (int) (Math.random() * 6);

            URL url = new URL("http://www.pm25.in/api/querys/pm2_5.json?city=" + n + "&token=" + token[tr]);
            System.err.println(url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(http.getInputStream());
            BufferedReader bReader = new BufferedReader(in);
            String body = new String();
            String s;
            while ((s = bReader.readLine()) != null)
                body += s;
            in.close();
            http.disconnect();

            try {
                JSONArray js = new JSONArray(body);
                for (int i = 0; i < js.length(); i++) {
                    JSONObject obj = js.getJSONObject(i);
                    if (obj != null) {
                        aqi = obj.getInt("aqi");
                        pm25 = obj.getInt("pm2_5");
                        quality = obj.getString("quality");
                        if (aqi > 0) {
                            onFinished();
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void onFinished() {
    }

    public void start() {
        AccuWeatherThread d = new AccuWeatherThread();
        Thread thread = new Thread(d);
        thread.start();
    }

    public class AccuWeatherThread implements Runnable {
        @Override
        public void run() {
            String ip = getIp();
            if (ip != null) {
                String key = getKey(ip);
                if (key != null)
                    getWeather(key);
                if (name != null)
                    getPm25(name);
            }
        }
    }
}
