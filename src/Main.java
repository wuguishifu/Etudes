import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

    // the dimensions of the game
    private static final int width = 800, height = 600;

    private static final int floorHeight = 500;

    // position and motion of the player
    private static int pd = 20;
    private static int px = width/2, py = floorHeight;
    private static final int dx = 10;
    private static int dy = 0;
    private static final int g = 2;

    private static boolean isOnFloor = true;
    private static boolean jumped = false;

    // left and right motion
    private static boolean left = false, right = false;

    // if the game is over
    private static boolean gameOver = false;

    // game data
    private static final long FPS = 20L;

    public static void main(String[] args) {
        // the display of the game
        JFrame frame = new JFrame();

        // handles painting the scene
        JPanel panel = new JPanel() {
            // handles painting the scene
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.fillRect(px - pd/2, py - pd, pd, pd);
            }
        };

        panel.setSize(new Dimension(width, height));


        frame.setSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE : gameOver = true; break;
                    case KeyEvent.VK_A: left = true; break; // left
                    case KeyEvent.VK_D: right = true; break; // right
                    case KeyEvent.VK_S: break; // down
                    case KeyEvent.VK_W: break; // up
                    case KeyEvent.VK_SPACE :
                        if (!jumped) {
                            py -= 1;
                            dy = -20;
                            isOnFloor = false;
                            jumped = true;
                        }
                        break;
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

        while (!gameOver) {
            try {
                Thread.sleep(FPS);
            } catch (InterruptedException ignored) {}
            System.out.println(py);
            movePlayer();
            panel.repaint();
        }

        // closing the game
        frame.dispose();
    }

    // moves the player
    public static void movePlayer() {
        if (left) {
            px -= dx;
        }
        if (right) {
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
}
