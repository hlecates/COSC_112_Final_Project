import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public interface Level { // Interface of game levels. Allows for creation of levels array in world
    public void update(double time, World w); // Override

    public void draw(Graphics g, World world); // Override

}

class TitleScreen extends Area implements Level { // Initial screen of game
    int height;
    int width;

    // Creates vars dealing wiht buttons
    boolean isButtons;
    levelStart levelStart;
    twoPlayerStart twoPlayerStart;
    Button[] buttons;

    public TitleScreen(World w) { // Constructor
        super(w, 10, 10);
        this.height = w.height;
        this.width = w.width;

        this.isButtons = true;
        this.levelStart = new levelStart(new Color(255, 255, 255), "Levels",
                new Pair(((width / 10) * 4) - 85, (height / 2) + 170));
        this.twoPlayerStart = new twoPlayerStart(new Color(255, 255, 255), "Arena",
                new Pair(((width / 10) * 6) - 40, (height / 2) + 170));
        this.buttons = new Button[] { levelStart, twoPlayerStart };
    }

    @Override
    public void update(double time, World w) { // Handles collisions between player and buttons, player acts as a
                                               // "cursor"
        if (isButtons) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].collision(w.player, w);
            }
        }
    }

    @Override
    public void draw(Graphics g, World w) {

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }

        g.drawImage(w.image.title, (width / 4) - 145, (height / 4) - 70, 800, 300, null);

        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("TimesRoman", Font.PLAIN, 100));
        // g.drawString("EXPLORER", (width / 3) - 260, (height / 2) - 50);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        g.drawString("Move the Player to the Desired Mode", (w.width / 2) - 120, 20); // Provides Instructiosn for
                                                                                      // begining game

        levelStart.draw(g, w);
        twoPlayerStart.draw(g, w);
    }
}

class Arena extends Area implements Level { // The twoplayer "level"
    int height;
    int width;
    List<Collectables> powerups;

    public Arena(World w) {
        super(w, 20, 20);
        this.height = w.height;
        this.width = w.width;
        this.powerups = new LinkedList<Collectables>();
    }

    @Override
    public void update(double time, World w) {
        int rand = w.random.nextInt(300); // Creates powerups if a random int is a specific int
        if (rand == 1) {
            powerups.add(new HealthBoost(new Pair(w.random.nextInt(w.width) - 20, w.random.nextInt(w.height) - 20), 40,
                    40, new Color(0, 0, 0)));
        }
        if (rand == 2) {
            powerups.add(
                    new ProjectilePowerup(new Pair(w.random.nextInt(w.width) - 20, w.random.nextInt(w.height) - 20), 40,
                            40, new Color(0, 0, 0)));
        }
        if (rand == 3) {
            powerups.add(new Invulnerable(new Pair(w.random.nextInt(w.width) - 20, w.random.nextInt(w.height) - 20), 40,
                    40, new Color(0, 0, 0)));
        }

        List<Collectables> powerupsToRemove = new LinkedList<Collectables>(); // Creates new linked list which will
                                                                              // house poweerups need to remove, Avoid
                                                                              // concurrent modification
        for (Player player : w.players) {
            for (Collectables p : powerups) {
                if (p.collisionPlayer(player)) { // Handles collision between player and powerup
                    player.addPowerup(p); // If player collides adds it to powerup queue
                    powerupsToRemove.add(p);
                }
            }
        }
        this.powerups.removeAll(powerupsToRemove); // Removes the powerups from the main linked list
    }

    @Override
    public void draw(Graphics g, World w) { // Draws
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }
        for (Collectables p : powerups) {
            p.draw(g, w);
        }
    }
}

class LevelOne extends Area implements Level {
    int totalEnemies;
    boolean keyGenerated;
    boolean isButtons;
    Button[] buttons;
    Key key;
    private boolean resetPlayerPosition;
    private boolean resetPlayerHealth;

