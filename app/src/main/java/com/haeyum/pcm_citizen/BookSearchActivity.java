package com.haeyum.pcm_citizen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class BookSearchActivity extends AppCompatActivity {

    public ArrayAdapter<Object> bookAdapter;

    private ListView listBook;
    private EditText editBookSearch;
    private Button btnSearch;
    private TextView tvResult;

    Handler handler = new Handler();

    String web_text, cookie, cookieResult;
    String urlAddress = "";
    String schoolCode;

    int count;

    //해윰의 역사 - 유광무 / 해윰출판사 / 330.20전307C

    String bookCode[] = new String[50];
    String bookTitle[] = new String[50];
    String bookWriter[] = new String[50];
    String bookPublisher[] = new String[50];
    String bookType[] = new String[50];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        listBook = (ListView)findViewById(R.id.listBook);
        tvResult = (TextView) findViewById(R.id.tvResult);

        editBookSearch = (EditText)findViewById(R.id.editBookSearch);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listBook.setAdapter(bookAdapter);

        listBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                BookInformationActivity.bookCode = bookCode[position];
                startActivity(new Intent(getApplicationContext(), BookInformationActivity.class));
            }
        });

        ConnectivityManager cManager;
        NetworkInfo mobile;
        NetworkInfo wifi;

        cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(!(mobile.isConnected() || wifi.isConnected()))
            Toast.makeText(this, "도서 검색시 네트워크에 연결되어 있어야합니다!", Toast.LENGTH_SHORT).show();
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
                            urlAddress = "http://reading.gglec.go.kr/r/reading/search/schoolCodeSetting.jsp?schoolCode=3246";
                            break;

                        case 1:
                            urlAddress = "http://reading.gglec.go.kr/r/reading/search/schoolSearchResult.jsp?&dataType=ALL&division1=ALL&connect1=A&searchCon1=" + editBookSearch.getText().toString();
                            break;
                    }

                    URL url = new URL(urlAddress);
                    final HttpURLConnection conn =
                            (HttpURLConnection)url.openConnection();// 접속
                    if (conn != null) {
                        conn.setConnectTimeout(2000);

                        if(menu == 1) {
                            String myCookies = "JSESSIONID=" + cookieResult;
                            conn.setRequestProperty("Cookie", myCookies);

                            //Log.i("쿠키데이터", cookieResult);
                        }

                        if (conn.getResponseCode()
                                ==HttpURLConnection.HTTP_OK){
                            //    데이터 읽기

                            BufferedReader br
                                    = new BufferedReader(new InputStreamReader
                                    (conn.getInputStream(),"UTF-8")); //"euc-kr"
                            while(true) {
                                String line = br.readLine();
                                if (line == null) break;
                                sb.append(line+"\n");
                            }
                            br.close(); // 스트림 해제
                        }

                        if(menu == 0) {
                            Map m = conn.getHeaderFields();
                            if (m.containsKey("Set-Cookie")) {
                                Collection c = (Collection) m.get("Set-Cookie");
                                for (Iterator i = c.iterator(); i.hasNext(); ) {
                                    cookie = (String) i.next();
                                }
                            }
                        }

                        conn.disconnect(); // 연결 끊기
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            web_text = sb.toString();

                            String text = web_text;

                            int pos1, pos2;

                            switch (menu)
                            {

                                case 0:
                                    pos1 = cookie.indexOf("=");
                                    pos2 = cookie.indexOf(";");

                                    cookie = cookie.substring(pos1 + 1, pos2);

                                    cookieResult = cookie;

                                    loadHtml(1);

                                    break;

                                case 1:
                                    String data = "";

                                    pos1 = text.indexOf("color\">") + 7;
                                    pos2 = text.indexOf("</span>건");
                                    count = Integer.parseInt(text.substring(pos1, pos2));

                                    tvResult.setText(editBookSearch.getText().toString() + " 도서의 " + count + "건의 결과");

                                    pos1 = text.indexOf("<tbody>");
                                    pos2 = text.indexOf("</tbody>") + 8;
                                    text = text.substring(pos1 , pos2);

                                    //c언어

                                    if(count > 10)
                                        count = 10;

                                    bookAdapter.clear();
                                    for(int i=0; i<count; i++)
                                    {
                                        pos1 = text.indexOf("goDetail('") + 10;
                                        pos2 = text.indexOf("');");
                                        bookCode[i] = text.substring(pos1, pos2);

                                        pos1 = text.indexOf("bold\">") + 6;
                                        pos2 = text.indexOf("</span>");
                                        bookTitle[i] = text.substring(pos1, pos2);

                                        pos1 = text.indexOf("</td>") + 5;
                                        pos2 = text.indexOf("</tbody>") + 8;
                                        text = text.substring(pos1, pos2);

                                        pos1 = text.indexOf("<td>");
                                        pos2 = text.indexOf("</td>");
                                        bookWriter[i] = text.substring(pos1 + 4, pos2);
                                        bookWriter[i] = bookWriter[i].replace("\n", "");
                                        bookWriter[i] = bookWriter[i].replace("\t", "");

                                        pos1 = pos2 + 4;
                                        pos2 = text.indexOf("</tbody>") + 8;
                                        text = text.substring(pos1, pos2);

                                        pos1 = text.indexOf("<td>");
                                        pos2 = text.indexOf("</td>");
                                        bookPublisher[i] = text.substring(pos1 + 4, pos2);
                                        bookPublisher[i] = bookPublisher[i].replace("\n", "");
                                        bookPublisher[i] = bookPublisher[i].replace("\t", "");
                                        bookPublisher[i] = bookPublisher[i].substring(0, bookPublisher[i].indexOf("("));

                                        pos1 = pos2 + 4;
                                        pos2 = text.indexOf("</tbody>") + 8;
                                        text = text.substring(pos1, pos2);

                                        pos1 = text.indexOf("<td>");
                                        pos2 = text.indexOf("</td>");
                                        bookType[i] = text.substring(pos1 + 4, pos2);
                                        bookType[i] = bookType[i].replace("\n", "");
                                        bookType[i] = bookType[i].replace("\t", "");

                                        pos1 = pos2 + 4;
                                        pos2 = text.indexOf("</tbody>") + 8;
                                        text = text.substring(pos1, pos2);

                                        data = bookTitle[i] + " - " + bookWriter[i] + "\n" + bookPublisher[i] + " - " + bookType[i];
                                        bookAdapter.add(data);
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

    public void onClick(View v)
    {
        loadHtml(0);
    }
}
