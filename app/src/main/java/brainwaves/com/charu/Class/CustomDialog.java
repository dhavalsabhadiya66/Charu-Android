package brainwaves.com.charu.Class;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import brainwaves.com.charu.R;


public class CustomDialog extends Dialog {
    Context objContext;

    public CustomDialog(Context context) {
        super(context);
        objContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        LinearLayout parent = new LinearLayout(getContext());
//        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        parent.setOrientation(LinearLayout.HORIZONTAL);
//        parent.setGravity(Gravity.CENTER);
//        ProgressBar iv = new ProgressBar(getContext());
//        parent.addView(iv);


        setContentView(R.layout.layout_progress);
      //  setContentView(R.layout.cutom_progress_bar);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().gravity = Gravity.CENTER;


        ImageView img = findViewById(R.id.img);

//        Animation rotation = AnimationUtils.loadAnimation(objContext, R.anim.rotate);
//        rotation.setFillAfter(true);
//        img.startAnimation(rotation);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}