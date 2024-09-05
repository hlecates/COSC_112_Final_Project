import java.awt.*;
import java.util.LinkedList;
import java.util.List;


public abstract class Person { //Abstract class representing person in game
    Pair position;
    Pair velocity;

    double width;
    double height;
    double radius;

    Color color;

    int health;

    Projectile projectile;
    long previousProjectileTime;

    public Person(Pair POSITION, Pair VELOCITY, int HEIGHT, int WIDTH, Color COLOR, int HEALTH) {
        this.position = POSITION;
        this.velocity = VELOCITY;
        this.height = HEIGHT;
        this.radius = this.height / 2;
        this.color = COLOR;
        this.width = WIDTH;
        this.health = HEALTH;
    }

    public void update(World w, double time) {
        position = position.add(velocity.times(time));
    }

    public void setPosition(Pair p) {
        position = p;
    }

    public void setVelocity(Pair v) {
        velocity = v;
    }

    abstract public void draw(Graphics g, World w); //Override method
}

class Player extends Person { //Player of game
    //Assists in damage timer
    long previousDamageTime;

    boolean key;

    //Dash Vars
    int dashDistance;
    boolean dashUp;
    boolean dashDown;
    boolean dashLeft;
    boolean dashRight;
    long previousDashTime;

    //Projectiles
    List<Projectile> projectiles; //Allows for more than one projectile
    int projectileCooldown;

    GenericQueue<Collectables> powerups; //Powerups in arena
    long projectileStartTimer; 
    boolean invulnerable;
    long invulnerableStartTime;

    public Player(Pair POSITION, Pair VELOCITY, int HEIGHT, int WIDTH, Color COLOR, int HEALTH) {
        super(POSITION, VELOCITY, HEIGHT, WIDTH, COLOR, HEALTH);
        this.key = false;
        this.dashUp = false;
        this.dashDown = false;
        this.dashLeft = false;
        this.dashRight = false;
        this.dashDistance = 120;
        this.projectile = null;
        this.projectiles = new LinkedList<Projectile>();
        this.projectileCooldown = 1000;
        this.powerups = new GenericQueue<>();
        this.invulnerable = false;
    }

    @Override
    public void update(World w, double time) {
        super.update(w, time);
        if (System.currentTimeMillis() < projectileStartTimer + 10000) {
            projectileCooldown = 0;
        } else {
            projectileCooldown = 1000;
        }
        if (System.currentTimeMillis() < invulnerableStartTime + 10000) {
            invulnerable = true;
        } else {
            invulnerable = false;
        }
    }

    public boolean outBounds(World w) { //Methods controlling player and bounds of screen
        if (outBoundsUp(w) || outBoundsDown(w) || outBoundsLeft(w) || outBoundsRight(w)) {
            return true;
        }
        return false;
    }

    public boolean outBoundsUp(World w) { //Methods controlling player and bounds of screen
        if (position.y < 0) {
            return true;
        }
        return false;
    }

    public boolean outBoundsDown(World w) { //Methods controlling player and bounds of screen
        if (position.y > w.height - height) {
            return true;
        }
        return false;
    }

    public boolean outBoundsLeft(World w) { //Methods controlling player and bounds of screen
        if (position.x < 0) {
            return true;
        }
        return false;
    }

    public boolean outBoundsRight(World w) { //Methods controlling player and bounds of screen
        if (position.x > w.width - width) {
            return true;
        }
        return false;
    }

    public void dash() { //Allows player to travel set distance immediately
        if (previousDashTime != 0 && previousDashTime + 1000 > System.currentTimeMillis()) { //Cooldown for dashing
            return;
        }
        //Deals with the direction of the dash
        if (velocity.x > 0) {
            position.x = position.x + dashDistance;
            dashRight = true;
        }
        if (velocity.x < 0) {
            position.x = position.x - dashDistance;
            dashLeft = true;
        }
        if (velocity.y > 0) {
            position.y = position.y + dashDistance;
            dashDown = true;
        }
        if (velocity.y < 0) {
            position.y = position.y - dashDistance;
            dashUp = true;
        }

        previousDashTime = System.currentTimeMillis(); //Resets time

    }

    public void isHit(World w) {
        if (!invulnerable) { //Accounts for invulnerable powerup
            if (previousDamageTime != 0 && previousDamageTime + 500 > System.currentTimeMillis()) { //Timer to stop constant damage
                return;
            }
            health = health - 10;
            //System.out.println("You've been hit! -10 HP");
            w.newAction("Player was hit (-10 HP)");
            if (health <= 0) { //If player dies, ends game
                w.gameOver(); 
            }
            previousDamageTime = System.currentTimeMillis();
        }
    }

