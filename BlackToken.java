public class BlackToken extends Token {
    public BlackToken() {
        super("Hawkmoth", "Black", 1);
    }

    @Override
    public void endAction(Player p, Board b) {
        int myCount = p.getCauldron().countColor("Black");
        if (myCount == 0) return;

        int leftCount = b.getLeftNeighbor(p).getCauldron().countColor("Black");
        int rightCount = b.getRightNeighbor(p).getCauldron().countColor("Black");

        if (myCount > leftCount && myCount > rightCount) {
            p.getCauldron().moveDroplet(1);
            p.addRubies(1);
        } else if (myCount > leftCount || myCount > rightCount) {
            p.getCauldron().moveDroplet(1);
        }
    }
}
