package org.benpao.viewactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by shaom on 2016/10/30.
 */

public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_start);

        new Handler().postDelayed(new Runnable() {
            public void run() {
				Intent it = new Intent();
				it.setClass(Start.this, MainActivity.class);
				startActivity(it);
                finish();
            }
        }, 1000 * 2);
    }
}
