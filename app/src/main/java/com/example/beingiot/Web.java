package com.example.beingiot;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Web {
    private static final String SERVER_URL = "http://greyk.iptime.org:8890/";

//    public static void powerSwitch(boolean b){
//        try {
//            URL url = new URL(SERVER_URL + "poweron.php");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setConnectTimeout(10000);
//            connection.setRequestMethod("POST");
//            connection.setDefaultUseCaches(false);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    };

    public static JSONObject fetchDataFromServer() {
        try {
            // URL 설정하고 접속하기
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);    // 10초 내 연결
            connection.setRequestMethod("GET");    // 방식은 POST
            connection.setDefaultUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
//            connection.setRequestProperty();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String str;

            JSONObject jsonObject;
            while((str = bufferedReader.readLine()) != null) {
                response.append(str.trim());
                Log.i("str", str);
            }
            return new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
