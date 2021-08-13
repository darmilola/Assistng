package ng.assist;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       // PaystackSdk.initialize(getApplicationContext());
    }
}