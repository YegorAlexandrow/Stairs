package stairs.iceberg.com.stairs;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by User on 30.12.2017.
 */
public class LevelReader {
    private Level level;
    private Scanner in;

    public LevelReader(Level level) {
        this.level = level;
    }

    private void decor(String name, int lvl, int x, int y) {
        switch (name) {
            case "&_flag":
                level.getRenderer().addDecor(new FlagDecor(Res.getId(name), x, y));
                break;

            case "&_big":
                level.getRenderer().addDecor(new BigDecor(Res.getId(name), x, y));
                break;

            case "&_little":
                level.getRenderer().addDecor(new LittleDecor(Res.getId(name), x, y));
                break;

            case "&_decor":
                level.getRenderer().addDecor(new StairDecor(Res.getId(name), x, y));
                break;

            case "clock":
                level.getRenderer().addDecor(new ClockDecor(x, y));
                break;

            case "&_water_tl": case "&_water_tm": case "&_water_tr":
            case "&_water_bl": case "&_water_bm": case "&_water_br":
                level.getRenderer().addDecor(new WaterDecor(Res.getId(name), lvl, x, y));
                break;

            case "pointer":
                level.getRenderer().addDecor(new PointerDecor(Res.getId(in.next()), x, y, in.nextInt(), in.nextInt()));
                break;

            default:
                level.getRenderer().addDecor(new StaticDecor(Res.getId(name), lvl, x, y));
                break;
        }

    }

    private void unit(String name , int x, int y) {
        switch (name) {
            case "ghost":
                level.getUnits().add(new Ghost(x, y, level));
                break;

            case "anvil":
                level.getUnits().add(new Anvil(x, y, level));
                break;

            case "lift":
                level.getUnits().add(new Lift(x, y, level));
                break;

            case "lift_ver":
                level.getUnits().add(new LiftVer(x, y, level));
                break;

            case "fish":
                level.getUnits().add(new Fish(x, y, level));
                break;

            case "hero":
                level.setHero(x, y);
                break;

            case "questman":
                level.getUnits().add(new Questman(Res.getId(in.next()), x, y, level, Res.getId(in.next()), Res.getId(in.next())));
                break;

            default:
                level.getUnits().add(new Unit(Res.getId(name), x, y, level));
                break;
        }
    }

    private void item(String name, int x, int y, boolean pickable) {
        switch (name) {
            case "chest_yellow":
            case "chest_blue":
            case "chest_orange":
            case "chest_green":
                level.getUnits().add(new Chest(Res.getId(name), x, y, level, Res.getId(in.next()), Res.getId("key" + name.substring(5))));
                break;

            case "lock_yellow":
            case "lock_blue":
            case "lock_orange":
            case "lock_green":
                level.getUnits().add(new Lock(Res.getId(name), x, y, level, Res.getId("key" + name.substring(4))));
                //frees.add(new Point(x, y));
                break;

            case "exit":
                level.getUnits().add(new Exit(x, y, level));
                break;

            default:
                level.getUnits().add(new Item(Res.getId(name), x, y, level, pickable));
                break;
        }
    }

    public void loadLevelFromRow(String name) throws IOException {
        String str;

        InputStreamReader isr;
            InputStream inputstream = Main.main.openFileInput(name);
            isr = new InputStreamReader(inputstream);
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder buffer = new StringBuilder();

            while ((str = reader.readLine()) != null) {
                buffer.append(str).append("\n");
            }
            inputstream.close();
            in = new Scanner(buffer.toString());
            loadLevel();
    }

    public void loadLevelFromAsset(String name) throws IOException {
        in = new Scanner(new InputStreamReader(Main.main.getAssets().open(name)));
        loadLevel();
    }

