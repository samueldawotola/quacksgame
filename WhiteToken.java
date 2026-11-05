public class WhiteToken extends Token {
    private int value;
    public WhiteToken(int value) {
        super("cherry bombs", "white", value);
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    @Override
    public boolean isWhite() {
        return true;
    }

}
