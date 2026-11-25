import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;

public class Board {
    private int day;
    public static final int MAX_DAYS = 9;
    private ArrayList<Player> players;
    private int startPlayer;
    private ArrayList<Card> fortuneDeck;
    private ArrayList<Card> discardDeck;
    private ArrayList<PotSpace> potSpaces;
    private HashSet<Integer> ratTailSpaces;
    private HashMap<String, int[]> shopPrices;
    private HashMap<String, Integer> chipStock;
    private HashSet<String> unlockedColors;

    public Board(ArrayList<Player> players) {
        this.players = players;
        this.day = 1;
        this.startPlayer = (int) (Math.random() * players.size());
        this.fortuneDeck = new ArrayList<>();
        this.discardDeck = new ArrayList<>();
        this.potSpaces = new ArrayList<>();
        this.ratTailSpaces = new HashSet<>();
        this.shopPrices = new HashMap<>();
        this.chipStock = new HashMap<>();
        this.unlockedColors = new HashSet<>();

        buildPot();
        buildRatTails();
        buildShop();
        buildFortuneDeck();
    }

    private void buildPot() {
        // 54 spaces (0 to 53). Spiral coordinates relative to a 360x360 cauldron panel.
        int[] coinsTable = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 15, 16, 16, 17,
            17, 18, 18, 19, 19, 20, 20, 21, 21, 22, 22, 23, 23, 24, 24, 25, 25, 26,
            26, 27, 27, 28, 28, 29, 29, 30, 30, 31, 31, 32, 32, 33, 33, 35
        };
        int[] vpTable = {
            0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4,
            4, 4, 5, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9,
            10, 10, 10, 11, 11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15
        };
        HashSet<Integer> rubySpaces = new HashSet<>(Arrays.asList(
            5, 9, 13, 16, 20, 24, 28, 30, 34, 36, 40, 42, 46, 50, 52
        ));

