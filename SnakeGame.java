import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SnakeGame {
    public static final int SIZE = 20;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    // координаты одной клетки поля.
    public static class Cell {
        public final int x;
        public final int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // данные, которые может прочитать интерфейс.
    public static class Snapshot {
        public final List<Cell> snake;
        public final Cell food;
        public final int score;
        public final boolean gameOver;

        public Snapshot(List<Cell> snake, Cell food, int score, boolean gameOver) {
            this.snake = snake;
            this.food = food;
            this.score = score;
            this.gameOver = gameOver;
        }
    }

    private final List<Cell> snake = new ArrayList<>();
    private final Random random;
    private Direction direction;
    private Direction nextDirection;
    private Cell food;
    private int score;
    private boolean gameOver;

    public SnakeGame() {
        this(new Random());
    }

    // позволяет подставить генератор случайных чисел в тестах.
    SnakeGame(Random random) {
        this.random = random;
        reset();
    }

    public void reset() {
        snake.clear();
        snake.add(new Cell(10, 10)); // голова
        snake.add(new Cell(9, 10));
        snake.add(new Cell(8, 10)); // хвост
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;
        gameOver = false;
        placeFood();
    }

    public void turn(Direction newDirection) {
        if (gameOver || newDirection == null)
            return;

        // нельзя развернуться на 180 градусов.
        if (direction == Direction.UP && newDirection == Direction.DOWN)
            return;
        if (direction == Direction.DOWN && newDirection == Direction.UP)
            return;
        if (direction == Direction.LEFT && newDirection == Direction.RIGHT)
            return;
        if (direction == Direction.RIGHT && newDirection == Direction.LEFT)
            return;

        nextDirection = newDirection;
    }

    public void step() {
        if (gameOver)
            return;

        direction = nextDirection;
        int x = snake.get(0).x;
        int y = snake.get(0).y;

        switch (direction) {
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        // Столкновение со стеной.
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            gameOver = true;
            return;
        }

        boolean ate = food != null && x == food.x && y == food.y;

        // если еду не съели, хвост сейчас уйдёт с последней клетки.
        int cellsToCheck = snake.size() - (ate ? 0 : 1);
        for (int i = 0; i < cellsToCheck; i++) {
            Cell part = snake.get(i);
            if (part.x == x && part.y == y) {
                gameOver = true;
                return;
            }
        }

        snake.add(0, new Cell(x, y)); // новая голова

        if (ate) {
            score++;
            if (snake.size() == SIZE * SIZE) {
                food = null; // всё поле заполнено
                gameOver = true;
            } else {
                placeFood();
            }
        } else {
            snake.remove(snake.size() - 1); // обычное движение: убрали хвост
        }
    }

    public Snapshot snapshot() {
        return new Snapshot(
                Collections.unmodifiableList(new ArrayList<>(snake)),
                food, score, gameOver);
    }

    private void placeFood() {
        while (true) {
            Cell candidate = new Cell(random.nextInt(SIZE), random.nextInt(SIZE));
            boolean occupied = false;

            for (Cell part : snake) {
                if (part.x == candidate.x && part.y == candidate.y) {
                    occupied = true;
                    break;
                }
            }

            if (!occupied) {
                food = candidate;
                return;
            }
        }
    }
}
