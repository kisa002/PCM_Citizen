package com.haeyum.pcm_citizen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    //String infoGrade;
    //String infoClass;
    //String infoNumber;

    int infoGrade;
    int infoClass;
    int infoNumber;

    String infoName;
    String infoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //final TextView tv = (TextView)findViewById(R.id.textView1);

        Spinner spinnerGrade = (Spinner)findViewById(R.id.spinner_grade);
        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this, R.array.Grade, R.layout.spinner_item);

        spinnerGrade.setAdapter(gradeAdapter);
        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoGrade = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner spinnerClass = (Spinner)findViewById(R.id.spinner_class);
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this, R.array.Class, R.layout.spinner_item); //android.R.layout.simple_spinner_dropdown_item

        spinnerClass.setAdapter(classAdapter);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoClass = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        Spinner spinnerNumber = (Spinner)findViewById(R.id.spinner_number);
        ArrayAdapter<CharSequence> numberAdapter = ArrayAdapter.createFromResource(this, R.array.Number, R.layout.spinner_item);

        spinnerNumber.setAdapter(numberAdapter);
        spinnerNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoNumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btn_register:
                EditText et = (EditText)findViewById(R.id.editTextName);
                infoName = et.getText().toString();

                if(infoName != "")
                    if(infoGrade != 0 && infoClass != 0 && infoNumber != 0 && !infoName.equals("")) {
                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(RegisterActivity.this);
                        alert_confirm.setTitle("학생 정보 확인");
                        alert_confirm.setMessage(String.valueOf(infoGrade) + "학년 " + String.valueOf(infoClass) + "반 " + String.valueOf(infoNumber) + "번 " + infoName + "\n위의 내용으로 학생 정보를 저장합니다.").setCancelable(false).setPositiveButton("정보 저장",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(infoClass < 10)
                                                infoCode = infoGrade + "0" + infoClass;
                                            else
                                                infoCode = infoGrade + String.valueOf(infoClass);

                                        SharedPreferences user = getSharedPreferences("User", 0);
                                        SharedPreferences.Editor edit = user.edit();

                                        edit.putString("infoName", infoName);
                                        edit.putInt("infoGrade", infoGrade);
                                        edit.putInt("infoClass", infoClass);
                                        edit.putInt("infoNumber", infoNumber);
                                        edit.putString("infoCode", infoCode);
                                        edit.commit();

                                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(main);
                                    }
                                }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(RegisterActivity.this, infoCode, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                });
                        AlertDialog alert = alert_confirm.create();
                        alert.show();
                    }
                    else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        alert.setTitle("학생 정보 확인");
                        alert.setMessage("빈 내용이 존재합니다.\n모든 내용을 기입후 학생을 등록해주세요");
                        alert.show();
                    }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        finish();
    }
}
