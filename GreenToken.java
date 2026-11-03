import java.util.*;
public class GreenToken extends Token {
    private int value;
    public GreenToken(int value) {
        super("garden spider", "green", value);
        this.value=value;
    }
    public int getValue() {
        return value;
    }
    public void addGreenToken(Cauldron c) {
        if (c.getWhiteTotal() == 7) {
            int total = 0;
            Iterator<Token> it = c.tokensOf("green").iterator();
        while (it.hasNext()) {
            Token current = it.next();
            total += current.getValue();
        }
        c.moveLastToken(total);
        }
    }
    @Override
    public void endAction(Player p, Board b) {
        addGreenToken(p.getCauldron());
    }
}
