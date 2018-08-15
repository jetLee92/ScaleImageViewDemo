package com.jetlee.scaleimageviewdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                startActivity(MainActivity.this, Button1Activity.class);
                break;
            case R.id.button2:
                startActivity(MainActivity.this, Button2Activity.class);
                break;
            case R.id.button3:
                startActivity(MainActivity.this, Button3Activity.class);
                break;
            case R.id.button4:
                startActivity(MainActivity.this, Button4Activity.class);
                break;
        }
    }

    private void startActivity(Context source, Class target) {
        Intent intent = new Intent(source, target);
        startActivity(intent);
    }
}
