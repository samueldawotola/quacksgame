public class BlueToken extends Token {
    public BlueToken(int value) {
        super("Crow Skull", "Blue", value);
    }

    @Override
    public void onPlaced(Player p, Board b) {
        PotSpace ps = b.getSpace(getSpace());
        if (ps != null && ps.hasRuby()) {
            p.addRubies(1);
        }
    }
}