    public void newProjectile(World w) { 
        if (previousProjectileTime != 0 && previousProjectileTime + projectileCooldown > System.currentTimeMillis()) { //Timer to stop uto firing
            return;
        }

        previousProjectileTime = System.currentTimeMillis();
        Projectile projectile = new Projectile(new Pair((this.position.x + (this.width / 2)), (this.position.y + (this.height / 2))),new Pair((this.velocity.x) * 2, (this.velocity.y) * 2), this.color, 0, this);
        projectiles.add(projectile);
        w.addProjectile(projectile);
        w.newAction("Player fired a projectile");
    }

    public void addPowerup(Collectables p) { //Adds powerup to queue
        powerups.addElement(p);
    }

    public void usePowerup(World w) { //Uses powerup function and removes from queue
        if (!powerups.isEmpty()) {
            Collectables p = powerups.pop();
            p.function(w, this);
        }
    }

    public void healthBoost() { //Powerup function
        this.health += 10;
    }

    public void projectileSpeedup(long startTime) { //Powerup function
        projectileStartTimer = startTime;
    }

    public void invulnerablePowerup(long startTime) { //Powerup function
        invulnerableStartTime = startTime;
    }

    @Override
    public void draw(Graphics g, World w) { //draws

        g.setColor(color);
        g.drawImage(w.image.player, (int) position.x, (int) position.y, (int) height, (int) width, null);
        //g.drawOval((int) position.x, (int) position.y, (int) width, (int) height);
        if (invulnerable) {
            g.drawOval((int) position.x - 5, (int) position.y - 5, (int) width + 10, (int) height + 10);
        }
        if(! w.twoPlayer){
            int hX = 10; 
            int hY = w.height - 40; 
            int hWidth = 30;
            int spacing = 10; 

            int fullHearts = health / 10; // Calculate the number of full hearts

            for (int i = 0; i < fullHearts; i++) {
                g.drawImage(w.image.heart, hX + (hWidth + spacing) * i, hY, hWidth, hWidth, null);
            }
        }
    }
}

class Enemy extends Person { //Enemies in game, used in level one
    public int health;
    int detetectionRadius;

    long previousShootTime;

    public Enemy(Pair POSITION, Pair VELOCITY, int HEIGHT, int WIDTH, Color COLOR, int HEALTH) { //Creates an enemy that can detect players in a radius of 250.
        super(POSITION, VELOCITY, HEIGHT, WIDTH, COLOR, HEALTH);
        this.health = HEALTH;
        this.detetectionRadius = 250;
        this.projectile = null;
    }

    public void draw(Graphics g, World w) {
        g.drawImage(w.image.enemy, (int) position.x, (int) position.y, (int) height, (int) width, null);
        g.setColor(color);
        // g.fillRect((int)position.x, (int)position.y, (int)width, (int)height);
    }

    // use of this method is to follow the player when in a certain range
    // can also be used to determine if a player is "hit" by an enemy if in a
    // certain distance range
    public double distanceFromPlayer(World w) { //Detects the distance from the player to determine if in following/attacking range.
        double playerX = w.player.position.x;
        double playerY = w.player.position.y;
        double enemyX = position.x;
        double enemyY = position.y;
        //Uses the distance formula to calculate the distance the player is from the enemy.
        return (Math.sqrt(Math.pow(enemyX - playerX, 2) + Math.pow(enemyY - playerY, 2)));
    }

