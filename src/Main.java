import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

    // the dimensions of the game
    private static final int width = 800, height = 600;

    // position and motion of the player
    private static int pd = 20;
    private static int px = width/2, py = height/2;
    private static int dx = 1, dy = 1;

    // if the game is over
    private static boolean gameOver = false;

    public static void main(String[] args) {
        // the display of the game
        JFrame frame = new JFrame();

        // handles painting the scene
        JPanel panel = new JPanel() {
            // handles painting the scene
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawRect(px - pd/2, py - pd/2, px + pd/2, py + pd/2);
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
                    case KeyEvent.VK_A : px -= dx; break;
                    case KeyEvent.VK_D : px += dx; break;
                    case KeyEvent.VK_W : py += dy; break;
                    case KeyEvent.VK_S : py -= dy; break;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        frame.setVisible(true);

        int tps = 25;
        int numMills = 1000 / tps;
        long currentTime = System.currentTimeMillis();
        long lastTick = System.currentTimeMillis();

        while (!gameOver) {
            currentTime ++;
            if (currentTime - lastTick > numMills) {
                lastTick = currentTime;
                panel.repaint();
            }
        }
    }
}
