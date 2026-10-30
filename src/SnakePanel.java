import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakePanel extends JPanel {

    private final SnakeGame game;
    private final int grid = 20;
    private final int cell = 24;
    private int pause = 0;

    public SnakePanel(SnakeGame game) {
        this.game = game;

        setPreferredSize(new Dimension(grid * cell, grid * cell));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        setLayout(null);

        JButton button = new JButton("Заново");
        button.setBounds(370, 5, 100, 28);
        button.setFocusable(false);
        button.addActionListener(e -> {
            game.reset();
            pause = 0;
            requestFocusInWindow();
        });
        add(button);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                    game.turn(Direction.UP);
                }
                if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                    game.turn(Direction.DOWN);
                }
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                    game.turn(Direction.LEFT);
                }
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                    game.turn(Direction.RIGHT);
                }
            }
        });

        Timer timer = new Timer(200, e -> {
            if (game.snapshot().gameOver()) {
                pause = pause + 1;
                if (pause >= 10) {
                    game.reset();
                    pause = 0;
                }
            } else {
                game.step();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Snapshot state = game.snapshot();

        g.setColor(Color.GRAY);
        for (int i = 0; i <= grid; i++) {
            g.drawLine(i * cell, 0, i * cell, grid * cell);
            g.drawLine(0, i * cell, grid * cell, i * cell);
        }

        Cell food = state.food();
        if (food != null) {
            int fx = food.x() * cell;
            int fy = food.y() * cell;

            g.setColor(Color.RED);
            g.fillOval(fx + 2, fy + 4, cell - 4, cell - 4);

            g.setColor(new Color(139, 69, 19));
            g.fillRect(fx + 11, fy + 1, 3, 5);

            g.setColor(Color.GREEN);
            g.fillOval(fx + 14, fy + 1, 7, 5);
        }

        g.setColor(Color.GREEN);
        for (Cell c : state.snake()) {
            g.fillRect(c.x() * cell, c.y() * cell, cell, cell);
        }

        Cell head = state.snake().get(0);
        g.setColor(Color.YELLOW);
        g.fillRect(head.x() * cell, head.y() * cell, cell, cell);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Счёт: " + state.score(), 10, 22);

        if (state.gameOver()) {
            g.setColor(new Color(255, 0, 0, 110));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", 130, 220);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Итоговый счёт: " + state.score(), 130, 260);
        }
    }
}