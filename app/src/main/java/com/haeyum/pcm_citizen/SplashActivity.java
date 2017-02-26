package com.haeyum.pcm_citizen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {

    //static String infoName, infoCode;
    //static int infoGrade, infoClass, infoNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                SharedPreferences user = getSharedPreferences("User", 0);
                if(user.getString("infoCode", null) == null) {
                    Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(register);
                }
                else {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                }
                finish();
            }
        }, 2000);
    }
}
