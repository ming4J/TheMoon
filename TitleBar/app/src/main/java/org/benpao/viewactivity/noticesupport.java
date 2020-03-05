package org.benpao.viewactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by shaom on 2016/11/10.
 */

public class noticesupport extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticesupport);
        Log.e("21:","222222222222222");

        UserActivity.upstop();
        finish();
    }
}
