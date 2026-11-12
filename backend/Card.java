import java.awt.*;

public class Card {
    private int WIDTH, HEIGHT; // width and height of card
    
    private String name; // name of card
    private String description; // description of what the card does
    private boolean immediate; // true for purple cards, false for blue cards
    private int effectType; // the constant that selects the effect
    private int amount; // the number that effects uses, ex. spaces, rubies, chips
    private String[] options; // option labels for choice cards, null for other cards

    // constants for purple cards, need to add comments
    public static int DROPLET;
    public static int RUBY_FEWEST;
    public static int TOKEN_FEWEST_POINTS;
    public static int DOUBLE_RATS;
    public static int ROLL_ALL;
    public static int DRAW_CHIPS;
    public static int CHOICE;

    // constants for blue cards
    public static int RAISE_LIMIT;
    public static int ORANGE_BONUS;
    public static int FLASK_REFILL;
    public static int EXACT_SEVEN;
    public static int RUBY_SPACE_RUBY;
    public static int RUBY_SPACE_POINTS;
    public static int ROLL_TWICE;

    public Card(String name, String description, boolean immediate, int effectType, int amount, String[] options) {
        this.name = name;
        this.description = description;
        this.immediate = immediate;
        this.effectType = effectType;
        this.amount = amount;
        this.options = options;
    }

    // accesses the width of card
    public int getWidth() {
        return width;
    }

    // accesses the height of card
    public int getHeight() {
        return height;
    }

    // returns the name of the card
    public String getName() {
        return name;
    }

    // returns the description of the card
    public String getDescription() {
        return description;
    }
    
    // returns if the card is applied immediately or after the round
    public boolean isImmediate() {
        return immediate;
    }

    // returns the constant that selects the effect
    public int getEffectType() {
        returns effecType;
    }

    // returns the number that the effect uses
    public int getAmount() {
        return amount;
    }

    // returns the list of options
    public int[] getOptions() {
        return options;
    }

    // applies the immediate effects for purple cards
    public void apply(Board b, Round r) {
        // if the card is not of immediate type, then exit method
        if (!immediate) {
            return;
        }

        // runs through each effect type, and then applies the effect
        switch (effectType) {
            // moves droplet [amount] spaces
            case DROPLET:
                b.moveDroplet(amount);
                break;
            // gives [amount] rubies to the player with fewest victory points
            case RUBY_FEWEST:
                b.giveRubiesToFewest(amount);
                break;
            // gives a token to plater with the fewest victory points
            case TOKEN_FEWEST_POINTS:
                b.giveTokenToFewestPoints(amount);
                break;
            // doubles the rat tails for current round
            case DOUBLE_RATS:
                r.setDoubleRats(true);
                break;
            // every player rolls the bonus die
            case ROLL_ALL:
                r.rollDieForAll();
                break;
            // draws [amount] tokens from bag and immediately resolve
            case DRAW_CHIPS:
                r.drawChipsEffect(amount);
                break;
            // triggers the UI for the player to pick from options
            case CHOICE:
                r.promptCardChoic(this);
                break;
            default:
                break;
        }
    }

    
    // should probably build graphics class and frontend first
    public void draw(Graphics g, int x, int y) {
    }
}
