package stairs.iceberg.com.stairs;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

/**
 * (C) Iseberg Mobile
 * Created by User on 30.12.2017.
 */
public class MazeGenerator {
    private static Random random = new Random();

    private static class Cell {
        int x;
        int y;
        boolean left = false;
        boolean right = false;
        boolean top = false;
        boolean bottom = false;
        boolean visited;

        public Cell(int x, int y, boolean visited) {
            this.x = x;
            this.y = y;
            this.visited = visited;
        }
    }


    public static boolean[][] generate(int w, int h, int hero_x, int hero_y, ArrayList<Point> empties, ArrayList<Point> frees) {
        int width = w + h/2, height = w + h/2;

        Cell[][] labyrinth = new Cell[width][height];
        boolean[][] maze = new boolean[height*2+1][width*2+1];

        //заполняем начальные данные для ячеек
        int l = h/2, r = h/2, dl = -1, dr = +1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                labyrinth[x][y] = new Cell(x, y, x<l || x>r);
                if(x<l || x>r) maze[y*2+1][x*2+1] = false;
            }


            l += dl;
            r += dr;

            if(l==0) dl *= -1;
            if(r==width-1) dr *= -1;

            System.out.println();
        }

        if(empties != null) {
            for (Point p : empties) {
                if (p.x < width && p.x >= 0 && p.y >= 0 && p.y < height) {
                    labyrinth[p.x][p.y].visited = true;
                }
            }
        }

        if(frees != null) {
            for(Point p : frees) {
                labyrinth[p.x][p.y].visited = true;
            }
        }

        //Выбираем первую ячейку откуда начнем движение
        for(int startY = 1; startY < height; startY+=2)
        for(int startX = 1; startX < width; startX+=2) {

            labyrinth[startX][startY].visited = true;

            //Заносим нашу ячейке в path и начинаем строить путь
            ArrayList<Cell> path = new ArrayList<>();
            path.add(labyrinth[startX][startY]);

            while (!path.isEmpty()) {
                Cell _cell = path.get(path.size() - 1);

                //смотрим варианты, в какую сторону можно пойти
                ArrayList<Cell> nextStep = new ArrayList<>();
                if (_cell.x > 0 && (!labyrinth[_cell.x - 1][_cell.y].visited))
                    nextStep.add(labyrinth[_cell.x - 1][_cell.y]);

                if (_cell.x < width - 1 && (!labyrinth[_cell.x + 1][_cell.y].visited))
                    nextStep.add(labyrinth[_cell.x + 1][_cell.y]);

                if (_cell.y > 0 && (!labyrinth[_cell.x][_cell.y - 1].visited))
                    nextStep.add(labyrinth[_cell.x][_cell.y - 1]);

                if (_cell.y < height - 1 && (!labyrinth[_cell.x][_cell.y + 1].visited))
                    nextStep.add(labyrinth[_cell.x][_cell.y + 1]);

                if (!nextStep.isEmpty()) {
                    //выбираем сторону из возможных вариантов
                    Cell next = nextStep.get(random.nextInt(nextStep.size()));

                    //Открываем сторону, в которую пошли на ячейках
                    if (next.x != _cell.x) {
                        if (_cell.x - next.x > 0) {
                            labyrinth[_cell.x][_cell.y].left = true;
                            labyrinth[next.x][next.y].right = true;
                        } else {
                            labyrinth[_cell.x][_cell.y].right = true;
                            labyrinth[next.x][next.y].left = true;
                        }
                    }
                    if (next.y != _cell.y) {
                        if (_cell.y - next.y > 0) {
                            labyrinth[_cell.x][_cell.y].top = true;
                            labyrinth[next.x][next.y].bottom = true;
                        } else {
                            labyrinth[_cell.x][_cell.y].bottom = true;
                            labyrinth[next.x][next.y].top = true;
                        }
                    }

                    labyrinth[next.x][next.y].visited = true;
                    path.add(next);

                } else {
                    //если пойти никуда нельзя, возвращаемся к предыдущему узлу
                    path.remove(path.size() - 1);
                }
            }

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    maze[i * 2 + 1][j * 2 + 1] = true;
                    if (labyrinth[j][i].top) maze[i * 2 + 1 - 1][j * 2 + 1] = true;
                    if (labyrinth[j][i].bottom) maze[i * 2 + 1 + 1][j * 2 + 1] = true;
                    if (labyrinth[j][i].left) maze[i * 2 + 1][j * 2 + 1 - 1] = true;
                    if (labyrinth[j][i].right) maze[i * 2 + 1][j * 2 + 1 + 1] = true;
                }
            }

            if (frees != null) {
                for (Point p : frees) {
                    maze[p.y * 2 + 1 - 1][p.x * 2 + 1] =
                            maze[p.y * 2 + 1 + 1][p.x * 2 + 1] =
                                    maze[p.y * 2 + 1][p.x * 2 + 1 - 1] =
                                            maze[p.y * 2 + 1][p.x * 2 + 1 + 1] = true;
                }
            }
        }


        return maze;
    }
}