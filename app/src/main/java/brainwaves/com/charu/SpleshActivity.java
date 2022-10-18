package brainwaves.com.charu;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import brainwaves.com.charu.Utils.Const;

public class SpleshActivity extends AppCompatActivity {

    Runnable runnable;
    Context context = SpleshActivity.this;
    ImageView Logo;
    View viewTop, viewBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Const.deleteCache(context);

        Logo = (ImageView) findViewById(R.id.img);
        viewTop = (View) findViewById(R.id.viewTop);
        viewBottom = (View) findViewById(R.id.viewBottom);

        Animation an1 = AnimationUtils.loadAnimation(this, R.anim.swipe_left);
        viewTop.startAnimation(an1);
        Animation an2 = AnimationUtils.loadAnimation(this, R.anim.swipe_right);
        viewBottom.startAnimation(an2);

        splashFunction();
    }

    private void splashFunction() {
        if (Const.isInternetConnected(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, 3000);
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    splashFunction();
                }
            };
            Const.connectionErrorCustomDialog(this, runnable);
        }
    }
}