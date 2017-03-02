package com.haeyum.pcm_citizen;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.RadialGradient;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    private CheckBox cbAutoUpdate;
    private CheckBox cbNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        cbAutoUpdate = (CheckBox)findViewById(R.id.setting_auto_update);
        cbNotice = (CheckBox)findViewById(R.id.setting_notice);
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btn_report:
                break;

            case R.id.btn_reset:
                SharedPreferences spUser = getSharedPreferences("User", 0);
                SharedPreferences.Editor speUser = spUser.edit();

                speUser.clear();
                speUser.commit();

                onAlert("학생정보 초기화 완료!", "학생정보를 모두 초기화하였습니다!\n애플리케이션을 재시작합니다");
                break;

            case R.id.btn_history:
                break;

            case R.id.btn_producer:
                onAlert("개발자 정보", "개발자 : 2학년 7반 20번 유광무\n동아리 : multiCore\n\n본 애플리케이션의 저작권은 모두 유광무에게 있습니다.\n\n문의사항 있으시면 언제든지 연락주세요");
                break;

            case R.id.btn_save:
                break;
        }
    }

    private void onAlert(String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setTitle(title);
        alert.setMessage(msg);
    }
}
