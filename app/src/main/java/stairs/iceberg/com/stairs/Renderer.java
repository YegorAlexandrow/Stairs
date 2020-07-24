package stairs.iceberg.com.stairs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * (C) Iceberg Mobile
 * Created by User on 30.12.2017.
 */
public class Renderer extends View {
    static float cell_size = 110;
    float tabX, tabY;
    float mazeTabX, mazeTabY;
    float targetTabX, targetTabY;
    float minX = 100, maxX = -100;
    float minY = 100, maxY = -100;

    private boolean[][] maze = new boolean[1][1];
    private boolean[][] prev_maze = new boolean[1][1];
    private ImageButton[] items = new ImageButton[4];
    private int steps_per_move = 9;
    private int stair_transform = 0;
    private int width = 1, height = 1;
    private int display_width, display_height;
    private boolean dev_mode = false;

    private Context context;
    private Level level;
    static  Sprite[] sprites = new Sprite[Res.getCount()];
    private ArrayList<Movement> moves = new ArrayList<>();
    private ArrayList<Decor> decors = new ArrayList<>();
    GameBackground bg;

    private static String theme = "red";

    private ArrayList<QuestBalloon> balloons = new ArrayList<>();

    public Renderer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Renderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Renderer(Context context) {
        super(context);
        init(context);
    }

    public static String getTheme() {
        return theme;
    }

    public void clearQuestBalloons() {
        balloons.clear();
    }

    private void init(Context c) {
        if(isInEditMode()) return;
        context = c;

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        display_width  = metrics.widthPixels;
        display_height = metrics.heightPixels;

        cell_size = Math.min(display_width, display_height) / 8;

        tabX = 0; targetTabX = 0;
        tabY = 0; targetTabY = 0;

        bg = new GameBackground(c);

        initSprites();

        this.setOnTouchListener(new OnSwipeTouchListener(c) {
            private int ritual = 0;


            public void onTouch(MotionEvent e) {
                Main.main.hideMenu();

                if(e.getAction() == MotionEvent.ACTION_DOWN &&
                        Math.abs(e.getX() - display_width/2) < 50 && e.getY() < 100) {
                    ritual++;
                }

                if(ritual >= 15) {
                    ritual = 0;
                    dev_mode = true;
                    Intent intent = new Intent(Main.main, EditorActivity.class);
                    Main.main.startActivity(intent);
                }
            }

            public void onSwipeNW() {
                ddx = 0;
                ddy = -1;
            }

            public void onSwipeNE() {
                ddx = 1;
                ddy = 0;
            }

            public void onSwipeSE() {
                ddx = 0;
                ddy = 1;
            }

            public void onSwipeSW() {
                ddx = -1;
                ddy = 0;
            }
        });

        new Timer().schedule(loop, 48, 48);
    }

    static void setTheme(String new_theme) {
        if(!new_theme.equals(theme)) {
            theme = new_theme;
            initSprites();
        }
    }

