import java.awt.*;


public abstract class Collectables {
    Pair position;
    double width;
    double height;
    Color color;

    public Collectables(Pair POSITION, double WIDTH, double HEIGHT, Color COLOR) {
        this.position = POSITION;
        this.width = WIDTH;
        this.height = HEIGHT;
        this.color = COLOR;
    }

    public double distance(Pair p, int playerWidth) { //Finds the distance between the collectable and the player.
        double radius = width / 2;
        double changeX = (position.x + radius) - (p.x + (playerWidth / 2));
        double changeY = (position.y + radius) - (p.y + (playerWidth / 2));
        double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2));
        return distance;
    }

    public boolean collisionPlayer(Player p) { //Checks if the player and collectable collide.
        if (distance(p.position, (int) (p.width)) < (width / 2) + (p.width / 2)) {
            return true;
        }
        return false;
    }

    abstract void draw(Graphics g, World w); //Override Method

    abstract void function(World w, Player p); //Override Method
}

class Key extends Collectables { //Stores the key used for level progression.
    Color color;

    public Key(Pair POSITION, double WIDTH, double HEIGHT, Color COLOR) {
        super(POSITION, WIDTH, HEIGHT, COLOR);
        this.color = new Color(0, 0, 200);
    }

    @Override
    public void function(World w, Player p) {} //Funtion handled in Level classes

    public void update(Player p) {
        if (this != null) {
            if (collisionPlayer(p)) {
                p.key = true;
            }
        }
    }

    @Override
    public void draw(Graphics g, World w) { //Draws the key.
        g.drawImage(w.image.key, (int) position.x, (int) position.y, (int) height, (int) width, null);
    }
}

class Invulnerable extends Collectables { //Powerup for two person player mode
    public Invulnerable(Pair POSITION, double WIDTH, double HEIGHT, Color COLOR) {
        super(POSITION, WIDTH, HEIGHT, COLOR); //Does actions of abstract class constructor
        this.color = new Color(160, 160, 160);
    }
    @Override
    public void function(World w, Player p) { //Function of powerup
        w.newAction("Player used Invulnerable Powerup");
        p.invulnerablePowerup(System.currentTimeMillis()); //Calls method in player with timer
    }
    @Override
    public void draw(Graphics g, World w) {
        g.drawImage(w.image.invulnerablePowerup, (int) position.x, (int) position.y, (int) height, (int) width, null);
    }
}

class HealthBoost extends Collectables { //Powerup for two person player mode
    public HealthBoost(Pair POSITION, double WIDTH, double HEIGHT, Color COLOR) {
        super(POSITION, WIDTH, HEIGHT, COLOR); //Does actions of abstract class constructor
        this.color = new Color(255, 153, 204);
    }
    @Override
    public void function(World w, Player p) { //Function of powerup
        w.newAction("Player used HealthBoost Powerup");
        p.healthBoost(); //Calls method in player
    }
    @Override
    public void draw(Graphics g, World w) {
        g.drawImage(w.image.heart, (int) position.x, (int) position.y, (int) height, (int) width, null);
    }
}

class ProjectilePowerup extends Collectables { //Powerup for two person player mode
    public ProjectilePowerup(Pair POSITION, double WIDTH, double HEIGHT, Color COLOR) {
        super(POSITION, WIDTH, HEIGHT, COLOR); //Does actions of abstract class constructor
        this.color = new Color(255, 255, 51);
    }
    @Override
    public void function(World w, Player p) { //Function of powerup
        w.newAction("Player used Projectile Powerup");
        p.projectileSpeedup(System.currentTimeMillis()); //Calls method in player with timer
    }
    @Override
    public void draw(Graphics g, World w) {
        g.drawImage(w.image.projectilePowerup, (int) position.x, (int) position.y, (int) height, (int) width, null);
    }
}

