public class Board {
  private int day; // current day
  public static final int MAX_DAYS = 9; // the max amount of days
  private ArrayList<Player> players; // list of player objects
  private int startPlayer; // index of the start player, moves clockwise
  private ArrayList<Card> fortuneDeck; // list of Card objects currently in play
  private ArrayList<Card> discardDeck; // list of Card objects that have already been played
  private ArrayList<PotSpace> potSpaces; // 54 PotSpace objects, one occupies each pot space
  private HashSet<Integer> ratTailSpaces; // each scoring space that shows a rat tail
  private HashMap<String, int[]> shopPrices; // maps a color to an integer list of the costs of 1 chip, 2 chip, and 4 chip
  private HashMap<String, Integer> chipStock; // maps a color to a value that represents the number of it left
  private HashSet<String> unlockedColors; // colors that the shop sells, with yellow added on day 2 and purple on day 3

  public Board (ArrayList<Player> players) {
  }

  public void buildPot() {
  }

  public void buildFortuneDeck() {
  }

  public Card drawFortune() {
  }

  public void discard(Card c) {
  }

  public void nextDay() {
  }

  public boolean isGameOver() {
  }

  public int getLeaderPoints() {
  }

  public int getRatTailSteps(Player p) {
  }

  public PotSpace getSpace(int index) {
  }

  public int getPrice(String color, int value) {
  }

  public boolean inStock(String color, int value) {
  }

  public Player getLeftNeighbor(Player p) {
  }

  public Player getRightNeighbor(Player p) {
  }

  public ArrayList<Player> getPlayers() {
  }

  public int getDay() {
  }
}
