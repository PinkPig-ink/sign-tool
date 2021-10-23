package com.fengchengliu.signteacher.Activity.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fengchengliu.signteacher.R;

public class RegisternameOccupation extends AppCompatActivity {
    private  int num=0;
    private String name;
    private String name0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registername_occupation);
        RadioGroup check = (RadioGroup) findViewById(R.id.radioGroup);
        this.name0=getIntent().getStringExtra("name123");

        check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton check1 = findViewById(checkedId);
                name = check1.getText().toString();
            }
        });

        Button btn=findViewById(R.id.next1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teacher="老师";
                String num;
                if(name.equals(teacher))
                    num="1";
                else
                    num="0";
                Intent intent=new Intent(RegisternameOccupation.this,Registerpassword.class);
                intent.putExtra("name0",name0);
                intent.putExtra("type",num);
                startActivity(intent);
            }
        });
    }
}