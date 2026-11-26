import java.util.*;

public class EndOfRound {
    private Board board;

    public EndOfRound(Board board) {
        this.board = board;
    }

    public void runEndOfRound(Round r) {
        ArrayList<Player> players = board.getPlayers();

        rollBonusDie(r);

        for (Player p : players) {
            Cauldron c = p.getCauldron();
            for (String color : c.getUniqueColors()) {
                ArrayList<Token> tokens = c.tokensOf(color);
                if (!tokens.isEmpty()) {
                    tokens.get(0).endAction(p, board);
                }
            }

            rubyMethod(p);

            boolean exploded = r.wasExploded(p);
            if (!exploded) {
                PotSpace space = board.getSpace(c.getScoringSpace());
                p.addVictoryPoints(space.getVictoryPoints());
                p.addCoins(space.getCoins());
            } else {
                // scorePot(p, choiceBoolean); 
            }
        }

        for (Player p : players) {
            Cauldron c = p.getCauldron();
            c.setRatSteps(0);
            c.emptyInto(p.getBag());
            p.loseUnspentCoins();
        }

        board.discard(r.getFortune());
        board.nextDay();
    }

    public void rollBonusDie(Round r) {
        int maxSpace = -1;
        ArrayList<Player> tiedPlayers = new ArrayList<>();

        for (Player p : board.getPlayers()) {
            if (!r.wasExploded(p)) {
                int space = p.getCauldron().getScoringSpace();
                if (space > maxSpace) {
                    maxSpace = space;
                    tiedPlayers.clear();
                    tiedPlayers.add(p);
                } else if (space == maxSpace) {
                    tiedPlayers.add(p);
                }
            }
        }
        for (Player p : tiedPlayers) {
            int roll = (int) (Math.random() * 6);
        }
    }

    public void rubyMethod(Player p) {
        PotSpace space = board.getSpace(p.getCauldron().getScoringSpace());
        if (space.hasRuby()) {
            p.addRubies(1);
        }
    }

    public void finalScoring() {
        for (Player p : board.getPlayers()) {
            p.buyFinalPoints();
        }
    }
}