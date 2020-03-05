package org.benpao.viewactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by shaom on 2016/11/10.
 */

public class downloadsupport extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowsupport);
        Log.e("8787","32324224");
        UserActivity.dowstop();
        finish();
    }
}
