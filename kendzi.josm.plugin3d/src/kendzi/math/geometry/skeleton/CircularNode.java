package kendzi.math.geometry.skeleton;

public class CircularNode {
    CircularNode next;
    CircularNode previous;

    protected CircularList list;

    public CircularList list() {
        return this.list;
    }

    public CircularNode next() {
        return this.next;
    }

    public CircularNode previous() {
        return this.previous;
    }

    public void addNext(CircularNode node) {
        this.list.addNext(this, node);
    }

    public void addPrevious(CircularNode node) {
        this.list.addPrevious(this, node);
    }

    public void remove() {
        this.list.remove(this);
    }
}
