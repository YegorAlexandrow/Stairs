package stairs.iceberg.com.stairs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * (C) Iseberg Mobile
 * Created by User on 23.01.2018.
 */
public class FontText extends TextView {

    private static final String TAG = "TextView";

    public FontText(Context context) {
        super(context);
    }

    public FontText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        int id = a.getResourceId(R.styleable.TextViewPlus_customIcon, 0);
        if (id != 0) {
            Drawable img = getContext().getResources().getDrawable(id, context.getTheme());
            if (img != null) {
                img.setBounds(10, 0, 63, 53 );
                this.setCompoundDrawables( img, null, null, null );
            }

            a.recycle();
        }
    }

    public FontText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String customFont = a.getString(R.styleable.TextViewPlus_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: "+e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
