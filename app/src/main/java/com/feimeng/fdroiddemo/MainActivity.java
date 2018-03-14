package com.feimeng.fdroiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.crash).setOnClickListener(this);
    }
    TextView a;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crash:
               a.setText("");
        }
    }
}
