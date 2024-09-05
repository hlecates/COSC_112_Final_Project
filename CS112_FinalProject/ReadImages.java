import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Image;


public class ReadImages {
    public Image title;
    public Image levelsButton;
    public Image arenaButton;
    public Image key;
    public Image player;
    public Image enemy;
    public Image heart;
    public Image invulnerablePowerup;
    public Image projectilePowerup;
    public Image boss;
    public Image gameOver;

    public ReadImages() {
        try { // Reads each of the png files by name
            this.title = ImageIO.read(new File("Images/TitleCard.png"));
            this.levelsButton = ImageIO.read(new File("Images/LevelsButton.png"));
            this.arenaButton = ImageIO.read(new File("Images/ArenaButton.png"));
            this.key = ImageIO.read(new File("Images/Key.png"));
            this.player = ImageIO.read(new File("Images/Player.png"));
            this.enemy = ImageIO.read(new File("Images/Enemy.png"));
            this.heart = ImageIO.read(new File("Images/Heart.png"));
            this.invulnerablePowerup = ImageIO.read(new File("Images/Shield.png"));
            this.projectilePowerup = ImageIO.read(new File("Images/ProjectilePowerup.png"));
            this.boss = ImageIO.read((new File("Images/Boss.png")));
            this.gameOver = ImageIO.read((new File("Images/GameOver.png")));

        } catch (IOException e) { // If fails sends an error
            System.err.println(e);
        }
    }
}
