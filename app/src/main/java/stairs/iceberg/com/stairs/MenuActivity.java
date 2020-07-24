package stairs.iceberg.com.stairs;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * (C) Iceberg Mobile
 * Created by User on 21.01.2018.
 */
public class MenuActivity extends Activity {
    private SharedPreferences database;
    private ShadowBackground background;
    private Fragment level_menu;
    static MediaPlayer music;
    static Activity context;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        database = getSharedPreferences("database", MODE_PRIVATE);

        background = findViewById(R.id.view3);

        level_menu = getFragmentManager().findFragmentById(R.id.fragment);
        level_menu.getView().setVisibility(View.INVISIBLE);

        ((ImageButton) findViewById(R.id.button_menu_sfx)).setImageResource(database.getBoolean("sfx", true) ?
                R.drawable.icon_menu_sfx : R.drawable.icon_menu_sfx_no);

        ((ImageButton) findViewById(R.id.button_menu_bgm)).setImageResource(database.getBoolean("bgm", true) ?
                R.drawable.icon_menu_bgm : R.drawable.icon_menu_bgm_no);


        ScaleAnimation anim = new ScaleAnimation(1, 1.05f, 1, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.62f);
        anim.setDuration(1600);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        findViewById(R.id.imageView_logo_main).startAnimation(anim);

        music = MediaPlayer.create(this, R.raw.bgm);
        music.setLooping(true);
        music.setVolume(0.25f, 0.25f);

        context = this;
        SoundManager.init();

        background.start();

        ((GameBackground) findViewById(R.id.view158)).setIntensivity(0.015f);

        ((GameBackground) findViewById(R.id.view158)).fixClouds();

        startButtonAnimation();
    }

    public void startButtonAnimation() {
        Button b1 = findViewById(R.id.button_start);
        Button b2 = findViewById(R.id.button_continue);

        ScaleAnimation anim1 = new ScaleAnimation(1, 1.05f, 1, 1.05f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(250);
        anim1.setRepeatMode(Animation.REVERSE);
        anim1.setRepeatCount(1);
        anim1.setStartOffset(0);

        ScaleAnimation anim2 = new ScaleAnimation(1, 1.05f, 1, 1.05f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim2.setDuration(250);
        anim2.setRepeatMode(Animation.REVERSE);
        anim2.setRepeatCount(1);
        anim2.setStartOffset(200);

        b1.startAnimation(anim1);
        b2.startAnimation(anim2);
    }

    private void hideLevelMenu() {
        if(level_menu.getView().getAnimation() != null && !level_menu.getView().getAnimation().hasEnded()) return;
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(1200);
        anim.setInterpolator(new AnticipateOvershootInterpolator(1.1f));

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                level_menu.getView().setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        level_menu.getView().startAnimation(anim);
    }

    private void showLevelMenu() {
        if(level_menu.getView().getAnimation() != null && !level_menu.getView().getAnimation().hasEnded()) return;

        level_menu.getView().bringToFront();

        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(1200);
        anim.setInterpolator(new AnticipateOvershootInterpolator(1.1f));

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                level_menu.getView().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        level_menu.getView().startAnimation(anim);
    }

    public void onResume() {
        super.onResume();
        music.start();

        if(database.getBoolean("sfx", true)) SoundManager.on();
        else SoundManager.off();

        if(database.getBoolean("bgm", true)) music.setVolume(0.25f, 0.25f);
        else music.setVolume(0.0f, 0.0f);

        try {
            InputStream inputstream = openFileInput("autosave.txt");
            inputstream.close();
            findViewById(R.id.button_continue).setVisibility(View.VISIBLE);
        } catch (IOException e) {
            findViewById(R.id.button_continue).setVisibility(View.INVISIBLE);
        }
    }

    public void onPause() {
        super.onPause();
        music.pause();
    }

    @SuppressWarnings("ConstantConditions")
    public void onBackPressed() {
        if(level_menu.getView().getVisibility() == View.VISIBLE) {
            hideLevelMenu();
        } else super.onBackPressed();
    }

    public void loadAutosave(View v) {
        Level.level_to_start = "autosave.txt";
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        overridePendingTransition(R.anim.menu_out, R.anim.menu_in);
    }

    public void start(View v) {
        showLevelMenu();
    }

    public void clickSfx(View v) {
        SharedPreferences.Editor e = database.edit();
        e.putBoolean("sfx", !database.getBoolean("sfx", true));
        e.apply();

        ((ImageButton) v).setImageResource(database.getBoolean("sfx", true) ?
                R.drawable.icon_menu_sfx : R.drawable.icon_menu_sfx_no);

        if(database.getBoolean("sfx", true)) SoundManager.on();
        else SoundManager.off();
    }

    public void clickBgm(View v) {
        SharedPreferences.Editor e = database.edit();
        e.putBoolean("bgm", !database.getBoolean("bgm", true));
        e.apply();

        ((ImageButton) v).setImageResource(database.getBoolean("bgm", true) ?
                R.drawable.icon_menu_bgm : R.drawable.icon_menu_bgm_no);

        if(database.getBoolean("bgm", true)) music.setVolume(0.12f, 0.12f);
        else music.setVolume(0.0f, 0.0f);
    }
}
