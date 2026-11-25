import java.util.ArrayList;
import java.util.HashMap;

public class Bag {
    private ArrayList<Token> tokens;

    public Bag() {
        tokens = new ArrayList<>();
        fillStarterBag();
    }

    private void fillStarterBag() {
        for (int i = 0; i < 4; i++) tokens.add(new WhiteToken(1));
        for (int i = 0; i < 2; i++) tokens.add(new WhiteToken(2));
        tokens.add(new WhiteToken(3));
        tokens.add(new OrangeToken());
        tokens.add(new GreenToken(1));
    }

    public Token draw() {
        if (tokens.isEmpty()) return null;
        int idx = (int) (Math.random() * tokens.size());
        return tokens.remove(idx);
    }

    public void add(Token t) {
        if (t != null) tokens.add(t);
    }

    public void addAll(ArrayList<Token> list) {
        if (list != null) tokens.addAll(list);
    }

    public int size() { return tokens.size(); }
    public boolean isEmpty() { return tokens.isEmpty(); }

    public HashMap<String, Integer> countColors() {
        HashMap<String, Integer> counts = new HashMap<>();
        for (Token t : tokens) {
            counts.put(t.getColor(), counts.getOrDefault(t.getColor(), 0) + 1);
        }
        return counts;
    }
}
