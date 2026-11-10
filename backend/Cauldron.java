import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import src.token.Token;

/*
 * Cauldron: one player's pot for one day. Every Player owns one Cauldron.
 *
 * How it works:
 *   - Tokens are drawn from the Bag and placed here one at a time.
 *   - The first token goes its distance after the droplet (or the rat stone, if there is one).
 *     Every later token goes its distance after the previous token.
 *   - Distance = token value + move bonus (only red tokens have a bonus).
 *   - The scoring space is the space right after the last token.
 *   - If the white tokens add up to more than the explode limit, the pot explodes.
 *
 * Status markers used in the comments:
 *   [DONE]         already written
 *   [NOT WRITTEN]  does not exist yet; this file will not compile until it does
 *   [TODO]         unfinished work inside this file
 *
 * Needs from other classes:
 *   PotSpace and Token (with all color subclasses)   [DONE]
 *   Bag: add(Token)                                  [NOT WRITTEN]
 *   Board: getSpace(int) returning the PotSpace      [NOT WRITTEN]
 */
public class Cauldron {

    // ---------------- FIELDS ----------------

    // Tokens placed today, in order. The last one is the most recently placed.
    private ArrayList<Token> pot;

    // Space the droplet is on. Starts at 0 and only moves forward.
    private int dropletStart;

    // How many spaces the rat stone is ahead of the droplet today (0 = no rat stone).
    private int ratSteps;

    // true = flask can be used; false = already used until it is refilled.
    private boolean flaskFull;

    // Extra white token limit from a blue fortune card (0 if none).
    private int cardLimitBonus;

    // Last space of the pot (the spoon space). The pot has spaces 0 to 53.
    public static final int LAST_SPACE = 53;

    // ---------------- CONSTRUCTOR ----------------

    // Starts the game with an empty pot, droplet on 0, no rat stone, and a full flask.
    public Cauldron() {
        pot = new ArrayList<Token>();
        dropletStart = 0;
        ratSteps = 0;
        flaskFull = true;
        cardLimitBonus = 0;
    }

    // ---------------- PLACING TOKENS ----------------

    // Places a token in the pot. Called by Round.drawChip() [NOT WRITTEN].
    public void placeToken(Token t) {
        // The move bonus is read BEFORE t is added, so a red token
        // sees the previous token as the last one in the pot.
        int distance = t.getValue() + t.getMoveBonus(this);

        int space = lastSpace() + distance;
        if (space > LAST_SPACE) {
            space = LAST_SPACE;     // cannot go past the spoon space
        }
        t.setSpace(space);
        pot.add(t);
    }

    // The space the next token is measured from:
    // the droplet (plus rat stone) if the pot is empty, otherwise the last token's space.
    private int lastSpace() {
        if (pot.isEmpty()) {
            return dropletStart + ratSteps;
        }
        return pot.get(pot.size() - 1).getSpace();
    }

    // The space this pot scores on. An explosion does NOT change it.
    // Used by EndOfRound [NOT WRITTEN] for the bonus die, rubies, points, and coins.
    public int getScoringSpace() {
        int last = lastSpace();
        if (last >= LAST_SPACE) {
            return LAST_SPACE;      // reached the spoon space
        }
        return last + 1;
    }

    // ---------------- EXPLOSION ----------------

    // White token limit = 7 + fortune card bonus + the LARGEST token bonus in the pot.
    // Only yellow tokens give a token bonus, and we use the largest so it never stacks.
    public int getExplodeLimit() {
        int largestBonus = 0;
        Iterator<Token> it = pot.iterator();
        while (it.hasNext()) {
            int bonus = it.next().getLimitBonus(this);
            if (bonus > largestBonus) {
                largestBonus = bonus;
            }
        }
        return 7 + cardLimitBonus + largestBonus;
    }

    // Adds up the value of the white tokens only.
    public int getWhiteTotal() {
        int total = 0;
        Iterator<Token> it = pot.iterator();
        while (it.hasNext()) {
            Token t = it.next();
            if (t.isWhite()) {
                total += t.getValue();
            }
        }
        return total;
    }

    // true if there are too many white tokens.
    // Round checks this after every placement [NOT WRITTEN, Round.drawChip()].
    public boolean isExploded() {
        return getWhiteTotal() > getExplodeLimit();
    }

    // ---------------- FLASK ----------------

