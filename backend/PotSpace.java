import java.awt.*;

public class PotSpace implements Enlargeable {
    private int coins, victoryPoints;
    private boolean hasRuby;
    private int x, y;
    public PotSpace(int coins, int victoryPoints, boolean hasRuby, int x, int y) {
        this.coins = coins;
        this.victoryPoints = victoryPoints;
        this.hasRuby = hasRuby;
        this.x = x;
        this.y = y;
    }
    public int getCoins() {
        return coins;
    }
    public int getVictoryPoints() {
        return victoryPoints;
    }
    public boolean hasRuby() {
        return hasRuby;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void draw(Graphics g, int x, int y) {} //waiting until front end is in progress to continue
    public int getWidth() { return 50; } //waiting until front end is in progress to continue
    public int getHeight() { return 50; } //waiting until front end is in progress to continue
}