    //Similar to an update method. Follows the player if in a certain radius.
    public void followPlayer(World w, int speed, double time) {
        // checks if enemy is within a certain distance of player
        if (distanceFromPlayer(w) < detetectionRadius) {
            // checking for diagonal cases
            if (position.x < w.player.position.x && position.y < w.player.position.y) {
                // NE
                this.setVelocity(new Pair(speed, speed));
            } else if (position.x > w.player.position.x && position.y < w.player.position.y) {
                // NW
                this.setVelocity(new Pair(-speed, speed));
            } else if (position.x > w.player.position.x && position.y > w.player.position.y) {
                // SW
                this.setVelocity(new Pair(-speed, -speed));
            } else if (position.x < w.player.position.x && position.y > w.player.position.y) {
                // SE
                this.setVelocity(new Pair(speed, -speed));
            } else {
                if (position.x < w.player.position.x) {
                    // increase enemy xPos (right)
                    this.setVelocity(new Pair(speed, speed));
                }
                if (position.x > w.player.position.x) {
                    // decrease enemy xPos (left)
                    this.setVelocity(new Pair(speed, speed));
                }
                if (position.y < w.player.position.y) {
                    // increase enemy yPos (up)
                    this.setVelocity(new Pair(speed, speed));
                }
                if (position.y > w.player.position.y) {
                    // decrease enemy yPos (down)
                    this.setVelocity(new Pair(speed, speed));
                }
            }
            if (distanceFromPlayer(w) < detetectionRadius) { //If a player is within the detection radius, a projectile will be added to the world.
                if (System.currentTimeMillis() - previousShootTime >= 1000) {
                    if (projectile == null) {
                        projectile = new Projectile(
                                new Pair(this.position.x + (width / 2), this.position.y + (height / 2)),
                                new Pair((this.velocity.x) * 2, (this.velocity.y) * 2), this.color, 1, this);
                        w.addProjectile(projectile);
                    }
                }
            }
        } else {
            this.setVelocity(new Pair(0, 0));
        }
    }

    public void playerIsHit(World w) { //The player is hit and loses some health.
        // 1 hit = -10 player heath points
        w.player.isHit(w);
    }

    public void isHit() { //Used for when the enemy is hit by the player and the enemy loses health.
        // Have to add cool down
        health -= 50;
    }

    public void handleProjectileRemoval(Projectile removedProjectile) { //Used to remove projectile, avoid concurrent error
        if (projectile == removedProjectile) {
            projectile = null;
        }
    }
}

class Boss extends Person { //Boss of game, used in level four
    public int health;
    int detectionRadius;

    public Boss(Pair POSITION, Pair VELOCITY, int HEIGHT, int WIDTH, Color COLOR, int HEALTH) {
        super(POSITION, VELOCITY, HEIGHT, WIDTH, COLOR, HEALTH);
        this.health = HEALTH;
        this.detectionRadius = 500;
    }

    public void draw(Graphics g, World w) {
        g.setColor(color);
        // g.fillRect((int)position.x, (int)position.y, (int)width, (int)height);
        g.drawImage(w.image.boss, (int) position.x, (int) position.y, (int) width + 20, (int) height + 60, null);
    }

    // use of this method is to follow the player when in a certain range
    // can also be used to determine if a player is "hit" by an enemy if in a
    // certain distance range
    public double distanceFromPlayer(World w) {
        // currently cannot get players correct x and y
        double playerX = w.player.position.x;
        double playerY = w.player.position.y;
        double enemyX = position.x;
        double enemyY = position.y;
        // distance formula for player and enemy
        return (Math.sqrt(Math.pow(enemyX - playerX, 2) + Math.pow(enemyY - playerY, 2)));
    }

    // similar to that of public void update()
    public void followPlayer(World w, int speed, double time) {
        // checks if enemy is within a certain distance of player
        if (distanceFromPlayer(w) < detectionRadius) {
            // checking for diagonal cases
            if (position.x < w.player.position.x && position.y < w.player.position.y) {
                // NE
                this.setVelocity(new Pair(speed, speed));
            } else if (position.x > w.player.position.x && position.y < w.player.position.y) {
                // NW
                this.setVelocity(new Pair(-speed, speed));
            } else if (position.x > w.player.position.x && position.y > w.player.position.y) {
                // SW
                this.setVelocity(new Pair(-speed, -speed));
            } else if (position.x < w.player.position.x && position.y > w.player.position.y) {
                // SE
                this.setVelocity(new Pair(speed, -speed));
            } else {
                if (position.x < w.player.position.x) {
                    // increase enemy xPos (right)
                    this.setVelocity(new Pair(speed, speed));
                }
                if (position.x > w.player.position.x) {
                    // decrease enemy xPos (left)
                    this.setVelocity(new Pair(speed, speed));
                }
                if (position.y < w.player.position.y) {
                    // increase enemy yPos (up)
                    this.setVelocity(new Pair(speed, speed));
                }
                if (position.y > w.player.position.y) {
                    // decrease enemy yPos (down)
                    this.setVelocity(new Pair(speed, speed));
                }
            }
        } else {
            this.setVelocity(new Pair(0, 0));
        }
        if (distanceFromPlayer(w) < 200) { //If within distance fires projectile
            projectile = new Projectile(new Pair(this.position.x, this.position.y),new Pair((this.velocity.x * (w.random.nextInt(3) + 1)),(this.velocity.y * (w.random.nextInt(3) + 1))),Color.black, 1, this);
            if ((w.random.nextInt(100)) < 10) {
                w.addProjectile(projectile);
            }
        }
    }