    public LevelOne(World w) { // Level one of level mode, spawns enemies the player must defeat
        super(w, 15, 15);

        totalEnemies = 5 + w.random.nextInt(10); // Creates a random number enemies
        keyGenerated = false;

        this.resetPlayerPosition = false;

        for (int i = 0; i < totalEnemies; i++) { // Initializes enemies to random places
            w.enemies.add(new Enemy(new Pair(w.random.nextInt(w.width - 20), w.random.nextInt(w.height - 20)),
                    new Pair(0, 0), 60, 60, new Color(255, 0, 0), 100));
        }
        // System.out.println("Enemies:" + totalEnemies);
    }

    @Override
    public void update(double time, World w) {
        if (!resetPlayerHealth) {
            w.player.health = 100;
            resetPlayerHealth = true;
        }

        if (!resetPlayerPosition) { // resets player position and velocity to middle of screen and zero respectively
            w.player.setVelocity(new Pair(0, 0));
            w.player.setPosition(new Pair((w.width / 2 - 10), (w.height / 2) - 10));
            resetPlayerPosition = true;
        }

        for (Enemy e : w.enemies) {
            if (e != null) {
                e.followPlayer(w, w.random.nextInt(100), time);
                e.update(w, time);
            }
        }

        if (!keyGenerated) {
            if (w.enemiesKilled == totalEnemies) { // Signifies the end of the level
                this.key = new Key(new Pair(w.random.nextInt(w.width), w.random.nextInt(w.height) - 10), 70, 70,
                        new Color(0, 0, 255));
                keyGenerated = true;
                w.newAction("Player Completed Level 1");
            }
        }

        if (key != null) { // Null check
            this.key.update(w.player);
            if (w.player.key) {
                this.key = null;
            }
        }
    }

    public void draw(Graphics g, World w) {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }
        for (Enemy e : w.enemies) {
            if (e != null) {
                e.draw(g, w);
            }
        }
        if (key != null) {
            this.key.draw(g, w);
        }
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        g.drawString("Defeat the Enemies", (w.width / 2) - 66, 20);
        g.drawString("Press 'c' to Attack", (w.width / 2) - 60, 38);
    }
}

// Implements an alogorithm using recurvsie backtracking (utlizes a stack) to
// create a maze
class LevelTwo extends Area implements Level { // Level Two, a maze
    boolean isButtons;
    Button[] buttons;
    Key key;

    private boolean resetPlayerHealth;
    private boolean resetPlayerPosition;
    private boolean resetPlayerSize;
    private Random rand;

    private boolean[][] maze;
    private Pair[] directions;
    private Stack<Pair> stack;
    private List<Pair> availablePaths;

    public LevelTwo(World w) {
        super(w, 15, 15);
        this.isButtons = false;

        this.rand = new Random(w.seed);
        this.maze = new boolean[columns][rows];
        this.stack = new Stack<Pair>();
        this.directions = new Pair[] { new Pair(1, 0), new Pair(-1, 0), new Pair(0, 1), new Pair(0, -1) }; // All
                                                                                                           // possible
                                                                                                           // directions
        this.availablePaths = new ArrayList<Pair>(); // Used in finidng player and key location

        for (int i = 0; i < columns; i++) { // Initliazes the maze as a blank slate
            for (int j = 0; j < rows; j++) {
                maze[i][j] = true;
            }
        }

        createMaze(); // Creates maze

        Color[] wallColors = new Color[] { new Color(47, 79, 79), new Color(105, 105, 105), new Color(128, 128, 128) }; // Array
                                                                                                                        // of
                                                                                                                        // potential
                                                                                                                        // colors
                                                                                                                        // of
                                                                                                                        // walls

        for (int i = 0; i < columns; i++) { // Randomly sets wall colors and solid boolean
            for (int j = 0; j < rows; j++) {
                if (maze[i][j]) {
                    enviro[i][j].setColor(wallColors[w.random.nextInt(wallColors.length)]);
                    enviro[i][j].setSolid();
                }
            }
        }

        if (!availablePaths.isEmpty()) { // Using list of paths, places a key in a randomly selected path block
            Pair randomPath = availablePaths.get(w.random.nextInt(availablePaths.size())); // Randomizes path block
            this.key = new Key(
                    new Pair(enviro[(int) randomPath.x][(int) randomPath.y].position.x,
                            enviro[(int) randomPath.x][(int) randomPath.y].position.y - 10),
                    70, 70, new Color(0, 0, 255));
        }

    }

