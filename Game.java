import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*
 * Game: stores which screen is showing, draws everything, and reads mouse clicks.
 * Buttons are Rectangles that we draw and check with contains()
 *
 * Tags: [NOT WRITTEN] = does not exist yet, [ADD] = a method missing from the class
 * definition that the named class needs to get, [TODO] = unfinished.
 *
 * Needs [ADD]: Board.getStartPlayer(), Board.isUnlocked(String), Round.getChoiceOptions(),
 *              EndOfRound.addBoughtTokens()
 * Game does the Evaluation Phase clicks itself, so EndOfRound.runEndOfRound() is not used.
 */
public class Game extends JPanel implements MouseListener {

    // ================= GRAPHICS NOTE FOR THE GROUP =================
    // Every position, size, and color below is a placeholder. Change them however
    // you like so the screen matches the art, I don't know exactly how we want it to look. Drawing and click code use the same
    // constants in the rectangles, so changing a value here moves the drawing and its click area together, so hopefully it shouldn't cause a problem.
    // ================================================================

    public static final int STATE_MENU = 0;
    public static final int STATE_DRAW = 1;
    public static final int STATE_END_OF_ROUND = 2;
    public static final int STATE_GAME_OVER = 3;

    // Evaluation Phase steps that need a click
    private static final int PHASE_CHOICE = 0;   // exploded player picks points or coins
    private static final int PHASE_SHOP = 1;
    private static final int PHASE_RUBIES = 2;

    private static final int NUM_PLAYERS = 4;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;

    // Pot area. Board.buildPot() [NOT WRITTEN] should place the 54 PotSpaces inside it.
    public static final int POT_X = 40;
    public static final int POT_Y = 180;
    public static final int POT_SIZE = 600;

    // Scoring track (50 spaces around the edge of this rectangle)
    private static final int TRACK_X = 40;
    private static final int TRACK_Y = 20;
    private static final int TRACK_W = 1180;
    private static final int TRACK_H = 110;
    private static final int TRACK_CELL = 24;

    private static final int CARD_X = 1060;
    private static final int CARD_Y = 180;
    private static final int STATS_X = 680;
    private static final int STATS_Y = 450;
    private static final int STATS_H = 60;

    private static final int SHOP_X = 680;
    private static final int SHOP_Y = 250;
    private static final int SHOP_W = 135;
    private static final int SHOP_H = 40;

    // Clickable rectangles (x, y, width, height)
    private static final Rectangle PLAY_RECT = new Rectangle(540, 600, 200, 60);
    private static final Rectangle DRAW_RECT = new Rectangle(680, 700, 120, 50);
    private static final Rectangle FLASK_RECT = new Rectangle(820, 700, 120, 50);
    private static final Rectangle STOP_RECT = new Rectangle(960, 700, 120, 50);
    private static final Rectangle POINTS_RECT = new Rectangle(680, 200, 230, 60);
    private static final Rectangle COINS_RECT = new Rectangle(930, 200, 230, 60);
    private static final Rectangle DROPLET_RECT = new Rectangle(680, 200, 230, 60);
    private static final Rectangle RUBY_FLASK_RECT = new Rectangle(930, 200, 230, 60);
    private static final Rectangle DONE_RECT = new Rectangle(1100, 700, 120, 50);
    private static final Rectangle PLAY_AGAIN_RECT = new Rectangle(540, 700, 200, 60);

    private static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
    private static final String[] SHOP_COLORS = {"orange", "blue", "red", "green", "black", "yellow", "purple"};
    private static final int[] SHOP_VALUES = {1, 2, 4};
    private static final double ENLARGE_SCALE = 2.0;

    // ---------------- FIELDS ----------------

    private Board board;           
    private Round round;               
    private EndOfRound endOfRound;    
    private int state;

    private int[][] trackPoints;        // x and y of each track space (0 to 50)
    private Image menuImage;
    private Image potImage;
   

    // Items that can be enlarged this frame, keyed by their spot on screen.
    // LinkedHashMap keeps drawing order so the item on top wins a click.
    private HashMap<Rectangle, Enlargeable> zones;
    private Enlargeable enlargedItem;   // null when nothing is enlarged
    private int enlargedX, enlargedY;