    static void initSprites() {
        putSprite("&_floor",      1, 1, 0.75f, 0.25f);
        putSprite("&_stair",      9, 2);
        putSprite("exit",           1, 1, 0.7f, 0.93f);
        putSprite("exit_item",           1, 1);
        putSprite("cloud2",           1, 1, 2.56f, 1);
        putSprite("&_wall_r");
        putSprite("&_wall_l");
        putSprite("&_wall_rb");
        putSprite("&_wall_lb");
        putSprite("&_wall_sr");
        putSprite("&_wall_sl");
        putSprite("&_bg_r");
        putSprite("&_bg_l");
        putSprite("&_bg0_r");
        putSprite("&_bg0_l");
        putSprite("&_bg1_r");
        putSprite("&_bg1_l");
        putSprite("&_bg2_r");
        putSprite("&_bg2_l");
        putSprite("&_bridge_l0");
        putSprite("&_bridge_l1");
        putSprite("gray_bridge_l2");
        putSprite("gray_bridge_l3");
        putSprite("&_bridge_r0");
        putSprite("&_bridge_r1");
        putSprite("gray_bridge_r2");
        putSprite("gray_bridge_r3");
        putSprite("&_bridge");
        putSprite("pointer",        3, 3);
        putSprite("&_bg");
        putSprite("&_bgm");
        putSprite("&_big",          1, 1, 2, 2.5f);
        putSprite("&_little",       1, 1, 1.38f, 1.38f);
        putSprite("&_parapet_r");
        putSprite("&_parapet_l");
        putSprite("&_parapet");
        putSprite("grass_r");
        putSprite("grass_l");
        putSprite("grass_mid");
        putSprite("grass_deep");
        putSprite("grass");
        putSprite("&_grass");
        putSprite("&_grass_r");
        putSprite("&_grass_l");
        putSprite("&_grass_mid");
        putSprite("&_grass_mid_r");
        putSprite("&_grass_mid_l");
        putSprite("&_grass_deep");
        putSprite("&_grass_deep_r");
        putSprite("&_grass_deep_l");
        putSprite("&_grass_deep2");
        putSprite("hero",           9, 6, 0.7f, 0.93f);
        putSprite("ghost",          9, 3, 0.7f, 0.93f);
        putSprite("anvil",          9, 3, 0.7f, 0.93f);
        putSprite("fish",          9, 3, 0.7f, 0.93f);
        putSprite("&_decor",        3, 1);
        putSprite("&_portal",        1, 1);
        putSprite("&_flag",         3, 1);
        putSprite("key_yellow",     1, 1, 0.7f, 0.93f);
        putSprite("key_green",      1, 1, 0.7f, 0.93f);
        putSprite("key_orange",     1, 1, 0.7f, 0.93f);
        putSprite("key_blue",       1, 1, 0.7f, 0.93f);
        putSprite("crown",          1, 1, 0.7f, 0.93f);
        putSprite("star",           1, 1, 0.7f, 0.93f);
        putSprite("crystal",        1, 1, 0.7f, 0.93f);
        putSprite("sword",          1, 1, 0.7f, 0.93f);
        putSprite("egg",          1, 1, 0.7f, 0.93f);
        putSprite("coin",           1, 1, 0.7f, 0.93f);
        putSprite("potion",        1, 1, 0.7f, 0.93f);
        putSprite("bone",          1, 1, 0.7f, 0.93f);
        putSprite("crown_item",     1, 1);
        putSprite("star_item",      1, 1);
        putSprite("crystal_item",   1, 1);
        putSprite("sword_item",     1, 1);
        putSprite("egg_item",          1, 1);
        putSprite("coin_item",           1, 1);
        putSprite("potion_item",        1, 1);
        putSprite("bone_item",          1, 1);
        putSprite("key_yellow_item",1, 1);
        putSprite("key_green_item", 1, 1);
        putSprite("key_orange_item",1, 1);
        putSprite("key_blue_item",  1, 1);
        putSprite("chest_yellow",   1, 1, 0.7f, 0.93f);
        putSprite("chest_green",    1, 1, 0.7f, 0.93f);
        putSprite("chest_orange",   1, 1, 0.7f, 0.93f);
        putSprite("chest_blue",     1, 1, 0.7f, 0.93f);
        putSprite("lock_yellow",    1, 1, 0.7f, 0.93f);
        putSprite("lock_green",     1, 1, 0.7f, 0.93f);
        putSprite("lock_orange",    1, 1, 0.7f, 0.93f);
        putSprite("lock_blue",      1, 1, 0.7f, 0.93f);
        putSprite("&_parapet_lite_r");
        putSprite("&_parapet_lite_l");
        putSprite("&_roof0_l");
        putSprite("&_roof1_l");
        putSprite("&_roof2_l");
        putSprite("&_roof3_l");
        putSprite("gray_roof4_l");
        putSprite("&_roof_m");
        putSprite("&_roof0_r");
        putSprite("&_roof1_r");
        putSprite("&_roof2_r");
        putSprite("&_roof3_r");
        putSprite("gray_roof4_r");
        putSprite("&_water_bl");
        putSprite("&_water_br");
        putSprite("&_water_bm");
        putSprite("&_water_tl");
        putSprite("&_water_tr");
        putSprite("&_water_tm");
        putSprite("balloon", 1, 1, 1.21f, 0.9f);
        putSprite("questman", 1, 1, 0.7f, 0.93f);
        putSprite("questman_mage", 9, 1, 0.7f, 0.93f);
        putSprite("questman_merchant", 9, 1, 0.7f, 0.93f);
        putSprite("questman_goblin", 9, 1, 0.7f, 0.93f);
        putSprite("questman_mroom", 9, 1, 0.7f, 0.93f);
        putSprite("questman_snail", 9, 1, 0.7f, 0.93f);
        putSprite("questman_smurf", 9, 1, 0.7f, 0.93f);
        putSprite("clock", 1, 1, 3.714f, 3.714f);
        putSprite("clock_dot", 1, 1);
        putSprite("clock_numbers", 1, 1, 2.918f, 2.918f);
        putSprite("clock_pointer_long", 1, 1);
        putSprite("clock_pointer_short", 1, 1);
        putSprite("thread", 1, 2);
        putSprite("swipes", 1, 1);
        putSprite("chest_green_item");
        putSprite("question", 1, 1);
        putSprite("question_item", 1, 1);
        putSprite("lift", 1, 1, 0.844f, 1.086875f);
        putSprite("lift_ver", 1, 1, 0.844f, 1.086875f);
    }