    public void createMaze() {
        Pair startPoint = new Pair(0, 0); // Create the starting point
        stack.push(startPoint); // Adds it to the satck
        maze[(int) startPoint.x][(int) startPoint.y] = false; // Marks it as false

        while (!stack.isEmpty()) {
            Pair current = stack.peek(); // Gets top of stack
            Pair next = getRandomNeighbor(current); // Gets the random neighbors of cell
            if (next != null) {
                maze[(int) next.x][(int) next.y] = false;
                stack.push(next); // Adds cells to stack
                availablePaths.remove(next); // Removes cell from paths
                updateAvailablePath(current); // Updates the other options for paths
            } else {
                stack.pop(); // if there are no neighbors which are valid "Backtracks" by removing current
                             // cell from stack
            }
        }

    }

    public Pair getRandomNeighbor(Pair current) {
        shuffle(directions); // Randomly Shuffles potential directions

        for (Pair direction : directions) {
            Pair newXY = new Pair(current.x + direction.x * 2, current.y + direction.y * 2); // Looks pair two cells
                                                                                             // aways, to simulate the
                                                                                             // walls

            if (validNeighbor((int) newXY.x, (int) newXY.y)) {
                maze[(int) (current.x + direction.x)][(int) (current.y + direction.y)] = false;
                return newXY;
            }
        }
        return null;
    }

    public void shuffle(Pair[] directions) { // Shuffles using fisher yates algorithm
        for (int i = directions.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            Pair tmp = directions[index];
            directions[index] = directions[i];
            directions[i] = tmp;
        }
    }

    public boolean validNeighbor(int x, int y) { // The requirements for being a valid neighbor
        return x >= 0 && x < columns && y >= 0 && y < rows && maze[x][y];
    }

    private void updateAvailablePath(Pair current) { // Updates available paths
        for (Pair direction : directions) { // Looks at all directions and adds valid paths to availablele paths
            int x = (int) (current.x + direction.x * 2);
            int y = (int) (current.y + direction.y * 2);
            if (validNeighbor(x, y)) {
                availablePaths.add(new Pair(x, y));
            }
        }
    }

    @Override
    public void update(double time, World w) {
        if (!resetPlayerHealth) {
            w.player.health = 100;
            resetPlayerHealth = true;
        }

        if (!resetPlayerSize) { // Sets pl;ayer size so the player can fit in the paths without modifying size
                                // of maze or collisions
            w.player.height = w.player.height / 2;
            w.player.width = w.player.width / 2;
            resetPlayerSize = true;
        }
        if (!resetPlayerPosition) { // Resets player position to be a random availbale path
            Pair randomPath = availablePaths.get(w.random.nextInt(availablePaths.size()));
            w.player.setPosition(new Pair(enviro[(int) randomPath.x][(int) randomPath.x].position.x,
                    enviro[(int) randomPath.x][(int) randomPath.x].position.y));
            w.player.setVelocity(new Pair(0, 0));
            resetPlayerPosition = true;
        }
        if (key != null) {
            this.key.update(w.player);
            if (w.player.key) {
                this.key = null;
            }
        }

        checkWallCollisions(w.player, time); // Checks player collisions with walls
    }

    public void checkWallCollisions(Player p, double time) {

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (enviro[i][j].isSolid() && isColliding(p, enviro[i][j])) {
                    p.position = new Pair(p.position.x - p.velocity.x * time, p.position.y - p.velocity.y * time); // Resets
                                                                                                                   // player
                                                                                                                   // position
                                                                                                                   // using
                                                                                                                   // velocity
                                                                                                                   // formula
                    p.setVelocity(new Pair(0, 0));
                }
            }
        }
    }

    private boolean isColliding(Player p, Environment wall) { // Uses bounds of walls and player position to handle
                                                              // collisions
        return p.position.x < wall.position.x + wall.width && p.position.x + (int) p.width > wall.position.x
                && p.position.y < wall.position.y + wall.height && p.position.y + (int) p.height > wall.position.y;
    }

    @Override
    public void draw(Graphics g, World w) { // Draws
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }
        if (key != null) {
            this.key.draw(g, w);
        }
    }
}