        double cx = 165.0;
        double cy = 140.0;
        for (int i = 0; i <= Cauldron.LAST_SPACE; i++) {
            double progress = i / (double) Cauldron.LAST_SPACE;
            double angle = progress * 4.2 * Math.PI;
            double radius = 18.0 + progress * 115.0;
            int sx = (int) (cx + Math.cos(angle) * radius) - 16;
            int sy = (int) (cy + Math.sin(angle) * radius * 0.82) - 16;
            potSpaces.add(new PotSpace(coinsTable[i], vpTable[i], rubySpaces.contains(i), sx, sy));
        }
    }

    private void buildRatTails() {
        int[] tails = {1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49};
        for (int t : tails) ratTailSpaces.add(t);
    }

    private void buildShop() {
        shopPrices.put("Orange", new int[]{3});
        shopPrices.put("Blue",   new int[]{4, 8, 14});
        shopPrices.put("Red",    new int[]{5, 9, 15});
        shopPrices.put("Yellow", new int[]{8, 12, 18});
        shopPrices.put("Green",  new int[]{6, 11, 21});
        shopPrices.put("Purple", new int[]{10});
        shopPrices.put("Black",  new int[]{10});

        chipStock.put("Orange1", 20);
        chipStock.put("Blue1", 15); chipStock.put("Blue2", 10); chipStock.put("Blue4", 10);
        chipStock.put("Red1", 15);  chipStock.put("Red2", 10);  chipStock.put("Red4", 10);
        chipStock.put("Yellow1", 15); chipStock.put("Yellow2", 10); chipStock.put("Yellow4", 10);
        chipStock.put("Green1", 15); chipStock.put("Green2", 10); chipStock.put("Green4", 10);
        chipStock.put("Purple1", 15);
        chipStock.put("Black1", 18);

        unlockedColors.addAll(Arrays.asList("Orange", "Blue", "Red", "Green", "Black"));
    }

    private BufferedImage loadCardImg(String filename) {
        try {
            File f = new File(filename);
            if (f.exists()) return ImageIO.read(f);
        } catch (IOException ignored) {}
        return null;
    }

    private void buildFortuneDeck() {
        fortuneDeck.add(new Card("A Good Start", "Use your rat stone normally OR pass up 1-3 rat tails for rubies.", true, Card.CHOICE, 1, new String[]{"Use Rats Normally", "Trade Tails for Rubies"}, loadCardImg("FC-AGoodStart.png")));
        fortuneDeck.add(new Card("A Second Chance", "Every player moves their droplet 1 space forward.", true, Card.DROPLET, 1, null, loadCardImg("FC-ASecondChance.png")));
        fortuneDeck.add(new Card("An Opportunist Moment", "Players with the fewest rubies receive 1 ruby.", true, Card.RUBY_FEWEST, 1, null, loadCardImg("FC-AnOpportunistMoment.png")));
        fortuneDeck.add(new Card("Beginner's Bonus", "Players with the fewest victory points receive a 1-chip.", true, Card.TOKEN_FEWEST_POINTS, 1, null, loadCardImg("FC-Beginner'sBonus.png")));
        fortuneDeck.add(new Card("Charity", "Players with the fewest rubies receive 2 rubies.", true, Card.RUBY_FEWEST, 2, null, loadCardImg("FC-Charity.png")));
        fortuneDeck.add(new Card("Choose Wisely", "Choose: move your droplet 2 spaces OR take 1 ruby.", true, Card.CHOICE, 2, new String[]{"Move Droplet +2", "Take 1 Ruby"}, loadCardImg("FC-ChooseWisely.png")));
        fortuneDeck.add(new Card("It's Shining Extra Bright", "Reaching a ruby space gives 1 extra ruby.", false, Card.RUBY_SPACE_RUBY, 1, null, loadCardImg("FC-It'sShiningExtraBright.png")));
        fortuneDeck.add(new Card("Just In Time", "All flasks refill for free at the end of the day.", false, Card.FLASK_REFILL, 1, null, loadCardImg("FC-JustInTime.png")));
        fortuneDeck.add(new Card("Less Is More", "Draw 4 chips; lowest total gets 1 ruby.", true, Card.DRAW_CHIPS, 4, null, loadCardImg("FC-LessIsMore.png")));
        fortuneDeck.add(new Card("Living In Luxury", "Reaching a ruby space awards 2 extra victory points.", false, Card.RUBY_SPACE_POINTS, 2, null, loadCardImg("FC-LivingInLuxury.png")));
        fortuneDeck.add(new Card("Lucky Devil", "Players rolling the bonus die roll twice.", false, Card.ROLL_TWICE, 2, null, loadCardImg("FC-LuckyDevil.png")));
        fortuneDeck.add(new Card("Magic Potion", "All flasks refill at the end of the day.", false, Card.FLASK_REFILL, 1, null, loadCardImg("FC-MagicPotion.png")));
        fortuneDeck.add(new Card("Pumpkin Patch Party", "Every orange chip moves 1 extra space.", false, Card.ORANGE_BONUS, 1, null, loadCardImg("FC-PumpkinPatchParty.png")));
        fortuneDeck.add(new Card("Rat Infestation", "Double every player's rat tails today.", true, Card.DOUBLE_RATS, 2, null, loadCardImg("FC-RatInfestation.png")));
        fortuneDeck.add(new Card("Rats Are Your Friends", "Double every player's rat tails today.", true, Card.DOUBLE_RATS, 2, null, loadCardImg("FC-RatsAreYourFriends.png")));
        fortuneDeck.add(new Card("Roll The Die", "Every player rolls the bonus die right now.", true, Card.ROLL_ALL, 1, null, loadCardImg("FC-RollTheDie.png")));
        fortuneDeck.add(new Card("Schadenfreude", "Move droplet 1 space forward.", true, Card.DROPLET, 1, null, loadCardImg("FC-Schadenfreude.png")));
        fortuneDeck.add(new Card("Seasoned Perfectly", "White total of exactly 7 moves droplet 1 space at end of day.", false, Card.EXACT_SEVEN, 1, null, loadCardImg("FC-SeasonedPerfectly.png")));
        fortuneDeck.add(new Card("Strong Ingredient", "White chip explosion limit is raised by 2 today.", false, Card.RAISE_LIMIT, 2, null, loadCardImg("FC-StrongIngredient.png")));
        fortuneDeck.add(new Card("The Pot Is Filling Up", "White chip explosion limit is raised by 1 today.", false, Card.RAISE_LIMIT, 1, null, loadCardImg("FC-ThePotIsFillingUp.png")));
        fortuneDeck.add(new Card("The Pot Is Full", "White chip explosion limit is raised by 2 today.", false, Card.RAISE_LIMIT, 2, null, loadCardImg("FC-ThePotIsFull.png")));
        fortuneDeck.add(new Card("Well Stirred", "Choose: refill flask OR take 1 ruby.", true, Card.CHOICE, 1, new String[]{"Refill Flask", "Take 1 Ruby"}, loadCardImg("FC-WellStirred.png")));
        fortuneDeck.add(new Card("Wheel And Deal", "Choose: take 2 coins OR 1 ruby.", true, Card.CHOICE, 2, new String[]{"Take 2 Coins", "Take 1 Ruby"}, loadCardImg("FC-WheelAndDeal.png")));
        fortuneDeck.add(new Card("You Only Get To Choose One", "Choose: move droplet 1 space OR draw a green 1-chip.", true, Card.CHOICE, 1, new String[]{"Move Droplet +1", "Gain Green 1-Chip"}, loadCardImg("FC-YouOnlyGetToChooseOne.png")));
    }

    public Card drawFortune() {
        if (fortuneDeck.isEmpty()) {
            fortuneDeck.addAll(discardDeck);
            discardDeck.clear();
        }
        int idx = (int) (Math.random() * fortuneDeck.size());
        return fortuneDeck.remove(idx);
    }

    public void discard(Card c) {
        if (c != null) discardDeck.add(c);
    }

    public void nextDay() {
        day++;
        startPlayer = (startPlayer + 1) % players.size();
        if (day == 2) unlockedColors.add("Yellow");
        if (day == 3) unlockedColors.add("Purple");
        if (day == 6) {
            for (Player p : players) {
                p.getBag().add(new WhiteToken(1));
            }
        }
    }

    public boolean isGameOver() { return day > MAX_DAYS; }

    public int getLeaderPoints() {
        int max = 0;
        for (Player p : players) {
            if (p.getVictoryPoints() > max) max = p.getVictoryPoints();
        }
        return max;
    }

    public int getRatTailSteps(Player p) {
        if (day == 1) return 0;
        int leaderPts = getLeaderPoints();
        int myPts = p.getVictoryPoints();
        if (myPts >= leaderPts) return 0;

        int tails = 0;
        for (int pos = myPts + 1; pos <= leaderPts; pos++) {
            if (ratTailSpaces.contains(pos % 50)) {
                tails++;
            }
        }
        return tails;
    }

    public PotSpace getSpace(int index) {
        if (index < 0 || index >= potSpaces.size()) return null;
        return potSpaces.get(index);
    }

    public int getPrice(String color, int value) {
        int[] prices = shopPrices.get(color);
        if (prices == null) return 999;
        if (value == 1) return prices[0];
        if (value == 2 && prices.length > 1) return prices[1];
        if (value == 4 && prices.length > 2) return prices[2];
        return 999;
    }

    public boolean inStock(String color, int value) {
        if (!unlockedColors.contains(color)) return false;
        String key = color + value;
        return chipStock.getOrDefault(key, 0) > 0;
    }

    public Token buyTokenFromStock(String color, int value) {
        String key = color + value;
        int left = chipStock.getOrDefault(key, 0);
        if (left <= 0) return null;
        chipStock.put(key, left - 1);

        switch (color) {
            case "Orange": return new OrangeToken();
            case "Blue":   return new BlueToken(value);
            case "Red":    return new RedToken(value);
            case "Yellow": return new YellowToken(value);
            case "Green":  return new GreenToken(value);
            case "Purple": return new PurpleToken();
            case "Black":  return new BlackToken();
            default:       return null;
        }
    }

    public Player getLeftNeighbor(Player p) {
        int idx = players.indexOf(p);
        return players.get((idx - 1 + players.size()) % players.size());
    }

    public Player getRightNeighbor(Player p) {
        int idx = players.indexOf(p);
        return players.get((idx + 1) % players.size());
    }

    public ArrayList<Player> getPlayers() { return players; }
    public ArrayList<PotSpace> getPotSpaces() { return potSpaces; }
    public HashSet<String> getUnlockedColors() { return unlockedColors; }
    public int getDay() { return day; }
    public int getStartPlayer() { return startPlayer; }
}
