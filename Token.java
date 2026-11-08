import java.util.*;
import java.awt.*;

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
    }
    public String getName() {
        return name;
    }
    public String getColor() {
        return color;
    }
    public int getValue() {
        return value;
    }
    public int getSpace() {
        return space;
    }
    public void setSpace(int space) {
        this.space = space;
    }
    public boolean isWhite() {
        return false;
    }
    public int getMoveBonus(Cauldron c) {
        return 0;
    }
    public int getLimitBonus(Cauldron c) {
        return 0;
    }
    public void onPlaced(Player p, Board b) {}
    public void endAction(Player p, Board b) {}
    public void draw(Graphics g, int x, int y){}; //waiting until front end is in progress to continue
    public int getWidth(){ return SIZE; } //waiting until front end is in progress to continue
    public int getHeight(){ return SIZE; } //waiting until front end is in progress to continue
    public String toString() {
        return name + " (" + color + ", " + value + ")";
    }
    
    

}
