import java.util.*;
public class PurpleToken extends Token {
    public PurpleToken() {
        super("ghost's breath", "purple", 1);
    }
    public void addPurpleToken(Player p, Cauldron c) {
        Iterator<Token> it = c.tokensOf("purple").iterator();
        while (it.hasNext()) {
            Token current = it.next();
            int points = Math.min(3, current.getSpace()/10);
            p.addVictoryPoints(points);
        }
    }
    @Override
    public void endAction(Player p, Board b) {
        addPurpleToken(p, p.getCauldron());
    }
}
