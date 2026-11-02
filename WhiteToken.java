public class WhiteToken extends Token {
    private int value;
    public WhiteToken(int value) {
        super("cherry bombs", "white", value);
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public boolean isWhite() {
        return true;
    }

}
