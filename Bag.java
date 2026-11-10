import java.util.*;

public class Bag {
    private ArrayList<Token> tokens;
    private HashMap<String, Integer> countVar = new HashMap<String, Integer>();
    public Bag() {
        fillStarterBag();
    }
    //adds the starting tokens in bag
    private void fillStarterBag() {
        for (int i = 0; i < 4; i++) { //simple arraylist instead of adding the same thing 4 times...
        tokens.add(new Token("cherry bombs", "white", 1));
        }
        for (int i = 0; i < 2; i++) { //same thing
        tokens.add(new Token("cherry bombs", "white", 2));
        }
        tokens.add(new Token("cherry bombs", "white", 3));
        tokens.add(new Token("pumpkin", "orange", 1));
        tokens.add(new Token("garden spider", "green", 1));
    }
    public Token draw() {
        int randomIndex = (int)(Math.random() * tokens.size());
        if(tokens.isEmpty()) {
            return null;
        }
        return tokens.remove(randomIndex);
    }
    public void add(Token t) {
        tokens.add(t);
    }
    public void addAll(ArrayList<Token> list) {
        tokens.addAll(list);
    }
    public int size() {
        return tokens.size();
    }
    public boolean isEmpty() {
        if (tokens.size() == 0) {
            return true;
        }
        return false;
    }
    // Method to adding amount to each respective color.
    public HashMap<String, Integer> countColors() {
        for (int i = 0; i < tokens.size(); i++) { //Goes through entire tokens Arraylist
           if (countVar.containsKey(tokens.get(i).getColor())) { //condition to checking if that token color is already added to countVar map variable
                countVar.put(tokens.get(i).getColor(), countVar.get(tokens) + 1); //if yes, replaces current value to the added value from finding value of the token color inside of countVar map variable
           }
           else {
                countVar.put(tokens.get(i).getColor(), 1); // if not, simply add the color with the amount of 1
           }
        }
        return countVar; //returns our countVar Map with all colors added with respective amount.
    }
}
