package com.example.beingiot;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    public static Retrofit retrofit;
    private static String SERVER_ADDRESS;

    public static void initialize(Context context) {
        try(InputStream input = context.getAssets().open("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            SERVER_ADDRESS = prop.getProperty("server_address");

            if(SERVER_ADDRESS == null || SERVER_ADDRESS.isEmpty()) {
                throw new RuntimeException("config.properties 파일에 server_address 항목이 없음");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("초기화 중 오류 발생 : " + e.getMessage());
        }
    }
    public static Retrofit getRetrofit() {
        if (SERVER_ADDRESS == null) {
            throw new IllegalStateException("ApiClient.initialize(Context context)를 먼저 호출해야 합니다.");
        }
        Gson gson = new GsonBuilder().setLenient().create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(setHttpLoggingInterceptor())
                .build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDRESS)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static HttpLoggingInterceptor setHttpLoggingInterceptor()  {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.e("HttpLoggingInterceptor", "message : " + message));
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}
