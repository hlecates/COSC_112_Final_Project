import java.awt.*;


public abstract class Button {
    Color color;
    String string;
    Pair position;

    public Button(Color COLOR, String STRING, Pair POSITION) {
        this.color = COLOR;
        this.string = STRING;
        this.position = POSITION;
    }

    abstract public void collision(Player p, World w); // Override Methodadada

    abstract public void handleCollision(Player p, World w); // Override Method

    abstract public void draw(Graphics g, World w); // Override Method
}

class levelStart extends Button { //Button to start Level Mode
    public levelStart(Color COLOR, String STRING, Pair POSITION) {
        super(COLOR, STRING, POSITION);
    }

    @Override
    public void collision(Player p, World w) {
        if (checkCollision(p)) {
            handleCollision(p, w);
        }
    }
    @Override
    public void handleCollision(Player p, World w) { //Function of the button
        if (w.levelIndex == 0 && !w.twoPlayer) {
            w.levelIndex++;
        }
    }
    public boolean checkCollision(Player p) { //Deals with specific player collision
        return !(p.position.x > position.x - 10 + 150 || p.position.x + p.width < position.x - 10 || p.position.y > position.y - 50 + 70 || p.position.y + p.height < position.y - 50);
    }

    @Override
    public void draw(Graphics g, World w) { //Draws using image reader
        g.setColor(color);
        g.drawImage(w.image.levelsButton, (int) position.x - 10, (int) position.y - 50, 150, 70, null); 

        // g.drawRect((int)(position.x - 10), (int)(position.y - 50), 150, 70);
        // g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
        // g.drawString(string, (int)position.x, (int)position.y);s
    }
}

class twoPlayerStart extends Button { //Button to start arean mode / two person mode
    public twoPlayerStart(Color COLOR, String STRING, Pair POSITION) {
        super(COLOR, STRING, POSITION);
    }

    public void collision(Player p, World w) {
        if (checkCollision(p, w)) {
            handleCollision(p, w);
        }
    }

    public void handleCollision(Player p, World w) { //Function of button
        w.updateTwoPlayer();
    }

    public boolean checkCollision(Player p, World w) { //Deals with specific player collision
        return !(p.position.x > position.x - 10 + 150 || p.position.x + p.width < position.x - 10 || p.position.y > position.y - 50 + 70 || p.position.y + p.height < position.y - 50);
    }

    public void draw(Graphics g, World w) { //Draws using image reader
        g.setColor(color);
        g.drawImage(w.image.arenaButton, (int) position.x - 15, (int) position.y - 50, 150, 70, null);

        // g.drawRect((int)(position.x - 15), (int)(position.y - 50), 150, 70);
        // g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
        // g.drawString(string, (int)position.x, (int)position.y);
    }
}