    // Evaluation Phase progress
    private int endPhase;
    private int endIndex;                       // which player in the current phase
    private ArrayList<Player> order;            // players starting with the start player
    private ArrayList<Player> choiceQueue;      // exploded players who must choose
    private Player endFocus;                    // player whose pot is shown
    private ArrayList<String> shopColors;       // items the current shopper can pick
    private ArrayList<Integer> shopValues;
    private ArrayList<Integer> selected;        // chosen items (max 2)

    private ArrayList<Player> ranking;          // best player first
    private ArrayList<Player> winners;          // everyone tied for first

    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(this);

        trackPoints = new int[51][2];
        fillTrackPoints();

        zones = new LinkedHashMap<Rectangle, Enlargeable>();
        order = new ArrayList<Player>();
        choiceQueue = new ArrayList<Player>();
        shopColors = new ArrayList<String>();
        shopValues = new ArrayList<Integer>();
        selected = new ArrayList<Integer>();

        menuImage = new ImageIcon("Menu.png").getImage();
        potImage = new ImageIcon("DrawingPotsDefault.png").getImage();
        state = STATE_MENU;
    }

    // Spreads spaces 0 to 49 around the edge of the track rectangle. Space 50 = space 0.
    private void fillTrackPoints() {
        int perimeter = 2 * (TRACK_W + TRACK_H);
        for (int i = 0; i < 50; i++) {
            int d = i * perimeter / 50;
            int x, y;
            if (d < TRACK_W) {                               // top
                x = TRACK_X + d;
                y = TRACK_Y;
            } else if (d < TRACK_W + TRACK_H) {              // right
                x = TRACK_X + TRACK_W;
                y = TRACK_Y + (d - TRACK_W);
            } else if (d < 2 * TRACK_W + TRACK_H) {          // bottom
                x = TRACK_X + TRACK_W - (d - TRACK_W - TRACK_H);
                y = TRACK_Y + TRACK_H;
            } else {                                         // left
                x = TRACK_X;
                y = TRACK_Y + TRACK_H - (d - 2 * TRACK_W - TRACK_H);
            }
            trackPoints[i][0] = x;
            trackPoints[i][1] = y;
        }
        trackPoints[50][0] = trackPoints[0][0];
        trackPoints[50][1] = trackPoints[0][1];
    }

    // Game Setup Algorithm: create 4 players, the Board, Round, and EndOfRound, then start day 1.
    // Board's constructor should pick the random start player.
    private void startGame() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            players.add(new Player("Player " + (i + 1)));
        }
        board = new Board(players);
        round = new Round(board);
        endOfRound = new EndOfRound(board);
        enlargedItem = null;
        state = STATE_DRAW;
        round.startRound();
    }

    // ---------------- DRAWING ----------------

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        zones.clear();

        if (state == STATE_MENU) {
            drawMenu(g);
        } else if (state == STATE_DRAW) {
            drawBoard(g);
            drawPlayers(g);
            drawFortune(g);
            if (round.getChoiceOptions() != null) {
                drawChoice(g);
            } else {
                drawActionButtons(g);
            }
        } else if (state == STATE_END_OF_ROUND) {
            drawBoard(g);
            drawPlayers(g);
            drawEndOfRound(g);
        } else if (state == STATE_GAME_OVER) {
            drawBoard(g);
            drawPlayers(g);
            drawGameOver(g);
        }

        // Drawn last so the enlarged copy sits on top
        if (enlargedItem != null) {
            enlarge(g, enlargedItem, enlargedX, enlargedY, ENLARGE_SCALE);
        }
    }

    private void drawMenu(Graphics g) {
        g.drawImage(menuImage, 0, 0, WIDTH, HEIGHT, this);
        drawButton(g, PLAY_RECT, "Play");
    }

    // Scoring track, pot picture, and the 54 pot spaces (each can be enlarged).
    private void drawBoard(Graphics g) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int i = 0; i <= 50; i++) {
            g.setColor(Color.WHITE);
            g.fillRect(trackPoints[i][0], trackPoints[i][1], TRACK_CELL, TRACK_CELL);
            g.setColor(Color.BLACK);
            g.drawRect(trackPoints[i][0], trackPoints[i][1], TRACK_CELL, TRACK_CELL);
            g.drawString("" + i, trackPoints[i][0] + 3, trackPoints[i][1] + 15);
        }

        g.drawImage(potImage, POT_X, POT_Y, POT_SIZE, POT_SIZE, this);

        for (int i = 0; i <= Cauldron.LAST_SPACE; i++) {
            PotSpace s = board.getSpace(i);
            s.draw(g, s.getX(), s.getY());          // PotSpace.draw() is still empty [TODO]
            registerZone(new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight()), s);
        }
    }

    // Each player's stats and track marker, plus the focused player's pot.
    private void drawPlayers(Graphics g) {
        ArrayList<Player> players = board.getPlayers();
        Player focus = focusPlayer();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int y = STATS_Y + i * STATS_H;

            g.setColor(PLAYER_COLORS[i]);
            g.fillRect(STATS_X, y, 20, 20);
            if (p == focus) {
                g.drawRect(STATS_X - 4, y - 4, 480, STATS_H - 6);   // outline whose pot is showing
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(p.getName(), STATS_X + 30, y + 15);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("Points: " + p.getVictoryPoints() + "   Coins: " + p.getCoins()
                    + "   Rubies: " + p.getRubies(), STATS_X + 30, y + 35);

            // Marker on the track (small offset so markers on one space do not overlap)
            int pos = p.getTrackPosition();
            int mx = trackPoints[pos][0] + (i % 2) * 11 + 1;
            int my = trackPoints[pos][1] + (i / 2) * 11 + 1;
            g.setColor(PLAYER_COLORS[i]);
            g.fillOval(mx, my, 10, 10);
            g.setColor(Color.BLACK);
            g.drawOval(mx, my, 10, 10);

            if (p.hasFiftySeal()) {
                g.setFont(new Font("SansSerif", Font.BOLD, 10));
                g.drawString("50", mx - 2, my + 22);                // [TODO] real seal art
            }
        }

        if (focus != null) {
            Cauldron c = focus.getCauldron();
            c.draw(g, board);
            for (Token t : c.getPot()) {
                PotSpace s = board.getSpace(t.getSpace());
                registerZone(new Rectangle(s.getX(), s.getY(), t.getWidth(), t.getHeight()), t);
            }
        }
    }

    private void drawFortune(Graphics g) {
        Card card = round.getFortune();
        if (card == null) {
            return;
        }
        card.draw(g, CARD_X, CARD_Y);
        registerZone(new Rectangle(CARD_X, CARD_Y, card.getWidth(), card.getHeight()), card);
    }

    private void drawActionButtons(Graphics g) {
        drawButton(g, DRAW_RECT, "Draw");
        drawButton(g, FLASK_RECT, "Flask");
        drawButton(g, STOP_RECT, "Stop");
    }

    // One rectangle per option of a fortune card choice.
    private void drawChoice(Graphics g) {
        String[] options = round.getChoiceOptions();
        for (int i = 0; i < options.length; i++) {
            drawButton(g, choiceRect(i), options[i]);
        }
    }

    private Rectangle choiceRect(int i) {
        return new Rectangle(680, 200 + i * 70, 340, 55);
    }

    private Rectangle shopRect(int i) {
        return new Rectangle(SHOP_X + (i % 4) * SHOP_W, SHOP_Y + (i / 4) * (SHOP_H + 10), SHOP_W - 5, SHOP_H);
    }

    private void drawEndOfRound(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Day " + board.getDay() + " - Evaluation", 680, 170);
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));

        if (endPhase == PHASE_CHOICE) {
            Player p = choiceQueue.get(endIndex);
            PotSpace s = board.getSpace(p.getCauldron().getScoringSpace());
            g.drawString(p.getName() + " exploded. Choose one:", 680, 190);
            drawButton(g, POINTS_RECT, "Victory Points (" + s.getVictoryPoints() + ")");
            drawButton(g, COINS_RECT, "Coins (" + s.getCoins() + ")");

        } else if (endPhase == PHASE_SHOP) {
            Player p = order.get(endIndex);
            g.drawString(p.getName() + " has " + p.getCoins() + " coins. Pick up to 2 different colors.", 680, 190);
            for (int i = 0; i < shopColors.size(); i++) {
                Rectangle r = shopRect(i);
                drawButton(g, r, shopColors.get(i) + " " + shopValues.get(i) + " - "
                        + board.getPrice(shopColors.get(i), shopValues.get(i)) + "c");
                if (selected.contains(i)) {
                    g.setColor(Color.RED);
                    g.drawRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
                    g.setColor(Color.BLACK);
                }
            }
            drawButton(g, DONE_RECT, selected.isEmpty() ? "Skip" : "Buy");

        } else if (endPhase == PHASE_RUBIES) {
            Player p = order.get(endIndex);
            g.drawString(p.getName() + " has " + p.getRubies() + " rubies. 2 rubies per use.", 680, 190);
            drawButton(g, DROPLET_RECT, "Move droplet");
            drawButton(g, RUBY_FLASK_RECT, "Refill flask");
            drawButton(g, DONE_RECT, "Done");
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("Final Results", 680, 180);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        for (int i = 0; i < ranking.size(); i++) {
            Player p = ranking.get(i);
            String line = (i + 1) + ". " + p.getName() + " - " + p.getVictoryPoints() + " points";
            if (winners.contains(p)) {
                line += "  (WINNER)";
            }
            g.drawString(line, 680, 220 + i * 30);
        }
        drawButton(g, PLAY_AGAIN_RECT, "Play Again");
    }

    // Draws one labeled rectangle. Every button uses this.
    private void drawButton(Graphics g, Rectangle r, String label) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);
        g.drawRect(r.x, r.y, r.width, r.height);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, r.x + (r.width - fm.stringWidth(label)) / 2,
                r.y + (r.height + fm.getAscent()) / 2 - 2);
    }

    // ---------------- ENLARGING ----------------

    // Saves an item and its spot on screen so a click there can enlarge it.
    // Call this right after drawing any Enlargeable item.
    private void registerZone(Rectangle r, Enlargeable item) {
        zones.put(r, item);
    }

    // Works on any Enlargeable: scales the Graphics around the item's center and calls the
    // item's own draw method. (x, y) is the item's normal top-left corner.
    private void enlarge(Graphics g, Enlargeable item, int x, int y, double scale) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = item.getWidth();
        int h = item.getHeight();

        // Keep the enlarged copy on screen
        double cx = x + w / 2.0;
        double cy = y + h / 2.0;
        cx = Math.max(w * scale / 2, Math.min(getWidth() - w * scale / 2, cx));
        cy = Math.max(h * scale / 2, Math.min(getHeight() - h * scale / 2, cy));

        g2.translate(cx, cy);
        g2.scale(scale, scale);
        g2.translate(-w / 2.0, -h / 2.0);
        item.draw(g2, 0, 0);
        g2.dispose();
    }

    private void shrink() {
        enlargedItem = null;
    }

    // Enlarges the item under the click. The last match wins because it was drawn on top.
    private void tryEnlarge(Point click) {
        Rectangle foundRect = null;
        Enlargeable found = null;
        for (Map.Entry<Rectangle, Enlargeable> entry : zones.entrySet()) {
            if (entry.getKey().contains(click)) {
                foundRect = entry.getKey();
                found = entry.getValue();
            }
        }
        if (found != null) {
            enlargedItem = found;
            enlargedX = foundRect.x;
            enlargedY = foundRect.y;
        }
    }

    // ---------------- MOUSE INPUT ----------------

    public void mouseClicked(MouseEvent e) {
        Point click = e.getPoint();

        // While something is enlarged, the first click only closes it
        if (enlargedItem != null) {
            shrink();
            repaint();
            return;
        }

        boolean handled = false;
        if (state == STATE_MENU) {
            if (PLAY_RECT.contains(click)) {
                startGame();
                handled = true;
            }
        } else if (state == STATE_DRAW) {
            handled = handleDrawClick(click);
        } else if (state == STATE_END_OF_ROUND) {
            handled = handleEndClick(click);
        } else if (state == STATE_GAME_OVER) {
            if (PLAY_AGAIN_RECT.contains(click)) {
                state = STATE_MENU;
                handled = true;
            }
        }

        if (!handled) {
            tryEnlarge(click);      // not a button, so it may be a token, card, or pot space
        }

        updateState();
        repaint();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    // Potions Phase clicks. Returns true if the click hit a button.
    // Round is assumed to move to the next player by itself after each action.
    private boolean handleDrawClick(Point click) {
        Player p = round.getCurrentPlayer();
        String[] options = round.getChoiceOptions();

        if (options != null) {                      // a fortune card is waiting for a choice
            for (int i = 0; i < options.length; i++) {
                if (choiceRect(i).contains(click)) {
                    round.resolveChoice(p, i);
                    return true;
                }
            }
            return false;
        }

        if (DRAW_RECT.contains(click)) {
            round.drawChip(p);
            return true;
        }
        if (FLASK_RECT.contains(click)) {
            round.useFlask(p);
            return true;
        }
        if (STOP_RECT.contains(click)) {
            round.stop(p);
            return true;
        }
        return false;
    }

    // Evaluation Phase clicks. Returns true if the click hit a button.
    private boolean handleEndClick(Point click) {
        if (endPhase == PHASE_CHOICE) {
            Player p = choiceQueue.get(endIndex);
            if (POINTS_RECT.contains(click) || COINS_RECT.contains(click)) {
                endOfRound.scorePot(p, POINTS_RECT.contains(click));   // true = points, false = coins
                endIndex++;
                if (endIndex >= choiceQueue.size()) {
                    beginShop();
                } else {
                    endFocus = choiceQueue.get(endIndex);
                }
                return true;
            }

        } else if (endPhase == PHASE_SHOP) {
            for (int i = 0; i < shopColors.size(); i++) {
                if (shopRect(i).contains(click)) {
                    toggleShopItem(i);
                    return true;
                }
            }
            if (DONE_RECT.contains(click)) {
                buySelected();
                return true;
            }

        } else if (endPhase == PHASE_RUBIES) {
            Player p = order.get(endIndex);
            if (DROPLET_RECT.contains(click)) {
                if (p.spendRubies(2)) {
                    p.getCauldron().moveDroplet(1);
                }
                return true;
            }
            if (RUBY_FLASK_RECT.contains(click)) {
                if (p.spendRubies(2)) {
                    p.getCauldron().fillFlask();
                }
                return true;
            }
            if (DONE_RECT.contains(click)) {
                finishRubies();
                return true;
            }
        }
        return false;
    }

    // ---------------- STATE CHANGES ----------------

    // Moves to the Evaluation Phase when every player has finished drawing.
    private void updateState() {
        if (state == STATE_DRAW && round.isRoundOver()) {
            state = STATE_END_OF_ROUND;
            startEndOfRound();
        }
    }

    // Runs the steps that need no click (A, B, C, and D/E for players who did not explode),
    // then moves to the first step that needs a click.
    private void startEndOfRound() {
        ArrayList<Player> all = board.getPlayers();
        int start = board.getStartPlayer();
        order = new ArrayList<Player>();
        for (int i = 0; i < all.size(); i++) {
            order.add(all.get((start + i) % all.size()));
        }

        endOfRound.rollBonusDie(round);                  // step A
        for (Player p : order) {
            endOfRound.chipActions(p);                   // step B
        }
        for (Player p : order) {
            endOfRound.rubyMethod(p);                    // step C
            // Saved after step B because a green token can move the last token
            p.setLastTurnSpace(p.getCauldron().getScoringSpace());
        }

        // Steps D and E: exploded players must choose points or coins
        choiceQueue = new ArrayList<Player>();
        for (Player p : order) {
            if (round.wasExploded(p)) {
                choiceQueue.add(p);
            } else {
                endOfRound.scorePot(p, true);
            }
        }

        if (choiceQueue.isEmpty()) {
            beginShop();
        } else {
            endPhase = PHASE_CHOICE;
            endIndex = 0;
            endFocus = choiceQueue.get(0);
        }
    }

    // ---- Shopping ----

    private void beginShop() {
        endPhase = PHASE_SHOP;
        endIndex = 0;
        beginShopper();
    }

    // Lists the tokens the current shopper can pick from (unlocked and in stock).
    private void beginShopper() {
        endFocus = order.get(endIndex);
        selected.clear();
        shopColors.clear();
        shopValues.clear();
        for (String color : SHOP_COLORS) {
            for (int value : SHOP_VALUES) {
                if (board.isUnlocked(color) && board.inStock(color, value)) {
                    shopColors.add(color);
                    shopValues.add(value);
                }
            }
        }
    }

    // Selects or unselects an item. Max 2, different colors, and the coins must cover the total.
    private void toggleShopItem(int i) {
        if (selected.contains(i)) {
            selected.remove(Integer.valueOf(i));
            return;
        }
        if (selected.size() >= 2) {
            return;
        }
        int cost = board.getPrice(shopColors.get(i), shopValues.get(i));
        for (int s : selected) {
            if (shopColors.get(s).equals(shopColors.get(i))) {
                return;
            }
            cost += board.getPrice(shopColors.get(s), shopValues.get(s));
        }
        if (cost <= order.get(endIndex).getCoins()) {
            selected.add(i);
        }
    }

    // Buys the selected tokens (or nothing) and moves to the next shopper.
    // tokenMethod gets null and 0 for the second token when only one is bought.
    private void buySelected() {
        Player p = order.get(endIndex);
        if (selected.size() == 1) {
            endOfRound.tokenMethod(p, shopColors.get(selected.get(0)), shopValues.get(selected.get(0)), null, 0);
        } else if (selected.size() == 2) {
            endOfRound.tokenMethod(p, shopColors.get(selected.get(0)), shopValues.get(selected.get(0)),
                    shopColors.get(selected.get(1)), shopValues.get(selected.get(1)));
        }

        endIndex++;
        if (endIndex >= order.size()) {
            endOfRound.addBoughtTokens();               // bought tokens go in the bags once everyone is done
            beginRubies();
        } else {
            beginShopper();
        }
    }

    // ---- Spending rubies (step F) ----

    private void beginRubies() {
        endPhase = PHASE_RUBIES;
        endIndex = 0;
        endFocus = order.get(0);
    }

    // endOfTurn only cleans up the pot (rat stone, empty pot into bag, lose coins).
    private void finishRubies() {
        endOfRound.endOfTurn(order.get(endIndex));
        endIndex++;
        if (endIndex >= order.size()) {
            finishDay();
        } else {
            endFocus = order.get(endIndex);
        }
    }

    // Discards the fortune card, then starts the next day or ends the game after day 9.
    private void finishDay() {
        board.discard(round.getFortune());
        board.nextDay();
        if (board.isGameOver()) {
            endGame();
        } else {
            state = STATE_DRAW;
            round.startRound();
        }
    }

    // Final scoring, then sort the players. Everyone tied with the top player wins.
    private void endGame() {
        endOfRound.finalScoring();

        ranking = new ArrayList<Player>(board.getPlayers());
        Collections.sort(ranking);          // lowest to highest
        Collections.reverse(ranking);       // best player first

        winners = new ArrayList<Player>();
        Player top = ranking.get(0);
        for (Player p : ranking) {
            if (p.compareTo(top) == 0) {
                winners.add(p);
            }
        }
        state = STATE_GAME_OVER;
    }

    // The player whose pot is shown on screen (null when no pot should show).
    private Player focusPlayer() {
        if (state == STATE_DRAW) {
            return round.getCurrentPlayer();
        }
        if (state == STATE_END_OF_ROUND) {
            return endFocus;
        }
        return null;
    }
}
