import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

public class JumpingGame {

    // the dimensions of the game
    private static final int width = 800, height = 600;

    // world variables
    private static final int floorHeight = 500;
    private static final int velocity = 10;

    // scoring
    private static final String scoreText = "Score: ";
    private static int score = 0;
    private static long startTime;
    private static JLabel label;

    // cacti
    private static final int maxCactusHeight = 60;
    private static final int cactusWidth = 20;
    private static ArrayList<int[]> cacti = new ArrayList<>(); //[x_left, height]

    // clouds
    private static ArrayList<int[]> clouds = new ArrayList<>(); //[x_left, y_lower, width, height]

    // position and motion of the player
    private static int pd = 20;
    private static int defaultPx = width/2, defaultPy = floorHeight;
    private static int px = defaultPx, py = defaultPy;
    private static final int dx = 11;
    private static int dy = 0;
    private static final int g = 2;
    private static boolean hasCollided = false;

    private static boolean isOnFloor = true;
    private static boolean jumped = false;

    // left and right motion
    private static boolean left = false, right = false;

    // if the game is over
    private static boolean gameOver = false;

    // game data
    private static final long FPS = 20L;

    // frame and panel
    private static JPanel panel;
    private static JFrame frame;
    private static boolean frameShouldClose = false;

    public static void main(String[] args) {
        // the display of the game
        frame = new JFrame();

        // handles painting the scene
        panel = new JPanel() {
            // handles painting the scene
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.fillRect(px - pd/2, py - pd, pd, pd); // paint the player

                for (int[] c : cacti) { // paint the cacti
                    g.fillRect(c[0], floorHeight - c[1], cactusWidth, c[1]);
                }

                for (int[] c : clouds) {
                    g.fillRect(c[0], c[1]-c[3], c[2], c[3]);
                }

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, floorHeight, width, 100);

            }
        };

        panel.setSize(new Dimension(width, height));

        frame.setSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);

        label = new JLabel(score + "0", JLabel.CENTER);

        panel.add(label);

        // handle keystrokes
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE : frameShouldClose = true; gameOver = true; break;
                    case KeyEvent.VK_A: left = true; break; // left
                    case KeyEvent.VK_D: right = true; break; // right
                    case KeyEvent.VK_S: break; // down
                    case KeyEvent.VK_W: break; // up
                    case KeyEvent.VK_SPACE :
                        if (!jumped) {
                            py -= 1;
                            dy = -25;
                            isOnFloor = false;
                            jumped = true;
                        }
                        break;
                    case KeyEvent.VK_ENTER :
                        gameOver = false;
                        px = defaultPx;
                        py = defaultPy;
                        hasCollided = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_A: left = false; break; // left
                    case KeyEvent.VK_D: right = false; break; // right
                    case KeyEvent.VK_S: break; // down
                    case KeyEvent.VK_W: break; // up
                }
            }
        });

        frame.setVisible(true);

        while (!frameShouldClose) {
            while (!gameOver) {
                startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(FPS);
                } catch (InterruptedException ignored) {
                }
                movePlayer();
                updateCacti();
                if (hasCollided) {
                    Graphics g = panel.getGraphics();
                    g.setColor(Color.BLACK);
                    g.fillRect(0, height, width, height);
                    gameOver = true;
                }
                updateClouds();
                panel.repaint();
                updateScore();
                checkCollision();
            }
            clouds.clear();
            cacti.clear();
            panel.repaint();
            String gameOverString = "<html><div style='text-align:center'>" + scoreText + score + "<br/>You Collided! Game Over.<br/>Press Enter to Try Again, or Press Escape to Exit.</html>";
            label.setText(gameOverString);

            try {
                Thread.sleep(FPS);
            } catch (InterruptedException ignored) {
            }
        }
        frame.dispose();
    }

    public static void updateScore() {
        score++;
        label.setText(scoreText + score);
    }

    public static void checkCollision() {
        for (int[] c : cacti) {
            hasCollided = true;
            int x1 = c[0];
            int x2 = c[0] + cactusWidth;
            int y1 = floorHeight - c[1];

            if (px + pd < x1 || px > x2) {
                hasCollided = false;
            }
            if (py - pd > floorHeight || py < y1) {
                hasCollided = false;
            }

            if (hasCollided) {
                break;
            }
        }
    }

    // moves the player
    public static void movePlayer() {
        if (left && px - dx > 0) {
            px -= dx;
        }
        if (right && px + dx + pd < width) {
            px += dx;
        }
        if (!isOnFloor) {
            if (py + dy > floorHeight) {
                py = floorHeight;
                jumped = false;
                isOnFloor = true;
                dy = 0;
            } else {
                py += dy;
                dy += g;
            }
        }
    }

    public static void updateCacti() {
        cacti.removeIf(c -> c[0] < -cactusWidth);

        for (int[] c : cacti) {
            c[0] -= velocity;
        }

        if (cacti.size()<7) {
            if (Math.random() < 0.1) {
                int height = 50 + (int)(Math.random() * maxCactusHeight);
                cacti.add(new int[]{width, height});
            }
        }
    }

    public static void updateClouds() {
        clouds.removeIf(c -> c[0] < -c[2]);

        for (int[] c : clouds) {
            c[0] -= velocity/2;
        }

        if (clouds.size()<15) {
            if (Math.random() < 0.1) {
                int y = 100 + (int)(Math.random() * 200);
                int w = 20 + (int)(Math.random() * 200);
                int h = 20 + (int)(Math.random() * 100);
                clouds.add(new int[]{width, y, w, h});
            }
        }
    }
}
