package com.haeyum.pcm_citizen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class BookInformationActivity extends AppCompatActivity {

    static String bookCode = "null";

    public ArrayAdapter<Object> informationAdapter;

    SharedPreferences spHAEYUM;

    ImageView imageBook;
    ListView listInformation;

    Bitmap bitmap;

    Handler handler = new Handler();

    String baseShoppingURL = "";

    String web_text, cookie, cookieResult;
    String urlAddress = "";
    String schoolCode;
    String bookInfo[] = new String[10];
    String holdInfo[][] = new String[10][6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_information);

        imageBook = (ImageView)findViewById(R.id.imageBook);
        listInformation = (ListView)findViewById(R.id.listInformation);

        informationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listInformation.setAdapter(informationAdapter);

        loadHtml(0);
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
                            urlAddress = "http://reading.gglec.go.kr/r/reading/search/schoolBookDetail.jsp?controlNo=" + bookCode;
                            break;

                        case 2:
                            urlAddress = "http://reading.gglec.go.kr/r/reading/search/bookHoldInfo.jsp?controlNo=" + bookCode + "&dataType=MA&schoolCode=3246";
                            break;
                    }

                    URL url = new URL(urlAddress);
                    final HttpURLConnection conn =
                            (HttpURLConnection)url.openConnection();// 접속
                    if (conn != null) {
                        conn.setConnectTimeout(2000);

                        if(menu != 0) {
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

                            String text, temp;

                            int pos1, pos2, count;
                            boolean find = true;

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
                                    text = web_text;

                                    pos1 = text.indexOf("contents_c");
                                    pos2 = text.indexOf("contents 끝") + 10;
                                    text = text.substring(pos1, pos2);

                                    pos1 = text.indexOf("<dl class=\"left\"");
                                    pos2 = text.indexOf("<dl class=\"right\"");
                                    temp = text.substring(pos1, pos2);

                                    pos1 = temp.indexOf("src=\"") + 5;
                                    pos2 = temp.indexOf("\" alt");
                                    temp = temp.substring(pos1, pos2);

                                    baseShoppingURL = "http://reading.gglec.go.kr" + temp;

                                    Thread mThread = new Thread() {

                                        @Override
                                        public void run() {

                                            try {
                                                URL url = new URL(baseShoppingURL); // URL 주소를 이용해서 URL 객체 생성

                                                //  아래 코드는 웹에서 이미지를 가져온 뒤
                                                //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                                                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                                conn.setDoInput(true);
                                                conn.connect();

                                                InputStream is = conn.getInputStream();
                                                bitmap = BitmapFactory.decodeStream(is);

                                            } catch(IOException ex) {

                                            }
                                        }
                                    };

                                    mThread.start(); // 웹에서 이미지를 가져오는 작업 스레드 실행.

                                    try {

                                        //  메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
                                        //  대기해야 하므로 작업스레드의 join() 메소드를 호출해서
                                        //  메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.

                                        mThread.join();

                                        //  이제 작업 스레드에서 이미지를 불러오는 작업을 완료했기에
                                        //  UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정합니다.

                                        imageBook.setImageBitmap(bitmap);
                                    } catch (InterruptedException e) {

                                    }

                                    pos1 = text.indexOf("right\">") + 7;
                                    pos2 = text.indexOf("소장정보") + 4;
                                    text = text.substring(pos1, pos2);

                                    count = 0;
                                    find = true;

                                    while(find)
                                    {
                                        pos1 = text.indexOf(">") + 1;
                                        pos2 = text.indexOf("</dt>");
                                        bookInfo[count] = text.substring(pos1, pos2);

                                        pos1 = pos2 + 5;
                                        pos2 = text.indexOf("소장정보") + 4;
                                        text = text.substring(pos1, pos2);

                                        pos1 = text.indexOf(">") + 1;
                                        pos2 = text.indexOf("</dd>");
                                        bookInfo[count] += " : " + text.substring(pos1, pos2);

                                        bookInfo[count] = bookInfo[count].replace("\n", "");
                                        bookInfo[count] = bookInfo[count].replace("\t", "");
                                        bookInfo[count] = bookInfo[count].replace("<br>", "\n");
                                        bookInfo[count] = bookInfo[count].replace("&nbsp;cm", "");
                                        bookInfo[count] = bookInfo[count].replace(".;", " / ");
                                        bookInfo[count] = bookInfo[count].replace(";", " - ");
                                        bookInfo[count] = bookInfo[count].replace("서명사항", "제목");
                                        bookInfo[count] = bookInfo[count].replace("저자사항", "저자");
                                        bookInfo[count] = bookInfo[count].replace("발행사항", "출판");
                                        bookInfo[count] = bookInfo[count].replace("주제사항", "분류");
                                        bookInfo[count] = bookInfo[count].replace("형태사항", "내용");
                                        bookInfo[count] = bookInfo[count].replace("총서사항", "주제");
                                        bookInfo[count] = bookInfo[count].replace("가격정보", "가격");
                                        informationAdapter.add(bookInfo[count]);

                                        pos1 = pos2 + 5;
                                        pos2 = text.indexOf("소장정보") + 4;
                                        text = text.substring(pos1, pos2);

                                        count ++;

                                        if(text.indexOf("</dd>") < 1)
                                            find = false;
                                    }

                                    loadHtml(2);
                                    break;

                                case 2:
                                    count = 0;
                                    text = web_text;

                                    pos1 = text.indexOf("<tbody>");
                                    pos2 = text.indexOf("</tbody>") + 8;
                                    text = text.substring(pos1, pos2);

                                    find = true;

                                    while(find)
                                    {
                                        pos1 = text.indexOf("<td>") + 4;
                                        pos2 = text.indexOf("</td>");
                                        holdInfo[count][0] = text.substring(pos1, pos2);
                                        holdInfo[count][0] = holdInfo[count][0].replace("\t", "");
                                        holdInfo[count][0] = holdInfo[count][0].replace("\n", "");
                                        text = text.substring(pos2 + 5, text.indexOf("</tbody>") + 8);

                                        pos1 = text.indexOf("<td>") + 4;
                                        pos2 = text.indexOf("</td>");
                                        holdInfo[count][1] = text.substring(pos1, pos2);
                                        holdInfo[count][1] = holdInfo[count][1].replace("\t", "");
                                        holdInfo[count][1] = holdInfo[count][1].replace("\n", "");
                                        text = text.substring(pos2 + 5, text.indexOf("</tbody>") + 8);

                                        pos1 = text.indexOf("<td>") + 4;
                                        pos2 = text.indexOf("</td>");
                                        holdInfo[count][2] = text.substring(pos1, pos2);
                                        holdInfo[count][2] = holdInfo[count][2].replace("\t", "");
                                        holdInfo[count][2] = holdInfo[count][2].replace("\n", "");
                                        text = text.substring(pos2 + 5, text.indexOf("</tbody>") + 8);

                                        pos1 = text.indexOf("<td>") + 4;
                                        pos2 = text.indexOf("</td>");
                                        holdInfo[count][3] = text.substring(pos1, pos2);
                                        holdInfo[count][3] = holdInfo[count][3].replace("\t", "");
                                        holdInfo[count][3] = holdInfo[count][3].replace("\n", "");
                                        text = text.substring(pos2 + 5, text.indexOf("</tbody>") + 8);

                                        pos1 = text.indexOf("<td>") + 4;
                                        pos2 = text.indexOf("</td>");
                                        holdInfo[count][4] = text.substring(pos1, pos2);
                                        text = text.substring(pos2 + 5, text.indexOf("</tbody>") + 8);
                                        holdInfo[count][4] = holdInfo[count][4].replace("\t", "");
                                        holdInfo[count][4] = holdInfo[count][4].replace("\n", "");

                                        pos1 = text.indexOf("<td>") + 4;
                                        pos2 = text.indexOf("</td>");
                                        holdInfo[count][5] = text.substring(pos1, pos2);
                                        text = text.substring(pos2 + 5, text.indexOf("</tbody>") + 8);
                                        holdInfo[count][5] = holdInfo[count][5].replace("\t", "");
                                        holdInfo[count][5] = holdInfo[count][5].replace("\n", "");

                                        informationAdapter.add("");
                                        temp = "대여 현황\n\n" + holdInfo[count][0] + "번 - " + holdInfo[count][2] + "\n코드 : " + holdInfo[count][1] + "\n분류 : " + holdInfo[count][3] + "\n위치 : " + holdInfo[count][4] + "\n반납 예정일 : " + holdInfo[count][5];
                                        temp = temp.replace("&nbsp;", "");
                                        informationAdapter.add(temp);

                                        count += 1;

                                        if(text.indexOf("<td>") < 1)
                                            find = false;
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
}
