public class Player implements Comparable<Player> {
    private String name;
    private Bag bag;
    private Cauldron cauldron;
    private int victoryPoints;
    private int coins;
    private int rubies;
    private int lastTurnSpace;

    public Player(String name) {
        this.name = name;
        this.bag = new Bag();
        this.cauldron = new Cauldron();
        this.victoryPoints = 0;
        this.coins = 0;
        this.rubies = 1;
        this.lastTurnSpace = 0;
    }

    public Token drawToken() {
        return bag.draw();
    }

    public void addVictoryPoints(int n) { this.victoryPoints += n; }
    public void addCoins(int n) { this.coins += n; }
    public void addRubies(int n) { this.rubies += n; }

    public int getTrackPosition() {
        return victoryPoints % 50;
    }

    public boolean hasFiftySeal() {
        return victoryPoints >= 50;
    }

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

    public void loseUnspentCoins() {
        this.coins = 0;
    }

    public void buyFinalPoints() {
        while (coins >= 5) {
            coins -= 5;
            victoryPoints += 1;
        }
        while (rubies >= 2) {
            rubies -= 2;
            victoryPoints += 1;
        }
    }

    @Override
    public int compareTo(Player other) {
        if (this.victoryPoints != other.victoryPoints) {
            return Integer.compare(other.victoryPoints, this.victoryPoints);
        }
        return Integer.compare(other.lastTurnSpace, this.lastTurnSpace);
    }

    public Bag getBag() { return bag; }
    public Cauldron getCauldron() { return cauldron; }
    public String getName() { return name; }
    public int getVictoryPoints() { return victoryPoints; }
    public int getCoins() { return coins; }
    public int getRubies() { return rubies; }
    public int getLastTurnSpace() { return lastTurnSpace; }
    public void setLastTurnSpace(int space) { this.lastTurnSpace = space; }
}
