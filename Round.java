import java.util.ArrayList;
import java.util.HashSet;

public class Round {
    private Board board;
    private Card fortune;
    private HashSet<Player> finished;
    private HashSet<Player> exploded;
    private int turnIndex;
    private String[] choiceOptions;

    public Round(Board board) {
        this.board = board;
        this.finished = new HashSet<>();
        this.exploded = new HashSet<>();
    }

    public void startRound() {
        finished.clear();
        exploded.clear();
        choiceOptions = null;
        turnIndex = board.getStartPlayer();

        fortune = board.drawFortune();
        int limitBonus = fortune.isImmediate() ? 0 : fortune.getExplodeLimitBonus();

        for (Player p : board.getPlayers()) {
            p.getCauldron().setCardLimitBonus(limitBonus);
            p.getCauldron().setRatSteps(board.getRatTailSteps(p));
        }

        if (fortune.isImmediate()) {
            fortune.apply(board, this);
        }
    }

    public void drawChip(Player p) {
        if (finished.contains(p) || choiceOptions != null) return;

        Token t = p.drawToken();
        if (t == null) {
            finished.add(p);
            advanceTurn();
            return;
        }

        Cauldron c = p.getCauldron();
        c.placeToken(t);

        if (!fortune.isImmediate() && t.getColor().equalsIgnoreCase("Orange")) {
            c.moveLastToken(fortune.getOrangeMoveBonus());
        }

        t.onPlaced(p, board);

        if (c.isExploded()) {
            exploded.add(p);
            finished.add(p);
        } else if (p.getBag().isEmpty()) {
            finished.add(p);
        }

        advanceTurn();
    }

    public void startChoice(String[] options) {
        this.choiceOptions = options;
    }

    public void resolveChoice(Player p, int index) {
        if (choiceOptions == null) return;
        String chosen = choiceOptions[index];
        if (chosen.contains("Droplet +2")) p.getCauldron().moveDroplet(2);
        else if (chosen.contains("Droplet +1")) p.getCauldron().moveDroplet(1);
        else if (chosen.contains("Ruby") || chosen.contains("Rubies")) p.addRubies(1);
        else if (chosen.contains("Flask")) p.getCauldron().fillFlask();
        else if (chosen.contains("Coins")) p.addCoins(2);
        else if (chosen.contains("Green")) p.getBag().add(new GreenToken(1));

        choiceOptions = null;
    }

    public void stop(Player p) {
        if (finished.contains(p) || choiceOptions != null) return;
        finished.add(p);
        advanceTurn();
    }

    public void useFlask(Player p) {
        if (finished.contains(p) || choiceOptions != null) return;
        Token returned = p.getCauldron().useFlask();
        if (returned != null) {
            p.getBag().add(returned);
        }
    }

    private void advanceTurn() {
        ArrayList<Player> players = board.getPlayers();
        if (isRoundOver()) return;
        for (int i = 0; i < players.size(); i++) {
            turnIndex = (turnIndex + 1) % players.size();
            if (!finished.contains(players.get(turnIndex))) {
                break;
            }
        }
    }

    public Player getCurrentPlayer() { return board.getPlayers().get(turnIndex); }
    public boolean isRoundOver() { return finished.size() == board.getPlayers().size(); }
    public boolean wasExploded(Player p) { return exploded.contains(p); }
    public Card getFortune() { return fortune; }
    public String[] getChoiceOptions() { return choiceOptions; }
}
