import java.util.ArrayList;

public class EndOfRound {
    private Board board;
    private ArrayList<Player> pendingShopPlayers;
    private ArrayList<Token> pendingBoughtTokens;

    public EndOfRound(Board board) {
        this.board = board;
        this.pendingShopPlayers = new ArrayList<>();
        this.pendingBoughtTokens = new ArrayList<>();
    }

    public void runEndOfRound(Round r) {
        rollBonusDie(r);

        ArrayList<Player> players = board.getPlayers();
        int start = board.getStartPlayer();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get((start + i) % players.size());
            chipActions(p);
            rubyMethod(p);
            if (!r.wasExploded(p)) {
                scorePot(p, true);
            }
        }
    }

    public void rollBonusDie(Round r) {
        int maxSpace = -1;
        for (Player p : board.getPlayers()) {
            if (!r.wasExploded(p)) {
                int sp = p.getCauldron().getScoringSpace();
                if (sp > maxSpace) maxSpace = sp;
            }
        }
        if (maxSpace < 0) return;

        int rolls = (!r.getFortune().isImmediate() && r.getFortune().rollsTwice()) ? 2 : 1;
        for (Player p : board.getPlayers()) {
            if (!r.wasExploded(p) && p.getCauldron().getScoringSpace() == maxSpace) {
                for (int k = 0; k < rolls; k++) {
                    int face = (int) (Math.random() * 6);
                    Card.applyBonusDieFace(p, face);
                }
            }
        }
    }

    public void chipActions(Player p) {
        Cauldron c = p.getCauldron();
        for (String color : c.getUniqueColors()) {
            ArrayList<Token> tokens = c.tokensOf(color);
            if (!tokens.isEmpty()) {
                tokens.get(0).endAction(p, board);
            }
        }
    }

    public void rubyMethod(Player p) {
        int spIdx = p.getCauldron().getScoringSpace();
        PotSpace ps = board.getSpace(spIdx);
        if (ps != null && ps.hasRuby()) {
            p.addRubies(1);
        }
    }

    public void scorePot(Player p, boolean choosePoints) {
        int spIdx = p.getCauldron().getScoringSpace();
        p.setLastTurnSpace(spIdx);
        PotSpace ps = board.getSpace(spIdx);
        if (ps == null) return;

        if (spIdx == Cauldron.LAST_SPACE) {
            p.addVictoryPoints(15);
            p.addCoins(35);
            return;
        }

        // Non-exploded gets both; exploded gets only the chosen one
        Cauldron c = p.getCauldron();
        if (!c.isExploded()) {
            p.addVictoryPoints(ps.getVictoryPoints());
            p.addCoins(ps.getCoins());
        } else {
            if (choosePoints) p.addVictoryPoints(ps.getVictoryPoints());
            else p.addCoins(ps.getCoins());
        }
    }

    public void tokenMethod(Player p, String colorA, int valueA, String colorB, int valueB) {
        if (colorA != null && board.inStock(colorA, valueA)) {
            int priceA = board.getPrice(colorA, valueA);
            if (p.spendCoins(priceA)) {
                Token boughtA = board.buyTokenFromStock(colorA, valueA);
                pendingShopPlayers.add(p);
                pendingBoughtTokens.add(boughtA);
            }
        }
        if (colorB != null && !colorB.equals(colorA) && board.inStock(colorB, valueB)) {
            int priceB = board.getPrice(colorB, valueB);
            if (p.spendCoins(priceB)) {
                Token boughtB = board.buyTokenFromStock(colorB, valueB);
                pendingShopPlayers.add(p);
                pendingBoughtTokens.add(boughtB);
            }
        }
    }

    public void deliverPurchasedTokens() {
        for (int i = 0; i < pendingShopPlayers.size(); i++) {
            pendingShopPlayers.get(i).getBag().add(pendingBoughtTokens.get(i));
        }
        pendingShopPlayers.clear();
        pendingBoughtTokens.clear();
    }

    public void endOfTurn(Player p, Card fortune) {
        Cauldron c = p.getCauldron();
        if (!fortune.isImmediate() && fortune.refillsFlasks()) {
            c.fillFlask();
        }
        if (!fortune.isImmediate() && fortune.exactSevenDroplet() && c.getWhiteTotal() == 7) {
            c.moveDroplet(1);
        }
        c.setRatSteps(0);
        c.emptyInto(p.getBag());
        p.loseUnspentCoins();
    }

    public void finalScoring() {
        for (Player p : board.getPlayers()) {
            p.buyFinalPoints();
        }
    }
}
