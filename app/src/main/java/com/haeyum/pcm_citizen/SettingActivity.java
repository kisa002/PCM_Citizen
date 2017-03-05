package com.haeyum.pcm_citizen;

import android.content.DialogInterface;
import android.content.Intent;
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
    SharedPreferences spUser;
    SharedPreferences spHAEYUM;
    SharedPreferences spVersion;
    SharedPreferences spSchedule;
    SharedPreferences spCalendar;
    SharedPreferences spSetting;
    SharedPreferences.Editor speUser;
    SharedPreferences.Editor speHAEYUM;
    SharedPreferences.Editor speVersion;
    SharedPreferences.Editor speSchedule;
    SharedPreferences.Editor speCalendar;
    SharedPreferences.Editor speSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        cbAutoUpdate = (CheckBox)findViewById(R.id.setting_auto_update);
        cbNotice = (CheckBox)findViewById(R.id.setting_notice);

        spUser = getSharedPreferences("User", 0);
        spHAEYUM = getSharedPreferences("HAEYUM", 0);
        spVersion = getSharedPreferences("Version", 0);
        spSchedule = getSharedPreferences("Schedule", 0);
        spCalendar = getSharedPreferences("Calendar", 0);
        spSetting = getSharedPreferences("Setting", 0);
        speUser = spUser.edit();
        speHAEYUM = spHAEYUM.edit();
        speVersion = spVersion.edit();
        speSchedule = spSchedule.edit();
        speCalendar = spCalendar.edit();
        speSetting = spSetting.edit();

        if(spSetting.getBoolean("notice", true))
           cbNotice.setChecked(true);
        else
            cbNotice.setChecked(false);

        if(spSetting.getBoolean("autoUpdate", true))
            cbAutoUpdate.setChecked(true);
        else
            cbAutoUpdate.setChecked(false);

    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.setting_auto_update:
                if(cbAutoUpdate.isChecked() == false)
                    onAlert("경고", "자동 업데이트 해제시, 최신 급식 정보와 일정표를 저장할 수 없습니다.\n또한 네트워크가 연결된 상태에서만 급식과 일정표를 확인할 수 있게됩니다\n\n(자동업데이트 기능이 소모하는 데이터 양은 1회당 1mb도 안됩니다.)");
                break;

            case R.id.setting_notice:
                if(cbNotice.isChecked() == false)
                    onAlert("경고", "공지사항 해제시, 긴급 공지 또는 단축 시간과 같은 정보를 확인할 수 없게됩니다");
                break;

            case R.id.btn_report:
                onAlert("오류 제보", "밑의 연락처로 오타 또는 오류를 보내주세요\n\n전화번호 : 010-6348-1143\n\n예시) 1학년 7반 월요일과 화요일 시간표가 달라요!");
                break;

            case R.id.btn_reset:
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SettingActivity.this);
                alert_confirm.setMessage("학생정보를 초기화하시겠습니까?\n\n초기화후 학생 정보를 새로 가입하셔야 합니다").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                speSetting.clear();
                                speSetting.commit();

                                onAlert("학생정보 초기화 완료!", "학생정보를 모두 초기화하였습니다!\n애플리케이션을 재시작합니다");
                                finish();
                                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
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
                break;

            case R.id.btn_history:
                onAlert("업데이트 내역", "2017.02.26 이전 데이터 복구\n2017.02.27 학생등록 추가\n2016.02.28 시간표 알고리즘 변경\n2016.03.01 일정표 알고리즘 변경\n2016.03.02 급식 오류 수정 및 알고리즘 변경\n2016.03.04 설정 메뉴 추가 및 레이아웃 색상과 아이콘 변경\n2016.03.05 레이아웃 색상 변경 및 메뉴 추가, Splash 변경");
                break;

            case R.id.btn_producer:
                onAlert("개발자 정보", "개발자 : 2학년 7반 20번 유광무\n동아리 : multiCore\n\n본 애플리케이션의 저작권은 모두 유광무에게 있습니다.\n\n문의사항 있으시면 언제든지 연락주세요");
                break;

            case R.id.btn_save:
                speSetting.putBoolean("autoUpdate", cbAutoUpdate.isChecked());
                speSetting.putBoolean("notice", cbNotice.isChecked());
                speSetting.commit();

                Toast.makeText(this, "설정 내역을 저장하였습니다", Toast.LENGTH_SHORT).show();
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
