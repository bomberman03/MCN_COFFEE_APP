package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import koreatech.mcn.mcn_coffe_app.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler mHandler = new Handler();
        mHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                        finish();
                    }
                }, 500);
    }
}