    private static int getBmpId(String name) {
        int n = MenuActivity.context.getResources().getIdentifier(name.replace("&", theme),
                    "drawable", MenuActivity.context.getPackageName());

        Log.d("", name + "@@@" + + Res.getId(name) +"@@@" + n);

        if(n == 0) {
            return MenuActivity.context.getResources().getIdentifier(name.replace("&", "red"),
                    "drawable", MenuActivity.context.getPackageName());
        } else return  n;
    }

    static Sprite putSprite(String name, int w, int h, float dw, float dh) {
        return sprites[Res.getId(name)] = new Sprite(w, h, dw*cell_size, dh*cell_size, MenuActivity.context.getResources(), getBmpId(name));
    }

    static Sprite putSprite(String name, int w, int h) {
        return sprites[Res.getId(name)] = new Sprite(w, h, cell_size, cell_size, MenuActivity.context.getResources(), getBmpId(name));
    }

    static Sprite putSprite(String name) {
        return sprites[Res.getId(name)] = new Sprite(cell_size, MenuActivity.context.getResources(), getBmpId(name));
    }

    int ddx = -2, ddy = -2;

    TimerTask loop = new TimerTask() {
        @Override
        public void run() {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();

                    if(moves.size() > 0) {

                        for(int i=0; i<moves.size(); i++) {
                            Movement m = moves.get(i);

                            if (m.steps < 1) {
                                m.unit.setX((int) m.endX);
                                m.unit.setY((int) m.endY);
                                m.unit.onMoveFinished();
                                moves.remove(m);
                            } else {
                                m.currX += (m.endX - m.startX) / steps_per_move;
                                m.currY += (m.endY - m.startY) / steps_per_move;
                            }
                            m.steps--;
                        }

                        if (moves.isEmpty()) {
                            level.checkIntersection();
                            correctTabs(level.getHero());
                        }

                    } else if(ddx != -2) {
                        level.move(ddx, ddy);
                        ddx = ddy = -2;
                    }

                    if(targetTabX != tabX) tabX += 0.25f * Math.signum(targetTabX-tabX);
                    if(targetTabY != tabY) tabY += 0.25f * Math.signum(targetTabY-tabY);

                    if(stair_transform < steps_per_move) stair_transform++;

                }
            });
        }
    };

    public void setLevel(Level level) {
        this.level = level;
    }

    public void addDecor(Decor d) {

        if(d.getX() < minX) minX = d.getX();
        if(d.getX() > maxX) maxX = d.getX();

        if(d.getY() > minY) minY = d.getY();
        if(d.getY() > maxY) maxY = d.getY();

        if(decors.size() == 0) decors.add(d);

        for(int i=0; i<decors.size(); i++) {
            if(decors.get(i).getLevel() > d.getLevel()) {
                decors.add(i, d);
                return;
            }
        }

        decors.add(d);
    }

    public void setBackgroundView(GameBackground bg) {
        this.bg = bg;
    }

    public void setBackgroundColor(int color) {
        switch (theme) {
            case "red":
                bg.setBackgroundColor(Color.parseColor("#EBF9FA"));
                break;
            case "green":
                bg.setBackgroundColor(Color.parseColor("#DCEDDC"));
                break;
            case "viol":
                bg.setBackgroundColor(Color.parseColor("#FCF3F9"));
                break;
        }
        if(bg != null) bg.setBackgroundColor(color);
    }

    public void setMazeTabX(float mazeTabX) {
        this.mazeTabX = mazeTabX;
    }

    public void setMazeTabY(float mazeTabY) {
        this.mazeTabY = mazeTabY;
    }

    public void updateMaze(boolean[][] maze, int w, int h) {
        prev_maze = prev_maze.length == 1 ? maze : this.maze;

        this.maze = maze;
        width = w;
        height = h;
        stair_transform = 0;

        for(Decor d : decors) d.animate();
    }

    public void moveUnit(Unit unit, int x, int y) {
        moves.add(new Movement(unit, unit.getX(), unit.getY(), x, y));
    }

    public void setItemButtons(View... views) {
        for(int i=0; i<4; i++) {
            items[i] = (ImageButton) views[i];
        }
    }

    public void pickUp(int i, String name) {
        items[i].setImageBitmap(sprites[Res.getId(name)].getBitmap(0, 0));

        ScaleAnimation anim = new ScaleAnimation(0.2f, 1, 0.2f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setInterpolator(new OvershootInterpolator(1.2f));
        anim.setDuration(240);

        items[i].startAnimation(anim);
        SoundManager.playSound("pick_up", 0.5f);
    }

    public void pickDown(int i) {
        items[i].setImageDrawable(null);

        ScaleAnimation anim = new ScaleAnimation(1, 0.0f, 1, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(160);

        items[i].startAnimation(anim);
    }

    public void clearDecors() {
        decors.clear();
        minX = minY = 100;
        maxX = maxY = -100;
    }

    ArrayList<Decor> getDecors() {
        return decors;
    }

    public void fixClouds() {
        clearQuestBalloons();
        if(bg != null)     bg.fixClouds();
    }

    int getMazeTabX() {
        return (int) mazeTabX;
    }

    int getMazeTabY() {
        return (int) mazeTabY;
    }



    public void correctTabs(Unit unit) {
        int screen = (int)(display_height/cell_size);

        if(countX(unit.getX(), unit.getY())*2 + mazeTabX + tabX + countY(unit.getX(), unit.getY())%2 >= 7) {
            targetTabX = Math.max(8-maxX-1, tabX-4);
        }

        if(countX(unit.getX(), unit.getY())*2 + mazeTabX + tabX <= 0) {
            targetTabX = Math.min(minX, tabX+4);
        }

        if(countY(unit.getX(), unit.getY()) + mazeTabY + tabY <= 1) {
            targetTabY = Math.min(minY+2, tabY+Math.max(screen/3, 4));
        }

        if(countY(unit.getX(), unit.getY()) + mazeTabY + tabY>= screen-1) {
            targetTabY = Math.max(screen-maxY+1, tabY-Math.max(screen/3, 4));
        }
    }

    void correctTabsFast(Unit unit) {
        int half_screen = (int)(display_height/cell_size/2);

        targetTabX = tabX = -(countX(unit.getX(), unit.getY())*2 + mazeTabX) + 4;
        targetTabY = tabY = Math.max(-(countY(unit.getX(), unit.getY()) + mazeTabY) + half_screen, half_screen*2 - maxY+1);

        if(-tabX < minX) targetTabX = tabX = -minX;
        else if(-tabX+8 >= maxX) targetTabX = tabX = -maxX+8;
    }

    void correctTabsNotSoFast(Unit unit) {
        int half_screen = (int)(display_height/cell_size/2);

        targetTabX =  -(countX(unit.getX(), unit.getY())*2 + mazeTabX) + 4;
        targetTabY =  Math.max(-(countY(unit.getX(), unit.getY()) + mazeTabY) + half_screen, half_screen*2 - maxY+1);

        if(-tabX < minX) targetTabX = -minX;
        else if(-tabX+8 >= maxX) targetTabX = -maxX+8;
    }

    public void gotoNextLevel(int lvl) {
        ((Main)context).splashIn(lvl);
    }

    public Level getLevel() {
        return level;
    }

    public void showThread() {

    }

    private boolean swipes = false;

    public void showSwipeHelp() {
        swipes = true;
    }

    public void hideSwipeHelp() {
        swipes = false;
    }

    public void addQuestBalloon(QuestBalloon balloon) {
        balloons.add(balloon);
    }

    private class Movement {
        float startX, currX, endX;
        float startY, currY, endY;
        int steps = steps_per_move;
        Unit unit;


        public Movement(Unit u, int x0, int y0, int x1, int y1) {
            currX = startX  = x0;
            currY = startY  = y0;
            endX    = x1;
            endY    = y1;
            unit    = u;
        }
    }

    private boolean hasAnyStairs(int x, int y) {
        return  (y*2+1 < maze.length && maze[y*2+1+1][x*2+1]) || (y*2-1+1>0 && maze[y*2+1-1][x*2+1]) ||
                (x*2+1+1 < maze[0].length && maze[y*2+1][x*2+1+1]) || (x*2-1+1>0 && maze[y*2+1][x*2+1-1]);
    }

    private Paint p = new Paint();
    private RectF rect = new RectF();

    private void drawFloors(Canvas c) {
        p.setColor(Color.BLACK);

        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                if(j%2==1 && i == width-1) continue;

                if(hasAnyStairs(height/2 + i - j/2, j/2+j%2 + i)) {
                    c.drawBitmap(sprites[Res.floor].getBitmap(0, 0),
                            (mazeTabX + tabX + i * 2 - 0.375f + j % 2) * cell_size,
                            (mazeTabY + tabY + j - 0.125f) * cell_size, p);
                }
            }
        }
    }

    private void drawBalloons(Canvas c) {
        for(QuestBalloon b : balloons) {
            b.draw(c);
        }
    }

    private void drawStairs(Canvas c) {
        p.setColor(Color.BLACK);

        int x, y;

        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                if(i%2==1 && j == width-1) continue;

                x = height/2 + j - i/2;
                y = i/2+i%2 + j;

                if(x*2-1>0) {
                    if(maze[y*2+1][x*2-1+1] == prev_maze[y*2+1][x*2-1+1]) {
                        if(maze[y*2+1][x*2-1+1]) {
                            c.drawBitmap(sprites[Res.stair].getBitmap(1, steps_per_move - 1),
                                    (mazeTabX+tabX + j*2 - 1 + i%2)*cell_size, (mazeTabY+tabY + i)*cell_size, p);
                        }
                    } else {
                        int k = maze[y*2+1][x*2-1+1] ? Math.min(steps_per_move-1, stair_transform) : Math.max(0, steps_per_move - stair_transform-1);
                        c.drawBitmap(sprites[Res.stair].getBitmap(1, k),
                                (mazeTabX+tabX + j*2 - 1 + i%2)*cell_size, (mazeTabY+tabY + i)*cell_size, p);
                    }
                }

                if(y*2+1+1 < maze.length) {
                    if(maze[y*2+1+1][x*2+1] == prev_maze[y*2+1+1][x*2+1]) {
                        if(maze[y*2+1+1][x*2+1]) {
                            c.drawBitmap(sprites[Res.stair].getBitmap(0, steps_per_move - 1),
                                    (mazeTabX+tabX + j*2 + i%2)*cell_size, (mazeTabY+tabY + i)*cell_size, p);
                        }
                    } else {
                        int k = maze[y*2+1+1][x*2+1] ? Math.min(steps_per_move-1, stair_transform) : Math.max(0, steps_per_move - stair_transform-1);
                        c.drawBitmap(sprites[Res.stair].getBitmap(0, k),
                                (mazeTabX+tabX + j*2 + i%2)*cell_size,(mazeTabY+tabY + i)*cell_size, p);
                    }
                }
            }
        }
    }

    private void drawDecors(Canvas c, int lvl_min, int lvl_max) {
        for(Decor d : decors) {
            if(d.getLevel() >= lvl_min && (d.getLevel() <= lvl_max)) {
                d.onDraw(c, tabX, tabY, cell_size);
            }
        }
    }

    int countX(int x, int y) {
        for(int i=0; i<height; i++) {
            for (int j = 0; j < width; j++) {
                if(x == height/2 + j - i/2 && y == i/2+i%2 + j) return j;
            }
        }

        return -1;
    }

    int countY(int x, int y) {
        for(int i=0; i<height; i++) {
            for (int j = 0; j < width; j++) {
                if(x == height/2 + j - i/2 && y == i/2+i%2 + j) return i;
            }
        }

        return -1;
    }

    private double theta = 0;

    private void drawUnits(Canvas c) {
        p.setColor(Color.BLACK);

        theta += Math.PI/20;
        double alpha = 0;

        for(Unit u : level.getUnits()) {

            int j = countX(u.getX(), u.getY());
            int i = countY(u.getX(), u.getY());
            float dx = 0, dy = 0;

            for(Movement m : moves) {
                if (m.unit == u) {
                    float px = m.currX-u.getX();
                    float py = m.currY-u.getY();

                    //осторожнобыдлокод
                    if(px > 0 && py > 0)  { dx = 2;     dy = 0;  }
                    if(px > 0 && py < 0)  { dx = 0;     dy = -2; }
                    if(px > 0 && py == 0) { dx = 1;     dy = -1; }
                    if(px < 0 && py > 0)  { dx = 0;     dy = 2;  }
                    if(px < 0 && py < 0)  { dx = -2;    dy = 0;  }
                    if(px < 0 && py == 0) { dx = -1;    dy = 1;  }
                    if(px == 0 && py > 0) { dx = 1;     dy = 1;  }
                    if(px == 0 && py < 0) { dx = -1;    dy = -1; }

                    dx = dx * (steps_per_move-m.steps) / steps_per_move;
                    dy = dy * (steps_per_move-m.steps) / steps_per_move;
                    break;
                }
            }

            if(u instanceof Item) {
                if(((Item) u).isPickable()) {
                    dy = (float) Math.sin(theta + (alpha+=Math.PI/4))*0.09f - 0.06f;
                }
            }

            int m = 0;
            if(!(u instanceof Item) && sprites[u.getName()].getWidth() > 1) {
                if(System.currentTimeMillis()/900%(level.getUnits().indexOf(u)+2) == 1) {
                    m = (int) ((System.currentTimeMillis()%450)/50);
                }
            }

            int water = 0;

            if(sprites[u.getName()].getHeight() > 3) {
                for(Decor d : decors) {
                    if(Res.str(d.getName()).contains("water") &&
                            d.getX() == mazeTabX+j*2+i%2 && d.getY()+1 == mazeTabY+i) {
                        water  = 3;
                        break;
                    }
                }
            }

            Bitmap b = sprites[u.getName()].getBitmap(water + (dx == 0 ? 0 : (dx > 0 ? 1 : 2)),
                    dx == 0 ? m : Math.min(steps_per_move-1, stair_transform));

            c.drawBitmap(b, (mazeTabX+tabX + j*2 + i%2 + dx)*cell_size - b.getWidth()/2,
                    (mazeTabY+tabY + i - 0.93f + dy)*cell_size, p);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas c) {
        if(isInEditMode()) return;

        long time = System.currentTimeMillis();

        try {
            drawDecors(c, -100, -1);
            drawStairs(c);
            drawFloors(c);
            drawUnits(c);
            drawDecors(c, 1, 100);

            drawBalloons(c);
        } catch (Exception e) {
            Log.d("Castles-n-Stairs", "ArrayIndexOfBoundException");
        }

        //if(dev_mode)
            c.drawText("millis: " + (System.currentTimeMillis() - time+1), 0, 15, p);
    }
}

