import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class PotSpace implements Enlargeable {
    private int coins;
    private int victoryPoints;
    private boolean hasRuby;
    private int x;
    private int y;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;

    public PotSpace(int coins, int victoryPoints, boolean hasRuby, int x, int y) {
        this.coins = coins;
        this.victoryPoints = victoryPoints;
        this.hasRuby = hasRuby;
        this.x = x;
        this.y = y;
    }

    public int getCoins() { return coins; }
    public int getVictoryPoints() { return victoryPoints; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean hasRuby() { return hasRuby; }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.setColor(new Color(235, 225, 195, 210));
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(new Color(90, 65, 35));
        g.drawOval(x, y, WIDTH, HEIGHT);

        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(coins), x + 4, y + 14);

        if (victoryPoints > 0) {
            g.setColor(new Color(30, 110, 45));
            g.drawString(victoryPoints + "p", x + 14, y + 26);
        }
        if (hasRuby) {
            g.setColor(Color.RED);
            int[] rx = {x + 23, x + 27, x + 23, x + 19};
            int[] ry = {y + 4, y + 8, y + 12, y + 8};
            g.fillPolygon(rx, ry, 4);
        }
    }

    @Override
    public int getWidth() { return WIDTH; }

    @Override
    public int getHeight() { return HEIGHT; }
}
