package com.feimeng.fdroiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.feimeng.fdroid.base.FDApp;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        String lalala = getIntent().getStringExtra("feimeng");
        Toast.makeText(FDApp.getInstance().getApplicationContext(), lalala, Toast.LENGTH_LONG).show();
    }
}
