public class WhiteToken extends Token {
    public WhiteToken(int value) {
        super("cherry bombs", "white", value);
    }
    @Override
    public boolean isWhite() {
        return true;
    }

}
