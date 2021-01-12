package md.intelectsoft.petrolmpos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import md.intelectsoft.petrolmpos.Utils.SPFHelp;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar var10000 = this.getSupportActionBar();
        if (var10000 != null) {
            var10000.hide();
        }

        new Timer().schedule(new TimerTask() {
            public void run() {
                String licenseID = SPFHelp.getInstance().getString("LicenseID", null);
                if(licenseID == null)
                    startActivity(new Intent(SplashActivity.this, AuthorizeActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, TestActivity.class));

                finish();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideSystemUI();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}