class LevelThree extends Area implements Level { // Level Four, Node Puzzle
    boolean isButtons;
    Button[] buttons;

    Key key;

    boolean keyGenerated;
    private boolean resetPlayerSize;
    private boolean resetPlayerHealth;

    public LevelThree(World w) {
        super(w, 48, 48);
        this.keyGenerated = false;

        for (int i = 0; i < columns; i++) { // Initilaizes environment color
            for (int j = 0; j < rows; j++) {
                if (w.random.nextInt(100) < 30) {
                    enviro[i][j].setColor(new Color(50, 70, 10));
                } else if (w.random.nextInt(100) > 70) {
                    enviro[i][j].setColor(new Color(50, 110, 0));
                } else {
                    enviro[i][j].setColor(new Color(50, 160, 0));
                }
            }
        }

        this.key = new Key(new Pair(w.random.nextInt(w.width), w.random.nextInt(w.height) - 10), 70, 70,
                new Color(0, 0, 255)); // Creates random key
    }

    @Override
    public void update(double time, World w) { // Update
        if (!resetPlayerHealth) {
            w.player.health = 100;
            resetPlayerHealth = true;
        }

        if (!resetPlayerSize) { // Resets player size from level two
            w.player.height = w.player.height * 2;
            w.player.width = w.player.width * 2;
            resetPlayerSize = true;
        }
        if (key != null && keyGenerated) {
            this.key.update(w.player);
        }
        if (w.player.key) {
            this.key = null;
        }
        if (Math.abs(w.button.isPressed(w) - 99.99) < 0.01 && !keyGenerated) { // If the desired number of nodes is
                                                                               // correct, allows the key to generate
            if (!keyGenerated) {
                this.keyGenerated = true;
            }
        }
    }

    @Override
    public void draw(Graphics g, World w) { // Draws
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }

        if (key != null && keyGenerated) {
            this.key.draw(g, w);
        }

        w.node1.draw(g);
        w.node2.draw(g);
        w.node3.draw(g);
        w.node4.draw(g);
        w.node5.draw(g);
        w.node6.draw(g);
        w.node7.draw(g);
        w.node8.draw(g);
        w.node9.draw(g);
        // w.button.draw(g);
        w.button.drawNodeResult(g, w);
    }

}

class LevelFour extends Area implements Level { // Level Four, Boss Battle
    boolean isButtons;
    Button[] buttons;
    boolean keyGenerated;

    Key key;

    private boolean resetPlayerHealth;

    public LevelFour(World w) {
        super(w, 24, 24);
        this.keyGenerated = false;

        for (int i = 0; i < columns; i++) { // Intializes color of environments
            for (int j = 0; j < rows; j++) {
                if (Math.random() < .1) {
                    enviro[i][j].setColor(new Color(200, 50, 30));
                } else if (Math.random() > .7) {
                    enviro[i][j].setColor(new Color(250, 50, 100));
                } else {
                    enviro[i][j].setColor(new Color(250, 160, 0));
                }
            }
        }

        this.key = new Key(new Pair(w.random.nextInt(w.width), w.random.nextInt(w.height) - 10), 70, 70,
                new Color(0, 0, 255)); // Randomly creates a key
    }

    double variableSpeed = 100;

