import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Token implements Enlargeable {
    private String name;
    private String color;
    private int value;
    private int space;
    public static final int SIZE = 30;

    public Token(String name, String color, int value) {
        this.name = name;
        this.color = color;
        this.value = value;
        this.space = 0;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public int getValue() { return value; }
    public int getSpace() { return space; }
    public void setSpace(int s) { this.space = s; }

    public boolean isWhite() { return false; }
    public int getMoveBonus(Cauldron c) { return 0; }
    public int getLimitBonus(Cauldron c) { return 0; }
    public void onPlaced(Player p, Board b) {}
    public void endAction(Player p, Board b) {}

    protected Color getAwtColor() {
        switch (color.toLowerCase()) {
            case "white":  return new Color(245, 245, 240);
            case "orange": return new Color(235, 130, 30);
            case "blue":   return new Color(45, 120, 210);
            case "red":    return new Color(205, 45, 45);
            case "yellow": return new Color(235, 195, 35);
            case "green":  return new Color(55, 165, 75);
            case "purple": return new Color(145, 65, 185);
            case "black":  return new Color(45, 45, 50);
            default:       return Color.GRAY;
        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.setColor(getAwtColor());
        g.fillOval(x, y, SIZE, SIZE);

        g.setColor(Color.BLACK);
        g.drawOval(x, y, SIZE, SIZE);

        g.setColor(color.equalsIgnoreCase("black") || color.equalsIgnoreCase("blue") || color.equalsIgnoreCase("purple")
                ? Color.WHITE : Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        String label = String.valueOf(value);
        int tx = x + (SIZE - fm.stringWidth(label)) / 2;
        int ty = y + ((SIZE - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(label, tx, ty);
    }

    @Override
    public int getWidth() { return SIZE; }

    @Override
    public int getHeight() { return SIZE; }

    @Override
    public String toString() {
        return color + " " + value;
    }
}
