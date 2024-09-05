import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


public class Explorer extends JPanel implements KeyListener {//Main class 
    //Constants
    public static final int WIDTH = 1024; 
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    World world;

    class Runner implements Runnable { //Perfomrs game updates
        public void run() {
            while (true) {
                world.updateAll(1.0 / (double) FPS);

                repaint();
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    //Followining Overwritten methods handle keyBoard inputs
    @Override
    public void keyTyped(KeyEvent e) {
        // char c = e.getKeyChar();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 'w') { // Moves player up
            world.player.setVelocity(new Pair(0, -world.playerVel));
            world.newAction("Player Moved Up");
        }
        if (c == 'a') { // Moves player left
            world.player.setVelocity(new Pair(-world.playerVel, 0));
            world.newAction("Player Moved Left");
        }
        if (c == 's') { // Moves player down
            world.player.setVelocity(new Pair(0, world.playerVel));
            world.newAction("Player Moved Down");
        }
        if (c == 'd') { // Moves player right
            world.player.setVelocity(new Pair(world.playerVel, 0));
            world.newAction("Player Moved Right");
        }
        if (c == 'x') {
            world.player.setVelocity(new Pair(0, 0));
            world.newAction("Player Stopped");
        }
        if (c == 'e') {
            world.player.dash();
            world.newAction("Player Dashed");
        }
        if (c == 'c') {
            world.player.newProjectile(world);
        }
        if (c == 'q') {
            world.player.usePowerup(world);
        }

        if (c == 'i' && world.twoPlayer) { // Moves player up
            world.player2.setVelocity(new Pair(0, -world.playerVel));
        }
        if (c == 'j' && world.twoPlayer) { // Moves player left
            world.player2.setVelocity(new Pair(-world.playerVel, 0));
        }
        if (c == 'k' && world.twoPlayer) { // Moves player down
            world.player2.setVelocity(new Pair(0, world.playerVel));
        }
        if (c == 'l' && world.twoPlayer) { // Moves player right
            world.player2.setVelocity(new Pair(world.playerVel, 0));
        }
        if (c == 'm' && world.twoPlayer) {
            world.player2.setVelocity(new Pair(0, 0));
        }
        if (c == 'u' && world.twoPlayer) {
            world.player2.dash();
        }
        if (c == 'n' && world.twoPlayer) {
            world.player2.newProjectile(world);
        }
         if (c == 'o') {
            world.player2.usePowerup(world);
        }

        if (c == '1' && world.levelIndex == 0) {
            world.levelIndex++;
        }
        if (c == '2' && !world.twoPlayer) {
            world.updateTwoPlayer();
        }
        if (c == 'p') {
            world.levelIndex++;
        }
        if (c == 'r' && world.gameOver) {
            this.world = new World(HEIGHT, WIDTH);
        }
    }
    

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 'j') {
            world.player.dashUp = false;
            world.player.dashDown = false;
            world.player.dashLeft = false;
            world.player.dashRight = false;
        }
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public Explorer() { //Main Constructor
        world = new World(HEIGHT, WIDTH);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Explorer mainInstance = new Explorer();
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) { //Handles all drawing
        super.paintComponent(g);
        world.drawAll(g);
    }
}
