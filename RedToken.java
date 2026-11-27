import java.util.ArrayList;

public class RedToken extends Token {
    public RedToken(int value) {
        super("Toadstool", "Red", value);
    }

    @Override
    public int getMoveBonus(Cauldron c) {
        ArrayList<Token> pot = c.getPot();
        if (!pot.isEmpty()) {
            Token prev = pot.get(pot.size() - 1);
            if (prev.isWhite()) {
                return prev.getValue();
            }
        }
        return 0;
    }
}
