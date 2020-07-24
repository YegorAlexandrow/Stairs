package stairs.iceberg.com.stairs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameBackground extends View {
    private ArrayList<PointF> clouds = new ArrayList<>();
    private Random cloud_random = new Random();
    private int cloud_time = 0, cloud_delay = 170;

    public GameBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start();
    }

    public GameBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    public GameBackground(Context context) {
        super(context);
        start();
    }

    public void setIntensivity(float val) {
        cloud_time = (int) (1 / val);
    }

    public void fixClouds() {
        clouds.clear();

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if(Renderer.sprites[Res.cloud2] == null) Renderer.putSprite("cloud2", 1, 1);

        for(int i=0; i<8; i++) {
            clouds.add(new PointF(cloud_random.nextInt(8),
                    cloud_random.nextInt(metrics.heightPixels*2/3/(metrics.widthPixels/8))));
        }
    }

    private void start() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        }, 57, 57);
    }

    public void setBackgroundColor(int color) {
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas c) {
        if (isInEditMode()) return;
        super.onDraw(c);
        Paint paint = new Paint();

        switch (Renderer.getTheme()) {
            case "red":
                paint.setColor(Color.parseColor("#EBF9FA"));
                break;
            case "green":
                paint.setColor(Color.parseColor("#D0E8DB"));
                break;
            case "gray":
                paint.setColor(Color.parseColor("#EAEAED"));
                break;
            case "viol":
                paint.setColor(Color.parseColor("#FFEDFD"));
                break;
        }

        c.drawPaint(paint);

        int cell_size = getWidth()/8;

        if (cloud_time++ > cloud_delay) {
            clouds.add(new PointF(-3, cloud_random.nextInt(getHeight()*2/3/cell_size)));
            cloud_time = 0;
        }

        for (int i = 0; i < clouds.size(); i++) {
            c.drawBitmap(Renderer.sprites[Res.cloud2].getBitmap(0, 0), null,
                    new RectF((clouds.get(i).x-2.56f) * cell_size, clouds.get(i).y * cell_size,
                            ((clouds.get(i).x) + 0) * cell_size, (clouds.get(i).y + 1) * cell_size), paint);

            clouds.get(i).x += 0.0125f;

            if (clouds.get(i).x > 11) clouds.remove(i);
        }
    }
}