    private void readBase() {
        int size = in.nextInt();

        level.base_maze = new boolean[size][size];
        level.maze = new boolean[size][size][size][size];

        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                level.base_maze[i][j] = "#".equals(in.next());
            }
        }
    }

    private void loadLevel() {
        ArrayList<Point> empties = new ArrayList<>();
        ArrayList<Point> frees = new ArrayList<>();

        int x, y;
        boolean base_created = false;

            String s;
            while (in.hasNext()) {
                s = in.next();

                switch(s) {
                    case "base":
                        readBase();
                        base_created = true;
                        break;

                    case "theme":
                        Renderer.setTheme(in.next());
                        break;

                    case "inventory":
                        level.pickUp(Res.getId(in.next()));
                        break;

                    case "num":
                        Level.current_level_num = in.next();
                        Log.d("", "@@ num " + Level.current_level_num);
                        break;

                    case "maze":
                        level.getRenderer().setMazeTabX(in.nextInt());
                        level.getRenderer().setMazeTabY(in.nextInt());
                        if(base_created) {
                            level.setWidth(in.nextInt());
                            level.setHeight(in.nextInt());
                            level.generateSubmazes(empties);
                        }
                        else level.createMaze(in.nextInt(), in.nextInt(), empties, frees);
                        break;

                    case "empty":
                    case "empty1":
                        empties.add(new Point(in.nextInt(), in.nextInt()));
                        break;

                    case "nonempty":
                        x = in.nextInt(); y = in.nextInt();
                        for(Point p : empties) {
                            if(p.x == x && p.y == y) {
                                empties.remove(p);
                                break;
                            }
                        }
                        break;

                    case "background":
                        level.getRenderer().setBackgroundColor(Color.parseColor(in.next()));
                        break;

                    case "hero":
                        level.setHero(in.nextInt(), in.nextInt());
                        break;

                    case "item":
                        item(in.next(), in.nextInt(), in.nextInt(), "pickable".equals(in.next()));
                        break;

                    case "decor":
                        decor(in.next(), in.nextInt(), in.nextInt(), in.nextInt());
                        break;

                    case "unit":
                        unit(in.next(), in.nextInt(), in.nextInt());
                        break;

                    case "decorgrid":
                        String id = in.next();
                        int lvl = in.nextInt();
                        x = in.nextInt(); y = in.nextInt();
                        int w = in.nextInt(), h = in.nextInt();

                        for(int i=y+h; i>=y; i--) {
                            for(int j=x; j<x+w; j++) {
                                decor(id, lvl, j, i);
                            }
                        }
                        break;

                    default:
                        Log.w("", "@@ Unknown symbol '" + s + "'");
                        break;

                }
            }
        in.close();
    }

    private String baseMazeToText() {
        String txt = "";

        txt += "base " + level.getBaseMaze().length +"\n";

        for(int i=0; i<level.getBaseMaze().length; i++) {
            for(int j=0; j<level.getBaseMaze()[0].length; j++) {
                txt += level.getBaseMaze()[i][j] ? "# " : "` ";
            }
            txt += "\n";
        }

        return txt;
    }

    public void saveLevel() {
        String txt = "";

        txt += "background #EBF9FA\n";
        txt += "num " + Level.current_level_num + "\n";
        txt += "theme " + Renderer.getTheme() + "\n";

        txt += baseMazeToText();

        for(int i=0; i<4; i++) {
            if(level.getItemAt(i) > 0) {
                txt += "inventory " + Res.str(level.getItemAt(i)) + "\n";
            }
        }

        for(Decor d : level.getRenderer().getDecors()) {
            txt += "decor " + Res.str(d.getName()) + " " + d.getLevel() + " " + d.getX() + " " + d.getY();
            if(d.getClass() == PointerDecor.class) {
                txt +=  " " + Res.str(((PointerDecor) d).id) +
                        " " + ((PointerDecor) d).dx +
                        " " + ((PointerDecor) d).dy;
            }

            txt += "\n";
        }

        for(Unit u : level.getUnits()) {

            if(u.getName() == Res.hero) {
                txt += "hero " + u.getX() + " " + u.getY();

            } else if(u instanceof Item) {
                txt += "item " + Res.str(u.getName()) +
                        " " + u.getX() +
                        " " + u.getY() +
                        " " + (((Item) u).isPickable() ? "pickable" : "unpickable");

                if(u instanceof Chest) txt += " " + Res.str(((Chest)u).getContent());

            } else {
                if(u instanceof Questman) txt +=    "unit questman " +
                                                    u.getX() + " " +
                                                    u.getY() + " " +
                                                    Res.str(u.getName()) + " " +
                                                    Res.str(((Questman) u).getContent()) + " " +
                                                    Res.str(((Questman) u).getKey());

                else txt += "unit " + Res.str(u.getName()) +
                            " " + u.getX() +
                            " " + u.getY();
            }

            txt += "\n";
        }

        for(Point p: level.getEmpties()) txt += "empty " + p.x + " " + p.y + "\n";

        txt += "maze " + level.getRenderer().getMazeTabX() + " " + level.getRenderer().getMazeTabY() + " " + level.getWidth() + " " + level.getHeight();

        OutputStream outputstream;
        try {
            outputstream = Main.main.openFileOutput("autosave.txt", 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputstream);
            osw.write(txt);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
