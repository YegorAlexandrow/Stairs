package stairs.iceberg.com.stairs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * (C) Iseberg Mobile
 * Created by User on 01.01.2018.
 */
public class EditorActivity extends Activity {
    private Button button_eraser, button_save;
    private EditText text, file_text;
    private ImageButton button_demo;
    private LinearLayout palette;
    private EditorGrid editor;
    private Spinner spinner;
    private ArrayList<ImageButton> elements = new ArrayList<>();
    static String filename = Level.level_to_start;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_editor);

        button_eraser   = findViewById(R.id.button);
        button_save     = findViewById(R.id.button4);
        button_demo     = findViewById(R.id.imageButton);
        editor          = findViewById(R.id.view);
        palette         = findViewById(R.id.palette);
        text            = findViewById(R.id.editText);
        file_text       = findViewById(R.id.editText2);
        spinner         = findViewById(R.id.spinner);

        for(DrawElement i : items)  addElement(i);
        for(DrawElement i : units)  addElement(i);
        for(DrawElement i : decors) addElement(i);

        text.setBackgroundColor(0x66F0EAD2);

        button_save.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                file_text.setVisibility(View.VISIBLE);
                file_text.requestFocus();
                return false;
            }
        });

        button_demo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String str = "";

                for(int i=0; i<18; i++) {

                    InputStreamReader isr;
                    StringBuilder buffer = null;
                    try {
                        InputStream inputstream = Main.main.openFileInput("level_" + i + ".txt");
                        isr = new InputStreamReader(inputstream);
                        BufferedReader reader = new BufferedReader(isr);
                        String tmp;

                        buffer = new StringBuilder();
                        buffer.append("\n\n@@ level").append(i).append(" @@\n");

                        while ((tmp = reader.readLine()) != null) {
                            buffer.append(tmp).append("\n");
                        }
                        inputstream.close();
                    } catch (IOException e) {
                    }

                    str += buffer + "\n";
                }

                file_text.setText(str);
                file_text.setVisibility(View.VISIBLE);

                return false;
            }
        });

        file_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                    keyCode == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    filename = file_text.getText().toString() + ".txt";
                    editor.loadMap(filename);
                    file_text.setVisibility(View.INVISIBLE);
                    file_text.clearFocus();
                }
                return false;
            }
        });

        switch (Renderer.getTheme()) {
            case "red": spinner.setSelection(0); break;

            case "green": spinner.setSelection(1); break;

            case "gray": spinner.setSelection(2); break;

            case "viol": spinner.setSelection(3); break;

            default: spinner.setSelection(0); break;
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View item, int i, long id) {

                String[] choose = getResources().getStringArray(R.array.themes);
                Renderer.setTheme(choose[i]);
                editor.invalidate();

                elements.clear();
                palette.removeAllViews();

                for(DrawElement d : items)  addElement(d);
                for(DrawElement d : units)  addElement(d);
                for(DrawElement d : decors) addElement(d);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editor.loadMap(filename);
    }

    private void addElement(DrawElement name) {
        Log.d("", "name@@" + name.names[0]);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.width = 90;

        ImageButton button = new ImageButton(this);
        button.setImageBitmap(Renderer.sprites[Res.getId(name.names[0])].getBitmap(0, 0));
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setLayoutParams(params);
        palette.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = elements.indexOf(view);
                DrawElement d = n < items.length ? items[n] : (n < items.length+units.length ? units[n-items.length] : decors[n-items.length-units.length]);

                editor.setElement(n < items.length ? Etype.ITEM : (n < items.length+units.length ? Etype.UNIT : Etype.DECOR), d);

                button_demo.setImageBitmap(Renderer.sprites[Res.getId(d.names[0])].getBitmap(0, 0));
                editor.setErasing(false);
            }
        });

        elements.add(button);
    }

    public void clickEraser(View v) {
        editor.setErasing(true);
        button_demo.setImageBitmap(null);
    }

    public void clickCode(View v) {
        if(text.getVisibility() == View.VISIBLE) {
            editor.acceptSpecCode(text.getText().toString());
            editor.acceptSpecCode(text.getText().toString());
            text.setVisibility(View.INVISIBLE);
        } else {
            text.setText(editor.getSpecCode());
            text.setVisibility(View.VISIBLE);
        }
    }

    public void clickSave(View v) {
        editor.generateCode();
    }

    public void onPause() {
        super.onPause();
        MenuActivity.music.pause();
    }

    public void onResume() {
        super.onResume();
        if(!MenuActivity.music.isPlaying()) MenuActivity.music.start();
    }

    private DrawElement[] items =
            {
                    new DrawElement(0, "key_yellow"),
                    new DrawElement(0, "key_blue"),
                    new DrawElement(0, "key_orange"),
                    new DrawElement(0, "key_green"),

                    new DrawElement(0, "chest_yellow"),
                    new DrawElement(0, "chest_blue"),
                    new DrawElement(0, "chest_orange"),
                    new DrawElement(0, "chest_green"),

                    new DrawElement(0, "lock_yellow"),
                    new DrawElement(0, "lock_blue"),
                    new DrawElement(0, "lock_orange"),
                    new DrawElement(0, "lock_green"),

                    new DrawElement(0, "exit")
            };

    private DrawElement[] units =
            {
                    new DrawElement(1, "hero"),
                    new DrawElement(1, "ghost"),
                    new DrawElement(1, "questman")
            };

    private DrawElement[] decors =
            {
                    new DrawElement(-4, "&_grass"),
                    new DrawElement(-4, "&_grass_l"),
                    new DrawElement(-4, "&_grass_r"),
                    new DrawElement(-4, "&_grass_mid"),
                    new DrawElement(-4, "&_grass_mid_l"),
                    new DrawElement(-4, "&_grass_mid_r"),
                    new DrawElement(-4, "&_grass_deep"),
                    new DrawElement(-4, "&_grass_deep_l"),
                    new DrawElement(-4, "&_grass_deep_r"),
                    new DrawElement(-4, "&_grass_deep2"),

                    new DrawElement(1, "&_water_bl"),
                    new DrawElement(1, "&_water_bm"),
                    new DrawElement(1, "&_water_br"),
                    new DrawElement(1, "&_water_tl"),
                    new DrawElement(1, "&_water_tm"),
                    new DrawElement(1, "&_water_tr"),

                    new DrawElement(4, "&_parapet"),
                    new DrawElement(4, "&_parapet_l"),
                    new DrawElement(4, "&_parapet_r"),
                    new DrawElement(2, "&_parapet_lite_l"),
                    new DrawElement(2, "&_parapet_lite_r"),

                    new DrawElement(-1, "&_decor"),
                    new DrawElement(-1, "pointer"),

                    new DrawElement().setLevels(3, -2).setNames("&_wall_l", "&_bg_l"),
                    new DrawElement().setLevels(3, -2).setNames("&_wall_r", "&_bg_r"),
                    new DrawElement().setLevels(3, -2).setNames("&_wall_lb", "&_bg_l"),
                    new DrawElement().setLevels(3, -2).setNames("&_wall_rb", "&_bg_r"),


                    new DrawElement(-3, "&_bg"),
                    new DrawElement(-3, "&_bgm"),

                    new DrawElement(2, "&_bridge_l0"), new DrawElement(2, "&_bridge_l1"),
                    new DrawElement(2, "gray_bridge_l2"), new DrawElement(2, "gray_bridge_l3"),

                    new DrawElement(2, "&_bridge_r0"), new DrawElement(2, "&_bridge_r1"),
                    new DrawElement(2, "gray_bridge_r2"), new DrawElement(2, "gray_bridge_r3"),
                    new DrawElement(2, "&_bridge"),

                    new DrawElement().setLevels(3, -3).setNames("&_roof0_l", "&_bg_l"),
                    new DrawElement().setLevels(4, 3, -3).setNames("&_roof1_l", "&_wall_sl", "&_bg2_l"),
                    new DrawElement().setLevels(4).setNames("&_roof2_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof3_l", "&_bg1_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof1_l", "&_bg2_l"),

                    new DrawElement(4, "&_roof_m"),

                    new DrawElement().setLevels(3, -3).setNames("&_roof0_r", "&_bg_r"),
                    new DrawElement().setLevels(4, 3, -3).setNames("&_roof1_r", "&_wall_sr", "&_bg2_r"),
                    new DrawElement().setLevels(4).setNames("&_roof2_r"),
                    new DrawElement().setLevels(4, -2).setNames("&_roof3_r", "&_bg1_r"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof1_r", "&_bg2_r"),

                    new DrawElement(5, "&_flag"),
                    new DrawElement(-2, "&_big"),
                    new DrawElement(-2, "&_little"),
                    new DrawElement(-2, "clock"),


                    new DrawElement(-5, "&_bridge_l0"), new DrawElement(-4, "&_bridge_l1"),
                    new DrawElement(-4, "gray_bridge_l2"), new DrawElement(-5, "gray_bridge_l3"),

                    new DrawElement(-5, "&_bridge_r0"), new DrawElement(-4, "&_bridge_r1"),
                    new DrawElement(-4, "gray_bridge_r2"), new DrawElement(-5, "gray_bridge_r3"),
                    new DrawElement(-2, "&_bridge"),


                    new DrawElement(2, "&_bridge"),

                    new DrawElement().setLevels(3, -3).setNames("&_roof0_l", "&_bg_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof1_l", "&_bg0_l"),
                    new DrawElement().setLevels(4).setNames("&_roof2_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof3_l", "&_bg1_l"),
                    new DrawElement().setLevels(4, -3).setNames("gray_roof4_l", "&_bg2_l"),

                    new DrawElement(4, "&_roof_m"),

                    new DrawElement().setLevels(3, -3).setNames("&_roof0_r", "&_bg_r"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof1_r", "&_bg0_r"),
                    new DrawElement().setLevels(4).setNames("&_roof2_r"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof3_r", "&_bg1_r"),
                    new DrawElement().setLevels(4, -3).setNames("gray_roof4_r", "&_bg2_r"),

                    new DrawElement().setLevels(3).setNames("&_roof0_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof1_l", "&_bg0_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof2_l", "&_bg1_l"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof2_r", "&_bg1_r"),
                    new DrawElement().setLevels(4, -3).setNames("&_roof1_r", "&_bg0_r"),
                    new DrawElement().setLevels(3).setNames("&_roof0_r"),


                    new DrawElement(5, "&_water_bl"),
                    new DrawElement(5, "&_water_bm"),
                    new DrawElement(5, "&_water_br"),
                    new DrawElement(5, "&_water_tl"),
                    new DrawElement(5, "&_water_tm"),
                    new DrawElement(5, "&_water_tr"),
            };
}

class DrawElement {
        int[] levels;
        String[] names;

    DrawElement() {
    }

    DrawElement(int level, String str) {
        levels = new int[1];
        levels[0] = level;

        names = new String[1];
        names[0] = str;
    }

    DrawElement setLevels(int... levels) {
        this.levels = levels;
        return this;
    }

    DrawElement setNames(String... names) {
        this.names = names;
        return this;
    }
}

enum Etype { ITEM, UNIT, DECOR }