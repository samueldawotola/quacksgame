import java.util.*;

public class Token {
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
}
