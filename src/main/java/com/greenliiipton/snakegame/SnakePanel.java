package com.greenliiipton.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

/**
 * Swing UI for Snake. Game rules stay in SnakeGame; this class only renders
 * the current snapshot and forwards user input.
 */
public final class SnakePanel extends JPanel {
    private static final int GRID_SIZE = SnakeGame.SIZE;
    private static final int CELL_SIZE = 24;
    private static final int BOARD_SIZE = GRID_SIZE * CELL_SIZE;

    private final SnakeGame game;
    private final Board board;
    private final JLabel scoreLabel;
    private final JLabel statusLabel;
    private final Timer timer;

    public SnakePanel(SnakeGame game) {
        this.game = Objects.requireNonNull(game, "game");

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(new Color(28, 31, 38));
        setPreferredSize(new Dimension(BOARD_SIZE + 24, BOARD_SIZE + 100));
        setFocusable(true);

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);

        scoreLabel = new JLabel();
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 16f));

        statusLabel = new JLabel("Use arrows or WASD");
        statusLabel.setForeground(new Color(180, 190, 205));

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.add(scoreLabel);
        labels.add(Box.createVerticalStrut(4));
        labels.add(statusLabel);

        JButton restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.addActionListener(event -> restart());

        header.add(labels, BorderLayout.CENTER);
        header.add(restartButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        board = new Board();
        board.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        board.setFocusable(true);
        add(board, BorderLayout.CENTER);

        bindKey("UP", "moveUp", SnakeGame.Direction.UP);
        bindKey("W", "moveUpW", SnakeGame.Direction.UP);
        bindKey("DOWN", "moveDown", SnakeGame.Direction.DOWN);
        bindKey("S", "moveDownS", SnakeGame.Direction.DOWN);
        bindKey("LEFT", "moveLeft", SnakeGame.Direction.LEFT);
        bindKey("A", "moveLeftA", SnakeGame.Direction.LEFT);
        bindKey("RIGHT", "moveRight", SnakeGame.Direction.RIGHT);
        bindKey("D", "moveRightD", SnakeGame.Direction.RIGHT);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (game.snapshot().gameOver) {
                    restart();
                }
            }
        });

        timer = new Timer(150, event -> tick());
        timer.start();
        refreshStatus();

        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            board.requestFocusInWindow();
        });
    }

    private void bindKey(String keyStroke, String actionName, SnakeGame.Direction direction) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!game.snapshot().gameOver) {
                    game.turn(direction);
                }
            }
        });
    }

    private void tick() {
        if (!game.snapshot().gameOver) {
            game.step();
        }
        refreshStatus();
        board.repaint();
    }

    private void restart() {
        game.reset();
        refreshStatus();
        board.repaint();
        requestFocusInWindow();
        board.requestFocusInWindow();
    }

    private void refreshStatus() {
        SnakeGame.Snapshot state = game.snapshot();
        scoreLabel.setText("Score: " + state.score);
        statusLabel.setText(state.gameOver
                ? "Game Over — press Restart or Space"
                : "Use arrows or WASD to move");
    }

    /** Stop the Swing timer when the tool window content is disposed. */
    public void stop() {
        timer.stop();
    }

    private final class Board extends JPanel {
        private Board() {
            setBackground(new Color(18, 21, 27));
            setBorder(BorderFactory.createLineBorder(new Color(70, 78, 91)));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                SnakeGame.Snapshot state = game.snapshot();

                // Grid
                g.setColor(new Color(42, 47, 57));
                for (int i = 0; i <= GRID_SIZE; i++) {
                    int p = i * CELL_SIZE;
                    g.drawLine(p, 0, p, BOARD_SIZE);
                    g.drawLine(0, p, BOARD_SIZE, p);
                }

                // Food
                if (state.food != null) {
                    int fx = state.food.x * CELL_SIZE;
                    int fy = state.food.y * CELL_SIZE;
                    g.setColor(new Color(235, 75, 75));
                    g.fillOval(fx + 4, fy + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                }

                // Body
                for (int i = state.snake.size() - 1; i >= 1; i--) {
                    SnakeGame.Cell cell = state.snake.get(i);
                    g.setColor(new Color(65, 190, 115));
                    g.fillRoundRect(cell.x * CELL_SIZE + 2, cell.y * CELL_SIZE + 2,
                            CELL_SIZE - 4, CELL_SIZE - 4, 7, 7);
                }

                // Head
                if (!state.snake.isEmpty()) {
                    SnakeGame.Cell head = state.snake.get(0);
                    g.setColor(new Color(145, 235, 165));
                    g.fillRoundRect(head.x * CELL_SIZE + 1, head.y * CELL_SIZE + 1,
                            CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
                }

                if (state.gameOver) {
                    g.setColor(new Color(0, 0, 0, 155));
                    g.fillRect(0, 0, BOARD_SIZE, BOARD_SIZE);

                    g.setColor(Color.WHITE);
                    g.setFont(getFont().deriveFont(Font.BOLD, 30f));
                    drawCentered(g, "GAME OVER", BOARD_SIZE / 2 - 8);
                    g.setFont(getFont().deriveFont(Font.PLAIN, 16f));
                    drawCentered(g, "Final score: " + state.score, BOARD_SIZE / 2 + 22);
                    drawCentered(g, "Press Restart or Space", BOARD_SIZE / 2 + 48);
                }
            } finally {
                g.dispose();
            }
        }

        private void drawCentered(Graphics2D g, String text, int y) {
            FontMetrics metrics = g.getFontMetrics();
            int x = (BOARD_SIZE - metrics.stringWidth(text)) / 2;
            g.drawString(text, x, y);
        }
    }
}
