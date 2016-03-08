package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hobbyte.touringandroid.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private float x1, x2;
    private float y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RelativeLayout base = (RelativeLayout) findViewById(R.id.baseLayoutSplash);
        base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();

                        if (x1 < x2) {
                            goToStartActivity();
                        }
                        break;
                }
                return false;

            }
        });
    }



    private void goToStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);

    }
}
