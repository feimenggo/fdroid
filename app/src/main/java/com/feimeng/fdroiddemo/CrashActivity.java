package com.feimeng.fdroiddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        String lalala = getIntent().getStringExtra("feimeng");
        Toast.makeText(this, lalala, Toast.LENGTH_LONG).show();
    }
}
