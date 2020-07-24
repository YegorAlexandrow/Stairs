package stairs.iceberg.com.stairs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * (C) Iseberg Mobile
 * Created by User on 27.01.2018.
 */
public class ShadowBackground extends View {
    private boolean maze[][] = new boolean[1][1];
    private boolean prev_maze[][] = new boolean[1][1];
    private ArrayList<Decor> decors = new ArrayList<>();

    private int width = 8, height = 8, steps_per_move = 9, cell_size = 32, stair_transform = 0, delay = 9;

    public ShadowBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public ShadowBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShadowBackground(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        if(isInEditMode()) return;

        prev_maze = MazeGenerator.generate(width, height, 5, 5, null, null);
        maze = MazeGenerator.generate(width, height, 5, 5, null, null);

    }

    public void start() {
        Renderer.putSprite("&_stair", steps_per_move, 2);
        Renderer.putSprite("&_floor", 1, 1);
        Renderer.putSprite("&_bg", 1, 1);
        Renderer.putSprite("&_parapet", 1, 1);
        Renderer.putSprite("&_decor", 3, 1);

        for(int i=1; i<height; i++) {
            for(int j=1; j<width; j++) {
                if((j+i)%2==1) decors.add(new StairDecor(Res.decor, j, i));
            }
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(delay > 0) {
                            delay--;
                            if(delay == 0) {
                                for(Decor d : decors)  d.animate();

                                //((MenuActivity) getContext()).startButtonAnimation();
                            }
                        } else {
                            if(stair_transform >= steps_per_move) {
                                stair_transform = 0;
                                delay = 6 + (int) (Math.random()*15);

                                prev_maze = maze;
                                maze = MazeGenerator.generate(width, height, 5, 5, null, null);

                            } else stair_transform++;
                        }

                        invalidate();
                    }
                });
            }
        }, 40, 40);
    }

    private Paint p = new Paint();

    private void drawFloors(Canvas c) {
        Sprite floor = Renderer.sprites[Res.floor];

        for(int i=0; i<width; i++) {
            for(int j=1; j<height; j++) {
                if(j%2==1 && i == width-1) continue;

                c.drawBitmap(floor.getBitmap(0, 0), null,
                        new RectF((i*2 - 0.375f + j%2)*cell_size, (j-0.125f)*cell_size,
                                (i*2 + 0.375f + j%2)*cell_size, (j+0.125f)*cell_size), p);

            }
        }
    }

    private void drawStairs(Canvas c) {
        Sprite stairs = Renderer.sprites[Res.stair];
        int x, y;

        for(int i=1; i<height; i++) {
            for(int j=0; j<width; j++) {
                if(i%2==1 && j == width-1) continue;

                x = height/2 + j - i/2;
                y = i/2+i%2 + j;

                if(x*2-1>0) {
                    if(maze[y*2+1][x*2-1+1] == prev_maze[y*2+1][x*2-1+1]) {
                        if(maze[y*2+1][x*2-1+1])
                            c.drawBitmap(stairs.getBitmap(1, steps_per_move-1), null,
                                    new RectF((j*2 - 1 + i%2)*cell_size, (i)*cell_size,
                                            (j*2 + i%2)*cell_size, (i+1)*cell_size), p);
                    } else {
                        int k = maze[y*2+1][x*2-1+1] ? Math.min(steps_per_move-1, stair_transform) : Math.max(0, steps_per_move - stair_transform-1);

                        c.drawBitmap(stairs.getBitmap(1, k), null,
                                new RectF((j*2 - 1 + i%2)*cell_size, (i)*cell_size,
                                        (j*2 + i%2)*cell_size, (i+1)*cell_size), p);
                    }
                }

                if(y*2+1+1 < maze.length) {
                    if(maze[y*2+1+1][x*2+1] == prev_maze[y*2+1+1][x*2+1]) {
                        if(maze[y*2+1+1][x*2+1])
                            c.drawBitmap(stairs.getBitmap(0, steps_per_move-1), null,
                                    new RectF((j*2 + i%2)*cell_size, (i)*cell_size,
                                            (j*2 + 1 + i%2)*cell_size, (i+1)*cell_size), p);
                    } else {
                        int k = maze[y*2+1+1][x*2+1] ? Math.min(steps_per_move-1, stair_transform) : Math.max(0, steps_per_move - stair_transform-1);
                        c.drawBitmap(stairs.getBitmap(0, k), null,
                                new RectF((j*2 + i%2)*cell_size, (i)*cell_size,
                                        (j*2 + 1 + i%2)*cell_size, (i+1)*cell_size), p);
                    }
                }
            }
        }
    }

    private void drawBackground(Canvas c) {
        Sprite brick = Renderer.sprites[Res.bg];

        for(int i=0; i<width; i++) {
            for(int j = Renderer.getTheme().equals("gray") ? 0 : 1; j < height; j++) {
                c.drawBitmap(brick.getBitmap(0, 0), null, new RectF(i*cell_size, (j-0.05f)*cell_size, (i+1)*cell_size, (j+0.95f)*cell_size), p);
            }
        }
    }

    private void drawDecors(Canvas c) {
        Sprite parapet = Renderer.sprites[Res.parapet];

        for(Decor d : decors) {
            d.onDraw(c, 0, 0, cell_size);
        }

        for(int j = 0; j < width; j++) {
            c.drawBitmap(parapet.getBitmap(0, 0), null, new RectF(j*cell_size, 0, (j+1)*cell_size, cell_size),  p);
        }
    }

    protected void onDraw(Canvas c) {
        if(isInEditMode() || ((Activity) getContext()).isFinishing()) return;

        cell_size = getWidth()/8;// + (int) (Math.sin(System.currentTimeMillis()%6400/6400f*Math.PI*2)*getWidth()*0.04f);

        drawBackground(c);
        drawStairs(c);
        drawFloors(c);
        drawDecors(c);
    }
}