    public int getHealth(World w) { //Returns health
        return w.finalboss.health;
    }

    public void isHit(World w) { //Decrements health
        health = health - 50;
    }

    public void handleProjectileRemoval(Projectile removedProjectile) { //Does same as enemy method
        if (projectile == removedProjectile) {
            projectile = null;
        }
    }
}

class BossTimer { //It is a little rectangle that slides across the top of the screen. When its x position changes it changes its color and in turn its phase.
    public Color color;
    public int phase;
    private Pair position;
    private Pair velocity;
    private int height;
    private int width;

    public BossTimer() { //Creates a timer sprite starting at x=900 and moving left (starts at phase 1, moves to phase 2, back to phase 1, then to phase 3).
        position = new Pair(850, 0);
        velocity = new Pair(-75, 0);
        height = 10;
        width = 10;
        color = Color.yellow;
        phase = 0;
    }

    public void draw(Graphics g, World w) { //Draws
        g.setColor(color);
        g.fillRect((int) position.x, (int) position.y, (int) width, (int) height);
    }

    public void update(World w, double time) { //Changes the color of the timer based on its x position.
        // color/phase update
        // x --> 0 to 1024
        // phase 1 = x < 200 (400 total) ~30% SECOND MOST COMMON --> orange (phase 2)
        // phase 2 = 201 < x < 900 (699 total) ~50% MOST COMMON --> yellow (phase 1)
        // phase 3 = x > 901 (246 total) ~20% LEAST COMMON --> red (phase 3)
        if (position.x < 201) {
            color = Color.orange;
        }
        if (position.x > 200 && position.x < 851) {
            color = Color.yellow;
        }
        if (position.x > 850) {
            color = Color.red;
        }

        // position update
        if (position.x > w.width) { //Reverses the timers velocity when about to leave the bounds of the screen
            velocity = new Pair(-75, 0);
        } else if (position.x < 0) {
            velocity = new Pair(75, 0);
        }
        position = position.add(velocity.times(time)); //Adds velocity to position to move the timer.

    }

    public int getBossPhase() { //Returns the phase of the boss as an integer depending on the color of the boss timer.

        if (color == Color.yellow) {
            return 1;
            // normal phase
        }
        if (color == Color.orange) {
            return 2;
            // special phase
        }
        if (color == Color.red) {
            return 3;
            // danger/attack phase
        }
        return 0;
    }
}

class BossHealthBar { //Draws a health bar for level fours boss to the screen. This bar changes size and color based on the health of the boss.
    private int height;
    //private int width;
    private Pair position;
    Color color;

    public int health;

    public BossHealthBar() {
        position = new Pair(0, 50);
        height = 50;
        //width = health;
        color = Color.yellow;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect((int) position.x, (int) position.y, health, height);
    }

    int tempswitch = 1;

    public void update(World w, double time) { //Updates the healthbars size (and position to keep it in the middle of the screen) based on the health of the boss which changes the width of the healthbar.

        health = w.finalboss.getHealth(w);
       // w.healthbar.width = health;
        w.healthbar.position.x = w.width / 2 - health / 2;
        w.healthbar.color = w.healthbar.getCurrentBarColor(health);

        // boss killed
        if (health == 0 || health < 0) { //When the boss has no more health it is teleported away so the player no longer encounters it.

            if (tempswitch == 1) {
                w.finalboss.position.x = 2500;
                w.finalboss.position.y = 2500;
                if (w.levelIndex == 4) {

                }
                tempswitch--;
            }
        }
    }

    public boolean isDead() { //Checks if the boss is dead by seeing if it has any health left.

        return health == 0 || health < 0;
    }
    
    // gets the correct color of the bar dependent on the health of the boss
    public Color getCurrentBarColor(int currentHealth) { //Returns the color the bar should be as a function of the health of the final boss.
        if (currentHealth < 251) {
            return Color.red;
        }
        if (currentHealth < 501) {
            return Color.orange;
        }
        if (currentHealth < 1001) {
            return Color.yellow;
        }
        return Color.black;
    }
}

