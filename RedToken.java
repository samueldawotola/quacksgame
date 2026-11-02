public class RedToken extends Token {
    private int value;
    public RedToken(int value) {
        super("toad stools", "red", value);
        this.value = value;
    }
    public int getValue() {
        return value;
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