class QuestBalloon {
    int a = Res.exit, b = Res.exit;
    int alpha = 0, target_alpha = 0;
    int j, i;
    Renderer r;

    public QuestBalloon(int key, int content, int x, int y, Renderer renderer) {
        r = renderer;
        a = key;
        b = content;
        j = x;
        i = y;
        target_alpha = 0;
    }

    void hide() {
        target_alpha = 0;
    }

    void draw(Canvas c) {
        Paint p = new Paint();

        int x = r.countX(j, i);
        int yy = r.countY(j, i);

        if(target_alpha != alpha) alpha += 50 * Math.signum(target_alpha - alpha);
        p.setAlpha(alpha);

        float y = Math.max(-5/ Renderer.cell_size, r.mazeTabY+r.tabY + yy-1.47f-0.125f);
        RectF rect = new RectF();


        rect.set((r.mazeTabX+r.tabX + x*2 - 1.32f+0.125f + yy%2)*r.cell_size, (y)*r.cell_size,
                (r.mazeTabX+r.tabX + x*2 - 0.11f+0.125f + yy%2)*r.cell_size, (y + 0.9f)*r.cell_size);

        c.drawBitmap(Renderer.sprites[Res.balloon].getBitmap(0, 0), null, rect, p);

        rect.set((r.mazeTabX+r.tabX + x*2 - 1.32f+0.125f + yy%2)*r.cell_size, (y+0.1f)*r.cell_size,
                (r.mazeTabX+r.tabX + x*2 - 1.32f+0.125f+0.5f + yy%2)*r.cell_size, (y+0.5f+0.1f)*r.cell_size);

        c.drawBitmap(Renderer.sprites[Res.getId(Res.getItemId(a))].getBitmap(0, 0), null, rect, p);

        rect.set((r.mazeTabX+r.tabX + x*2 - 1.32f+0.125f+0.71f + yy%2)*r.cell_size, (y+0.1f)*r.cell_size,
                (r.mazeTabX+r.tabX + x*2 - 1.32f+0.125f+0.5f+0.71f + yy%2)*r.cell_size, (y+0.5f+0.1f)*r.cell_size);

        c.drawBitmap(Renderer.sprites[Res.getId(Res.getItemId(b))].getBitmap(0, 0), null, rect, p);
    }
}

class Movement {}