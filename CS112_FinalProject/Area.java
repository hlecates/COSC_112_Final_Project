
import java.awt.*;

//Works with the environment class to create an array that stores values for rows and columns of rectangles on the screen.
public class Area { //The background of each level
    Environment[][] enviro;
    int rows;
    int columns;

    public Area(World w, int ROWS, int COLUMNS) {
        this.rows = ROWS;
        this.columns = COLUMNS;
        enviro = new Environment[columns][rows];
        for (int i = 0; i < columns; i++) { //Intializes the enviroments 
            for (int j = 0; j < rows; j++) {
                enviro[i][j] = new Environment(new Pair(i * (w.width / columns), j * (w.height / rows)), w, rows,columns, new Color(0, 150 + w.random.nextInt(106), 0)); //Sets the initial color to a random shade of a color (in this case green)
            }
        }
    }
    //Draws each rectangle to fill the screen.
    public void draw(World w, Graphics g) {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                enviro[i][j].draw(g);
            }
        }
    }
}
class Environment { //What composes the area, ie indexs in a [][]
    Pair position;
    int height;
    int width;
    Color color;
    boolean solid;

    public Environment(Pair POSITION, World w, int rows, int columns, Color COLOR) { //Constructor
        this.position = POSITION;
        this.height = w.height / rows;
        this.width = w.width / columns;
        this.color = COLOR;
        this.solid = false;
    }

    public void setColor(Color c) { //Changes color, used for graphical purposes
        this.color = c;
    }

    public void setSolid() { //Used in levelTwo to create solid walls of maze
        this.solid = true;
    }

    public boolean isSolid() { //Returns solid boolean
        return solid;
    }

    public void draw(Graphics g) { //Draw
        g.setColor(color);
        g.fillRect((int) position.x, (int) position.y, (int) width, (int) height);
    }
}
