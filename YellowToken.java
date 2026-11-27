public class YellowToken extends Token {
    public YellowToken(int value) {
        super("Mandrake", "Yellow", value);
    }

    @Override
    public int getLimitBonus(Cauldron c) {
        int count = c.countColor("Yellow");
        if (count >= 3) return 2;
        if (count >= 1) return 1;
        return 0;
    }
}
