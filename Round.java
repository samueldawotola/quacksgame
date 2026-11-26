import java.util.*;

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

        fortune = board.drawFortune();
        
        if (fortune.isImmediate()) {
            fortune.apply(board, this);
        }

        ArrayList<Player> players = board.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            Cauldron c = p.getCauldron();
            
            if (board.getDay() > 1) {
                c.setRatSteps(board.getRatTailSteps(p));
            }
            
        }
        
        turnIndex = 0; 
    }

    public void drawChip(Player p) {
        Token t = p.drawToken();
        if (t == null) {
            finished.add(p);
            return;
        }

        Cauldron c = p.getCauldron();
        c.placeToken(t);
        t.onPlaced(p, board);

        if (c.isExploded()) {
            exploded.add(p);
            finished.add(p);
        }
        
        if (p.getBag().isEmpty()) {
            finished.add(p);
        }
    }

    public void useFlask(Player p) {
        Cauldron c = p.getCauldron();
        Token returnedToken = c.useFlask();
        if (returnedToken != null) {
            p.getBag().add(returnedToken);
        }
    }

    public void stop(Player p) {
        finished.add(p);
    }

    public void startChoice(String[] options) {
        this.choiceOptions = options;
    }

    public void resolveChoice(Player p, int index) {
        this.choiceOptions = null;
    }

    public boolean isRoundOver() {
        return finished.size() == board.getPlayers().size();
    }

    public boolean wasExploded(Player p) {
        return exploded.contains(p);
    }

    public Player getCurrentPlayer() {
        ArrayList<Player> players = board.getPlayers();
        Player p = players.get(turnIndex % players.size());
        
        while (finished.contains(p) && finished.size() < players.size()) {
            turnIndex++;
            p = players.get(turnIndex % players.size());
        }
        return p;
    }

    public void nextTurn() {
        turnIndex++;
    }

    public Card getFortune() {
        return fortune;
    }

    public String[] getChoiceOptions() {
        return choiceOptions;
    }
}