import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;


public class World { //Stores the players, levels, projectiles, and other objects used throughout the game.
    ReadImages image;
    Random random;
    int seed;

    boolean gameOver;

    GenericQueue<Action> actionQueue;

    int height;
    int width;

    boolean twoPlayer;
    Player player;
    Player player2;
    List<Player> players;

    int playerVel;
    
    // Area area;
    // Key key;

    Level[] levels;
    int levelIndex;

    //Nodes used in level three.
    Node node1;
    Node node2;
    Node node3;
    Node node4;
    Node node5;
    Node node6;
    Node node7;
    Node node8;
    Node node9;
    NodeButton button;

    //Boss, timer, and health bar used in level four.
    Boss finalboss;
    BossTimer timer;
    BossHealthBar healthbar;

    //Enemies used in level one.
    List<Enemy> enemies;
    int enemiesKilled;

    //Projectiles used throughout the game.
    List<Projectile> projectiles;

    public World(int initHeight, int initWidth) { //Initializes Variables
        this.image = new ReadImages();
        this.seed = 21;
        this.random = new Random(seed);

        this.gameOver = false;

        this.actionQueue = new GenericQueue<>();

        this.height = initHeight;
        this.width = initWidth;

        this.enemies = new LinkedList<Enemy>();
        this.enemiesKilled = 0;

        this.projectiles = new LinkedList<Projectile>();

        this.players = new LinkedList<Player>();
        this.playerVel = 150;
        this.player = new Player(new Pair(((this.width / 2) - 40), 3 * (this.height / 5) - 40), new Pair(0, 0), 80, 80, new Color(0, 0, 0), 100);
        players.add(player);

        //Initializes the nodes and button used in level three of the game.
        this.node1 = new Node(new Pair(width / 6, height / 5), 1, (int) (Math.random() * 2) + 1);
        this.node2 = new Node(new Pair(width / 2, height / 5), 1, (int) (Math.random() * 2) + 1);
        this.node3 = new Node(new Pair(width / 1.2, height / 5), 1, (int) (Math.random() * 2) + 1);
        this.node4 = new Node(new Pair(width / 6, height / 2), 1, (int) (Math.random() * 2) + 1);
        this.node5 = new Node(new Pair(width / 2, height / 2), 1, (int) (Math.random() * 2) + 1);
        this.node6 = new Node(new Pair(width / 1.2, height / 2), 1, (int) (Math.random() * 2) + 1);
        this.node7 = new Node(new Pair(width / 6, height / 1.2), 1, (int) (Math.random() * 2) + 1);
        this.node8 = new Node(new Pair(width / 2, height / 1.2), 1, (int) (Math.random() * 2) + 1);
        this.node9 = new Node(new Pair(width / 1.2, height / 1.2), 1, (int) (Math.random() * 2) + 1);
        this.button = new NodeButton(new Pair(width / 2, 50));

        //Initializes the boss, timer, and health bar used in level four of the game.
        this.finalboss = new Boss(new Pair(width / 2, 80), new Pair(0, 0), 80, 30, Color.black, 1000);
        this.timer = new BossTimer();
        this.healthbar = new BossHealthBar();

        //Stores all the levels in a variable used to track level progression.
        this.levelIndex = 0;
        levels = new Level[] { new TitleScreen(this), new LevelOne(this), new LevelTwo(this), new LevelThree(this),new LevelFour(this), new FinalScreen(this) };
    }

    private int tmp = 1;
    public void updateAll(double time) { //Updates all people, the background, and all projectiles in the game, unless the game is over.

        if (gameOver && tmp > 0) {
            levels[levelIndex] = new FinalScreen(this);
            tmp--;
        } else {
            updatePeople(time);
            updateArea(time);
            updateProjectiles(time);
        }
    }

    public void drawAll(Graphics g) { //Draws all aspects of game
        if (gameOver) {
            levels[levelIndex].draw(g, this);
        } else {
            drawArea(g);
            drawPlayer(g);
            drawProjectiles(g);
        }
    }

    public void updateTwoPlayer() { //If user desires, sets world to be two player duel
        this.twoPlayer = true;
        if (twoPlayer) {
            this.player.color = new Color(255, 0, 0);
            this.player.position = new Pair((this.width / 4 - 10), (this.height / 2) - 10);
            this.player2 = new Player(new Pair(3 * (this.width / 4) - 40, (this.height / 2) - 40), new Pair(0, 0), 80, 80, new Color(0, 0, 0), 100); //Creates new player 
            players.add(player2); //Adds to list of player
        }
        if (twoPlayer) {
            levels = new Level[] {new Arena(this)}; //Sets levels to only include the arena
        }
    }

    public void updatePeople(double time) { //Updates people
        for (Player p : players) { //Updates player by going through list
            updatePlayer(time, p);
        }
        updateEnemies(); //Updates enemies
    }

    public void updatePlayer(double time, Player p) { //Updtes a player
        p.update(this, time);

        //Handles border of world
        if (p.outBounds(this) && p.key) {
            p.setVelocity(new Pair(0, 0));
            p.setPosition(new Pair((this.width / 2 - 10), (this.height / 2) - 10));
            p.key = false;
            levelIndex++;
        }
        if (p.outBoundsUp(this) && !p.key) {
            p.setVelocity(new Pair(0, 0));
            p.setPosition(new Pair(p.position.x, 0));
        } else if (p.outBoundsDown(this) && !p.key) {
            p.setVelocity(new Pair(0, 0));
            p.setPosition(new Pair(p.position.x, height - p.height));
        } else if (p.outBoundsRight(this) && !p.key) {
            p.setVelocity(new Pair(0, 0));
            p.setPosition(new Pair(width - p.width, p.position.y));
        } else if (p.outBoundsLeft(this) && !p.key) {
            p.setVelocity(new Pair(0, 0));
            p.setPosition(new Pair(0, p.position.y));
        }
    }

