public class RedToken extends Token {
    public RedToken(int value) {
        super("toad stools", "red", value);
    }
    @Override
    public int getMoveBonus(Cauldron c) {
        Token previous = c.getLastToken();
        if (previous == null || !previous.isWhite() ) {
            return 0;
        }
        else {
            return previous.getValue();
        }
    }
}