    public void update(double time, World w) {
        if (!resetPlayerHealth) {
            w.player.health = 100;
            resetPlayerHealth = true;
        }
        if (w.healthbar.isDead() && !keyGenerated && key != null) {
            System.out.println('*');
            this.keyGenerated = true;
        }
        if (w.healthbar.isDead() == true && keyGenerated && key != null) {
            System.out.println('*');
            this.key.update(w.player);
        }
        if (w.player.key) {
            System.out.println('+');
            this.key = null;
            this.keyGenerated = false;
        }

        // healthbar update
        w.healthbar.update(w, time);
        // phase 1 (most common)
        if (w.timer.getBossPhase() == 1) {
            w.finalboss.color = Color.yellow;

            w.finalboss.followPlayer(w, (int) variableSpeed, time);
            w.finalboss.update(w, time);
            w.timer.update(w, time);

            variableSpeed = variableSpeed - 1;
            if (variableSpeed < 60) {
                variableSpeed = variableSpeed + Math.random() * 50;
            }
        }
        // phase 2 (second most common)
        if (w.timer.getBossPhase() == 2) {
            w.finalboss.color = Color.orange;

            w.finalboss.followPlayer(w, 50, time);
            w.finalboss.update(w, time);
            w.timer.update(w, time);

            Projectile projectile;
            if (w.finalboss.distanceFromPlayer(w) < 1000) {
                projectile = new Projectile(new Pair(w.finalboss.position.x, w.finalboss.position.y),
                        new Pair(w.finalboss.velocity.x * (1 + Math.random() * 5),
                                w.finalboss.velocity.y * (1 + Math.random() * 5)),
                        Color.WHITE, 1, w.finalboss);

                if (w.random.nextInt(100) < .5) {
                    w.addProjectile(projectile);
                }
            }
        }
        // phase 3 (least common)
        if (w.timer.getBossPhase() == 3) {
            w.finalboss.color = Color.red;

            w.finalboss.followPlayer(w, (int) variableSpeed, time);
            w.finalboss.update(w, time);
            w.timer.update(w, time);

            variableSpeed = variableSpeed - 1;
            if (variableSpeed < 60) {
                variableSpeed = variableSpeed + Math.random() * 100;
            }

            Projectile projectile;
            if (w.finalboss.distanceFromPlayer(w) < 500) {
                projectile = new Projectile(new Pair(w.finalboss.position.x, w.finalboss.position.y),
                        new Pair(w.finalboss.velocity.x * (1 + w.random.nextInt(3)),
                                w.finalboss.velocity.y * (1 + w.random.nextInt(3))),
                        Color.RED, 1, w.finalboss);

                if (w.random.nextInt(100) < 15) {
                    w.addProjectile(projectile);
                }
            }
        }
    }

    public void draw(Graphics g, World w) { // Draws sprites
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }
        // deleting level threes nodes
        w.node1.deleteNode(g);
        w.node2.deleteNode(g);
        w.node3.deleteNode(g);
        w.node4.deleteNode(g);
        w.node5.deleteNode(g);
        w.node6.deleteNode(g);
        w.node7.deleteNode(g);
        w.node8.deleteNode(g);
        w.node9.deleteNode(g);

        if (w.healthbar.isDead() == true && keyGenerated && key != null) { // If boss is defeated draws key
            System.out.println('*');
            this.key.draw(g, w);
        }
        w.finalboss.draw(g, w); // Draws boss
        w.timer.draw(g, w); // Bosstimer
        w.healthbar.draw(g); // Draws healthbar
    }
}

class FinalScreen extends Area implements Level { // Final screen of game, shows when player dies or wins
    int height;
    int width;
    boolean isButtons;
    Button[] buttons;
    boolean win;

    public FinalScreen(World w) {
        super(w, 15, 15);
        this.height = w.height;
        this.width = w.width;

        if (w.player.health > 0) {
            win = true;
        } else if (w.player.health <= 0) {
            win = false;
        }

        Color[] redColors = new Color[] { new Color(255, 0, 0), new Color(204, 0, 0), new Color(255, 102, 102),
                new Color(255, 51, 51) }; // Red shades
        Color[] glitchyColors = new Color[] { new Color(51, 153, 253), new Color(255, 255, 51),
                new Color(255, 102, 178), new Color(0, 204, 204), new Color(153, 255, 153), new Color(255, 153, 255),
                new Color(0, 204, 102), new Color(255, 153, 153) }; // Radnom color shades for different feel

        if (win) {
            for (int i = 0; i < columns; i++) { // Sets colors of environments to various shades of colors
                for (int j = 0; j < rows; j++) {
                    enviro[i][j].setColor(glitchyColors[w.random.nextInt(glitchyColors.length)]);
                }
            }
        }
        if (!win) {
            for (int i = 0; i < columns; i++) { // Sets colors of environments to various shades of colors
                for (int j = 0; j < rows; j++) {
                    enviro[i][j].setColor(redColors[w.random.nextInt(redColors.length)]);
                }
            }
        }

    }

    @Override
    public void update(double time, World w) {
    }

