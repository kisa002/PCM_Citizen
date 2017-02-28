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
import android.view.WindowManager;
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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //레이아웃
    private Button btn_weather;
    private Button btn_lunch;
    private Button btn_schedule;
    private Button btn_calendar;

    //학생 정보
    String infoName, infoCode;
    int infoGrade, infoClass, infoNumber;

    //웹파싱
    String urlAddress = null;
    Handler handler = new Handler(); // 화면에 그려주기 위한 객체

    //버전 체크
    String lunchVersion = null;
    String scheduleVersion = null;
    String calendarVersion = null;

    //웹파싱 임시 변수
    String web_text = null;
    String weather_text = null;
    String lunch_text = null;
    String schedule_text = null;
    String calendar_text = null;

    //데이터베이스
    String lunch_month[] = new String[32];
    String schedule[] = new String[5];
    String calendar[] = new String[13];
    String monthC; //monthChange

    //날짜
    Calendar oCalendar = Calendar.getInstance( );
    int year, month, day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //상태바 없애자
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //User 데이터 받아오기
        SharedPreferences user = getSharedPreferences("User", 0);
        infoName = user.getString("infoName", "ERROR");
        infoCode = user.getString("infoCode", "ERROR");

        infoGrade = user.getInt("infoGrade", 0);
        infoClass = user.getInt("infoClass", 0);
        infoNumber = user.getInt("infoNumber", 0);

        //날짜
        year = oCalendar.get(Calendar.YEAR);
        month = oCalendar.get(Calendar.MONTH) + 1; //이거 +1 지우자
        day = oCalendar.get(Calendar.DAY_OF_MONTH);
        if(month < 10)
            monthC = "0" + String.valueOf(month);
        else
            monthC = String.valueOf(month);
        //onAlert("날짜", String.valueOf(year) + "\n" + String.valueOf(month) + "\n" + String.valueOf(day));

        //Version 정보
        SharedPreferences spVersion = getSharedPreferences("Version", 0);
        //scheduleVersion = spVersion.getString("Schedule", null);
        calendarVersion = spVersion.getString("Calendar", null);
        lunchVersion = spVersion.getString("Lunch", null);

        //네비게이션
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //레이아웃
        btn_weather = (Button)findViewById(R.id.btn_weather);
        btn_lunch = (Button)findViewById(R.id.btn_lunch);
        btn_schedule = (Button)findViewById(R.id.btn_scheudle);
        btn_calendar = (Button)findViewById(R.id.btn_calendar);

        //데이터 파싱
        loadHtml(1);
        loadHtml(2);
        loadHtml(3);
        loadHtml(4);

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

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });

        switch(item.getItemId())
        {
            case R.id.nav_schedule:
                break;

            case R.id.nav_lunch:
                alert.setTitle("오늘 급식");
                alert.setMessage(lunch_text);
                //alert.show();
                break;

            case R.id.nav_calendar:
                break;

            case R.id.nav_notice:
                break;

            case R.id.nav_quit:
                break;
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
                loadHtml(0);
                alert.setTitle("오늘 급식");
                alert.setMessage(lunch_text);
                //alert.show();
                break;

            case R.id.btn_scheudle:
                loadHtml(3);
                alert.setMessage("[월요일]\n" + schedule[0] + "\n\n[화요일]\n" + schedule[1] + "\n\n[수요일]\n" + schedule[2] + "\n\n[목요일]\n" + schedule[3] + "\n\n[금요일]\n" + schedule[4]);
                alert.show();
                break;

            case R.id.btn_calendar:
                loadHtml(4);
                onAlert("올해 일정", "[1월 일정]\n" + calendar[1] + "\n\n[2월 일정]\n" + calendar[2] + "\n\n[3월 일정]\n" + calendar[3] + "\n\n[3월 일정]\n" + calendar[4] + "\n\n[5월 일정]\n" + calendar[5] + "\n\n[6월 일정]\n" + calendar[6] + "\n\n[7월 일정]\n" + calendar[7] + "\n\n[8월 일정]\n" + calendar[8] + "\n\n[9월 일정]\n" + calendar[9] + "\n\n[10월 일정]\n" + calendar[10] + "\n\n[11월 일정]\n" + calendar[11] + "\n\n[12월 일정\n" + calendar[12]);
                break;
        }
    }

    void lunch_load()
    {
        SharedPreferences lunch  = getSharedPreferences("HAEYUM", 0);

        int max_lunch = lunch.getInt("lunch_max" + year + monthC, 0);

        String temp = "";

        for(int i=1; i<=max_lunch; i++)
        {
            lunch_month[i] = lunch.getString("lunch_" + year + monthC + "m" + i + "d",null);

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
                    switch(menu)
                    {
                        case 0:
                            urlAddress = "http://stu.goe.go.kr/sts_sci_md00_001.do?domainCode=J10&schYm="+ year + monthC + "&schulCode=J100000836&schulCrseScCode=4&schulKndScCode=04";
                            break;
                        case 1:
                            urlAddress = "http://www.accuweather.com/ko/kr/pyeongchon-dong/2041963/current-weather/2041963";
                            break;
                        case 2:
                            urlAddress = "http://stu.goe.go.kr/sts_sci_md00_001.do?domainCode=J10&schYm="+ year + monthC + "&schulCode=J100000836&schulCrseScCode=4&schulKndScCode=04";
                            break;
                        case 3:
                            urlAddress = "http://multicore.dothome.co.kr/PCM_Citizen/DataBase/Schedule.ini";
                            break;
                        case 4:
                            urlAddress = "http://multicore.dothome.co.kr/PCM_Citizen/DataBase/Calendar.ini";
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

                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            int pos1, pos2, i;

                            String ver, tmp, tmp2, temp = "";

                            switch (menu)
                            {

                                case 0:
                                    lunch_month[0] = web_text;

                                    int max_lunch;

                                    pos1 = lunch_month[0].indexOf("<tbody>") + 7;
                                    pos2 = lunch_month[0].indexOf("알레르기") + 4;

                                    lunch_month[0] = lunch_month[0].substring(pos1, pos2);

                                    for(i=34; ; i--)
                                        if(lunch_month[0].indexOf("<div>" + i) != -1)
                                        {
                                            max_lunch = i;
                                            break;
                                        }

                                    for(i=1; i <= max_lunch; i++)
                                    {
                                        pos1 = lunch_month[0].indexOf("<div>" + String.valueOf(i) + "<");
                                        pos2 = lunch_month[0].indexOf("알레르기") + 4;
                                        lunch_month[0] = lunch_month[0].substring(pos1, pos2);

                                        pos1 = lunch_month[0].indexOf("<div>" + String.valueOf(i));
                                        pos2 = lunch_month[0].indexOf("</td>");

                                        lunch_month[i] = lunch_month[0].substring(pos1, pos2 + 6);
                                    }

                                    SharedPreferences lunch = getSharedPreferences("HAEYUM", 0);
                                    SharedPreferences.Editor editor = lunch.edit();

                                    editor.putInt("lunch_max201703",max_lunch);
                                    editor.commit();

                                    for(i=1; i <= max_lunch; i++)
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
                                    break;

                                case 2:
                                    lunch_text = web_text;

                                    pos1 = lunch_text.indexOf("<div>" + day + "<") + 13;
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
                                        //break;
                                    }
                                    else {

                                        lunch_text = "오늘 급식은 없습니다.";
                                        btn_lunch.setText("TODAY LUNCH\n\n" + lunch_text);

                                        alert.setTitle("오늘 급식");
                                        alert.setMessage(lunch_text);
                                        //alert.show();
                                    }
                                    break;

                                case 3:
                                    //시간표
                                    schedule_text = web_text;

                                    SharedPreferences spSchedule = getSharedPreferences("Schedule", 0);
                                    SharedPreferences.Editor speSchedule = spSchedule.edit();

                                    scheduleVersion = spSchedule.getString("Version", "20000330");

                                    pos1 = schedule_text.indexOf("=");
                                    pos2 = schedule_text.indexOf(";");
                                    ver = schedule_text.substring(pos1 + 1, pos2);

                                    if(!scheduleVersion.equals(ver))
                                    {
                                        StringBuffer sb;

                                        pos1 = schedule_text.indexOf("[" + infoCode + "]");
                                        schedule_text = schedule_text.substring(pos1, schedule_text.length());

                                        pos1 = schedule_text.indexOf("월") + 2;
                                        pos2 = schedule_text.indexOf(";");
                                        schedule[0] = schedule_text.substring(pos1, pos2);
                                        schedule[0] = schedule[0].replaceAll("/", "\ni교시 : ");
                                        schedule_text = schedule_text.substring(pos2 + 1, schedule_text.length());
                                        schedule[0] = "i교시 : " + schedule[0];
                                        sb = new StringBuffer(schedule[0]);

                                        for(i=1; i<=7;  i++)
                                        {
                                            pos1 = sb.indexOf("i교시");
                                            sb.delete(pos1, pos1 + 1);
                                            sb.insert(pos1, i);
                                        }
                                        schedule[0] = sb.toString();

                                        pos1 = schedule_text.indexOf("화") + 2;
                                        pos2 = schedule_text.indexOf(";");
                                        schedule[1] = schedule_text.substring(pos1, pos2);
                                        schedule[1] = schedule[1].replace("/", "\ni교시 : ");
                                        schedule_text = schedule_text.substring(pos2 + 1, schedule_text.length());
                                        schedule[1] = "i교시 : " + schedule[1];
                                        sb = new StringBuffer(schedule[1]);

                                        for(i=1; i<=7;  i++)
                                        {
                                            pos1 = sb.indexOf("i교시");
                                            sb.delete(pos1, pos1 + 1);
                                            sb.insert(pos1, i);
                                        }
                                        schedule[1] = sb.toString();

                                        pos1 = schedule_text.indexOf("수") + 2;
                                        pos2 = schedule_text.indexOf(";");
                                        schedule[2] = schedule_text.substring(pos1, pos2);
                                        schedule[2] = schedule[2].replaceAll("/", "\ni교시 : ");
                                        schedule_text = schedule_text.substring(pos2 + 1, schedule_text.length());
                                        schedule[2] = "i교시 : " + schedule[2];
                                        sb = new StringBuffer(schedule[2]);
                                        for(i=1; i<=7;  i++)
                                        {
                                            pos1 = sb.indexOf("i교시");
                                            sb.delete(pos1, pos1 + 1);
                                            sb.insert(pos1, i);
                                        }
                                        schedule[2] = sb.toString();

                                        pos1 = schedule_text.indexOf("목") + 2;
                                        pos2 = schedule_text.indexOf(";");
                                        schedule[3] = schedule_text.substring(pos1, pos2);
                                        schedule[3] = schedule[3].replaceAll("/", "\ni교시 : ");
                                        schedule_text = schedule_text.substring(pos2 + 1, schedule_text.length());
                                        schedule[3] = "i교시 : " + schedule[3];
                                        sb = new StringBuffer(schedule[3]);
                                        for(i=1; i<=7;  i++)
                                        {
                                            pos1 = sb.indexOf("i교시");
                                            sb.delete(pos1, pos1 + 1);
                                            sb.insert(pos1, i);
                                        }
                                        schedule[3] = sb.toString();

                                        pos1 = schedule_text.indexOf("금") + 2;
                                        pos2 = schedule_text.indexOf(";");
                                        schedule[4] = schedule_text.substring(pos1, pos2);
                                        schedule[4] = schedule[4].replaceAll("/", "\ni교시 : ");
                                        schedule_text = schedule_text.substring(pos2 + 1, schedule_text.length());
                                        schedule[4] = "i교시 : " + schedule[4];
                                        sb = new StringBuffer(schedule[4]);

                                        for(i=1; i<=7;  i++)
                                        {
                                            pos1 = sb.indexOf("i교시");
                                            sb.delete(pos1, pos1 + 1);
                                            sb.insert(pos1, i);
                                        }
                                        schedule[4] = sb.toString();

                                        btn_schedule.setText("Today Schedule\n\n" + schedule[1]);
                                        //alert.setMessage(schedule_text);
                                        //alert.setMessage(String.valueOf(pos1));
                                        //alert.setMessage(ver);
                                        //alert.setMessage(schedule[4]);
                                        //alert.setMessage(schedule[0] + "\n\n" + schedule[1] + "\n\n" + schedule[2] + "\n\n" + schedule[3] + "\n\n" + schedule[4]);
                                        //alert.show();

                                        speSchedule.putString("Version", ver);
                                        speSchedule.putString("Monday", schedule[0]);
                                        speSchedule.putString("Tuesday", schedule[1]);
                                        speSchedule.putString("Wednesday", schedule[2]);
                                        speSchedule.putString("Thursday", schedule[3]);
                                        speSchedule.putString("Friday", schedule[4]);
                                        speSchedule.commit();

                                        alert.setTitle("시간표 업데이트");
                                        alert.setMessage("시간표를 최신버전으로 업데이트하였습니다.\n" + "업데이트된 버전 : " + ver);
                                        alert.show();
                                    }
                                    else
                                    {
                                        schedule[0] = spSchedule.getString("Monday", null);
                                        schedule[1] = spSchedule.getString("Tuesday", null);
                                        schedule[2] = spSchedule.getString("Wednesday", null);
                                        schedule[3] = spSchedule.getString("Thursday", null);
                                        schedule[4] = spSchedule.getString("Friday", null);

                                        //final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

                                        if(oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                                            btn_schedule.setText("Today Schedule\n\n행복한 주말");
                                        else
                                            btn_schedule.setText("Today Schedule\n\n" + schedule[oCalendar.get(Calendar.DAY_OF_WEEK) - 2]);
                                    }
                                    break;

                                case 4:
                                    calendar_text = web_text;

                                    StringBuffer sb2;

                                    SharedPreferences spCalendar = getSharedPreferences("Calendar", 0);
                                    SharedPreferences.Editor speCalendar = spCalendar.edit();

                                    calendarVersion = spCalendar.getString("Version", "20000330");

                                    pos1 = calendar_text.indexOf("=");
                                    pos2 = calendar_text.indexOf(";");
                                    ver = calendar_text.substring(pos1 + 1, pos2);

                                    if(!calendarVersion.equals(ver))
                                    {
                                        calendar_text = calendar_text.substring(pos1, calendar_text.length());

                                        for(i=1; i<=12; i++) {
                                            calendar[i] = "일정이 없습니다.";

                                            if(i < 10) {
                                                pos1 = calendar_text.indexOf("[" + year + "0" + i + "]") + 9; //201703
                                                pos2 = calendar_text.indexOf("[" + year + "0" + i + "]$"); //201703$
                                            }
                                            else {
                                                pos1 = calendar_text.indexOf("[" + year + i + "]") + 9; //201703
                                                pos2 = calendar_text.indexOf("[" + year + i + "]$"); //201703$
                                            }

                                            //onAlert("", pos1 + " / " + pos2);

                                            if(pos1 != -1) {
                                                calendar[i] = calendar_text.substring(pos1, pos2);
                                                calendar[i] = calendar[i].replaceAll("=", " : ");
                                                calendar[i] = calendar[i].replaceAll("월", "월 ");

                                                if(calendar[i].length() > 0)
                                                    calendar[i] = calendar[i].substring(0, calendar[i].length() - 1);
                                                //else
                                                    //onAlert("", String.valueOf(i));
                                            }

                                            //Log.d("i : " + i, calendar[i]);
                                        }

                                        btn_calendar.setText("Month Calendar\n\n" + calendar[month]);

                                        for(i=1; i<13; i++)
                                            speCalendar.putString("Calendar[" + i + "]", calendar[i]);
                                        speCalendar.putString("Version", ver);
                                        speCalendar.commit();

                                        onAlert("일정표 업데이트", "일정표를 최신버전으로 업데이트하였습니다.\n" + "업데이트된 버전 : " + ver);
                                    }
                                    else {
                                        for (i = 1; i < 13; i++)
                                            calendar[i] = spCalendar.getString("Calendar[" + i + "]", "ERROR");

                                        btn_calendar.setText("Month Calendar\n\n" + calendar[month]);
                                    }
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

    private void onAlert(String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });

        alert.setTitle(title);
        alert.setMessage(msg);
        alert.show();
    }
}
