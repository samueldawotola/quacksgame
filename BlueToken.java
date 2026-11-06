public class BlueToken extends Token {
    public BlueToken(int value) {
        super("crow skull", "blue", value);
    }
    @Override
    public void onPlaced(Player p, Board b) {
        if (b.getSpace(this.getSpace()).hasRuby()) {
            p.addRubies(1);
        }
    }
}