    // Removes the last token if the flask is full, the pot has not exploded,
    // and that token is white. Returns the token (Round puts it back in the bag),
    // or null if the flask could not be used. Called by Round.useFlask() [NOT WRITTEN].
    public Token useFlask() {
        if (flaskFull && !pot.isEmpty() && !isExploded() && getLastToken().isWhite()) {
            Token t = pot.remove(pot.size() - 1);
            flaskFull = false;
            return t;
        }
        return null;
    }

    // ---------------- TOKEN ACTIONS ----------------

    // Moves the last token forward (used by GreenToken.endAction). Capped at the last space.
    // This changes the scoring space, since it comes from the last token.
    public void moveLastToken(int steps) {
        if (pot.isEmpty()) {
            return;
        }
        Token last = getLastToken();
        int space = last.getSpace() + steps;
        if (space > LAST_SPACE) {
            space = LAST_SPACE;
        }
        last.setSpace(space);
    }

    // ---------------- SETTERS ----------------

    // Set by Round.startRound() [NOT WRITTEN] each day, and back to 0 at the end of the day.
    public void setRatSteps(int n) {
        ratSteps = n;
    }

    // Set by Round.startRound() [NOT WRITTEN] from a blue fortune card [Card NOT WRITTEN].
    public void setCardLimitBonus(int n) {
        cardLimitBonus = n;
    }

    // Used by EndOfRound.endOfTurn() [NOT WRITTEN] (2 rubies) and the flask-refill card.
    public void fillFlask() {
        flaskFull = true;
    }

    // Raises where the first token of the next day is placed. Used by BlackToken [DONE],
    // and by EndOfRound and Card [NOT WRITTEN].
    public void moveDroplet(int n) {
        dropletStart += n;
    }

    // ---------------- LOOKUPS ----------------

    // How many tokens of a color (like "green") are in the pot.
    public int countColor(String color) {
        int count = 0;
        for (Token t : pot) {
            if (t.getColor().equals(color)) {
                count++;
            }
        }
        return count;
    }

    // Every token of one color in the pot.
    public ArrayList<Token> tokensOf(String color) {
        ArrayList<Token> list = new ArrayList<Token>();
        for (Token t : pot) {
            if (t.getColor().equals(color)) {
                list.add(t);
            }
        }
        return list;
    }

    // The different colors in the pot, each listed once, so EndOfRound.chipActions()
    // [NOT WRITTEN] runs each color's endAction only one time.
    public HashSet<String> getUniqueColors() {
        HashSet<String> colors = new HashSet<String>();
        for (Token t : pot) {
            colors.add(t.getColor());
        }
        return colors;
    }

    // ---------------- END OF DAY ----------------

    // Moves every token back into the bag. Called by EndOfRound.endOfTurn() [NOT WRITTEN].
    // Uses it.remove() because removing inside a for-each loop crashes the program.
    public void emptyInto(Bag b) {
        Iterator<Token> it = pot.iterator();
        while (it.hasNext()) {
            b.add(it.next());
            it.remove();
        }
    }

    // ---------------- GETTERS ----------------

    // The most recent token, or null if the pot is empty (check for null before using).
    public Token getLastToken() {
        if (pot.isEmpty()) {
            return null;
        }
        return pot.get(pot.size() - 1);
    }

    public ArrayList<Token> getPot() {
        return pot;
    }

    public int getDropletStart() {
        return dropletStart;
    }

    // ---------------- GRAPHICS ----------------

    // Draws the droplet, rat stone, and tokens. Called by Game.drawPlayers() [NOT WRITTEN].
    // Board is passed in because it holds the PotSpaces, which know where each space is drawn.
    // [TODO] Droplet and rat stone are placeholder circles; replace when the front end starts.
    // [TODO] Token.draw() and PotSpace.draw() are still empty, so only the placeholders show.
    public void draw(Graphics g, Card b) {
        // Droplet (never drawn past the last space)
        PotSpace dropletSpace = b.getSpace(Math.min(dropletStart, LAST_SPACE));
        g.setColor(Color.YELLOW);
        g.fillOval(dropletSpace.getX(), dropletSpace.getY(), 15, 15);

        // Rat stone, only if this player has one today
        if (ratSteps > 0) {
            PotSpace ratSpace = b.getSpace(Math.min(dropletStart + ratSteps, LAST_SPACE));
            g.setColor(Color.GRAY);
            g.fillOval(ratSpace.getX(), ratSpace.getY(), 15, 15);
        }

        // Each token on the space it was placed on
        for (Token t : pot) {
            PotSpace space = b.getSpace(t.getSpace());
            t.draw(g, space.getX(), space.getY());
        }
    }
}
