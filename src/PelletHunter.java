import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PelletHunter {

    // the square grid size 16x16
    private static final int gridSize = 8;
    private static final int squareWidth = 100;

    // the frame dimensions
    private static final int width = gridSize * squareWidth, height = gridSize * squareWidth;

    // the frame and panel
    private static JFrame frame;
    private static JPanel panel;

    // game variables
    private static boolean gameOver = false;
    private static boolean frameShouldClose = false;
    private static long startTime;
    private static final long FPS = 20L;
    private static int score = 0;
    private static String gameName = "Etudes: Pellet Hunter | Score: ";

    // player motion
    private static boolean moveNorth = false;
    private static boolean moveSouth = false;
    private static boolean moveWest = false;
    private static boolean moveEast = false;

    // player position
    private static int playerX = gridSize/2;
    private static int playerY = gridSize/2;

    // pellet position
    private static int pelletX = 0;
    private static int pelletY = 0;

    // enemy positions
    private static int numEnemies = 5;
    private static int[][] enemyPositions = new int[numEnemies][2];
    private static final double enemyMotionChance = 0.01;

    public static void main(String[] args) {

        frame = new JFrame();
        frame.setTitle(gameName + score);

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {

                g.clearRect(0, 0, width, height);

                // draw the player
                g.setColor(new Color(57, 227, 210));
                g.fillRect(playerX * squareWidth, playerY * squareWidth, squareWidth, squareWidth);

                // draw the enemies
                g.setColor(new Color(255, 94, 94));
                for (int[] position : enemyPositions) {
                    int ex = position[0];
                    int ey = position[1];
                    g.fillRect(ex * squareWidth, ey * squareWidth, squareWidth, squareWidth);
                }

                // draw the pellet
                g.setColor(new Color(255, 213, 0));
                g.fillOval(pelletX * squareWidth, pelletY * squareWidth, squareWidth, squareWidth);

                // draw the grid lines
                g.setColor(Color.BLACK);
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        g.drawLine(i * squareWidth, j * squareWidth, i * squareWidth, height);
                        g.drawLine(i * squareWidth, j * squareWidth, width, j * squareWidth);
                    }
                }

                if (gameOver) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, width, height);
                }
            }
        };

        panel.setSize(new Dimension(width, height));
        panel.setBackground(new Color(115, 87, 68));

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_A : moveWest = true; break;
                    case KeyEvent.VK_D : moveEast = true; break;
                    case KeyEvent.VK_W : moveNorth = true; break;
                    case KeyEvent.VK_S : moveSouth = true; break;
                    case KeyEvent.VK_ESCAPE:
                        gameOver = true;
                        frameShouldClose = true;
                        break;
                    case KeyEvent.VK_ENTER:
                        if (gameOver) {
                            reset();
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        };

        frame.addKeyListener(keyListener);
        frame.add(panel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(width, height));

        frame.setVisible(true);

        Dimension actualSize = frame.getContentPane().getSize();
        int extraW = width - actualSize.width;
        int extraH = height - actualSize.height;
        frame.setSize(width + extraW, height + extraH);

        // randomly set enemy positions
        for (int[] position : enemyPositions) {
            position[0] = Math.abs((int)(gridSize * (Math.random())));
            position[1] = Math.abs((int)(gridSize * (Math.random())));
        }

        while (!frameShouldClose) {
            while (!gameOver) {
                // update game
                movePlayer();
                moveEnemies();
                testCollisions();
                checkScore();

                // update graphics
                panel.repaint();

                // update score
                frame.setTitle(gameName + score);


                // manage the FPS
                startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(FPS);
                } catch (InterruptedException ignored) {
                }
            }
            frame.setTitle(gameName + score + " | Game Over! Press Enter to Restart.");
            // manage the FPS
            startTime = System.currentTimeMillis();
            try {
                Thread.sleep(FPS);
            } catch (InterruptedException ignored) {
            }
        }
        frame.dispose();
    }

    /**
     * resets the game
     */
    public static void reset() {
        // randomly set enemy positions
        for (int[] position : enemyPositions) {
            position[0] = Math.abs((int) (gridSize * Math.random()));
            position[1] = Math.abs((int) (gridSize * Math.random()));
        }

        // reset player position
        playerX = gridSize / 2;
        playerY = gridSize / 2;

        // reset player motion
        moveNorth = false;
        moveSouth = false;
        moveEast = false;
        moveWest = false;

        // reset boolean variables
        gameOver = false;

        // reset pellet position
        pelletX = Math.abs((int) (gridSize * Math.random()));
        pelletY = Math.abs((int) (gridSize * Math.random()));
    }

    /**
     * tests if a player collides with an enemy
     */
    public static void testCollisions() {
        for (int[] position: enemyPositions) {
            if (position[0] == playerX && position[1] == playerY) {
                gameOver = true;
                break;
            }
        }
    }

    /**
     * tests to see if a player has acquired a pellet
     */
    public static void checkScore() {
        if (playerX == pelletX && playerY == pelletY) {
            score ++;

            // reset pellet position
            pelletX = Math.abs((int) (gridSize * Math.random()));
            pelletY = Math.abs((int) (gridSize * Math.random()));
        }
    }

    /**
     * moves the enemies
     */
    public static void moveEnemies() {
        for (int[] position : enemyPositions) {

            // move the enemy x
            if (position[0] > 0 && Math.random() < enemyMotionChance) {
                position[0]--;
            }
            if (position[0] < gridSize-1 && Math.random() < enemyMotionChance) {
                position[0]++;
            }

            // move the enemy y
            if (position[1] > 0 && Math.random() < enemyMotionChance) {
                position[1]--;
            }
            if (position[1] < gridSize-1 && Math.random() < enemyMotionChance) {
                position[1]++;
            }
        }
    }

    /**
     * moves the player
     */
    public static void movePlayer() {
        if (moveNorth) {
            if (playerY > 0) {
                playerY--;
            }
            moveNorth = false;
        }
        if (moveSouth) {
            if (playerY < gridSize-1) {
                playerY++;
            }
            moveSouth = false;
        }
        if (moveEast) {
            if (playerX < gridSize-1) {
                playerX++;
            }
            moveEast = false;
        }
        if (moveWest) {
            if (playerX > 0) {
                playerX--;
            }
            moveWest = false;
        }
    }

}