import java.util.*;

public class Card {
    private String name;
    private String description;
    private boolean immediate; // true for purple cards, false for blue cards
    private int effectType; // the constant that selects the effect
    private int amount; // the number that effects uses, ex. spaces, rubies, chips
    private String[] options; // option labels for choice cards, null for other cards

    public Card(String name, String description, boolean immediate, int effectType, int amount, String[] options) {

    }

    public boolean isImmediate() {
        return immediate;
    }

    public void apply(Board b, Round r) {

    }

    public void draw(Graphics g, int x, int y) {
        
    }
}
