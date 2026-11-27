import java.util.Iterator;

public class GreenToken extends Token {
    public GreenToken(int value) {
        super("Garden Spider", "Green", value);
    }

    @Override
    public void endAction(Player p, Board b) {
        Cauldron c = p.getCauldron();
        if (c.getWhiteTotal() == 7) {
            int sum = 0;
            Iterator<Token> it = c.getPot().iterator();
            while (it.hasNext()) {
                Token t = it.next();
                if (t.getColor().equalsIgnoreCase("Green")) {
                    sum += t.getValue();
                }
            }
            c.moveLastToken(sum);
        }
    }
}
