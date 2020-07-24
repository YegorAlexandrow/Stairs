package stairs.iceberg.com.stairs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import java.io.IOException;
import java.util.ArrayList;

/**
 * (C) Iceberg Mobile
 * Created by User on 30.12.2017.
 */
public class Level {
    private Renderer renderer;
    boolean[][][][] maze;
    boolean[][] base_maze;
    private int[] items = new int[4];
    private ArrayList<Unit> units = new ArrayList<>();
    private ArrayList<Point> empties;
    private Unit hero;
    private int width, height;
    public static String level_to_start = "level_0.txt";
    public static String current_level_num = "0";


    private LevelReader reader;

    public Level(Renderer renderer) {
        this.renderer = renderer;
        renderer.setLevel(this);
        reader = new LevelReader(this);

        loadLevel(level_to_start);
}

    public void loadLevel(String name) {
        units.clear();

        for(int i=0; i<4; i++) {
            items[i] = -1;
            renderer.pickDown(i);
        }
        renderer.clearDecors();
        renderer.fixClouds();

        try {
            reader.loadLevelFromRow(name);
        } catch (IOException e) {
            try {
                reader.loadLevelFromAsset(name);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        renderer.updateMaze(getBaseMaze(), width, height);
        renderer.updateMaze(maze[hero.getY()][hero.getX()], width, height);

        renderer.correctTabsFast(hero);

        checkIntersection();
    }

    public void createMaze(int w, int h, ArrayList<Point> empties, ArrayList<Point> frees) {
        maze = new boolean[w+h/2][w+h/2][w+h/2][w+h/2];
        width = w;
        height = h;

        base_maze = MazeGenerator.generate(w, h, hero.x, hero.y, empties, frees);

        //Вывод
        for (int i = 0; i < base_maze.length; i++) {
            for (int j = 0; j < base_maze[0].length; j++) {
                System.out.print(base_maze[i][j] ? "  " : "# ");
            }
            System.out.println();
        }

         generateSubmazes(empties);

    }

    public void generateSubmazes(ArrayList<Point> empties) {
        this.empties = empties;

        for(int y=0; y<width+height/2; y++) {
            for(int x=0; x<width+height/2; x++) {
                maze[y][x] = MazeGenerator.generate(width, height, hero.x, hero.y, empties, null);
                maze[y][x][y*2+1][x*2+1+1] = base_maze[y*2+1][x*2+1+1];
                maze[y][x][y*2+1][x*2+1-1] = base_maze[y*2+1][x*2+1-1];
                maze[y][x][y*2+1+1][x*2+1] = base_maze[y*2+1+1][x*2+1];
                maze[y][x][y*2+1-1][x*2+1] = base_maze[y*2+1-1][x*2+1];
            }
        }
    }

    public boolean mayMove(int x, int y, int dx, int dy) {
        if(!hero.isAlive()) return false;

        for(Unit u : units) {
            if (u.getClass() == Lock.class)
                if (u.getX() == x + dx && u.getY() == y + dy) {
                    if (hero.getX() == x && hero.getY() == y && hasItem(((Lock) u).getKey()))
                        return true;
                    return false;
                }
        }

        return maze[y][x][y*2+1+dy][x*2+1+dx];
    }

    public void move(int x, int y) {
        if(mayMove(hero.getX(), hero.getY(), x, y)) {
            renderer.moveUnit(hero, hero.getX() + x, hero.getY() + y);
            renderer.updateMaze(maze[hero.getY() + y][hero.getX() + x], width, height);
            for(Unit u :units) {
                if(u != hero) {
                    Point p = u.move(maze[hero.getY() + y][hero.getX() + x], hero);
                    if(!p.equals(0, 0)) renderer.moveUnit(u, u.getX()+p.x, u.getY()+p.y);
                }
            }
            SoundManager.playSound("step", 0.25f);
        } else SoundManager.playSound("no", 0.16f);
    }

    public void checkIntersection() {

        for(int i=0; i<units.size(); i++) {
            Unit u = units.get(i);
            if(u!= null && u != hero) {
                boolean no_intersection = true;
                if(u.getX() == hero.getX() && u.getY() == hero.getY()) {
                    if(u.onIntersectHero(hero)) return;
                    if(u.getClass() == Item.class && ((Item) u).isPickable()) pickUp((Item) u);
                    no_intersection = false;
                } else if(Math.abs(u.getX() - hero.getX()) + Math.abs(u.getY() - hero.getY()) <= 2) {
                    u.onNearby(hero);
                    no_intersection = false;
                }
                if(no_intersection && u instanceof Questman) ((Questman)u).hideQuestBalloon();
            }
        }

    }

    public void setHero(int x, int y) {
        hero = new Unit(Res.hero, x, y, this);
        units.add(hero);
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public boolean hasItem(int key) {
        for(int i=0; i<4; i++) if(key == items[i]) return true;

        return false;
    }

    public void removeItem(int key) {
        for(int i=0; i<4; i++) {
            if (key == items[i]) {
                items[i] = -1;
                renderer.pickDown(i);
                return;
            }
        }
    }

    public void pickUp(int content) {
        for(int i=0; i<4; i++) if(items[i] <= 0)  {
            items[i] = content;
            renderer.pickUp(i, Res.getItemId(content));
            return;
        }
    }

    public int getItemAt(int i) {
        return items[i];
    }

    public void pickUp(Item item) {
        for(int i=0; i<4; i++) {
            if(items[i] <= 0) {
                items[i] = item.getName();
                units.remove(item);
                renderer.pickUp(i, Res.getItemId(item.getName()));
                return;
            }
        }
    }

    public boolean[][] getBaseMaze() {
        return base_maze;
    }

    public boolean[][] getCurrentMaze() {
        return  maze[hero.getY()][hero.getX()];
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void win() {

        SharedPreferences database = renderer.getContext().getSharedPreferences("database", Context.MODE_PRIVATE);

        if(database.getInt("completed", 0) == Integer.parseInt(current_level_num)) {
            SharedPreferences.Editor editor = database.edit();
            editor.putInt("completed", database.getInt("completed", 0) + 1);
            editor.apply();
        }

        if(Integer.parseInt(current_level_num) < LevelFragment.real_level_count-1) {
            current_level_num = "" + (Integer.parseInt(current_level_num)+1);
            level_to_start = "level_" + Integer.parseInt(current_level_num) + ".txt";
            renderer.gotoNextLevel(Integer.parseInt(current_level_num));
        } else {
            ((Activity)renderer.getContext()).onBackPressed();
        }

        SoundManager.playSound("win", 0.18f);
    }

    public void lose() {
        Main.main.splashIn(Integer.parseInt(current_level_num), R.string.gamesplash_lose);
        SoundManager.playSound("lose", 0.68f);
    }

    public LevelReader getLevelReader() {
        return reader;
    }

    public ArrayList<Point> getEmpties() {
        return empties;
    }

    public Unit getHero() {
        return hero;
    }

    public void thread() {
        renderer.showThread();
    }
}
