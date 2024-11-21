package com.example.beingiot;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ImageButton btnSettings;
    TextView tvTemp;
    TextView tvHumidity;
    public static TextView tvRoomName;
    WebView webView;
    RadioGroup rgMode;
    RadioButton rbRemoteMode;
    RadioButton rbAutoMode;
    AppCompatButton colorPowerButton;
    View viewPowerButton;

    private final Handler handler = new Handler();
    private final int REFRESH_INTERVAL = 5000;  // 5초마다 한번씩 업데이트

    private static final String SERVER_ADDRESS = "http://greyk.iptime.org:8890";

    private final String TAG = this.getClass().getSimpleName();
    private View[] viewsToDisable;
    private Runnable enableViewsRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSettings = findViewById(R.id.btnSettings);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        webView = findViewById(R.id.webView);
        initNiceWebView();
        handler.post(refreshWebViewRunnable);
        rgMode = findViewById(R.id.rgMode);
        rbAutoMode = findViewById(R.id.rbAutoMode);
        rbRemoteMode = findViewById(R.id.rbRemoteMode);
        tvRoomName = findViewById(R.id.tvRoomName);
        colorPowerButton = (AppCompatButton) findViewById(R.id.colorPowerButton);
        viewPowerButton = findViewById(R.id.viewPowerButton);

        viewsToDisable = new View[] {colorPowerButton, rgMode, rbAutoMode, rbRemoteMode};

        ApiClient.initialize(this);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // 전원버튼을 누를 때 서버로 서보모터 동작 명령어를 보내는 함수
        // {"Power" : "ON"}
        colorPowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                CommandModeData commandModeData = new CommandModeData("On");
                Call<Void> call = apiInterface.sendModeCommand(commandModeData);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Log.d("Success", "Command sent successfully.");
                        }
                        else {
                            Log.d("Error", "Failed to send command. Response code : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Error", "Failed to send command : " + t.getMessage());
                    }
                });

                Toast toastPower = Toast.makeText(MainActivity.this, "전원 버튼을 작동시켰습니다.", Toast.LENGTH_SHORT);
                toastPower.show();
            }
        });

        // Mode 설정 함수
        // 리모컨 모드의 경우 {"Mode" : "Remote"}
        rbRemoteMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                CommandModeData commandModeData = new CommandModeData("Off");
                Call<Void> call = apiInterface.sendModeCommand(commandModeData);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Log.d("Success", "Command sent successfully.");
                        }
                        else {
                            Log.d("Error", "Failed to send command. Response code : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("Error", "Failed to send command : " + t.getMessage());
                    }
                });
                colorPowerButton.setEnabled(true);
            }
        });

        // 자동 모드의 경우 {"Mode" : "Auto"}
        rbAutoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                CommandModeData commandModeData = new CommandModeData("Auto");
                Call<Void> call = apiInterface.sendModeCommand(commandModeData);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Log.d("Success", "Command sent successfully.");
                        }
                        else {
                            Log.d("Error", "Failed to send command. Response code : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("Error", "Failed to send command : " + t.getMessage());
                    }
                });
                colorPowerButton.setEnabled(false);
            }
        });

        // 모드 RadioButton을 누를 때마다 전원 버튼과 모드 버튼을 비활성화
       rgMode.setOnCheckedChangeListener((group, checkedId) -> disableViews());
    }

    // 앱 실행 시 처음으로 WebView를 설정하고 값을 가져오는 함수
    private void initNiceWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                Call<String> call = apiInterface.getData();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.e(TAG, "성공 : " + response.body());

                            try {
                                String stringObject = new String(response.body());
                                parseJson(stringObject);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                Log.e(TAG, "실패 : " + response.errorBody().string());
                            }   catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "에러 : " + t.getMessage());
                    }
                });
            }

        });

        webView.loadUrl(SERVER_ADDRESS);
    }

    //5초마다 멀티 쓰레드로 WebView를 새로고침하는 Runnable 함수
    private final Runnable refreshWebViewRunnable = new Runnable() {
        @Override
        public void run() {
            webView.loadUrl(SERVER_ADDRESS);
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    // String 형태로 받아온 json을 각자 필요한 형식으로 바꾸고 설정함
    private void parseJson(String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            String strTemp = "기온 : " + jsonObject.getDouble("Temp") + "°C";
            double floatHumid = jsonObject.getDouble("Humi");

            String strHumid = "습도 : " + String.format("%.2f", floatHumid) + "%";
            tvTemp.setText(strTemp);
            tvHumidity.setText(strHumid);

            String colorStatus = jsonObject.getString("Status");
            if(colorStatus.equals("Green")) {
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.green_circle_button_shape, null);
                viewPowerButton.setBackground(drawable);
            }
            else if(colorStatus.equals("Red")) {
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.red_circle_button_shape, null);
                viewPowerButton.setBackground(drawable);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshWebViewRunnable);        // 5초마다 WebView 새로고침 하는 것을 없앰
    }

    private void disableViews() {
        // 기존 예약된 enableViewsRunnable를 취소
        if(enableViewsRunnable != null) {
            handler.removeCallbacks(enableViewsRunnable);
        }

        // view 비활성화
        for(View view : viewsToDisable) {
            view.setEnabled(false);
        }

        // 한 시간 후에 다시 활성화하는 Runnable 정의
        enableViewsRunnable = () -> {
            for(View view : viewsToDisable) {
                view.setEnabled(true);
            }
        };

        // 한시간 후에 enableViewsRunnable 실행 예약
        handler.postDelayed(enableViewsRunnable, 1000);
    }
}
