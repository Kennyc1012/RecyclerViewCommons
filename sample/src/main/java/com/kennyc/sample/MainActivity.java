package com.kennyc.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.simple).setOnClickListener(this);
        findViewById(R.id.custom).setOnClickListener(this);
        findViewById(R.id.menu).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simple:
                startActivity(RVActivity.createIntent(getApplicationContext(), 0));
                break;

            case R.id.custom:
                startActivity(RVActivity.createIntent(getApplicationContext(), 1));
                break;

            case R.id.menu:
                startActivity(RVActivity.createIntent(getApplicationContext(), 2));
                break;
        }
    }
}
