package stairs.iceberg.com.stairs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * (C) Iceberg Mobile
 * Created by User on 20.01.2018.
 */
public class LevelIcon extends View {

    private Context c;

    private static Bitmap stick, flag_red, flag_green, flag_gray, flag_closed;
    private Bitmap flag_opened;
    private static Typeface font;
    private static Paint paint;

    private boolean opened = true;
    private int level = 0;
    private int width, height;
    private boolean showing = true;
    private float progress = 1, y = 0;

    private Timer timer = new Timer();

    public LevelIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LevelIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LevelIcon(Context context) {
        super(context);
        init(context);
    }

    private void checkBitmaps() {
        if(stick == null) {
            stick = BitmapFactory.decodeResource(c.getResources(), R.drawable.level_icon_stick);
            flag_red = BitmapFactory.decodeResource(c.getResources(), R.drawable.level_icon_opened);
            flag_green = BitmapFactory.decodeResource(c.getResources(), R.drawable.level_icon_opened_green);
            flag_gray = BitmapFactory.decodeResource(c.getResources(), R.drawable.level_icon_opened_gray);

            flag_opened = flag_red;

            flag_closed = BitmapFactory.decodeResource(c.getResources(), R.drawable.level_icon_closed);

            font = Typeface.createFromAsset(c.getAssets(), "font.ttf");

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTypeface(font);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(width*0.014f + c.getResources().getDisplayMetrics().density);
        }
    }

    private float increase(float x) {
        return 0.07f*x*x*x*x + 0.45f*x*x*x - 2.79f*x*x + 3.27f*x - 1;
    }

    private float decrease(float x) {
        return (0.07f*x*x*x*x + 0.45f*x*x*x - 2.79f*x*x + 3.27f*x)*(-1);
    }

    private void init(Context context) {
        if(isInEditMode()) return;
        c = context;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = (int) (Math.min(metrics.widthPixels, metrics.heightPixels)*0.22f);
        height = Math.max(metrics.widthPixels, metrics.heightPixels);

        checkBitmaps();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN) showOut(level, 0);
                return false;
            }
        });

    }

    public void startDrawing() {
        stopDrawing();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(progress >= 1) {
                    if(!showing) showIn(next_level, 0);
                    return;
                }
                progress += 0.08f;
                if(progress < 0) return;

                y = showing ? increase(progress) : decrease(progress);

                ((Activity)c).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });

            }
        }, 50, 50);
    }

    public void stopDrawing() {
        timer.cancel();
        timer.purge();
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if(isInEditMode()) return;
        super.onDraw(canvas);

        canvas.drawBitmap(opened ? flag_opened : flag_closed, null,
                new RectF(0.08f*width, (y-0.15f)*width*1.513f, 0.08f*width+width, (y-0.15f)*width*1.513f+width*1.513f), paint);


        paint.setTextSize(width*0.4444f);

        if(opened) canvas.drawText("" + level, 0.08f*width + width/2, (y-0.15f)*width*1.513f+width*0.962f, paint);

        canvas.drawBitmap(stick, null, new RectF(0.08f*width, 0, 0.08f*width+width, width*0.123f), paint);
    }

    private int next_level = 0;

    public boolean isAnimating() {
        return progress < 1;
    }

    public void showOut(int level, int delay) {
        if(isAnimating()) {
            if(showing) {
                this.level = level;
                opened = level != -1;
            } else next_level = level;
            return;
        }

        next_level = level;
        showing = false;
        progress = delay/35 * -0.04f;
    }

    public void showIn(int level, int delay) {
        opened = level != -1;
        this.level = level;
        showing = true;
        progress = delay/35 * -0.04f;
    }

    public void setTheme(String theme) {
        switch (theme) {
            case "red":     flag_opened = flag_red; break;
            case "green":   flag_opened = flag_green; break;
            case "gray":    flag_opened = flag_gray; break;
        }
    }
}
