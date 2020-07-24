package stairs.iceberg.com.stairs;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by User on 30.12.2017.
 */
public class Unit {
    protected int name;
    protected Level level;
    protected int x = 0, y = 0;
    protected boolean is_alive = true;

    public Unit(int name, int x, int y, Level level) {
        this.name = name;
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() {
        return is_alive;
    }

    public void kill() {
        is_alive = false;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public Point move(boolean[][] maze, Unit hero) { return new Point(0, 0); }

    public boolean onIntersectHero(Unit hero) { return false; }

    public void onNearby(Unit hero) { }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void onMoveFinished() {

    }
}

class Ghost extends Unit {
    private boolean upstairs;

    public Ghost(int x, int y, Level level) {
        super(Res.ghost, x, y, level);
        upstairs = Math.random() > 0.5;
    }

    private boolean hasAnyStairs(boolean[][] maze, int x, int y) {
        if(x<0 || y<0 || x>=maze.length/2 || y>=maze.length/2) return false;


        return  (y*2+1 < maze.length && maze[y*2+1+1][x*2+1]) || (y*2-1+1>0 && maze[y*2+1-1][x*2+1]) ||
                (x*2+1+1 < maze[0].length && maze[y*2+1][x*2+1+1]) || (x*2-1+1>0 && maze[y*2+1][x*2+1-1]);
    }

    public Point move(boolean[][] maze, Unit hero) {

        if(upstairs) {
            if(!hasAnyStairs(maze, x+1, y)) upstairs = false;
        }

        if(!upstairs) {
            if(!hasAnyStairs(maze, x-1, y)) upstairs = true;
        }

        return new Point(upstairs ? 1 : -1, 0);
    }

    public boolean onIntersectHero(Unit hero) {
        level.getHero().kill();
        level.lose();
        return true;
    }

}

class Fish extends Unit {
    private boolean right;

    public Fish(int x, int y, Level level) {
        super(Res.fish, x, y, level);
        right = Math.random() > 0.5;
    }

    private boolean hasAnyStairs(boolean[][] maze, int x, int y) {
        return  (y*2+1 < maze.length && maze[y*2+1+1][x*2+1]) || (y*2-1+1>0 && maze[y*2+1-1][x*2+1]) ||
                (x*2+1+1 < maze[0].length && maze[y*2+1][x*2+1+1]) || (x*2-1+1>0 && maze[y*2+1][x*2+1-1]);
    }

    public Point move(boolean[][] maze, Unit hero) {

        if(right) {
            if(hasAnyStairs(maze, x+1, y+1)) {
                return new Point(1, 1);
            } else right = false;
        }

        if(!right) {
            if(hasAnyStairs(maze, x-1, y-1)) {
                return new Point(-1, -1);
            } else right = true;
        }

        return new Point(1, 1);
    }

    public boolean onIntersectHero(Unit hero) {
        level.getHero().kill();
        level.lose();
        return true;
    }

}

class Anvil extends Unit {

    public Anvil(int x, int y, Level level) {
        super(Res.anvil, x, y, level);
    }

    public Point move(boolean[][] maze, Unit hero) {

        return new Point(0, 0);
    }

    public boolean onIntersectHero(Unit hero) {
        level.getRenderer().moveUnit(this, x-1, y+1);
        return false;
    }

    public void onMoveFinished() {
        for(int i=0; i<level.getUnits().size(); i++) {
            Unit u = level.getUnits().get(i);
            if(u != this && u.x == x && u.y == y) {
                boolean b = true;

                if(u instanceof Ghost || u instanceof Fish) {
                    u.kill();
                    level.getUnits().remove(u);
                }
                else if(u instanceof Lock) {
                    level.getUnits().remove(u);
                }
                else if(u instanceof Anvil) {
                    level.getRenderer().moveUnit(u, u.x-1, u.y+1);
                }
                else if(u instanceof Chest) {
                    level.getUnits().add(new Item(((Chest)u).getContent(), u.x, u.y, level, true));
                    level.getUnits().remove(u);
                }
                else if(u instanceof Questman) {
                    u.kill();
                    level.getUnits().add(new Item(((Questman)u).getContent(), u.x, u.y, level, true));
                    level.getUnits().remove(u);
                    ((Questman) u).sellOut();
                    ((Questman) u).hideQuestBalloon();
                } else b = false;
                if(b) SoundManager.playSound("anvil", 0.5f);
            }
        }
    }

}

class Lift extends Unit {
    private int dir = 0;

    public Lift(int x, int y, Level level) {
        super(Res.lift, x, y, level);
    }

    public Point move(boolean[][] maze, Unit hero) {
        return new Point(0, 0);
    }

    private boolean hasAnyStairs(boolean[][] maze, int x, int y) {
        return  (y*2+1 < maze.length && maze[y*2+1+1][x*2+1]) || (y*2-1+1>0 && maze[y*2+1-1][x*2+1]) ||
                (x*2+1+1 < maze[0].length && maze[y*2+1][x*2+1+1]) || (x*2-1+1>0 && maze[y*2+1][x*2+1-1]);
    }

    public boolean onIntersectHero(Unit hero) {
        if(dir == 0) {
            if(!hasAnyStairs(level.getBaseMaze(), x-1, y-1)) dir = -1;
            else dir = 1;

            level.getRenderer().moveUnit(this, x+dir, y+dir);
            level.getRenderer().moveUnit(hero, x+dir, y+dir);
            level.getRenderer().updateMaze(level.maze[y+dir][x+dir],
                    level.getWidth(), level.getHeight());
            level.getRenderer().correctTabsNotSoFast(hero);
        } else {
            if(hasAnyStairs(level.getBaseMaze(), x, y)) dir = 0;
            else {
                level.getRenderer().moveUnit(this, x+dir, y+dir);
                level.getRenderer().moveUnit(hero, x+dir, y+dir);
                level.getRenderer().updateMaze(level.maze[y+dir][x+dir],
                        level.getWidth(), level.getHeight());
                level.getRenderer().correctTabsNotSoFast(hero);
            }
        }


        return false;
    }

    public void onMoveFinished() {

    }

}

class LiftVer extends Unit {
    private int dir = 0;

    public LiftVer(int x, int y, Level level) {
        super(Res.lift_ver, x, y, level);
    }

    public Point move(boolean[][] maze, Unit hero) {
        return new Point(0, 0);
    }

    private boolean hasAnyStairs(boolean[][] maze, int x, int y) {
        return  (y*2+1 < maze.length && maze[y*2+1+1][x*2+1]) || (y*2-1+1>0 && maze[y*2+1-1][x*2+1]) ||
                (x*2+1+1 < maze[0].length && maze[y*2+1][x*2+1+1]) || (x*2-1+1>0 && maze[y*2+1][x*2+1-1]);
    }

    public boolean onIntersectHero(Unit hero) {
        if(dir == 0) {
            if(!hasAnyStairs(level.getBaseMaze(), x+1, y-1)) dir = -1;
            else dir = 1;

            level.getRenderer().moveUnit(this, x-dir, y+dir);
            level.getRenderer().moveUnit(hero, x-dir, y+dir);
            level.getRenderer().updateMaze(level.maze[y+dir][x-dir],
                    level.getWidth(), level.getHeight());
            level.getRenderer().correctTabsNotSoFast(hero);
        } else {
            if(hasAnyStairs(level.getBaseMaze(), x, y)) dir = 0;
            else {
                level.getRenderer().moveUnit(this, x-dir, y+dir);
                level.getRenderer().moveUnit(hero, x-dir, y+dir);
                level.getRenderer().updateMaze(level.maze[y+dir][x-dir],
                        level.getWidth(), level.getHeight());
                level.getRenderer().correctTabsNotSoFast(hero);
            }
        }


        return false;
    }

    public void onMoveFinished() {

    }

}

class Item extends Unit {
    private boolean pickable = true;

    public Item(int name, int x, int y, Level level, boolean pickable) {
        super(name, x, y, level);

        this.pickable = pickable;
    }

    public boolean onIntersectHero(Unit hero) { return false; }

    public boolean isPickable() {
        return pickable;
    }

    public void setPickable(boolean pickable) {
        this.pickable = pickable;
    }
}

class Chest extends Item {
    private int content, key;

    public Chest(int name, int x, int y, Level level, int content, int key) {
        super(name, x, y, level, false);

        this.content = content;
        this.key = key;
    }

    public int getContent() {
        return content;
    }

    public boolean onIntersectHero(Unit hero) {
        if(level.hasItem(key)) {
            SoundManager.playSound("chest_open", 0.5f);
            level.removeItem(key);
            level.pickUp(content);
            level.getUnits().remove(this);
        }
        return false;
    }
}

class Questman extends Unit {
    private int content, key;
    private boolean sold_out = false;
    private QuestBalloon balloon;

    public Questman(int name, int x, int y, Level level, int content, int key) {
        super(name, x, y, level);

        this.content = content;
        this.key = key;
        balloon = new QuestBalloon(key, content, x, y, level.getRenderer());
        level.getRenderer().addQuestBalloon(balloon);
    }

    public QuestBalloon getBalloon() {
        return balloon;
    }

    public int getContent() {
        return content;
    }

    public int getKey() {
        return key;
    }

    public boolean onIntersectHero(Unit hero) {
        if(sold_out) return false;

        if(level.hasItem(key)) {
            level.removeItem(key);
            level.pickUp(content);
            sold_out = true;
            balloon.target_alpha = 0;
            SoundManager.playSound("quest", 0.5f);
        } else {
            balloon.target_alpha = 250;
        }

        return false;
    }

    public void onNearby(Unit hero) {
        if(!sold_out) balloon.target_alpha = 250;
    }

    public void hideQuestBalloon() {
        balloon.target_alpha = 0;
    }

    public void sellOut() {
        sold_out = true;
    }
}

class Lock extends Item {
    private int key;

    public Lock(int name, int x, int y, Level level, int key) {
        super(name, x, y, level, false);

        this.key = key;
    }

    public boolean onIntersectHero(Unit hero) {
        if(level.hasItem(key)) {
            SoundManager.playSound("lock_open", 0.5f);
            level.removeItem(key);
            level.getUnits().remove(this);
            return false;
        }

        return false;
    }

    public int getKey() {
        return key;
    }
}

class Exit extends Item {

    public Exit(int x, int y, Level level) {
        super(Res.exit, x, y, level, false);
    }

    public boolean onIntersectHero(Unit hero) {
        level.win();
        return true;
    }
}