package stairs.iceberg.com.stairs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Activity {
    static Main main;

    private Renderer renderer;
    private Level level;
    private FontButton button_sfx, button_bgm, button_joy, button_back;
    private LinearLayout menu;
    private RelativeLayout layout;
    private GameBackground bg;
    private RelativeLayout splash;
    private TextView splash_text1, splash_text2, hint_count_text;
    private String[] cues;

    private SharedPreferences database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        renderer = findViewById(R.id.renderer);
        renderer.setItemButtons(
                findViewById(R.id.button_item_0), findViewById(R.id.button_item_1),
                findViewById(R.id.button_item_2), findViewById(R.id.button_item_3));

        button_sfx  = findViewById(R.id.button_sfx);
        button_bgm  = findViewById(R.id.button_bgm);
        button_joy  = findViewById(R.id.button_keys);
        button_back = findViewById(R.id.button_back);
        menu        = findViewById(R.id.menu_game);
        layout      = findViewById(R.id.game_layout);
        splash      = findViewById(R.id.level_splash);
        bg          = findViewById(R.id.view2);

        splash_text1 = findViewById(R.id.myImageViewText);
        splash_text2 = findViewById(R.id.fontText1);
        hint_count_text = findViewById(R.id.text_hint);

        cues = getResources().getStringArray(R.array.level_cues);

        renderer.setBackgroundView(bg);

        level = new Level(renderer);

        database = getSharedPreferences("database", MODE_PRIVATE);
        confirmDatabase();

        splash_message = getString(R.string.gamesplash_waiting);
        new Timer().schedule(dots, 400, 400);

        findViewById(R.id.joystick).setVisibility(
                database.getBoolean("joy", false) ? View.VISIBLE : View.INVISIBLE);

        hint_count_text.setText("" + database.getInt("hint_count", 1));

        findViewById(R.id.button_hint).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(Main.this, EditorActivity.class);
                startActivity(intent);
                return false;
            }
        });

        ScaleAnimation anim = new ScaleAnimation(1, 1.05f, 1, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.62f);
        anim.setDuration(1600);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        findViewById(R.id.imageView3).startAnimation(anim);

        splashOut(Integer.parseInt(Level.current_level_num));
    }

    private String splash_message = "none";

    private TimerTask dots = new TimerTask() {
        int dots = 0;

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = splash_message;
                    String spaces = "\n";
                    String dotss = "";

                    for(int i=0; i<=dots%3; i++) {
                        spaces += " ";
                        dotss += ".";
                    }

                    splash_text2.setText( text.replaceFirst("\n", spaces) + dotss);
                    dots++;
                }
            });
        }
    };

    public void confirmDatabase() {
        if(!database.contains("sfx")) {
            SharedPreferences.Editor e = database.edit();
            e.putBoolean("sfx", true);
            e.apply();
        }

        if(!database.contains("bgm")) {
            SharedPreferences.Editor e = database.edit();
            e.putBoolean("bgm", true);
            e.apply();
        }

        button_sfx.setText(getString(R.string.menu_sfx));
        button_sfx.setIcon(database.getBoolean("sfx", true) ?
                R.drawable.icon_sfx : R.drawable.icon_sfx_no);
        if(database.getBoolean("sfx", true)) SoundManager.on();
        else SoundManager.off();

        button_bgm.setText(getString(R.string.menu_bgm));
        button_bgm.setIcon(database.getBoolean("bgm", true) ?
                R.drawable.icon_bgm : R.drawable.icon_bgm_no);
        if(database.getBoolean("bgm", true)) MenuActivity.music.setVolume(0.25f, 0.25f);
        else MenuActivity.music.setVolume(0.0f, 0.0f);
    }

    public void splashIn(int level) {
        splashIn(level, R.string.gamesplash_waiting);
    }

    public void splashIn(final int level, int string) {
        if(splash.getAnimation() != null && !splash.getAnimation().hasEnded()) return;

        splash_text1.setText(getString(R.string.gamesplash_castle) + " #" + (level+1));
        splash_message = getString(string);

        switch (LevelFragment.getLevelTheme(level)) {
            case "red": splash.setBackground(getResources().getDrawable(R.drawable.brickbitmap)); break;

            case "green": splash.setBackground(getResources().getDrawable(R.drawable.brickbitmap_green)); break;

            case "gray": splash.setBackground(getResources().getDrawable(R.drawable.brickbitmap_gray)); break;
        }

        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(1800);
        anim.setInterpolator(new AnticipateOvershootInterpolator(1.1f));
        anim.setFillAfter(true);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                splash.setAlpha(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                renderer.getLevel().loadLevel("level_" + level + ".txt");
                splashOut(level);
                splash.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        splash.startAnimation(anim);
    }

    public void splashOut(final int level) {
        splash_text1.setText(getString(R.string.gamesplash_castle) + " #" + (level+1));

        final TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1.1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(1800);
        anim.setInterpolator(new AnticipateOvershootInterpolator(1.1f));
        anim.setStartOffset(2800);
        anim.setFillAfter(true);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                splash.setAlpha(1);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(!"---".equals(cues[level])/* && database.getInt("completed", 0) <= level*/) {
                            showCue(level);
                        }
                    }

                }, 4040);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splash.setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        splash.startAnimation(anim);
    }

    private AlertDialog dialog;

    private void showCue(int level) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.cue_hint).setMessage(cues[level]).setIcon(R.drawable.icon_hint).setCancelable(true);

        if(level == 0) {
            ad.setNegativeButton(R.string.cue_swipes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor e = database.edit();
                    e.putBoolean("joy", false);
                    e.apply();

                    findViewById(R.id.joystick).setVisibility(View.INVISIBLE);
                }
            });

            ad.setPositiveButton(R.string.cue_buttons, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor e = database.edit();
                    e.putBoolean("joy", true);
                    e.apply();

                    findViewById(R.id.joystick).setVisibility(View.VISIBLE);
                }
            });
        } else {
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        dialog = ad.create();
        try {
            dialog.show();


            TextView textView = dialog.getWindow().findViewById(android.R.id.message);
            Typeface face = Typeface.createFromAsset(getAssets(), "font.ttf");
            textView.setTypeface(face);

            Button b1 = dialog.getWindow().findViewById(android.R.id.button1);
            Button b2 = dialog.getWindow().findViewById(android.R.id.button2);
            Button b3 = dialog.getWindow().findViewById(android.R.id.button3);

            b1.setTypeface(face);
            b2.setTypeface(face);
            b3.setTypeface(face);


            b1.setAlpha(0.60f);
            b1.setAllCaps(false);
            b1.setTextColor(Color.parseColor("#7D0C0C"));
        } catch(WindowManager.BadTokenException e) {

        }
}

    public void restart(View v) {
        level.loadLevel("level_" + level.current_level_num + ".txt");
    }

    public void hint(View v) {
        if(database.getInt("hint_count", 1) > -1000) {
            SharedPreferences.Editor e = database.edit();
            e.putInt("hint_count", database.getInt("hint_count", 1)-1);
            e.apply();
            level.thread();
        }
    }

    public void onStop() {
        super.onStop();
        level.getLevelReader().saveLevel();
    }

    public void onDestroy() {
        super.onDestroy();
        level.getLevelReader().saveLevel();
    }

    public void onPause() {
        super.onPause();
        MenuActivity.music.pause();
        level.getLevelReader().saveLevel();
    }

    public void onResume() {
        super.onResume();
        if(!MenuActivity.music.isPlaying()) MenuActivity.music.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (Renderer.getTheme()) {
                case "red": splash.setBackground(getDrawable(R.drawable.brickbitmap)); break;

                case "green": splash.setBackground(getDrawable(R.drawable.brickbitmap_green)); break;

                case "gray": splash.setBackground(getDrawable(R.drawable.brickbitmap_gray)); break;
            }
        } else {
            switch (Renderer.getTheme()) {
                case "red": splash.setBackground(getResources().getDrawable(R.drawable.brickbitmap)); break;

                case "green": splash.setBackground(getResources().getDrawable(R.drawable.brickbitmap_green)); break;

                case "gray": splash.setBackground(getResources().getDrawable(R.drawable.brickbitmap_gray)); break;
            }
        }
    }

    private void showButton(View v, int delay) {
        TranslateAnimation anim1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
        AlphaAnimation anim2 = new AlphaAnimation(0, 1);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(anim1);
        set.addAnimation(anim2);
        set.setDuration(300);
        set.setStartOffset(delay);

        v.startAnimation(set);
    }

    public void showMenu(View view) {
        if(menu.getVisibility() == View.VISIBLE) return;

        menu.setVisibility(View.VISIBLE);
        menu.setAlpha(1f);
        showButton(button_sfx,  0);
        showButton(button_bgm,  100);
        showButton(button_joy,  200);
        showButton(button_back, 300);
    }

    public void hideMenu() {
        if(menu.getAlpha() != 1 || menu.getVisibility() == View.INVISIBLE) return;

        TranslateAnimation anim1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.18f);
        AlphaAnimation anim2 = new AlphaAnimation(1, 0);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(anim1);
        set.addAnimation(anim2);
        set.setDuration(400);

        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                menu.setAlpha(0.99f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menu.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        menu.startAnimation(set);
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else {
            Toast.makeText(getBaseContext(), R.string.press_to_exit, Toast.LENGTH_SHORT).show();
            if(dialog != null) {
                dialog.setCancelable(true);
                dialog.dismiss();
            }
        }
        back_pressed = System.currentTimeMillis();
    }

    public boolean onKeyDown(int code, KeyEvent e) {
        super.onKeyDown(code, e);

        switch (code) {
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                break;

            case 21:
                renderer.ddx = 0;
                renderer.ddy = -1;
                break;

            case 22:
                renderer.ddx = 0;
                renderer.ddy = 1;
                break;

            case 19:
                renderer.ddx = 1;
                renderer.ddy = 0;
                break;

            case 20:
                renderer.ddx = -1;
                renderer.ddy = 0;
                break;
        }

        return false;
    }

    public void clickSfx(View view) {
        SharedPreferences.Editor e = database.edit();
        e.putBoolean("sfx", !database.getBoolean("sfx", true));
        e.apply();

        confirmDatabase();
    }

    public void clickBgm(View view) {
        SharedPreferences.Editor e = database.edit();
        e.putBoolean("bgm", !database.getBoolean("bgm", true));
        e.apply();

        confirmDatabase();
    }

    public void clickBack(View view) {
        hideMenu();
        super.onBackPressed();
    }

    public void move(View view) {
        switch (view.getId()) {
            case R.id.button_nw:
                renderer.ddx = 0;
                renderer.ddy = -1;
                break;

            case R.id.button_se:
                renderer.ddx = 0;
                renderer.ddy = 1;
                break;

            case R.id.button_ne:
                renderer.ddx = 1;
                renderer.ddy = 0;
                break;

            case R.id.button_sw:
                renderer.ddx = -1;
                renderer.ddy = 0;
                break;
        }
    }

    public void clickJoystick(View view) {
        SharedPreferences.Editor e = database.edit();
        e.putBoolean("joy", !database.getBoolean("joy", false));
        e.apply();

        findViewById(R.id.joystick).setVisibility(
                database.getBoolean("joy", false) ? View.VISIBLE : View.INVISIBLE);
    }
}