    public void drawPlayer(Graphics g) { //Draws player
        for (Player p : players) {
            p.draw(g, this);
        }
    }
 
    public void updateEnemies() { //Updates enemy position and status
        List<Enemy> defeatedEnemies = new LinkedList<Enemy>(); //Creates a linked list used to track when an enemy collides with a projectile and loses enough health to be defeated
        for (Enemy e : enemies) {
            if (e.health <= 0) {
                defeatedEnemies.add(e);
                enemiesKilled++;
            }
        }
        for (Enemy e : defeatedEnemies) {
            if (e.projectile != null) {
                removeProjectile(e.projectile);
            }
        }
        enemies.removeAll(defeatedEnemies); //Removes enemies from main list
    }

    public void updateArea(double time) { //Updates levels
        if (levelIndex >= 0 && levelIndex < levels.length) {
            levels[levelIndex].update(time, this);
        } else { //Error checks if index is valid
            System.err.println("Invalid levelIndex: " + levelIndex); 
            System.exit(1);
        }
    }

    public void drawArea(Graphics g) { //Draws levels
        if (levelIndex >= 0 && levelIndex < levels.length) {
            levels[levelIndex].draw(g, this);
        } else { //Error checks if index is valid
            System.err.println("Invalid levelIndex: " + levelIndex);
        }
    }

    public void updateProjectiles(double time) { //Updates projectiles in world
        List<Projectile> projectilesToRemove = new LinkedList<Projectile>(); //Creates anw list ot track what projectiles are to be rmeoved

        for (Projectile p : projectiles) {
            p.updateProjectile(time, this);
            if (p.outOfBounds(this) || p.stopped() || p.collided) { //Conditions for removal
                projectilesToRemove.add(p);
                for (Enemy e : enemies) {
                    e.handleProjectileRemoval(p);
                }
            }
        }
        // Remove projectiles after the loop
        projectiles.removeAll(projectilesToRemove);
    }

    public void addProjectile(Projectile p) { //Adds projectile
        projectiles.add(p);
    }

    public void removeProjectile(Projectile p) { //Removes projectile
        projectiles.remove(p);
    }

    public void drawProjectiles(Graphics g) { //Draws Projectiles}
        for (Projectile p : projectiles) {
            p.draw(g);
        }
    }

    public void gameOver() { //Called if player dies
        printActions();
        gameOver = true;
    }

    public void newAction(String a) { //Adds a new action of game to action queue
        Action action = new Action(a);
        actionQueue.addElement(action);
    }

    public void printActions() { //Prints actiosn to .txt file
        try {
            PrintWriter writer = new PrintWriter("Game_Actions.txt"); //Creates file writer

            while (!actionQueue.isEmpty()) { //While there are still actions prints actions
                Action action = actionQueue.pop();
                writer.write(action.toString());
                writer.write("\n");
                System.out.println(action);
            }

            writer.close();
        } catch (FileNotFoundException e) { //Catshes error if method does not work
            System.out.println("printActions did not compile");
            System.out.println(e);
        }
    }
}

class Pair { // Pair class, which stores the velocities and positions of the objects
    double x;
    double y;

    public Pair(double X, double Y) {
        this.x = X;
        this.y = Y;
    }

    public Pair add(Pair toAdd) {
        return new Pair(x + toAdd.x, y + toAdd.y);
    } // Allows for mathematical operations with Pairs

    public Pair times(double multiplier) {
        return new Pair(x * multiplier, y * multiplier);
    } // Allows for mathematical operations with Pairs

    public Pair divide(double denominator) {
        return new Pair(x / denominator, y / denominator);
    } // Allows for mathematical operations with Pairs

    public void flipX() {
        this.x *= -1;
    }

    public void flipY() {
        this.y *= -1;
    }
}

class QueueNode<T> { // The node class for the generic queue
    T value; // Holds the data
    QueueNode<T> next; // Pointer

    public QueueNode(T VALUE) {
        this.value = VALUE;
        this.next = null;
    }
}

class GenericQueue<T> { //A generic queue
    private QueueNode<T> next; // Front of queue
    private QueueNode<T> previous; // Rear of queue

    public GenericQueue() {
        this.next = null;
        this.previous = null;
    }

    public void addElement(T value) { // Adds element to back of queue
        QueueNode<T> newValue = new QueueNode<T>(value);
        if (next == null) { // If no elements in queue stes next and previous to new node
            this.next = newValue;
            this.previous = newValue;
        } else { // Update the previous node's next reference and move the previous pointer
            previous.next = newValue;
            previous = newValue;
        }
    }

    public T pop() { // Method to remove and return the element from the front of the queue
        T value = next.value; // Retrieve the value from the front node
        next = next.next; // Move the front pointer to the next node
        if (next == null) { // If the queue is now empty, update the rear pointer
            previous = null;
        }
        return value; // Return the popped value
    }

    public boolean isEmpty() { // Method to check if the queue is empty
        return next == null;
    }
}

class Action { //Stores all action and movement in the game.
    private String action;

    public Action(String ACTION) {
        this.action = ACTION;
    }

    public String toString() {
        return action;
    }
}