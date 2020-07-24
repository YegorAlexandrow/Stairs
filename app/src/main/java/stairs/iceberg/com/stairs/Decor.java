package stairs.iceberg.com.stairs;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import java.util.Calendar;
import java.util.Random;

/**
 * (C) Iseberg Mobile
 * Created by User on 30.12.2017.
 */
public class Decor {
    private int level = 10;
    private int x, y;
    protected static RectF rect = new RectF();
    protected static Paint p = new Paint();

    public Movement animate() {
        return new Movement();
    }

    public void onDraw(Canvas c, float tabX, float tabY, float size) {

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getName() { return -1; }
}

class StaticDecor extends Decor {
    private int id;
    private static Bitmap bg;

    public StaticDecor(int name, int level, int x, int y) {
        id = name;
        setLevel(level);
        setX(x);
        setY(y);
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {
        c.drawBitmap(Renderer.sprites[id].getBitmap(0, 0), (tabX+getX())*size, (tabY+getY())*size, p);
    }

    public int getName() { return id; }
}

class WaterDecor extends Decor {
    private int id;

    public WaterDecor(int name, int level, int x, int y) {
        id = name;
        setLevel(level);
        setX(x);
        setY(y);
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {
        float dy = (float) Math.sin((System.currentTimeMillis()%5000)/50*Math.PI/50)*0.08f + 0.08f;

        if(Renderer.getTheme().equals("viol")) dy -= 0.21f;

        c.drawBitmap(Renderer.sprites[id].getBitmap(0, 0), (tabX+getX())*size, (tabY+getY()+dy)*size, p);
    }

    public int getName() { return id; }
}

class BigDecor extends Decor {
    private int id;

    public BigDecor(int name, int x, int y) {
        id = name;
        setLevel(-2);
        setX(x);
        setY(y);
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect((tabX+getX()-1f)*size, (tabY+getY()-1.5f)*size,
                (tabX+getX()+1f)*size, (tabY+getY()+1f)*size, clearPaint);

        c.drawBitmap(Renderer.sprites[id].getBitmap(0, 0), (tabX+getX()-1f)*size, (tabY+getY()-1.5f)*size, p);
    }

    public int getName() { return id; }
}

class ClockDecor extends Decor {
    private float alpha = (float) (Math.PI/2), beta = (float) (Math.PI/2);

    public ClockDecor(int x, int y) {
        setLevel(-2);
        setX(x);
        setY(y);
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {
        c.drawBitmap(Renderer.sprites[Res.clock].getBitmap(0, 0), null,
                new RectF((tabX+getX()-1.857f)*size, (tabY+getY()-1.857f)*size,
                        (tabX+getX()+1.857f)*size, (tabY+getY()+1.857f)*size), p);

        c.drawBitmap(Renderer.sprites[Res.clock_numbers].getBitmap(0, 0), null,
                new RectF((tabX+getX()-1.459f)*size, (tabY+getY()-1.459f)*size,
                        (tabX+getX()+1.459f)*size, (tabY+getY()+1.459f)*size), p);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int sec = calendar.getTime().getSeconds();
        int min = calendar.getTime().getMinutes();
        int hrs = calendar.getTime().getHours();

        c.rotate(min*6, (tabX+getX())*size, (tabY+getY())*size);
        c.drawBitmap(Renderer.sprites[Res.clock_pointer_long].getBitmap(0, 0), null,
                new RectF((tabX+getX()-0.1535625f)*size, (tabY+getY()-1.4375f)*size,
                (tabX+getX()+0.1535625f)*size, (tabY+getY()+0.4375f)*size), p);
        c.rotate(min*(-6), (tabX+getX())*size, (tabY+getY())*size);

        c.rotate(hrs%12*30+min*0.25f, (tabX+getX())*size, (tabY+getY())*size);
        c.drawBitmap(Renderer.sprites[Res.clock_pointer_short].getBitmap(0, 0), null,
                new RectF((tabX+getX()-0.1535625f)*size, (tabY+getY()-0.9f)*size,
                        (tabX+getX()+0.1535625f)*size, (tabY+getY()+0.35f)*size), p);
        c.rotate(hrs%12*(-30)-min*0.25f, (tabX+getX())*size, (tabY+getY())*size);

        c.drawBitmap(Renderer.sprites[Res.clock_dot].getBitmap(0, 0), null,
                new RectF((tabX+getX()-0.1875f)*size, (tabY+getY()-0.1875f)*size,
                        (tabX+getX()+0.1875f)*size, (tabY+getY()+0.1875f)*size), p);
    }

    public int getName() { return Res.clock; }
}

class LittleDecor extends Decor {
    private int id;

