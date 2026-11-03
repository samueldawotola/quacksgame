public class BlackToken extends Token {
    private int value;
    public BlackToken(int value) {
        super("hawkmoth", "black", value);
        this.value=value;
    }
    public int getValue() {
        return value;
    }
    public void compareBlackTokens(BlackToken other) {
        
    }
    @Override
    public void endAction(Player p, Board b) {
        Player left = b.getleftNeighbor(p);
        Player right = b.getRightNeighbor(p);
        int myCount = p.getCauldron().countColor("black");
        int leftCount = left.getCauldron().countColor("black");
        int rightCount = right.getCauldron().countColor("black");
        if (myCount > leftCount || myCount > rightCount) {
            p.getCauldron().moveDroplet(1);
        }
        if (myCount > leftCount && myCount > rightCount) {
            p.addRubies(1);
        }
    }
}