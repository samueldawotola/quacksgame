public class BlackToken extends Token {
    public BlackToken(int value) {
        super("hawkmoth", "black", 1);
    }
    @Override
    public void endAction(Player p, Board b) {
        Player left = b.getLeftNeighbor(p);
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