package brainwaves.com.charu;

/**
 * Created by Sneha Shah.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.Map;

public class SDTextView extends android.support.v7.widget.AppCompatTextView {

    /// Created By: Sneha Shah

    private static Map<String, Typeface> mTypefaces;

    public SDTextView(Context context) {
        super(context);
    }

    public SDTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);

    }

    public SDTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);

    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.SDTextView);
        String customFont = a.getString(R.styleable.SDTextView_customTypeface);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        setTypeface(typeface);
        return true;
    }
}
