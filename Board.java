import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;


public class Board {
    public static final int MAX_DAYS = 9;
    private static final int POT_SIZE = 54;
    private static final int FORTUNE_CARD_COUNT = 24;

    private int day;
    private ArrayList<Player> players;
    private int startPlayer;

    private ArrayList<Card> fortuneDeck;
    private ArrayList<Card> discardDeck;

    private ArrayList<PotSpace> potSpaces;
    private HashSet<Integer> ratTailSpaces;

    private HashMap<String, int[]> shopPrices;
    private HashMap<String, Integer> chipStock;
    private HashSet<String> unlockedColors;

    private Random rand = new Random();

    private static final int[][] POT_DATA = {
        {0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 0, 0}, {4, 0, 0}, {5, 0, 1},
        {6, 1, 0}, {7, 1, 0}, {8, 1, 0}, {9, 1, 1}, {10, 2, 0}, {11, 2, 0},
        {12, 2, 0}, {13, 2, 1}, {14, 3, 0}, {15, 3, 0}, {15, 3, 1}, {16, 3, 0},
        {16, 4, 0}, {17, 4, 0}, {17, 4, 1}, {18, 4, 0}, {18, 5, 0}, {19, 5, 0},
        {19, 5, 1}, {20, 5, 0}, {20, 6, 0}, {21, 6, 0}, {21, 6, 1}, {22, 7, 0},
        {22, 7, 1}, {23, 7, 0}, {23, 8, 0}, {24, 8, 0}, {24, 8, 1}, {25, 9, 0},
        {25, 9, 1}, {26, 9, 0}, {26, 10, 0}, {27, 10, 0}, {27, 10, 1}, {28, 11, 0},
        {28, 11, 1}, {29, 11, 0}, {29, 12, 0}, {30, 12, 0}, {30, 12, 1}, {31, 12, 0},
        {31, 13, 0}, {32, 13, 0}, {32, 13, 1}, {33, 14, 0}, {33, 14, 1}, {35, 15, 0}
    };

    private static final int[] RAT_TAIL_DATA = {
        1, 3, 5, 7, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48, 51, 54
    };

    public Board(ArrayList<Player> players) {
        this.players = players;
        this.day = 1;
        this.startPlayer = 0;

        fortuneDeck = new ArrayList<>();
        discardDeck = new ArrayList<>();
        potSpaces = new ArrayList<>();
        ratTailSpaces = new HashSet<>();
        shopPrices = new HashMap<>();
        chipStock = new HashMap<>();
        unlockedColors = new HashSet<>();

        buildPot();
        buildFortuneDeck();

        for (int s : RAT_TAIL_DATA) {
            ratTailSpaces.add(s);
        }

        shopPrices.put("orange", new int[] {3});
        shopPrices.put("blue", new int[] {5, 10, 19});
        shopPrices.put("red", new int[] {6, 10, 16});
        shopPrices.put("green", new int[] {4, 8, 14});
        shopPrices.put("yellow", new int[] {8, 12, 18});
        shopPrices.put("purple", new int[] {9});
        shopPrices.put("black", new int[] {10});

        chipStock.put("orange1", 20);
        chipStock.put("blue1", 14);
        chipStock.put("blue2", 10);
        chipStock.put("blue4", 13);
        chipStock.put("red1", 12);
        chipStock.put("red2", 8);
        chipStock.put("red4", 12);
        chipStock.put("green1", 15);
        chipStock.put("green2", 10);
        chipStock.put("green4", 18);
        chipStock.put("yellow1", 12);
        chipStock.put("yellow2", 8);
        chipStock.put("yellow4", 12);
        chipStock.put("purple1", 15);
        chipStock.put("black1", 18);

        unlockedColors.add("orange");
        unlockedColors.add("blue");
        unlockedColors.add("red");
        unlockedColors.add("green");
        unlockedColors.add("black");
    }

    private void buildPot() {
        for (int i = 0; i < POT_SIZE; i++) {
            int coins = POT_DATA[i][0];
            int vp = POT_DATA[i][1];
            boolean ruby = POT_DATA[i][2] == 1;
            potSpaces.add(new PotSpace(coins, vp, ruby));
        }
    }

    private void buildFortuneDeck() {
        for (int i = 0; i < FORTUNE_CARD_COUNT; i++) {
            fortuneDeck.add(new Card(i));
        }
    }

    public Card drawFortune() {
        if (fortuneDeck.isEmpty()) {
            fortuneDeck.addAll(discardDeck);
            discardDeck.clear();
        }
        if (fortuneDeck.isEmpty()) {
            return null;
        }
        return fortuneDeck.remove(rand.nextInt(fortuneDeck.size()));
    }

    public void discard(Card c) {
        discardDeck.add(c);
    }

    public void nextDay() {
        day++;
        startPlayer = (startPlayer + 1) % players.size();

        if (day == 2) {
            unlockedColors.add("yellow");
        }
        if (day == 3) {
            unlockedColors.add("purple");
        }
        if (day == 6) {
            for (Player p : players) {
                p.getBag().add(new Chip("white", 1));
            }
        }
    }

    public boolean isGameOver() {
        return day > MAX_DAYS;
    }

    public int getLeaderPoints() {
        int max = 0;
        for (Player p : players) {
            max = Math.max(max, p.getVictoryPoints());
        }
        return max;
    }

    public int getRatTailSteps(Player p) {
        int leader = getLeaderPoints();
        int mine = p.getVictoryPoints();
        if (day == 1 || mine >= leader) {
            return 0;
        }
        int steps = 0;
        for (int s : ratTailSpaces) {
            if (s > mine && s <= leader) {
                steps++;
            }
        }
        return steps;
    }

    public PotSpace getSpace(int index) {
        return potSpaces.get(index);
    }

    public int getPrice(String color, int value) {
        int[] prices = shopPrices.get(color);
        if (prices == null) {
            return -1;
        }
        if (value == 1) return prices[0];
        if (value == 2 && prices.length > 1) return prices[1];
        if (value == 4 && prices.length > 2) return prices[2];
        return -1;
    }

    public boolean inStock(String color, int value) {
        Integer left = chipStock.get(color + value);
        return left != null && left > 0;
    }

    public boolean isUnlocked(String color) {
        return unlockedColors.contains(color);
    }

    public Player getLeftNeighbor(Player p) {
        int i = players.indexOf(p);
        return players.get((i + 1) % players.size());
    }

    public Player getRightNeighbor(Player p) {
        int i = players.indexOf(p);
        return players.get((i - 1 + players.size()) % players.size());
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getDay() {
        return day;
    }

    public int getStartPlayer() {
        return startPlayer;
    }
}
