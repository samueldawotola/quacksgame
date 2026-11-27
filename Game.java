import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Game extends JPanel implements MouseListener {
    private Board board;
    private Round round;
    private EndOfRound endOfRound;

    private int state;
    public static final int STATE_MENU = 0;
    public static final int STATE_DRAW = 1;
    public static final int STATE_END_OF_ROUND = 2;
    public static final int STATE_GAME_OVER = 3;
    public static final int STATE_RULES = 4;

    private int[][] trackPoints;
    private static final Rectangle PLAY_RECT = new Rectangle(320, 440, 140, 120);
    private static final Rectangle RULES_RECT = new Rectangle(75, 105, 225, 80);
    private static final Rectangle RULES_HOME_RECT = new Rectangle(95, 115, 70, 70);
    private static final Rectangle RULES_NEXT_RECT = new Rectangle(1110, 625, 70, 70);
    private static final Rectangle DRAW_RECT = new Rectangle(520, 560, 110, 42);
    private static final Rectangle FLASK_RECT = new Rectangle(645, 560, 110, 42);
    private static final Rectangle STOP_RECT = new Rectangle(770, 560, 110, 42);
    private static final Rectangle NEXT_DAY_RECT = new Rectangle(1060, 650, 180, 45);
    private static final Rectangle PLAY_AGAIN_RECT = new Rectangle(540, 620, 200, 50);

    private static final double ENLARGE_SCALE = 2.0;
    private HashMap<Rectangle, Enlargeable> zones;
    private Enlargeable enlargedItem;
    private int enlargedX;
    private int enlargedY;

    // Loaded image assets
    private BufferedImage menuImg;
    private BufferedImage potsDefaultImg;
    private BufferedImage diceRollImg;
    private BufferedImage allCardsBigImg;
    private BufferedImage enlargingBackImg;
    private BufferedImage[] rulesImgs;
    private int rulePageIndex = 0;

    // End-of-round interactive state
    private int eorPlayerIdx = 0;
    private boolean awaitingExplodeChoice = false;
    private String selectedShopColor = null;

    public Game() {
        setPreferredSize(new Dimension(1280, 720));
        addMouseListener(this);
        zones = new HashMap<>();
        state = STATE_MENU;
        loadAssets();
        initTrackPoints();
    }

    private BufferedImage loadImg(String name) {
        try {
            File f = new File(name);
            if (f.exists()) return ImageIO.read(f);
        } catch (IOException ignored) {}
        return null;
    }

    private void loadAssets() {
        menuImg = loadImg("Menu.png");
        potsDefaultImg = loadImg("DrawingPotsDefault.png");
        diceRollImg = loadImg("DiceRoll.png");
        allCardsBigImg = loadImg("AllCardsBig.png");
        enlargingBackImg = loadImg("EnlargingBack.png");
        rulesImgs = new BufferedImage[] {
            loadImg("Rules1-2.png"),
            loadImg("Rules3-4.png"),
            loadImg("Rules5-6.png"),
            loadImg("Rules7-8.png"),
            loadImg("Rules9-10.png"),
            loadImg("Rules11-12.png")
        };
    }

    private void initTrackPoints() {
        trackPoints = new int[51][2];
        // 51 spaces arranged around the central board track
        for (int i = 0; i <= 50; i++) {
            if (i <= 12) {
                trackPoints[i][0] = 355;
                trackPoints[i][1] = 635 - i * 36;
            } else if (i <= 26) {
                trackPoints[i][0] = 355 + (i - 12) * 38;
                trackPoints[i][1] = 200;
            } else if (i <= 37) {
                trackPoints[i][0] = 885;
                trackPoints[i][1] = 200 + (i - 26) * 39;
            } else {
                trackPoints[i][0] = 885 - (i - 37) * 38;
                trackPoints[i][1] = 635;
            }
        }
    }

    private void startNewGame() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Green Apothecary"));
        players.add(new Player("Blue Apothecary"));
        players.add(new Player("Yellow Apothecary"));
        players.add(new Player("Red Apothecary"));

        board = new Board(players);
        round = new Round(board);
        endOfRound = new EndOfRound(board);
        state = STATE_DRAW;
        round.startRound();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        zones.clear();

        switch (state) {
            case STATE_MENU:         drawMenu(g); break;
            case STATE_RULES:        drawRules(g); break;
            case STATE_DRAW:         drawPlayScreen(g); break;
            case STATE_END_OF_ROUND: drawEndOfRound(g); break;
            case STATE_GAME_OVER:    drawGameOver(g); break;
        }

        if (enlargedItem != null) {
            enlarge(g, enlargedItem, enlargedX, enlargedY, ENLARGE_SCALE);
        }
    }

    private void drawMenu(Graphics g) {
        if (menuImg != null) {
            g.drawImage(menuImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(165, 65, 42));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(245, 225, 165));
            g.setFont(new Font("Serif", Font.BOLD, 52));
            g.drawString("The Quacks of Quedlinburg", 330, 180);
            drawButton(g, PLAY_RECT, "START");
            drawButton(g, RULES_RECT, "RULES");
        }
    }

    private void drawRules(Graphics g) {
        BufferedImage page = rulesImgs[rulePageIndex];
        if (page != null) {
            g.drawImage(page, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(165, 65, 42));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Serif", Font.BOLD, 28));
            g.drawString("Rules Page " + (rulePageIndex + 1) + " of " + rulesImgs.length, 480, 340);
            drawButton(g, RULES_HOME_RECT, "HOME");
            drawButton(g, RULES_NEXT_RECT, "NEXT");
        }
    }

    private void drawPlayScreen(Graphics g) {
        if (potsDefaultImg != null) {
            g.drawImage(potsDefaultImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(110, 72, 48));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        drawPlayers(g);
        drawFortune(g);
        drawActionButtons(g);

        if (round.getChoiceOptions() != null) {
            drawChoice(g, round.getChoiceOptions());
        }
    }

    private void drawBoard(Graphics g) {
        if (diceRollImg != null) {
            g.drawImage(diceRollImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(110, 72, 48));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(235, 220, 185));
            g.fillRect(340, 185, 575, 475);
        }

        for (int i = 0; i <= 50; i++) {
            g.setColor(new Color(245, 235, 200));
            g.fillRect(trackPoints[i][0] - 12, trackPoints[i][1] - 12, 24, 24);
            g.setColor(Color.BLACK);
            g.drawRect(trackPoints[i][0] - 12, trackPoints[i][1] - 12, 24, 24);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.drawString(String.valueOf(i), trackPoints[i][0] - 7, trackPoints[i][1] + 4);
        }
    }

    private void drawPlayers(Graphics g) {
        // 4 quadrant offsets matching DrawingPotsDefault.png
        int[][] quadOffsets = {
            {75, 45},   // Top-left: Green
            {845, 45},  // Top-right: Blue
            {85, 395},  // Bottom-left: Yellow
            {845, 395}  // Bottom-right: Red
        };
        Color[] pColors = {
            new Color(65, 165, 75),
            new Color(55, 130, 215),
            new Color(225, 185, 40),
            new Color(210, 55, 55)
        };

        ArrayList<Player> players = board.getPlayers();
        Player current = round.getCurrentPlayer();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int ox = quadOffsets[i][0];
            int oy = quadOffsets[i][1];

            // Status HUD banner
            g.setColor(new Color(30, 25, 20, 210));
            g.fillRoundRect(ox, oy - 35, 340, 30, 8, 8);
            if (p == current && state == STATE_DRAW) {
                g.setColor(Color.YELLOW);
                g.drawRoundRect(ox - 2, oy - 37, 344, 34, 8, 8);
            }

            g.setColor(pColors[i]);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            String status = p.getName() + " | VP:" + p.getVictoryPoints()
                    + " R:" + p.getRubies()
                    + " W:" + p.getCauldron().getWhiteTotal() + "/" + p.getCauldron().getExplodeLimit()
                    + (round.wasExploded(p) ? " [BOOM]" : "");
            g.drawString(status, ox + 8, oy - 16);

            // Register PotSpaces and draw Cauldron
            for (PotSpace ps : board.getPotSpaces()) {
                int sx = ox + ps.getX();
                int sy = oy + ps.getY();
                if (potsDefaultImg == null) {
                    ps.draw(g, sx, sy);
                }
                registerZone(new Rectangle(sx, sy, ps.getWidth(), ps.getHeight()), ps);
            }

            p.getCauldron().draw(g, board, ox, oy, 1.0);
            for (Token t : p.getCauldron().getPot()) {
                PotSpace ps = board.getSpace(t.getSpace());
                if (ps != null) {
                    registerZone(new Rectangle(ox + ps.getX(), oy + ps.getY(), t.getWidth(), t.getHeight()), t);
                }
            }
        }
    }

    private void drawFortune(Graphics g) {
        Card c = round.getFortune();
        if (c != null) {
            int cx = 585;
            int cy = 260;
            c.draw(g, cx, cy);
            registerZone(new Rectangle(cx, cy, c.getWidth(), c.getHeight()), c);
        }
        g.setColor(new Color(30, 25, 20, 220));
        g.fillRoundRect(565, 215, 150, 34, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.BOLD, 16));
        g.drawString("Day " + board.getDay() + " / " + Board.MAX_DAYS, 595, 238);
    }

    private void drawActionButtons(Graphics g) {
        drawButton(g, DRAW_RECT, "Stir! (Draw)");
        drawButton(g, FLASK_RECT, round.getCurrentPlayer().getCauldron().isFlaskFull() ? "Use Flask" : "Flask Empty");
        drawButton(g, STOP_RECT, "Stop!");
    }

    private void drawChoice(Graphics g, String[] options) {
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.BOLD, 24));
        g.drawString(round.getCurrentPlayer().getName() + " - Make a Choice:", 460, 270);

        for (int i = 0; i < options.length; i++) {
            Rectangle r = new Rectangle(480, 310 + i * 65, 320, 48);
            drawButton(g, r, options[i]);
        }
    }

    private void drawEndOfRound(Graphics g) {
        drawBoard(g);

        // Draw track markers & 50 seals
        ArrayList<Player> players = board.getPlayers();
        Color[] pColors = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED};
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int pos = p.getTrackPosition();
            int mx = trackPoints[pos][0] + (i % 2) * 8 - 8;
            int my = trackPoints[pos][1] + (i / 2) * 8 - 8;
            g.setColor(pColors[i]);
            g.fillOval(mx, my, 14, 14);
            g.setColor(Color.BLACK);
            g.drawOval(mx, my, 14, 14);

            if (p.hasFiftySeal()) {
                g.setColor(new Color(245, 200, 60));
                g.fillOval(mx + 12, my - 4, 14, 14);
                g.setColor(Color.BLACK);
                g.setFont(new Font("SansSerif", Font.BOLD, 8));
                g.drawString("50", mx + 14, my + 6);
            }
        }

        // End of Round HUD & Shop Panel
        Player active = players.get(eorPlayerIdx);
        g.setColor(new Color(35, 25, 20, 235));
        g.fillRoundRect(365, 230, 500, 375, 14, 14);
        g.setColor(new Color(220, 185, 110));
        g.drawRoundRect(365, 230, 500, 375, 14, 14);

        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString("End of Day " + board.getDay() + " - " + active.getName(), 390, 265);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        g.drawString("VP: " + active.getVictoryPoints() + " | Coins: " + active.getCoins() + " | Rubies: " + active.getRubies(), 390, 290);

        if (awaitingExplodeChoice) {
            g.setColor(Color.ORANGE);
            g.drawString("Your pot exploded! Choose Points OR Coins:", 390, 330);
            drawButton(g, new Rectangle(390, 355, 200, 45), "Take Victory Points");
            drawButton(g, new Rectangle(620, 355, 200, 45), "Take Coins");
            return;
        }

        // Shop buttons for unlocked colors
        String[] colors = {"Orange", "Blue", "Red", "Yellow", "Green", "Purple", "Black"};
        int row = 0;
        for (String col : colors) {
            if (!board.getUnlockedColors().contains(col)) continue;
            int y = 315 + row * 34;
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(col + ":", 390, y + 20);

            int[] vals = (col.equals("Orange") || col.equals("Purple") || col.equals("Black"))
                    ? new int[]{1} : new int[]{1, 2, 4};
            for (int vIdx = 0; vIdx < vals.length; vIdx++) {
                int val = vals[vIdx];
                int price = board.getPrice(col, val);
                Rectangle btn = new Rectangle(465 + vIdx * 115, y, 105, 26);
                drawButton(g, btn, val + "-chip (" + price + "c)");
            }
            row++;
        }

        // Ruby actions
        drawButton(g, new Rectangle(390, 555, 215, 36), "2 Rubies: Droplet +1");
        drawButton(g, new Rectangle(620, 555, 215, 36), "2 Rubies: Refill Flask");
        drawButton(g, NEXT_DAY_RECT, "Finish Turn");
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(45, 28, 22));
        g.fillRect(0, 0, getWidth(), getHeight());

        ArrayList<Player> sorted = new ArrayList<>(board.getPlayers());
        Collections.sort(sorted);

        g.setColor(new Color(245, 210, 115));
        g.setFont(new Font("Serif", Font.BOLD, 42));
        g.drawString("Final Standings - After Day 9", 370, 110);

        Player top = sorted.get(0);
        StringBuilder winners = new StringBuilder("Winner: " + top.getName());
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).compareTo(top) == 0) {
                winners.append(" & ").append(sorted.get(i).getName());
            }
        }
        g.setFont(new Font("Serif", Font.BOLD, 26));
        g.drawString(winners.toString(), 420, 165);

        for (int i = 0; i < sorted.size(); i++) {
            Player p = sorted.get(i);
            int y = 240 + i * 80;
            g.setColor(new Color(75, 50, 40));
            g.fillRoundRect(340, y, 600, 60, 12, 12);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            String line = (i + 1) + ". " + p.getName() + " — " + p.getVictoryPoints()
                    + " VP (Track: " + p.getTrackPosition() + (p.hasFiftySeal() ? " + 50 Seal" : "") + ")";
            g.drawString(line, 365, y + 38);
        }

        drawButton(g, PLAY_AGAIN_RECT, "Play Again");
    }

    private void drawButton(Graphics g, Rectangle r, String label) {
        g.setColor(new Color(235, 218, 180));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setColor(new Color(75, 45, 20));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        int strW = g.getFontMetrics().stringWidth(label);
        g.drawString(label, r.x + (r.width - strW) / 2, r.y + (r.height / 2) + 5);
    }

    private void registerZone(Rectangle r, Enlargeable item) {
        zones.put(r, item);
    }

    private void enlarge(Graphics g, Enlargeable item, int x, int y, double scale) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = (int) (item.getWidth() * scale);
        int h = (int) (item.getHeight() * scale);

        int cx = Math.max(w / 2 + 10, Math.min(getWidth() - w / 2 - 10, x + item.getWidth() / 2));
        int cy = Math.max(h / 2 + 10, Math.min(getHeight() - h / 2 - 10, y + item.getHeight() / 2));

        if (enlargingBackImg != null) {
            g2.drawImage(enlargingBackImg, cx - w / 2 - 12, cy - h / 2 - 12, w + 24, h + 24, null);
        }

        g2.translate(cx - w / 2.0, cy - h / 2.0);
        g2.scale(scale, scale);
        item.draw(g2, 0, 0);
        g2.dispose();
    }

    private void shrink() {
        enlargedItem = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (enlargedItem != null) {
            shrink();
            repaint();
            return;
        }

        if (state == STATE_MENU) {
            if (PLAY_RECT.contains(mx, my)) {
                startNewGame();
            } else if (RULES_RECT.contains(mx, my)) {
                rulePageIndex = 0;
                state = STATE_RULES;
            }
            repaint();
            return;
        }

        if (state == STATE_RULES) {
            if (RULES_HOME_RECT.contains(mx, my)) {
                state = STATE_MENU;
            } else if (RULES_NEXT_RECT.contains(mx, my)) {
                rulePageIndex = (rulePageIndex + 1) % rulesImgs.length;
            }
            repaint();
            return;
        }

        if (state == STATE_DRAW) {
            Player current = round.getCurrentPlayer();
            String[] opts = round.getChoiceOptions();
            if (opts != null) {
                for (int i = 0; i < opts.length; i++) {
                    Rectangle r = new Rectangle(480, 310 + i * 65, 320, 48);
                    if (r.contains(mx, my)) {
                        round.resolveChoice(current, i);
                        repaint();
                        return;
                    }
                }
                return;
            }

            if (DRAW_RECT.contains(mx, my)) {
                round.drawChip(current);
                updateState();
            } else if (FLASK_RECT.contains(mx, my)) {
                round.useFlask(current);
            } else if (STOP_RECT.contains(mx, my)) {
                round.stop(current);
                updateState();
            } else {
                for (Map.Entry<Rectangle, Enlargeable> entry : zones.entrySet()) {
                    if (entry.getKey().contains(mx, my)) {
                        enlargedItem = entry.getValue();
                        enlargedX = entry.getKey().x;
                        enlargedY = entry.getKey().y;
                        break;
                    }
                }
            }
            repaint();
            return;
        }

        if (state == STATE_END_OF_ROUND) {
            handleEndOfRoundClick(mx, my);
            repaint();
            return;
        }

        if (state == STATE_GAME_OVER) {
            if (PLAY_AGAIN_RECT.contains(mx, my)) {
                state = STATE_MENU;
                repaint();
            }
        }
    }

    private void handleEndOfRoundClick(int mx, int my) {
        ArrayList<Player> players = board.getPlayers();
        Player active = players.get(eorPlayerIdx);

        if (awaitingExplodeChoice) {
            Rectangle ptsBtn = new Rectangle(390, 355, 200, 45);
            Rectangle coinBtn = new Rectangle(620, 355, 200, 45);
            if (ptsBtn.contains(mx, my)) {
                endOfRound.scorePot(active, true);
                awaitingExplodeChoice = false;
            } else if (coinBtn.contains(mx, my)) {
                endOfRound.scorePot(active, false);
                awaitingExplodeChoice = false;
            }
            return;
        }

        // Check shop clicks
        String[] colors = {"Orange", "Blue", "Red", "Yellow", "Green", "Purple", "Black"};
        int row = 0;
        for (String col : colors) {
            if (!board.getUnlockedColors().contains(col)) continue;
            int y = 315 + row * 34;
            int[] vals = (col.equals("Orange") || col.equals("Purple") || col.equals("Black"))
                    ? new int[]{1} : new int[]{1, 2, 4};
            for (int vIdx = 0; vIdx < vals.length; vIdx++) {
                Rectangle btn = new Rectangle(465 + vIdx * 115, y, 105, 26);
                if (btn.contains(mx, my)) {
                    if (selectedShopColor == null) {
                        endOfRound.tokenMethod(active, col, vals[vIdx], null, 0);
                        selectedShopColor = col;
                    } else if (!selectedShopColor.equals(col)) {
                        endOfRound.tokenMethod(active, null, 0, col, vals[vIdx]);
                        selectedShopColor = "DONE";
                    }
                    return;
                }
            }
            row++;
        }

        // Check Ruby spending buttons
        if (new Rectangle(390, 555, 215, 36).contains(mx, my)) {
            if (active.spendRubies(2)) active.getCauldron().moveDroplet(1);
            return;
        }
        if (new Rectangle(620, 555, 215, 36).contains(mx, my)) {
            if (active.spendRubies(2)) active.getCauldron().fillFlask();
            return;
        }

        // Advance to next player's end-of-round shopping/cleanup
        if (NEXT_DAY_RECT.contains(mx, my)) {
            endOfRound.endOfTurn(active, round.getFortune());
            selectedShopColor = null;
            eorPlayerIdx++;

            if (eorPlayerIdx < players.size()) {
                awaitingExplodeChoice = round.wasExploded(players.get(eorPlayerIdx));
            } else {
                endOfRound.deliverPurchasedTokens();
                board.discard(round.getFortune());
                board.nextDay();
                if (board.isGameOver()) {
                    endOfRound.finalScoring();
                    state = STATE_GAME_OVER;
                } else {
                    round.startRound();
                    state = STATE_DRAW;
                }
            }
        }
    }

    private void updateState() {
        if (round.isRoundOver()) {
            state = STATE_END_OF_ROUND;
            endOfRound.runEndOfRound(round);
            eorPlayerIdx = 0;
            awaitingExplodeChoice = round.wasExploded(board.getPlayers().get(0));
            selectedShopColor = null;
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
