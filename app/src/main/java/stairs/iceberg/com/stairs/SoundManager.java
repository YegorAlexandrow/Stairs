package stairs.iceberg.com.stairs;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * (C) Iceberg Mobile
 * Created by User on 29.01.2018.
 */
public class SoundManager {
    private static float volume = 0.84f;
    private static SoundPool sound_pool;
    private static HashMap<String, int[]> sounds;
    private static Random random = new Random();
    private static boolean initialized = false;

    public static void init() {
        if(initialized) return;

        sounds = new HashMap<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sound_pool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(6)
                    .build();
        } else {
            sound_pool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        loadSounds("step", 6);
        loadSounds("quest", 2);
        loadSounds("blob", 2);
        loadSounds("no", 3);
        loadSounds("chest_open", 1);
        loadSounds("click", 1);
        loadSounds("lock_open", 1);
        loadSounds("win", 1);
        loadSounds("lose", 1);
        loadSounds("pick_up", 1);
        loadSounds("anvil", 1);

        initialized = true;
    }

    public static void on() {
        volume = 0.45f;
    }

    public static void off() {
        volume = 0;
    }

    public static void playSound(String sound, float v) {
        if(!initialized) init();

            sound_pool.play(sounds.get(sound)[random.nextInt(sounds.get(sound).length)], volume * v, volume * v, 0, 0, 1);
    }

    private static void loadSounds(String name, int n) {
        sounds.put(name, new int[n]);

        if(n == 1) sounds.get(name)[0] = loadSound(name + ".wav");
        else {
            for(int i=0; i<n; ++i) {
                sounds.get(name)[i] = loadSound(name + "_" + i + ".wav");
            }
        }
    }

    private static int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = MenuActivity.context.getAssets().openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return sound_pool.load(afd, 1);
    }

}
