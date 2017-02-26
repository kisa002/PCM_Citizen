package com.haeyum.pcm_citizen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ///버튼 관리
    private ImageView img_wather;
    String imgUrl = "https://ssl.pstatic.net/static/weather/images/w_icon/w_t01.gif";
    Bitmap bmImg;
    back task;

    private Button btn_weather;
    private Button btn_lunch;

    String lunch_month[] = new String[32];

    String urlAddress = null;
    Handler handler = new Handler(); // 화면에 그려주기 위한 객체

    String web_text = null;

    String weather_text = null;
    String lunch_text = null;
    String schedule_text = null;
    String calendar_text = null;

    Boolean data_load = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btn_weather = (Button)findViewById(R.id.btn_weather);
        btn_lunch = (Button)findViewById(R.id.btn_lunch);

        //데이터 파싱
        urlAddress = "http://www.accuweather.com/ko/kr/pyeongchon-dong/2041963/current-weather/2041963";
        loadHtml(1);

        /*
        img_wather = (ImageView)findViewById(R.id.img_weather);

        task = new back();
        task.execute("https://ssl.pstatic.net/static/weather/images/w_icon/w_t01.gif");
        */

        lunch_load();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            // Handle the camera action
        } else if (id == R.id.nav_lunch) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });

            alert.setTitle("오늘 급식");
            alert.setMessage(lunch_text);
            //alert.show();
        } else if (id == R.id.nav_calendar) {

        } else if (id == R.id.nav_notice) {

        } else if (id == R.id.nav_quit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    ///[실제 코딩]

    public void onClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        switch (v.getId()) {

            case R.id.btn_weather:
                urlAddress = "http://www.accuweather.com/ko/kr/pyeongchon-dong/2041963/current-weather/2041963";
                loadHtml(1);
                alert.setTitle("오늘 날씨");
                alert.setMessage(weather_text);
                alert.show();
                break;

            case R.id.btn_lunch:
                //urlAddress = "http://stu.goe.go.kr/sts_sci_md00_001.do?domainCode=J10&schYm="+"201703"+"&schulCode=J100000836&schulCrseScCode=4&schulKndScCode=04";

                loadHtml(0);
                alert.setTitle("오늘 급식");
                alert.setMessage(lunch_text);
                //alert.show();
                break;

            case R.id.btn_scheudle:
                urlAddress = "http://multicore.dothome.co.kr/PCM_Citizen/DataBase/Schedule.ini";
                loadHtml(3);
                alert.setMessage(schedule_text);
                //alert.show();
                break;

            case R.id.btn_calendar:

                break;
        }
    }

    void lunch_load()
    {
        SharedPreferences lunch  = getSharedPreferences("HAEYUM", 0);

        int max_lunch = lunch.getInt("lunch_max201703", 0);

        String temp = "";

        for(int i=1; i<=max_lunch; i++)
        {
            lunch_month[i] = lunch.getString("lunch_201703m" + i + "d",null);

            temp += "[" + i + "일 급식]\n" + lunch_month[i] + "\n";
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });

        alert.setTitle("이번달 급식");
        alert.setMessage(temp);
        //alert.show();


    }

    void loadHtml(final int menu) { // 웹에서 html 읽어오기
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuffer sb = new StringBuffer();

                try {
                    URL url = new URL(urlAddress);
                    HttpURLConnection conn =
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

                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            int pos1, pos2;

                            switch (menu)
                            {

                                case 0:
                                    lunch_month[0] = web_text;

                                    int max_lunch;

                                    pos1 = lunch_month[0].indexOf("<tbody>") + 7;
                                    pos2 = lunch_month[0].indexOf("알레르기") + 4;

                                    lunch_month[0] = lunch_month[0].substring(pos1, pos2);

                                    for(int i=34; ; i--)
                                        if(lunch_month[0].indexOf("<div>" + i) != -1)
                                        {
                                            max_lunch = i;
                                            break;
                                        }

                                    for(int i=1; i <= max_lunch; i++)
                                    {
                                        pos1 = lunch_month[0].indexOf("<div>" + String.valueOf(i) + "<");
                                        pos2 = lunch_month[0].indexOf("알레르기") + 4;
                                        lunch_month[0] = lunch_month[0].substring(pos1, pos2);

                                        pos1 = lunch_month[0].indexOf("<div>" + String.valueOf(i));
                                        pos2 = lunch_month[0].indexOf("</td>");

                                        lunch_month[i] = lunch_month[0].substring(pos1, pos2 + 6);
                                    }

                                    String temp = "";

                                    SharedPreferences lunch = getSharedPreferences("HAEYUM", 0);
                                    SharedPreferences.Editor editor = lunch.edit();

                                    editor.putInt("lunch_max201703",max_lunch);
                                    editor.commit();

                                    for(int i=1; i <= max_lunch; i++)
                                    {
                                        temp += "[" + i + "일]";
                                        lunch_month[i] = lunch_month[i].replace("<div>" + i,"");
                                        lunch_month[i] = lunch_month[i].replace("</div>","");
                                        lunch_month[i] = lunch_month[i].replace("<br />","\n");
                                        lunch_month[i] = lunch_month[i].replace("</td>","");

                                        if(lunch_month[i].indexOf("중식") == -1)
                                            lunch_month[i] = "\n급식이 없습니다.\n";
                                        else
                                            lunch_month[i] = lunch_month[i].substring(1, lunch_month[i].length());

                                        lunch_month[i] = lunch_month[i].replace("[중식]","");

                                        temp += lunch_month[i] + "\n";

                                        editor.putString("lunch_201703m"  + i + "d", lunch_month[i]); //lunch_10m3d
                                        editor.commit();
                                    }

                                    alert.setTitle("이번달 급식");
                                    alert.setMessage(temp);
                                    alert.show();

                                    break;

                                case 1:
                                    weather_text = web_text;

                                    pos1 = weather_text.indexOf("temp:");
                                    pos2 = weather_text.indexOf("\"});") + 2;

                                    weather_text = weather_text.substring(pos1, pos2);

                                    weather_text = weather_text.replace("temp:'", "현재 온도 : ");
                                    weather_text = weather_text.replace("',  realfeel:'", "도\n체감 온도 : ");
                                    weather_text = weather_text.replace("',  text:\"","도\n\n현재 상태 : ");
                                    weather_text = weather_text.replace("\"}","");

                                    alert.setTitle("오늘 날씨");
                                    alert.setMessage(weather_text);
                                    //alert.show();

                                    btn_weather.setText("TODAY WEATHER\n\n" + weather_text);

                                    urlAddress = "http://stu.goe.go.kr/sts_sci_md00_001.do?domainCode=J10&schYm="+"201703"+"&schulCode=J100000836&schulCrseScCode=4&schulKndScCode=04";
                                    loadHtml(2);
                                    break;

                                case 2:
                                    lunch_text = web_text;

                                    pos1 = lunch_text.indexOf("<div>" + 10 + "<") + 13;
                                    pos2 = lunch_text.indexOf("알레르기");

                                    lunch_text = lunch_text.substring(pos1, pos2);

                                    pos1 = 0;
                                    pos2 = lunch_text.indexOf("</div>") -1;

                                    lunch_text = lunch_text.substring(pos1, pos2);

                                    pos1 = lunch_text.indexOf("td>");

                                    if(pos1 >= 5 || pos1 == -1){

                                        lunch_text = lunch_text.replaceAll("<br />", "\n");

                                        lunch_text = lunch_text.replace(">[중식]", "");
                                        lunch_text = lunch_text.replace("[중식]", "");

                                        btn_lunch.setText("TODAY LUNCH\n" + lunch_text);

                                        alert.setTitle("오늘 급식");
                                        alert.setMessage(lunch_text);
                                        //alert.show();
                                        break;
                                    }
                                    else {

                                        lunch_text = "오늘 급식은 없습니다.";
                                        btn_lunch.setText("TODAY LUNCH\n\n" + lunch_text);

                                        alert.setTitle("오늘 급식");
                                        alert.setMessage(lunch_text);
                                        //alert.show();
                                    }
                                    urlAddress = "http://multicore.dothome.co.kr/PCM_Citizen/DataBase/Schedule.ini";
                                    loadHtml(3);
                                    break;

                                case 3:
                                    //시간표
                                    schedule_text = web_text;
                                    alert.setMessage(schedule_text);
                                    alert.show();
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

    private class back extends AsyncTask<String, Integer,Bitmap> {



        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            }catch(IOException e){
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img){
            img_wather.setImageBitmap(bmImg);
        }

    }

}
