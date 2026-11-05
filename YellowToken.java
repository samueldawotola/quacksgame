public class YellowToken extends Token {
    private int value;
    public YellowToken(int value) {
        super("mandrake", "yellow", value);
        this.value=value;
    }
    public int getValue() {
        return value;
    }
    @Override
    public int getLimitBonus(Cauldron c) {
        if (c.countColor("yellow") == 1 || c.countColor("yellow") == 2) {
            return 1;
        }
        else if (c.countColor("yellow") >= 3) {
            return 2;
        }
        else {
            return 0;
        }
    }
}
