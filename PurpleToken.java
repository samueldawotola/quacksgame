public class PurpleToken extends Token {
    public PurpleToken() {
        super("Ghost's Breath", "Purple", 1);
    }

    @Override
    public void endAction(Player p, Board b) {
        for (Token t : p.getCauldron().tokensOf("Purple")) {
            int pts = Math.min(3, t.getSpace() / 10);
            p.addVictoryPoints(pts);
        }
    }
}
