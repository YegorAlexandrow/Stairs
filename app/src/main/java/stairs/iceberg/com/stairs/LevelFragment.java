package stairs.iceberg.com.stairs;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * (C) Iceberg Mobile
 * Created by User on 20.01.2018.
 */
public class LevelFragment extends Fragment {
    private LevelIcon[] icons = new LevelIcon[9];
    private TextView title;
    private RelativeLayout layout;
    private SeekBar bar;
    private SharedPreferences database;

    private String[] stages;
    static final int total_levels = 36, real_level_count = 19;
    int curr = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.activity_levels, container, false);

        title = v.findViewById(R.id.textView4);
        title.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "font.ttf"));

        //осторожнобыдлокод
        icons[0] = v.findViewById(R.id.view00);
        icons[1] = v.findViewById(R.id.view01);
        icons[2] = v.findViewById(R.id.view02);
        icons[3] = v.findViewById(R.id.view10);
        icons[4] = v.findViewById(R.id.view11);
        icons[5] = v.findViewById(R.id.view12);
        icons[6] = v.findViewById(R.id.view20);
        icons[7] = v.findViewById(R.id.view21);
        icons[8] = v.findViewById(R.id.view22);

        bar = v.findViewById(R.id.seekBar);

        stages = getResources().getStringArray(R.array.stage_titles);

        layout = v.findViewById(R.id.levels_layout);
        layout.setOnTouchListener(new SimpleOnSwipeTouchListener(MenuActivity.context) {
            public void onSwipeRight() {
                prev(layout);
            }

            public void onSwipeLeft() {
                next(layout);
            }
        });

        database = v.getContext().getSharedPreferences("database", Activity.MODE_PRIVATE);

        for(int i=0; i<9; i++) {
            icons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickLevel(view);
                }
            });
        }

        v.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next(view);
            }
        });


        v.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev(view);
            }
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(curr/9 != Math.round(seekBar.getProgress()/333f)) {
                    curr = Math.round(seekBar.getProgress()/333f)*9;
                    updateIcons();
                    title.setText("\"" + stages[curr/9] + "\"");
                }

                animateSeekBar();
            }
        });

        return v;
    }

    public void onResume() {
        super.onResume();
        if(!MenuActivity.music.isPlaying()) MenuActivity.music.start();
        updateIcons();

        for(int i=0; i<9; i++) {
            icons[i].startDrawing();
        }

        animateSeekBar();
        /*
        switch (Renderer.getTheme()) {
            case "red": layout.setBackground(getDrawable(R.drawable.brickbitmap)); break;

            case "green": layout.setBackground(getDrawable(R.drawable.brickbitmap_green)); break;

            case "gray": layout.setBackground(getDrawable(R.drawable.brickbitmap_gray)); break;
        }
        */
    }

    public void onPause() {
        super.onPause();
        MenuActivity.music.pause();

        for(int i=0; i<9; i++) {
            icons[i].stopDrawing();
        }
    }

    public static String getLevelTheme(int lvl) {
        try {
            Scanner in = new Scanner(MenuActivity.context.getAssets().open("level_" + lvl + ".txt"));

            while (in.hasNext()) {
                if("theme".equals(in.next())) {
                    String n = in.next();
                    in.close();
                    return n;
                }
            }

            in.close();
        } catch (IOException e) {
            return "red";
        }

        return "red";
    }

    private void updateIcons() {
        int n = Math.min(database.getInt("completed", 0), real_level_count-1);

        for(int i=0; i<icons.length; i++) {
            icons[i].setTheme(getLevelTheme(curr+i));
            icons[i].showOut(i + curr <= n ? i+curr+1 : -1, (int) (Math.random()*625));
        }
    }

    private void animateSeekBar() {
        ValueAnimator anim = ValueAnimator.ofInt(bar.getProgress(), curr/9*333);
        anim.setDuration(1000);
        anim.setInterpolator(new OvershootInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                bar.setProgress(animProgress);
            }
        });

        anim.start();
    }

    public void next(View v) {
        if(curr + 9 < total_levels) {
            curr += 9;
            updateIcons();
            title.setText("\"" + stages[curr/9] + "\"");

            animateSeekBar();
        }
    }

    public void prev(View v) {
        if(curr - 8 >= 0) {
            curr -= 9;
            updateIcons();
            title.setText("\"" + stages[curr/9] + "\"");

            animateSeekBar();
        }
    }

    private void showAutosaveDialog(final int n) {
        AlertDialog.Builder ad = new AlertDialog.Builder(MenuActivity.context);
        ad.setTitle("Continue?").setMessage(R.string.autosave_dialog_msg).setIcon(R.drawable.icon_help);
        ad.setPositiveButton(R.string.autosave_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                loadLevel("autosave.txt");
            }
        });
        ad.setNegativeButton(R.string.autosave_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Level.current_level_num = "" + n;
                loadLevel("level_" + n + ".txt");
            }
        });

        AlertDialog dialog = ad.create();
        dialog.show();

        TextView textView = dialog.getWindow().findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(MenuActivity.context.getAssets(),"font.ttf");
        textView.setTypeface(face);

        Button b1 = dialog.getWindow().findViewById(android.R.id.button1);
        Button b2 = dialog.getWindow().findViewById(android.R.id.button2);
        Button b3 = dialog.getWindow().findViewById(android.R.id.button3);

        //b1.setAllCaps(false);
        b1.setTypeface(face);
        //b2.setAllCaps(false);
        b2.setTypeface(face);
        //b3.setAllCaps(false);
        b3.setTypeface(face);
        //((TextView) dialog.getWindow().findViewById(R.id.alertTitle)).setTypeface(face);
    }

    private void loadLevel(String str) {
        Level.level_to_start = str;//"level_" + (curr+i) + ".txt";
        //Level.current_level_num = "" + (curr + i);
        Intent intent = new Intent(MenuActivity.context, Main.class);
        startActivity(intent);
        MenuActivity.context.overridePendingTransition(R.anim.menu_in, R.anim.menu_out);
    }

    private int getAutosaveNum() {
        try {
            InputStream inputstream = MenuActivity.context.openFileInput("autosave.txt");
            Scanner in = new Scanner(inputstream);

            while (in.hasNext()) {
                if("num".equals(in.next())) {
                    int n = in.nextInt();
                    in.close();
                    inputstream.close();
                    return n;
                }
            }

            in.close();
            inputstream.close();
        } catch (IOException e) {
            return -1;
        }

        return -1;
    }

    public void clickLevel(View v) {
        int n = Math.min(database.getInt("completed", 0), real_level_count-1);

        for(int i=0; i<9; i++) {
            if(icons[i] == v) {
                if(i + curr > n ) return;

                if(getAutosaveNum() == i+curr) {
                    showAutosaveDialog(curr+i);
                } else {
                    Level.current_level_num = "" + (curr + i);
                    loadLevel("level_" + (curr+i) + ".txt");
                }

                break;
            }
        }
    }
}
