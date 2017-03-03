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
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private CheckBox cbAutoUpdate;
    private CheckBox cbNotice;

    //Data Read Write
    SharedPreferences spUser = getSharedPreferences("User", 0);
    SharedPreferences spHAEYUM = getSharedPreferences("HAEYUM", 0);
    SharedPreferences spVersion = getSharedPreferences("Version", 0);
    SharedPreferences spSchedule = getSharedPreferences("Schedule", 0);
    SharedPreferences spCalendar = getSharedPreferences("Calendar", 0);
    SharedPreferences spSetting = getSharedPreferences("Setting", 0);
    SharedPreferences.Editor speUser = spUser.edit();
    SharedPreferences.Editor speHAEYUM = spHAEYUM.edit();
    SharedPreferences.Editor speVersion = spVersion.edit();
    SharedPreferences.Editor speSchedule = spSchedule.edit();
    SharedPreferences.Editor speCalendar = spCalendar.edit();
    SharedPreferences.Editor speSetting = spSetting.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        cbAutoUpdate = (CheckBox)findViewById(R.id.setting_auto_update);
        cbNotice = (CheckBox)findViewById(R.id.setting_notice);

        if(spSetting.getBoolean("notice", false))
           cbNotice.setChecked(true);
        else
            cbNotice.setChecked(false);

        if(spSetting.getBoolean("autoUpdate", false))
            cbAutoUpdate.setChecked(true);
        else
            cbAutoUpdate.setChecked(false);

    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btn_report:
                Toast.makeText(this, "FUCK", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_reset:
                speUser.clear();
                speUser.commit();
                speHAEYUM.clear();
                speHAEYUM.commit();
                speVersion.clear();
                speVersion.commit();
                speSchedule.clear();
                speSchedule.commit();
                speCalendar.clear();
                speCalendar.commit();

                onAlert("학생정보 초기화 완료!", "학생정보를 모두 초기화하였습니다!\n애플리케이션을 재시작합니다");
                break;

            case R.id.btn_history:
                onAlert("업데이트 내역", "역사따윈 남기지 않습니다.");
                break;

            case R.id.btn_producer:
                onAlert("개발자 정보", "개발자 : 2학년 7반 20번 유광무\n동아리 : multiCore\n\n본 애플리케이션의 저작권은 모두 유광무에게 있습니다.\n\n문의사항 있으시면 언제든지 연락주세요");
                break;

            case R.id.btn_save:
                speSetting.putBoolean("autoUpdate", true);
                speSetting.putBoolean("notice", true);
                speSetting.commit();

                Toast.makeText(this, "데이터를 저장하였습니다", Toast.LENGTH_SHORT).show();
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
        alert.show();
    }
}