    public LittleDecor(int name, int x, int y) {
        id = name;
        setLevel(-2);
        setX(x);
        setY(y);
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawCircle((tabX+getX())*size, (tabY+getY())*size, 0.6f*size, clearPaint);

        c.drawBitmap(Renderer.sprites[id].getBitmap(0, 0), (tabX+getX()-0.69f)*size, (tabY+getY()-0.69f)*size, p);
    }

    public int getName() { return id; }
}

class StairDecor extends Decor {
    private int id;
    private int num = -1, prev_num = -1, counter = 0;
    private float steps = 5;
    private Random r = new Random();

    public StairDecor(int name, int x, int y) {
        id = name;
        setLevel(-1);
        setX(x);
        setY(y);
        num = r.nextInt(3);
        prev_num = r.nextInt(3);
    }

    public Movement animate() {
        prev_num = num;
        num = r.nextInt(100) > 38 ? (r.nextBoolean() ? (r.nextBoolean() ? (r.nextBoolean() ? -1 : 0) : 2) : 1) : -1;
        counter = prev_num == -1 ? (int) steps : 0;
        return new Movement();
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {

        if(counter < steps) {
            float k = (steps-counter)/steps;

            rect.set((tabX+getX()-0.5f*k)*size, (tabY+getY()-0.5f*k)*size,
                    (tabX+getX()+0.5f*k)*size, (tabY+getY()+0.5f*k)*size);

            c.drawBitmap(Renderer.sprites[Res.decor].getBitmap(0, prev_num), null, rect, p);
        } else {
            float k = (counter-steps)/steps;
            if(num != -1) {
                rect.set((tabX + getX() - 0.5f * k) * size, (tabY + getY() - 0.5f * k) * size,
                        (tabX + getX() + 0.5f * k) * size, (tabY + getY() + 0.5f * k) * size);

                c.drawBitmap(Renderer.sprites[Res.decor].getBitmap(0, num), null, rect, p);
            }
        }

        if(counter < steps*2) counter++;
    }

    public int getName() { return id; }
}

class FlagDecor extends Decor {
    private int id;
    private int num = -1;
    private Random r = new Random();

    public FlagDecor(int name, int x, int y) {
        id = name;

        switch (Renderer.getTheme()) {
            case "red":     setLevel(1); break;

            case "green":   setLevel(4); break;

            case "gray":    setLevel(5); break;
        }

        setX(x);
        setY(y);
        num = r.nextInt(3);
    }

    public Movement animate() {
        if(r.nextBoolean()) {
            num = r.nextInt(3);
        }
        return new Movement();
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {
        float x0 = 0, y0 = 0, x1 = 0, y1 = 0;

        switch (Renderer.getTheme()) {
            case "red":
                x0 = 0.06f; y0 = 0.33f+0.15f;
                x1 = 0.90f; y1 = 1.29f+0.15f;
                break;

            case "green":
                x0 = 0.06f; y0 = 0.33f;
                x1 = 0.90f; y1 = 1.29f;
                break;

            case "gray":
                x0 = 0.06f; y0 = 0.33f;
                x1 = 0.90f; y1 = 1.29f;
                break;
        }

        c.drawBitmap(Renderer.sprites[id].getBitmap(0, num), (tabX+getX() - x0)*size, (tabY+getY() + y0)*size, p);
    }

    public int getName() { return id; }
}



class PointerDecor extends Decor {
    int id;
    int dx, dy;

    public PointerDecor(int name, int x, int y, int dx, int dy) {
        id = name;
        setLevel(-1);
        setX(x);
        setY(y);

        this.dx = dx;
        this.dy = dy;
    }

    public Movement animate() {
        return new Movement();
    }

    @Override
    public void onDraw(Canvas c, float tabX, float tabY, float size) {
        rect.set((tabX+getX()-0.5f)*size, (tabY+getY()-0.5f)*size,
                (tabX+getX()+0.5f)*size, (tabY+getY()+0.5f)*size);
        c.drawBitmap(Renderer.sprites[Res.pointer].getBitmap(dy+1, dx+1), null, rect, p);

        rect.set((tabX+getX()-0.265f)*size, (tabY+getY()-0.272f)*size,
                (tabX+getX()+0.265f)*size, (tabY+getY()+0.248f)*size);
        c.drawBitmap(Renderer.sprites[id].getBitmap(0, 0), null, rect, p);

    }

    public int getName() { return Res.pointer; }
}