    @Override
    public void draw(Graphics g, World w) { // Draws
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }

        if (!win) {
            g.drawImage(w.image.gameOver, (width / 2) - 320, (height / 2) - 290, 600, 400, null);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.setColor(new Color(255, 255, 255));
            g.drawString("Press 'r' to Play Again ", (width / 2) - 75, (height / 2) + 50);
        }
        if (win) {
            g.setColor(new Color(0, 0, 0));
            g.setFont(new Font("TimesRoman", Font.BOLD, 60));
            g.drawString("Press 'r' to Play Again ", (width / 2) - 275, (height / 2));
        }
    }
}

// Given a random correct value, either 1 or 2. Being shot by a projectile
// changes their value from either 1->2 or 2->1. If their color is matched to
// their correctly given random value, the node percent will increment by 11.11
// %.
class Node { // Used on level 3.
    Pair position;
    Color color;
    int correctColor; // 1 = white, 2 = black
    int currentColor; // 1 = white, 2 = black
    int height;
    int width;

    public Node(Pair POSITION, int currentColor, int correctColor) {
        this.position = POSITION;
        this.height = 50;
        this.width = 50;
        this.currentColor = currentColor;
        this.correctColor = correctColor;

        if (currentColor == 1) {
            this.color = new Color(255, 255, 255);
        }
        if (currentColor == 2) {
            this.color = new Color(0, 0, 0);
        }
    }

    // black --> white, white --> black
    public void changeNode(Node n) {
        if (n.currentColor == 1) {
            n.currentColor = 2;
            n.color = new Color(0, 0, 0);
        } else if (n.currentColor == 2) {
            n.currentColor = 1;
            n.color = new Color(255, 255, 255);
        }
    }

    public void deleteNode(Graphics g) { // Deletes the node after level three is completed and has been left.

        g.fillRect(0, 0, 0, 0);
    }

    public static boolean checkNode(Node n) { // Checks if the nodes current color is its correct given color.

        if (n.currentColor == n.correctColor) {
            return true;
        } else {
            return false;
        }
    }

    public void draw(Graphics g) { // Draws the node.
        g.setColor(color);
        g.fillOval((int) position.x, (int) position.y, width, height);
    }

    // Determines how many nodes are correct and adds to the total percent correct.
    // At 99.99%, level three will be complete and you will be allowed to progress
    // to level four after collecting the key.
    public static double nodePercentCorrect(World w) {
        double total = 0.00;
        double inc = 11.11;
        if (checkNode(w.node1)) {
            total += inc;
        }
        if (checkNode(w.node2)) {
            total += inc;
        }
        if (checkNode(w.node3)) {
            total += inc;
        }
        if (checkNode(w.node4)) {
            total += inc;
        }
        if (checkNode(w.node5)) {
            total += inc;
        }
        if (checkNode(w.node6)) {
            total += inc;
        }
        if (checkNode(w.node7)) {
            total += inc;
        }
        if (checkNode(w.node8)) {
            total += inc;
        }
        if (checkNode(w.node9)) {
            total += inc;
        }
        return total;
    }
}

class NodeButton { // Collects all the node data to determine nodes are correct.

    Pair position;
    Color color;
    int height;
    int width;

    public NodeButton(Pair POSITION) { // Creates a node button.
        this.color = new Color(100, 150, 200);
        this.height = 0;
        this.width = 0;
        this.position = POSITION;
    }

    public double isPressed(World w) { // When this method is called it will return the number used for the % of nodes
                                       // correct (ex. 99.99% or 11.11%).
        this.color = new Color((int) Math.random() * 255, (int) Math.random() * 255, (int) Math.random() * 255);
        return Node.nodePercentCorrect(w);
    }

    public void drawNodeResult(Graphics g, World w) { // Draws the % of nodes correct to the game screen so the player
                                                      // can see if a node switching makes the total % go up or down to
                                                      // determine whether or not a node should be shot.
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
        double num = isPressed(w);
        g.drawString("Node Activation % = " + num, w.width / 2 - 100, 30);
    }

    public void draw(Graphics g) { // Draws the node button (not currently used).
        g.setColor(color);
        g.fillRect((int) position.x, (int) position.y, width, height);
    }
}
