package com.haeyum.pcm_citizen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    String infoGrade;
    String infoClass;
    String infoNumber;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //final TextView tv = (TextView)findViewById(R.id.textView1);

        Spinner spinnerGrade = (Spinner)findViewById(R.id.spinner_grade);
        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this, R.array.Grade, android.R.layout.simple_spinner_dropdown_item);

        spinnerGrade.setAdapter(gradeAdapter);
        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoGrade = (String) parent.getItemAtPosition(position);
                code = infoGrade + infoClass + infoNumber;

                //tv.setText(code);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner spinnerClass = (Spinner)findViewById(R.id.spinner_class);
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this, R.array.Class, android.R.layout.simple_spinner_dropdown_item);

        spinnerClass.setAdapter(classAdapter);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoClass = (String) parent.getItemAtPosition(position);
                code = infoGrade + infoClass + infoNumber;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        Spinner spinnerNumber = (Spinner)findViewById(R.id.spinner_number);
        ArrayAdapter<CharSequence> numberAdapter = ArrayAdapter.createFromResource(this, R.array.Number, android.R.layout.simple_spinner_dropdown_item);

        spinnerNumber.setAdapter(numberAdapter);
        spinnerNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoNumber = (String) parent.getItemAtPosition(position);
                code = infoGrade + infoClass + infoNumber;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }
}
