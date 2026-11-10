public class BlueToken extends Token {
    public BlueToken(int value) {
        super("crow skull", "blue", value);
    }
    @Override
    public void onPlaced(Player p, Card b) {
        if (b.getSpace(this.getSpace()).hasRuby()) {s
            p.addRubies(1);
        }
    }
}
