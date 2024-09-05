import java.awt.*;
import java.util.List;


public class Projectile { //Represents a projectile in the game
    Pair position;
    Pair velocity;
    double deAcceleration;
    Color color;
    int height;
    int width;

    int designation;
    // 0 = player, 1 = enemy

    Person shooter;

    boolean collided;

    public Projectile(Pair POSITION, Pair VELOCITY, Color COLOR, int DESIGNATION, Person SHOOTER) {
        this.position = POSITION;
        this.velocity = VELOCITY;
        this.designation = DESIGNATION;
        this.deAcceleration = 0.992;
        this.color = COLOR;
        this.height = 10;
        this.width = 10;
        this.collided = false;
        this.shooter = SHOOTER;
    }

    public void updateProjectile(double time, World w) { // Update the projectile's position and check for collisions
        position = position.add(velocity.times(time));
        velocity = velocity.times(deAcceleration);
        checkPlayerCollision(w.players, w);
        checkEnemyCollision(w.enemies, w);

        checkBossCollision(w.finalboss, w);

        checkNCollision(w.node1, w);
        checkNCollision(w.node2, w);
        checkNCollision(w.node3, w);
        checkNCollision(w.node4, w);
        checkNCollision(w.node5, w);
        checkNCollision(w.node6, w);
        checkNCollision(w.node7, w);
        checkNCollision(w.node8, w);
        checkNCollision(w.node9, w);
    }

    public boolean outOfBounds(World w) {   //Check if the projectile is out of bounds
        if (position.x < 0) {
            return true;
        }
        if (position.x > w.width) {
            return true;
        }
        if (position.y < 0) {
            return true;
        }
        if (position.y > w.height) {
            return true;
        }
        return false;
    }

    public boolean stopped() { //Check is the projectile has reached a slow enough speed, uses physcis functions
        double velocityMagnitude = Math.sqrt(Math.pow(this.velocity.x, 2) + Math.pow(this.velocity.y, 2));
        return velocityMagnitude < 10.0;
    }

    private void checkPlayerCollision(List<Player> players, World w) { // Check for collision with players and handle accordingly
        for (Player p : players) {
            if ((collision(p) && this.designation == 1) || (collision(p) && w.twoPlayer && shooter != p)) { // Check for collision with singular player and handle accordingly
                p.isHit(w); //Calls method in player to decrement health
                collided = true;
            }
        }
    }

    private void checkEnemyCollision(List<Enemy> enemies, World w) { //Check for collision with enemies and handle accordingly
        for (Enemy e : enemies) {
            if (collision(e) && this.designation == 0) { //Check for collision with singular enemy and handle accordingly
                e.isHit(); //Class method in enemy to decrement health
                collided = true;
            }
        }
    }
    
    private boolean collision(Person person) { //Collision between projectile and person
        boolean xOverlap = position.x + width > person.position.x && position.x < person.position.x + person.width;
        boolean yOverlap = position.y + height > person.position.y && position.y < person.position.y + person.height;
        return xOverlap && yOverlap;
    }

    private void checkBossCollision(Person boss, World w) { //Check for collision with a boss and handle accordingly
        if (collision(boss) && this.designation == 0) {
            w.finalboss.isHit(w);
            collided = true;
        }
    }

    private void checkNCollision(Node node, World w) {  //Check for collision with a node and handle accordingly
        if (nodeCollision(node)) {
            node.changeNode(node);
            collided = true;
        }
    }

    private boolean nodeCollision(Node n) { //Checks if the projectile collides with the nodes.
        double distanceX = Math.abs(position.x + width / 2.0 - (n.position.x + n.width / 2.0));
        double distanceY = Math.abs(position.y + height / 2.0 - (n.position.y + n.height / 2.0));

        return distanceX < n.width / 2.0 / 2.0 && distanceY < n.height / 2.0 + height / 2.0;
    }
    
    

    public void draw(Graphics g) { //Draws simple projectile
        g.setColor(color);
        g.fillOval((int) position.x, (int) position.y, width, height);
    }
}

