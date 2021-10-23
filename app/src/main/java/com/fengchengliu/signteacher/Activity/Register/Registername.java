package com.fengchengliu.signteacher.Activity.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fengchengliu.signteacher.R;

public class Registername extends AppCompatActivity {

    private EditText lastname;
    private EditText name;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registername);
        lastname=findViewById(R.id.lastname);
        name=findViewById(R.id.name);
        btn=findViewById(R.id.next);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name0=lastname.getText().toString();
                String name1=name.getText().toString();
                String username=name0+name1;
                Log.i("aa","aaaaaaaaaaaaaaaa"+name0);
                Intent intent=new Intent(Registername.this,RegisternameOccupation.class);
                if(name0.length()!=0&&name1.length()!=0){
                    intent.putExtra("name123",username);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Registername.this, "输入框为空请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}