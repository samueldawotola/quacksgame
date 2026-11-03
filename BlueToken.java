public class BlueToken extends Token {
    private int value;
    public BlueToken(int value) {
        super("crow skull", "blue", value);
        this.value=value;
    }
    public int getValue() {
        return value;
    }
    @Override
    public void onPlaced(Player p, Board b) {
        if (b.getSpace(this.getSpace()).hasRuby()) {
            p.addRubies(1);
        }
    }
}
