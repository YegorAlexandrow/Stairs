package stairs.iceberg.com.stairs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * (C) Iceberg Mobile
 * Created by User on 31.12.2017.
 */
public class Sprite {
    protected Bitmap[][] sprite;
    private float width = 1, height = 1;

    public Sprite(int width, int height, float w, float h, Resources res, int id) {
        init(width, height, w, h, res, id);
    }

    public Sprite(int width, int height, float size, Resources res, int id) {
        init(width, height, size, size, res, id);
    }

    public Sprite(int width, int height, Resources res, int id) {
        sprite = new Bitmap[height][width];
        this.width = width;
        this.height = height;

        Bitmap b = BitmapFactory.decodeResource(res, id);
        int size_x = b.getWidth() / width;
        int size_y = b.getHeight() / height;

        for(int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                sprite[y][x] = Bitmap.createBitmap(b, x * size_x, y * size_y, size_x, size_y);
            }
        }
    }

    public Sprite(float size, Resources res, int id) {
        sprite = new Bitmap[1][1];

        sprite[0][0] =
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, id), (int) size, (int) size, true);
    }

    private void init(int width, int height, float size_w, float size_h, Resources res, int id) {
        sprite = new Bitmap[height][width];
        this.width = width;
        this.height = height;

        Bitmap b = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, id),
                (int) (width*size_w), (int) (height*size_h), true);
        int size_x = b.getWidth() / width;
        int size_y = b.getHeight() / height;

        for(int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                sprite[y][x] = Bitmap.createBitmap(b, x * size_x, y * size_y, size_x, size_y);
            }
        }
    }

    public Bitmap getBitmap(int y, int x) {
        if(y>=sprite.length || x >=sprite[0].length) return sprite[0][0];
        return sprite[y][x];
    }
    public Bitmap[][] getBitmaps() {
        return sprite;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}