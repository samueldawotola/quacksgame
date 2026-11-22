// One player. Owns a Bag and a Cauldron.
public class Player implements Comparable<Player> {

    private String name;
    private Bag bag;              
    private Cauldron cauldron;
    private int victoryPoints;
    private int coins;
    private int rubies;           // starts at 1
    private int lastTurnSpace;    // scoring space on the last day, used as the tiebreaker

    public Player(String name) {
        this.name = name;
        bag = new Bag();
        cauldron = new Cauldron();
        victoryPoints = 0;
        coins = 0;
        rubies = 1;
        lastTurnSpace = 0;
    }

    // Draws a random token. Returns null if the bag is empty, so callers must check for null.
    public Token drawToken() {
        return bag.draw();
    }

    public void addVictoryPoints(int n) {
        victoryPoints += n;
    }

    public void addCoins(int n) {
        coins += n;
    }

    public void addRubies(int n) {
        rubies += n;
    }

    // Subtracts only if the player has enough. Returns true if it worked.
    public boolean spendCoins(int n) {
        if (coins >= n) {
            coins -= n;
            return true;
        }
        return false;
    }

    public boolean spendRubies(int n) {
        if (rubies >= n) {
            rubies -= n;
            return true;
        }
        return false;
    }

    // Unspent coins are lost at the end of each day.
    public void loseUnspentCoins() {
        coins = 0;
    }

    // After day 9: 5 coins = 1 point, then 2 rubies = 1 point, as many times as possible.
    public void buyFinalPoints() {
        while (spendCoins(5)) {
            addVictoryPoints(1);
        }
        while (spendRubies(2)) {
            addVictoryPoints(1);
        }
    }

    // Marker position on the 50-space track (wraps around after 50 points).
    public int getTrackPosition() {
        return victoryPoints % 50;
    }

    // true once the marker has gone around, so the 0/50 seal shows 50.
    public boolean hasFiftySeal() {
        return victoryPoints >= 50;
    }

    // Victory points first, then lastTurnSpace. Sorts lowest to highest.
    public int compareTo(Player other) {
        if (victoryPoints != other.victoryPoints) {
            return Integer.compare(victoryPoints, other.victoryPoints);
        }
        return Integer.compare(lastTurnSpace, other.lastTurnSpace);
    }

    public Bag getBag() {
        return bag;
    }

    public Cauldron getCauldron() {
        return cauldron;
    }

    public String getName() {
        return name;
    }

    // [ADDED] Game needs these to draw the stats and save the tiebreaker.
    public int getVictoryPoints() {
        return victoryPoints;
    }

    public int getCoins() {
        return coins;
    }

    public int getRubies() {
        return rubies;
    }

    public int getLastTurnSpace() {
        return lastTurnSpace;
    }

    public void setLastTurnSpace(int space) {
        lastTurnSpace = space;
    }
}
