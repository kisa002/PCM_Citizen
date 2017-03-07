package com.haeyum.pcm_citizen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class SplashActivity extends Activity {

    //static String infoName, infoCode;
    //static int infoGrade, infoClass, infoNumber;

    //웹파싱
    String urlAddress, web_text;
    Handler handler = new Handler(); // 화면에 그려주기 위한 객체

    String version = "20170306";

    SharedPreferences spVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d("Result","CONNECT Failed");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                SharedPreferences user = getSharedPreferences("User", 0);
                if (user.getString("infoCode", null) == null) {
                    Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(register);
                } else {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                }
                finish();
            }
        }, 2000);

        TextView tvSplash = (TextView)findViewById(R.id.TextSplash);
        tvSplash.setText("ver 1.0");

        //SP
        //spVersion = getSharedPreferences("Version", 0);

        /*
        //네트워크 연결 확인
        ConnectivityManager cManager;
        NetworkInfo mobile;
        NetworkInfo wifi;

        cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobile.isConnected() || wifi.isConnected()) {
            //Log.d("Result","CONNECT SUCCESS");
            //loadHtml(0);
            Log.d("Result","CONNECT Failed");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    SharedPreferences user = getSharedPreferences("User", 0);
                    if (user.getString("infoCode", null) == null) {
                        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(register);
                    } else {
                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(main);
                    }
                    finish();
                }
            }, 2000);

            TextView tvSplash = (TextView)findViewById(R.id.TextSplash);
            tvSplash.setText("ver 1.0");
        }
        else {
            //Log.d("Result","CONNECT Failed");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    SharedPreferences user = getSharedPreferences("User", 0);
                    if (user.getString("infoCode", null) == null) {
                        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(register);
                    } else {
                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(main);
                    }
                    finish();
                }
            }, 2000);
        }
        */
    }

    /*
    void loadHtml(final int menu) { // 웹에서 html 읽어오기
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final StringBuffer sb = new StringBuffer();

                try {
                    switch(menu)
                    {
                        case 0:
                            urlAddress = "http://haeyum.com/PCM_Citizen/DataBase/Version.ini";
                            break;
                    }

                    URL url = new URL(urlAddress);
                    final HttpURLConnection conn =
                            (HttpURLConnection)url.openConnection();// 접속
                    if (conn != null) {
                        conn.setConnectTimeout(2000);
                        conn.setUseCaches(false);
                        if (conn.getResponseCode()
                                ==HttpURLConnection.HTTP_OK){
                            //    데이터 읽기
                            BufferedReader br
                                    = new BufferedReader(new InputStreamReader
                                    (conn.getInputStream(),"UTF-8"));//"euc-kr"
                            while(true) {
                                String line = br.readLine();
                                if (line == null) break;
                                sb.append(line+"\n");
                            }
                            br.close(); // 스트림 해제
                        }
                        conn.disconnect(); // 연결 끊기
                    }
                    // 값을 출력하기
                    //Log.d("test", sb.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            web_text = sb.toString();

                            switch (menu)
                            {

                                case 0:
                                    //현재 일시 버전 -> 20170306
                                    //updateCheck();

                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start(); // 쓰레드 시작
    }


    private void updateCheck() {
        version = spVersion.getString("Version", "20170306");

        final int[] pos1 = new int[1];
        final int[] pos2 = new int[1];

        String ver;
        final String[] url = new String[1];

        pos1[0] = web_text.indexOf("=");
        pos2[0] = web_text.indexOf(";");
        ver = web_text.substring(pos1[0] + 1, pos2[0]);

        if (!version.equals(ver)) {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SplashActivity.this);
            alert_confirm.setTitle("경영시민 업데이트");
            alert_confirm.setMessage("새로운 버전이 출시되었습니다.\n\n현재 버전 : " + "\n최신 버전 : " + "\n\n업데이트하시겠습니까?").setCancelable(false).setPositiveButton("업데이트",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            web_text = web_text.substring(pos2[0], web_text.length());
                            pos1[0] = web_text.indexOf("=");
                            pos2[0] = web_text.indexOf(";");

                            url[0] = web_text.substring(pos1[0] + 1, pos2[0]);

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url[0])));
                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'No'
                            return;
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    }
    */
}
