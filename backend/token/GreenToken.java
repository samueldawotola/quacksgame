import java.util.*;
public class GreenToken extends Token {
    public GreenToken(int value) {
        super("garden spider", "green", value);
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
    public void endAction(Player p, Card b) {
        addGreenToken(p.getCauldron());
    }
}
