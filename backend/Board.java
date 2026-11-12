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


  // have to finish buildfortunedeck and buildpot
  public Board (ArrayList<Player> players) {
  }

  // todo
  public void buildPot() {
    for (int i = 0; i < 53; i++) {
      PotSpace new ps = PotSpace(
  }

  // finish card class first
  public void buildFortuneDeck() {
    for (int 
  }

  public Card drawFortune() {
  }

  // adds Card c to the discard deck
  public void discard(Card c) {
    discardDeck.add(c);
  }

  public void nextDay() {
  }

  // checks to see if game is over by checking if 9 days have been played 
  public boolean isGameOver() {
    return day > MAX_DAYS;
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

  // returns the array list of player objects
  public ArrayList<Player> getPlayers() {
    return players;
  }

  // returns the current day
  public int getDay() {
    return day;
  }
}
