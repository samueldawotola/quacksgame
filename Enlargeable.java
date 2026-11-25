import java.awt.Graphics;

public interface Enlargeable {
    void draw(Graphics g, int x, int y);
    int getWidth();
    int getHeight();
}
