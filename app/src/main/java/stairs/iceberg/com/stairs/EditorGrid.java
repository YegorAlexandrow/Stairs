package stairs.iceberg.com.stairs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * (C) Iceberg Mobile
 * Created by User on 01.01.2018.
 */

public class EditorGrid extends View {
    private Context context;
    private float cell_size, full_w, full_h;
    private Etype type = Etype.DECOR;
    private DrawElement curr = new DrawElement(0, "ghost");
    private ArrayList<Element>[][] elements = new ArrayList[32][32];
    private String spec_code = "";

    private boolean erasing = true;

    public EditorGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public EditorGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditorGrid(Context context) {
        super(context);
        init(context);
    }

    private void init(Context c) {
        if (isInEditMode()) return;
        context = c;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        full_w = metrics.widthPixels;
        full_h = metrics.heightPixels;

        cell_size = Math.min(metrics.heightPixels, metrics.widthPixels) / 16;

        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++) {
                elements[i][j] = new ArrayList<>();
            }

        this.setOnTouchListener(l);
    }

    private void erase(int x, int y) {
        for(int i=0; i<elements[y][x].size(); i++) {
            if(elements[y][x].get(i).type != Etype.DECOR) {
                elements[y][x].remove(elements[y][x].get(i));

                invalidate();
                //recalc();
                return;
            }
        }

        for(int i=0; i<elements[y][x].size(); i++) {
            if(elements[y][x].get(i).type == Etype.DECOR) {
                elements[y][x].remove(elements[y][x].get(i));

                invalidate();
                //recalc();
                return;
            }
        }
    }

    private void brush(int x, int y) {
        for(int n=0; n<curr.names.length; n++) {
            Element e = new Element(type, x, y, curr.levels[n], curr.names[n]);

            pushElement(e);
        }

        //recalc();
    }


    //оченьстрашнаявешьлучшенетрогать
    private void recalc() {
        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++) {
                for (Element e : elements[i][j]) {
                    if(e.replicable) {
                        Log.d("", "@@replicable " + e.name);

                        boolean l = false, r = false;

                        if(j-1 >= 0) for (Element f : elements[i][j-1]) {
                            if(f.name.contains(e.name.substring(0, e.name.length()-2))) {
                                l = true;
                                break;
                            }
                        }

                        if(j+1 < 32) for (Element f : elements[i][j+1]) {
                            if(f.name.contains(e.name.substring(0, e.name.length()-2))) {
                                r = true;
                                break;
                            }
                        }

                        if((l && r) || (!l && !r)) {
                            if(e.name.contains("_l") || e.name.contains("_r")) {
                                e.name = e.name.substring(0, e.name.length()-2);
                            }
                        } else if(e.name.contains("_l") || e.name.contains("_r")) {
                            e.name = e.name.replace("_l", "@@").replace("_r", "@@").replace("@@", l ? "_l" : "_r");
                        } else  e.name += l ? "_l" : "_r";
                    }
                }
            }
    }

    private int x0, y0, x1, y1;

    private OnTouchListener l = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            x1 = (int) (motionEvent.getX()/cell_size);
            y1 = (int) (motionEvent.getY()/cell_size);

            if(motionEvent.getAction()== MotionEvent.ACTION_DOWN) {
                x0 = x1;
                y0 = y1;
            } else if(motionEvent.getAction()== MotionEvent.ACTION_UP) {
                if(erasing) {
                    for(int i=Math.min(y1, y0); i<=Math.max(y1, y0); i++) {
                        for(int j=Math.min(x1, x0); j<=Math.max(x1, x0); j++) {
                            erase(j, i);
                        }
                    }
                } else {
                    for(int i=Math.min(y1, y0); i<=Math.max(y1, y0); i++) {
                        for(int j=Math.min(x1, x0); j<=Math.max(x1, x0); j++) {
                            brush(j, i);
                        }
                    }
                }

                x0 = y0 = x1 = y1 = 0;
                invalidate();
            }

            invalidate();
            return true;
        }
    };

    private void pushElement(Element e) {
        int i;
        for (i = 0; i<elements[e.y][e.x].size(); i++) {
            if(elements[e.y][e.x].get(i).level>e.level) break;
        }

        elements[e.y][e.x].add(i, e);
    }

    public void setErasing(boolean erasing) {
        this.erasing = erasing;
    }

    public void save() {
        OutputStream outputstream;
        try {
            outputstream = context.openFileOutput(EditorActivity.filename, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputstream);
            osw.write(code);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawElements(Canvas c) {
        Paint p = new Paint();
        int x, y;

        updateMins();

        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++) {
                for(Element e : elements[i][j]) {
                    switch (e.type) {
                        case DECOR:
                            c.drawBitmap(Renderer.sprites[Res.getId(e.name)].getBitmap(0, 0),
                                    null, new RectF(j*cell_size, i*cell_size,
                                            (j+1)*cell_size, (i+1)*cell_size), p);
                            break;
                    }
                }
            }

        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++) {
                for(Element e : elements[i][j]) {
                    switch (e.type) {
                        case UNIT:
                        case ITEM:
                            y = y_min + countY(e.x, e.y, x_max-x_min, y_max-y_min) + 2;
                            x = x_min + countX(e.x, e.y, x_max-x_min, y_max-y_min)*2 - (y-y_min+1)%2;

                            Log.d("", x_min + " : " + y_min + " (" + e.x + ", " + e.y + ")\t->\t(" + x + ", " + y + ")");

                            c.drawBitmap(Renderer.sprites[Res.getId(e.name)].getBitmap(0, 0),
                                    null, new RectF((x - 0.35f)*cell_size, (y - 0.925f)*cell_size,
                                            (x + 0.35f)*cell_size, (y + 0.0f)*cell_size), p);
                            break;
                    }
                }
            }

    }

    int x_min = 100, x_max = -100;
    int y_min = 100, y_max = -100;

    private void updateMins() {
        x_min = 100; x_max = -100;
        y_min = 100; y_max = -100;

        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++)
                for(Element e : elements[i][j]) {
                    if(e.name.equals("&_bg")) {
                        if(j < x_min) x_min = j;
                        if(j > x_max) x_max = j;
                        if(i < y_min) y_min = i;
                        if(i > y_max) y_max = i;
                    }
                }
    }

    private void drawGuides(Canvas c) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setAlpha(50);
        p.setPathEffect(new DashPathEffect(new float[] {7,7}, 0));

        for(int y=0; y<full_h/cell_size; y++) c.drawLine(0, y*cell_size, full_w, y*cell_size, p);

        for(int x=0; x<full_w/cell_size; x++) c.drawLine(x*cell_size, 0, x*cell_size, full_h, p);


        code = "background #EBF9FA\n";

        updateMins();

        p.setColor(Color.RED);
        p.setStrokeWidth(2);
        c.drawRect(x_min*cell_size, y_min*cell_size, (x_max+1)*cell_size, (y_max+1)*cell_size, p);

        p.setColor(Color.BLUE);
        p.setStrokeWidth(2);
        c.drawRect((Math.min(x1, x0)+0.5f)*cell_size,
                (Math.min(y1, y0)+0.5f)*cell_size,
                (Math.max(x1, x0)+0.5f)*cell_size,
                (Math.max(y1, y0)+0.5f)*cell_size, p);

        p.setPathEffect(new PathEffect());

        Scanner s = new Scanner(spec_code);
        while(s.hasNext()) {
            String str = s.next();
            if("empty1".equals(str)) {
                p.setColor(Color.BLUE);

                int x = s.nextInt(), y = s.nextInt();

                int yy = y_min + countY(x, y, x_max-x_min, y_max-y_min) + 2;
                int xx = x_min + countX(x, y, x_max-x_min, y_max-y_min)*2 - (yy-y_min+1)%2;

                c.drawLine((xx - 0.25f)*cell_size, (yy - 0.25f)*cell_size,
                        (xx + 0.25f)*cell_size, (yy + 0.25f)*cell_size, p);

                c.drawLine((xx + 0.25f)*cell_size, (yy - 0.25f)*cell_size,
                        (xx - 0.25f)*cell_size, (yy + 0.25f)*cell_size, p);
            }
            else if("nonempty".equals(str)) {
                p.setColor(Color.BLUE);

                int x = s.nextInt(), y = s.nextInt();
                int yy = y_min + countY(x, y, x_max-x_min, y_max-y_min) + 2;
                int xx = x_min + countX(x, y, x_max-x_min, y_max-y_min)*2 - (yy-y_min+1)%2;

                c.drawCircle(xx * cell_size, yy * cell_size, 0.18f * cell_size, p);
            }
        }
    }

    private String code = "";

    public String generateCode() {
        int x_min = 100, x_max = -100;
        int y_min = 100, y_max = -100;

        code = "background #EBF9FA\n";
        code += "theme " + Renderer.getTheme() + "\n";

        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++)
                for(Element e : elements[i][j]) {
                    if(e.name.equals("&_bg") || e.name.equals("&_bg_r")) {
                        if(e.x < x_min) x_min = e.x;
                        if(e.x > x_max) x_max = e.x;
                        if(e.y < y_min) y_min = e.y;
                        if(e.y > y_max) y_max = e.y;
                    }

                    if(e.type == Etype.DECOR) {
                        code += "decor " + e.name + " " + e.level + " " + e.x + " " + e.y + "\n";
                    }
        }

        Log.d("", "X_MIN @@ " + x_min);
        Log.d("", "Y_MIN @@ " + y_min);

        int w = (x_max-x_min+1)/2 + 1;
        int h = y_max-y_min+1;

        Log.d("", "    W @@" + w);
        Log.d("", "    H @@ " + h);

        boolean[][] a = new boolean[w+h/2][w+h/2];

        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++)
                for(Element e : elements[i][j]) {
            if(e.name.equals("&_bg")) {
                int x = (h/2 - (i-y_min)/2 + (j-x_min)/2);
                int y = ((i-y_min)/2 + (i-y_min)%2 + (j-x_min)/2 + (j-x_min)%2);

                a[y][x] = true;
            }
        }

        for(int i=0; i<w+h/2; i++) {
            for(int j=0; j<w+h/2; j++) {
                System.out.print(a[i][j] ? "1 " : "0 ");
                if(!a[i][j]) code += "empty " + j + " " + i + "\n";
            }
            System.out.println();
        }

        System.out.println(code);

        code += spec_code + "\n";

        code += "maze " + x_min + ' ' + (y_min+1) + ' ' + w + ' ' + h + '\n';

        save();

        return code;
    }


    private int countX(int x, int y, int width, int height) {
        for(int i=0; i<height; i++) {
            for(int j = 0; j < width; j++) {
                if(x == height/2 + j - i/2 && y == i/2+i%2 + j) return j;
            }
        }

        return -1;
    }

    private int countY(int x, int y, int width, int height) {
        for(int i=0; i<height; i++) {
            for(int j = 0; j < width; j++) {
                if(x == height/2 + j - i/2 && y == i/2+i%2 + j) return i;
            }
        }

        return -1;
    }

    public void loadMap(String name) {
        for(int i=0; i<elements.length; i++)
            for(int j=0; j<elements[i].length; j++)
                elements[i][j].clear();

        String str;

        InputStreamReader isr;
        StringBuilder buffer;
        try {
            InputStream inputstream = Main.main.openFileInput(name);
            isr = new InputStreamReader(inputstream);
            BufferedReader reader = new BufferedReader(isr);

            buffer = new StringBuilder();

            while ((str = reader.readLine()) != null) {
                buffer.append(str).append("\n");
            }
            inputstream.close();
        } catch (IOException e) {
            Toast.makeText(context, "New map created!", Toast.LENGTH_LONG).show();
            return;
        }

        Scanner in = new Scanner(buffer.toString());

        String cmd = "", n, line_str="";
        int lvl = 0;

        spec_code = "";

        while(in.hasNextLine()) {
            line_str = in.nextLine();
            Scanner s = new Scanner(line_str);
            try {
                cmd = s.next();
            } catch (NoSuchElementException e) {
                continue;
            }
            switch (cmd) {
                case "decor":
                    n = s.next();

                    if(n.equals("pointer")) {
                        spec_code += line_str + "\n";
                        break;
                    }

                    lvl = s.nextInt();
                    pushElement(new Element(Etype.DECOR, s.nextInt(), s.nextInt(), lvl, n));

                    break;
/*
                case "item":
                    n = in.next();
                    pushElement(new Element(Etype.ITEM, in.nextInt(), in.nextInt(), 0, n));
                    break;

                case "unit":
                    n = in.next();
                    pushElement(new Element(Etype.UNIT, in.nextInt(), in.nextInt(), 0, n));
                    break;
*/
                case "empty":
                case "maze":
                case "background":
                    //Nothing to do
                    break;

                case "theme":
                    Renderer.setTheme(s.next());
                    break;

                default:
                    spec_code += line_str + "\n";
                    break;
            }
        }
        in.close();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {
        Paint p = new Paint();
        p.setColor(Color.parseColor("#EBF9FA"));
        c.drawPaint(p);

        drawElements(c);
        drawGuides(c);
    }

    public void setElement(Etype etype, DrawElement d) {
        type = etype;
        curr = d;

        invalidate();
    }

    public String getSpecCode() {
        return spec_code;
    }

    public void acceptSpecCode(String s) {
        spec_code = s;

            for(int i=0; i<elements.length; i++)
                for(int j=0; j<elements[i].length; j++)
                    for (int n=0; n<elements[i][j].size(); n++) {
                        if (elements[i][j].get(n).type != Etype.DECOR) {
                            elements[i][j].remove(n);
                        }
                    }

        Scanner in = new Scanner(spec_code);

        while(in.hasNext()) {
            String str = in.next();
            String name;

            switch(str) {
                case "item":
                    name = in.next();
                    pushElement(new Element(Etype.ITEM, in.nextInt(), in.nextInt(), 10, name));
                    break;

                case "unit":
                    name = in.next();
                    pushElement(new Element(Etype.UNIT, in.nextInt(), in.nextInt(), 11, name));
                    break;

                case "hero":
                    pushElement(new Element(Etype.UNIT, in.nextInt(), in.nextInt(), 12, "hero"));
                    break;
            }
        }

        in.close();
        invalidate();
    }

    class Element {
        Etype type = Etype.DECOR;
        int x;
        int y;
        int level = -1;
        boolean replicable;
        String name;

        Element(Etype type, int x, int y, int level, String name, String code) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.level = type == Etype.UNIT ? 0 : level;
            this.name = name;
        }

        Element(Etype type, int x, int y, int level, String name) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.level = type == Etype.UNIT ? 0 : level;
            this.name = name;
        }
    }
}
