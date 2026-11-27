public class WhiteToken extends Token {
    public WhiteToken(int value) {
        super("Cherry Bomb", "White", value);
    }

    @Override
    public boolean isWhite() {
        return true;
    }
}
